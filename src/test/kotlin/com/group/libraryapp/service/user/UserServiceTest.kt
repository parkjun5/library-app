package com.group.libraryapp.service.user

import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanStatus
import com.group.libraryapp.dto.user.request.UserCreateRequest
import com.group.libraryapp.dto.user.request.UserUpdateRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@Suppress("NonAsciiCharacters")
@SpringBootTest
class UserServiceTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val userLoanHistoryRepository: UserLoanHistoryRepository,
) {

    @AfterEach
    fun clean() {
        userRepository.deleteAll()
    }

    @Test
    fun `회원을 등록 테스트`() {
        //given
        val userCreateRequest = UserCreateRequest("테스터", null)

        //when
        userService.saveUser(userCreateRequest)

        //then
        val results = userRepository.findAll()
        assertThat(results).hasSize(1)
        assertThat(results[0].name).isEqualTo("테스터")
        assertThat(results[0].age).isNull()
    }

    @Test
    fun `회원 조회 테스트`() {
        //given
        userRepository.saveAll(
            listOf(
                User("테스터1", 13),
                User("테스터2", null),
                User("테스터3", 33),
            )
        )

        //when
        val results = userService.getUsers()

        //then
        assertThat(results).hasSize(3)
        assertThat(results[0].name).isEqualTo("테스터1")
        assertThat(results[0].age).isNotNull()

        assertThat(results).extracting("name").containsExactlyInAnyOrder("테스터1", "테스터2", "테스터3")
        assertThat(results).extracting("age").containsExactlyInAnyOrder(13, null, 33)

    }

    @Test
    fun `회원 변경 테스트`() {
        //given
        val savedUser = userRepository.save(User("테스터1", null))
        val request = UserUpdateRequest(savedUser.id!!, "변경될 이름")

        //when
        userService.updateUserName(request)

        //then
        val result = userRepository.findById(savedUser.id!!).get()
        assertThat(result.name).isEqualTo("변경될 이름")
        assertThat(result.age).isNull()
    }

    @Test
    fun `회원 삭제 테스트`() {
        //given
        userRepository.saveAll(
            listOf(
                User("테스터1", 13),
                User("테스터2", null),
                User("테스터3", 33),
            )
        )

        //when
        userService.deleteUser("테스터2")

        //then
        val results = userRepository.findAll()
        assertThat(results).hasSize(2)
        assertThat(results).extracting("name").containsExactlyInAnyOrder("테스터1", "테스터3")
        assertThat(results).extracting("age").containsExactlyInAnyOrder(13, 33)
    }

    @Test
    fun `대출 기록이 없는 유저도 응답이 포함된다`() {
        //given
        userRepository.save(User("테스터", null))

        //when
        val userLoanHistories = userService.getUserLoanHistories()

        //then
        assertThat(userLoanHistories).hasSize(1)
        assertThat(userLoanHistories[0].name).isEqualTo("테스터")
    }

    @Test
    fun `대출 기록이 많은 유저의 응답이 정상 등작한다`() {
        //given
        val saveUser = userRepository.save(User("테스터", null))
        userLoanHistoryRepository.saveAll(
            listOf(
                UserLoanHistory.fixture(saveUser, "자본론", UserLoanStatus.RETURNED),
                UserLoanHistory.fixture(saveUser, "책1", UserLoanStatus.LOANED),
                UserLoanHistory.fixture(saveUser, "책2", UserLoanStatus.LOANED),
                UserLoanHistory.fixture(saveUser, "책3", UserLoanStatus.LOANED),
            )
        )
        //when
        val userLoanHistories = userService.getUserLoanHistories()

        //then
        assertThat(userLoanHistories).hasSize(1)
        assertThat(userLoanHistories[0].books).hasSize(4)
        assertThat(userLoanHistories[0].books)
            .extracting("name")
            .containsExactlyInAnyOrder("자본론", "책1", "책2", "책3")

        assertThat(userLoanHistories[0].books)
            .extracting("isReturn")
            .containsExactlyInAnyOrder(true, false, false, false)
    }
}
package com.group.libraryapp.service.user

import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
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
    private val userService: UserService
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
        userRepository.saveAll(listOf(
            User("테스터1", 13),
            User("테스터2", null),
            User("테스터3", 33),
        ))

        //when
        val results = userService.users

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
        val request = UserUpdateRequest(savedUser.id, "변경될 이름")

        //when
        userService.updateUserName(request)

        //then
        val result = userRepository.findById(savedUser.id).get()
        assertThat(result.name).isEqualTo("변경될 이름")
        assertThat(result.age).isNull()

    }

    @Test
    fun `회원 삭제 테스트`() {
        //given
        userRepository.saveAll(listOf(
            User("테스터1", 13),
            User("테스터2", null),
            User("테스터3", 33),
        ))

        //when
        userService.deleteUser("테스터2")

        //then
        val results = userRepository.findAll()
        assertThat(results).hasSize(2)
        assertThat(results).extracting("name").containsExactlyInAnyOrder("테스터1", "테스터3")
        assertThat(results).extracting("age").containsExactlyInAnyOrder(13, 33)
    }
}
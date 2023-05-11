package com.group.libraryapp.service.book

import com.group.libraryapp.domain.book.Book
import com.group.libraryapp.domain.book.BookRepository
import com.group.libraryapp.domain.book.BookType
import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanStatus
import com.group.libraryapp.dto.book.request.BookLoanRequest
import com.group.libraryapp.dto.book.request.BookRequest
import com.group.libraryapp.dto.book.request.BookReturnRequest
import com.group.libraryapp.util.fail
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@Suppress("NonAsciiCharacters")
@SpringBootTest
class BookServiceTest @Autowired constructor(
    private val bookService: BookService,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val userLoanHistoryRepository: UserLoanHistoryRepository
) {

    @BeforeEach
    fun savingBasicBook() {
        bookRepository.save(Book.fixture("코틀린 테스트"))
        userRepository.save(User("테스터", 13))
    }

    @AfterEach
    fun clean() {
        bookRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    fun `책 저장 테스트`() {
        //given
        val request = BookRequest("새로운 책", BookType.COMPUTER)

        //when
        bookService.saveBook(request)

        //then
        val book = bookRepository.findByName("새로운 책") ?: fail()
        assertThat(book.type.name).isEqualTo("COMPUTER")
        assertThat(book.type).isEqualTo(BookType.COMPUTER)
        assertThat(book.name).isEqualTo("새로운 책")
    }

    @Test
    fun `책을 대출하는 기능 정상작동 테스트`() {
        //given
        val request = BookLoanRequest("테스터", "코틀린 테스트")

        //when
        bookService.loanBook(request)

        //then
        val result: List<UserLoanHistory> = userLoanHistoryRepository.findAll()
        assertThat(result).hasSize(1)
        assertThat(result).extracting("bookName").containsExactly("코틀린 테스트")
        assertThat(result).extracting("user").extracting("name").containsExactly("테스터")
        assertThat(result[0].status).isEqualTo(UserLoanStatus.LOANED)
    }

    @Test
    fun `책을 찾지 못한 상황`() {
        //given
        val request = BookLoanRequest("테스터", "코틀린 테스트>?!")

        //when && then
        assertThrows<IllegalArgumentException> { bookService.loanBook(request) }
    }

    @Test
    fun `책을 대출할 때, 이미 빌린 책일 경우 에러 발생`() {
        //given
        val request = BookLoanRequest("테스터", "코틀린 테스트")

        //when
        bookService.loanBook(request)

        //then
        assertThrows<IllegalArgumentException> { bookService.loanBook(request) }
            .apply { assertThat(message).isEqualTo("진작 대출되어 있는 책입니다") }
    }

    @Test
    fun `책 반납 정상 작동 테스트`() {
        //given
        val request = BookReturnRequest("반납자", "코틀린 테스트")
        val user = userRepository.save(User("반납자", 234))
        userLoanHistoryRepository.save(
            UserLoanHistory.fixture(
                user, "코틀린 테스트"
            )
        )

        //when
        bookService.returnBook(request)

        //then
        val result: List<UserLoanHistory> = userLoanHistoryRepository.findAll()
        assertThat(result).hasSize(1)
        assertThat(result[0].status).isEqualTo(UserLoanStatus.RETURNED)
    }


}
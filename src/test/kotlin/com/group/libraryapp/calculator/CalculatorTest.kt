package com.group.libraryapp.calculator

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CalculatorTest {

    companion object {
        private lateinit var calculator: Calculator
        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            println("모든 테스트 시작전")
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            println("모든 테스트 종료후")
        }
    }

    @BeforeEach
    fun beforeEach() {
        calculator = Calculator(3)
        val initNum = calculator.number
        assertThat(initNum).isEqualTo(3)
    }

    @Test
    fun addTest() {
        //when
        calculator.add(2)

        //then
        val plusNumber = calculator.number
        assertThat(plusNumber).isEqualTo(5)
    }

    @Test
    fun minusTest() {
        //when
        calculator.minus(2)

        //then
        val plusNumber = calculator.number
        assertThat(plusNumber).isEqualTo(1)
    }

    @Test
    fun multiplyTest() {
        //when
        calculator.multiply(2)

        //then
        val plusNumber = calculator.number
        assertThat(plusNumber).isEqualTo(6)
    }

    @Test
    fun divideTest() {
        //when
        calculator.divide(3)
        //then
        val plusNumber = calculator.number
        assertThat(plusNumber).isEqualTo(1)

    }

    @Test
    fun divideTo0ExceptionTest() {
        //then

        assertThrows<IllegalArgumentException> {
            calculator.divide(0)
        }.apply {
            assertThat(message).isEqualTo("0으로 나눌 수 없습니다.")
        }
    }
}
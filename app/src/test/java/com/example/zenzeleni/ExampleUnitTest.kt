package com.example.zenzeleni

import org.junit.Test
import org.junit.Assert.*

class ExampleUnitTest {

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    @Test
    fun emptyEmail_isInvalid() {
        assertFalse(isValidEmail(""))
    }

    @Test
    fun properEmail_isValid() {
        assertTrue(isValidEmail("test@example.com"))
    }

    @Test
    fun shortPassword_isInvalid() {
        assertFalse(isValidPassword("123"))
    }

    @Test
    fun properPassword_isValid() {
        assertTrue(isValidPassword("password123"))
    }

    @Test
    fun pointsCalculation_addsCorrectly() {
        val currentPoints = 50
        val earnedPoints = 20
        val total = currentPoints + earnedPoints
        assertEquals(70, total)
    }
}
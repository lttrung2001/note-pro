package com.lttrung.notepro.utils

class ValidationHelper {
    var hasError = false
    fun matchesPasswordLength(password: String): Boolean {
        val length = password.length
        val value = length in 8..32
        if (!value) {
            hasError = true
        }
        return value
    }

    fun matchesFullName(fullName: String): Boolean {
        val value = fullName.split(" ").size > 1
        if (!value) {
            hasError = true
        }
        return value
    }

    fun matchesPhoneNumber(phoneNumber: String): Boolean {
        val value = phoneNumber.substring(1).length == 9
        if (!value) {
            hasError = true
        }
        return value
    }

    fun matchesConfirmPassword(password: String, confirmPassword: String): Boolean {
        val value = password == confirmPassword
        if (!value) {
            hasError = true
        }
        return value
    }
}
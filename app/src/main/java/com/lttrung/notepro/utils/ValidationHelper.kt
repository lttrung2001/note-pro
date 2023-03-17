package com.lttrung.notepro.utils

object ValidationHelper {
    var hasError = false
    fun matchesPasswordLength(password: String): Boolean {
        val length = password.length
        val value = length in 8..32
        hasError = !value
        return value
    }

    fun matchesFullName(fullName: String): Boolean {
        val value = fullName.split(" ").size < 2
        hasError = !value
        return value
    }

    fun matchesPhoneNumber(phoneNumber: String): Boolean {
        val value = phoneNumber.substring(1).length == 9
        hasError = !value
        return value
    }
}
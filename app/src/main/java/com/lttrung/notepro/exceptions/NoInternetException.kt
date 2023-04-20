package com.lttrung.notepro.exceptions

data class NoInternetException(override val message: String = "No internet connection") :
    Exception(message)
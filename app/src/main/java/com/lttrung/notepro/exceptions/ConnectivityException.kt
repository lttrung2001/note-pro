package com.lttrung.notepro.exceptions

data class ConnectivityException(override val message: String = "No internet connection") :
    Exception(message)
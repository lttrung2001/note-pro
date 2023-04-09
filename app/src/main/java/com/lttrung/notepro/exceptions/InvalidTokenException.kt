package com.lttrung.notepro.exceptions

class InvalidTokenException(override val message: String = "Invalid token exception") :
    Exception(message)
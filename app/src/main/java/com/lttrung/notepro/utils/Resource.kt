package com.lttrung.notepro.utils

sealed interface Resource<T> {
    class Success<T>(val data: T): Resource<T>
    class Loading<T>(val data: T? = null): Resource<T>
    class Error<T>(val t: Throwable, val data: T? = null): Resource<T>
}
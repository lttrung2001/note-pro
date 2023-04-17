package com.lttrung.notepro.domain.data.networks.models

data class ApiResponse<T>(val message: String, val data: T)
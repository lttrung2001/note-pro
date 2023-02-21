package com.lttrung.notepro.database.data.networks.models

data class ApiResponse<T>(val message: String, val data: T)
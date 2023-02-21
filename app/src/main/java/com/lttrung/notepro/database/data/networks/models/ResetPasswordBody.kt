package com.lttrung.notepro.database.data.networks.models

data class ResetPasswordBody(
    private val codeVerify: String,
    private val newPassword: String
)
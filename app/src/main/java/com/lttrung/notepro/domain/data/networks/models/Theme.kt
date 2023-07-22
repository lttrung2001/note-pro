package com.lttrung.notepro.domain.data.networks.models

import java.io.Serializable

data class Theme(
    val id: String,
    val bgUrl: String,
    val myMsgBgColor: String,
    val myMsgTextColor: String,
) : Serializable
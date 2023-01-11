package com.lttrung.notepro.model

data class Paging<E>(
    val data: List<E>,
    val hasNextPage: Boolean
)

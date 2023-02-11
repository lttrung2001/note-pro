package com.lttrung.notepro.model

data class Paging<E>(
    val hasPreviousPage: Boolean,
    val hasNextPage: Boolean,
    val data: List<E>
)

package com.lttrung.notepro.database.data.models

data class Paging<E>(
    val hasPreviousPage: Boolean,
    val hasNextPage: Boolean,
    val data: List<E>
)

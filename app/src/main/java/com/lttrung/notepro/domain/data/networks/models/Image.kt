package com.lttrung.notepro.domain.data.networks.models

import java.io.Serializable

open class Image(
    open val id: String,
    open val name: String,
    open val url: String,
    open val uploadTime: Long,
    open val uploadBy: String
) : Serializable {
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun toString(): String {
        return super.toString()
    }
}

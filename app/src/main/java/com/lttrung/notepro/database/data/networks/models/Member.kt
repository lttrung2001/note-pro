package com.lttrung.notepro.database.data.networks.models

import java.io.Serializable

data class Member(
    val id: String,
    val email: String,
    val fullName: String,
    val role: String,
    val phoneNumber: String
) : Serializable {
    override fun equals(other: Any?): Boolean {
        return if (other is Member) {
            this.id == other.id
        } else {
            super.equals(other)
        }
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + fullName.hashCode()
        result = 31 * result + role.hashCode()
        result = 31 * result + phoneNumber.hashCode()
        return result
    }
}

package com.lttrung.notepro.utils

import java.util.*

object Converter {
    fun longToDate(lastModified: Long): String {
        val today = Calendar.getInstance()
        val lastModifiedDate = Calendar.getInstance()
        lastModifiedDate.timeInMillis = lastModified
        return if (today.get(Calendar.YEAR) > lastModifiedDate.get(Calendar.YEAR)) {
            "Năm ${lastModifiedDate.get(Calendar.YEAR)}"
        } else if (today.get(Calendar.MONTH) > lastModifiedDate.get(Calendar.MONTH)) {
            "Tháng ${lastModifiedDate.get(Calendar.MONTH) + 1}"
        } else if (today.get(Calendar.DAY_OF_MONTH) > lastModifiedDate.get(Calendar.DAY_OF_MONTH)) {
            "Ngày ${lastModifiedDate.get(Calendar.DAY_OF_MONTH)}"
        } else {
            "${lastModifiedDate.get(Calendar.HOUR)}:${lastModifiedDate.get(Calendar.MINUTE)}"
        }
    }
}
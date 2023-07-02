package com.lttrung.notepro.utils

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

fun Bitmap.toByteArray(): ByteArray {
    val bos = ByteArrayOutputStream()
    val isSuccess = compress(Bitmap.CompressFormat.PNG, 0, bos)
    return if (isSuccess) {
        bos.toByteArray()
    } else {
        throw UnsupportedOperationException("Can not convert to byte array!")
    }
}
package com.lttrung.notepro.utils

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

fun requestPermissionToOpenCamera(activity: AppCompatActivity): Boolean {
    return if (ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.CAMERA),
            AppConstant.CAMERA_REQUEST
        )
        false
    } else {
        true
    }
}

fun requestPermissionToReadGallery(activity: AppCompatActivity): Boolean {
    return if (ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            AppConstant.READ_EXTERNAL_STORAGE_REQUEST
        )
        false
    } else {
        true
    }
}
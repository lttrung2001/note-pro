package com.lttrung.notepro.utils

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

object PermissionUtils {
    const val CAMERA_PERMISSION_REQUEST_CODE = 1
    const val READ_EXTERNAL_STORAGE_REQUEST_CODE = 2

    fun checkCameraPermission(activity: AppCompatActivity, f: () -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            f()
        }
    }

    fun checkReadExternalStoragePermission(activity: AppCompatActivity, f: () -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_REQUEST_CODE
            )
        } else {
            f()
        }
    }
}
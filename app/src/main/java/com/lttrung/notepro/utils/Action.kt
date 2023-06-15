package com.lttrung.notepro.utils

import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import com.lttrung.notepro.ui.activities.viewgallery.ViewGalleryActivity

fun openCamera(launcher: ActivityResultLauncher<Intent>) {
    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    launcher.launch(cameraIntent)
}

fun openGallery(launcher: ActivityResultLauncher<Intent>, context: Context) {
    val galleryIntent = Intent(context, ViewGalleryActivity::class.java)
    launcher.launch(galleryIntent)
}
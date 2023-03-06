package com.lttrung.notepro.ui.base

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.lttrung.notepro.ui.addimages.AddImagesFragment
import com.lttrung.notepro.ui.notedetails.adapters.ImagesAdapter
import com.lttrung.notepro.ui.viewgallery.ViewGalleryActivity
import com.lttrung.notepro.utils.AppConstant

abstract class AddImagesActivity : AppCompatActivity() {
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == AppConstant.CAMERA_REQUEST && permissions.contains(Manifest.permission.CAMERA)) {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            launcher.launch(cameraIntent)
        } else if (requestCode == AppConstant.READ_EXTERNAL_STORAGE_REQUEST && permissions.contains(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            val pickImagesIntent = Intent(this, ViewGalleryActivity::class.java)
            launcher.launch(pickImagesIntent)
        }
    }

    protected val openBottomSheetDialogListener: View.OnClickListener by lazy {
        View.OnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    AppConstant.READ_EXTERNAL_STORAGE_REQUEST
                )
            } else {
                openBottomSheetMenu()
            }
        }
    }

    private fun openBottomSheetMenu() {
        val bottomSheet = AddImagesFragment()
        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
    }

    protected abstract val launcher: ActivityResultLauncher<Intent>

    fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        launcher.launch(cameraIntent)
    }

    fun openGallery() {
        val pickImagesIntent = Intent(this, ViewGalleryActivity::class.java)
        launcher.launch(pickImagesIntent)
    }
}
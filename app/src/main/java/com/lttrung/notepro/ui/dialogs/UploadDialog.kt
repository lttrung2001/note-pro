package com.lttrung.notepro.ui.dialogs

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color.TRANSPARENT
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.DialogUploadFileBinding
import java.io.File

class UploadDialog(
    context: Context,
    val url: String,
    val callBack: (uri: String) -> Unit
) : AlertDialog(context) {
    private val binding by lazy {
        DialogUploadFileBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        window?.setBackgroundDrawable(ColorDrawable(TRANSPARENT))
        setCancelable(false)
        setContentView(R.layout.dialog_upload_file)
        val byteArray = File(url).readBytes()
        binding.progressBar.max = byteArray.size
        FirebaseStorage
            .getInstance()
            .reference
            .child("images/messages/${System.currentTimeMillis()}.jpg")
            .putBytes(byteArray)
            .addOnProgressListener { uploadSnapshot ->
                binding.progressBar.progress = uploadSnapshot.bytesTransferred.toInt()
            }.addOnSuccessListener {
                it.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                    callBack(uri.toString())
                    dismiss()
                }

            }
    }
}
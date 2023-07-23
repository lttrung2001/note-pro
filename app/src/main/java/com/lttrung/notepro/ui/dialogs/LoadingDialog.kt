package com.lttrung.notepro.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color.TRANSPARENT
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.DialogLoadingBinding

class LoadingDialog (context: Context) : Dialog(context) {
    val binding by lazy { DialogLoadingBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(TRANSPARENT))
        setCancelable(false)
        setContentView(binding.root)
    }
}
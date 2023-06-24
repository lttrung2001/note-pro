package com.lttrung.notepro.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color.TRANSPARENT
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import com.lttrung.notepro.R

class LoadingDialog (context: Context) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(TRANSPARENT))
        setCancelable(false)
        setContentView(R.layout.dialog_loading)
    }
}
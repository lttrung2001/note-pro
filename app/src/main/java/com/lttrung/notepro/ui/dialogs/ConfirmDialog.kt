package com.lttrung.notepro.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.text.util.Linkify
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.lttrung.notepro.R

class ConfirmDialog(val mContext: Context) : Dialog(mContext) {
    var mContent: String? = null
    var mLeftTitle: String? = null
    var mRightTitle: String? = null
    var mTitle: String? = null
    var mAlertIcon = -1
    var mBtLeft: TextView? = null
    var mBtRight: TextView? = null
    var mTvContent: TextView? = null
    var mTvTitle: TextView? = null
    var mIvAlert: ImageView? = null
    var needActionAll = false
    var leftOnClick: () -> Unit? = {}
    var rightOnClick: () -> Unit? = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_notice)
        mTvTitle = findViewById(R.id.tv_title)
        mTvContent = findViewById(R.id.tv_content)
        mBtRight = findViewById(R.id.bt_right)
        mBtLeft = findViewById(R.id.bt_left)
        mIvAlert = findViewById(R.id.ivAlert)
        mBtRight!!.setOnClickListener {
            rightOnClick()
            dismiss()
        }
        mBtLeft!!.setOnClickListener {
            leftOnClick()
            dismiss()
        }

        if (mLeftTitle != null) {
            if (mRightTitle != null) mBtRight!!.text = mRightTitle
            if (mBtLeft!!.visibility != View.VISIBLE) mBtLeft!!.visibility = View.VISIBLE
            mBtLeft!!.text = mLeftTitle
        } else {
            mBtLeft!!.visibility = View.GONE
            if (mRightTitle != null) mBtRight!!.setText(mRightTitle)
        }
        if (mTitle != null) {
            mTvTitle!!.text = mTitle
            mTvTitle!!.visibility = View.VISIBLE
        }
        if (mAlertIcon != -1) {
            mIvAlert!!.setImageDrawable(ContextCompat.getDrawable(mContext, mAlertIcon))
            mIvAlert!!.visibility = View.VISIBLE
        }
        setContent(mContent)
        setCancelable(true)
        setOnCancelListener { actionDialog() }
    }

    private fun actionDialog() {
        if (needActionAll
        ) {
            if (mBtLeft != null && mBtLeft!!.visibility == View.VISIBLE) leftOnClick()
            else
                rightOnClick()
        }
    }

    private fun setContent(content: String?) {
        mContent = content
        if (mTvContent != null) {
            if (mContent!!.contains("<strong>") || mContent!!.contains("</a>")) mTvContent!!.text =
                Html.fromHtml(mContent) else {
                mTvContent!!.text = mContent
                try {
                    Linkify.addLinks(mTvContent!!, Linkify.ALL)
                } catch (e: Exception) {
                    Log.wtf("EXX", e)
                }
            }
        }
    }


    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}
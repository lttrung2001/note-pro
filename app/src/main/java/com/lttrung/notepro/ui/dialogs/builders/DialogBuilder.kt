package com.lttrung.notepro.ui.dialogs.builders

import android.content.Context
import android.text.Html
import android.text.util.Linkify
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.lttrung.notepro.R
import com.lttrung.notepro.ui.dialogs.ConfirmDialog

class DialogBuilder(val mContext: Context) : IDialogBuilder {
    companion object {
        fun Builder(context: Context): IDialogBuilder {
            val newBuilder = DialogBuilder(context)
            if (newBuilder.mBtLeft != null) {
                newBuilder.mBtLeft!!.visibility = View.GONE
            }
            if (newBuilder.mTvTitle != null) newBuilder.mTvTitle!!.visibility = View.GONE
            newBuilder.needActionAll = false
            newBuilder.leftOnClick = { }
            newBuilder.rightOnClick = { }
            newBuilder.mLeftTitle = null
            newBuilder.mRightTitle = context.getString(R.string.agree)
            if (newBuilder.mBtRight != null) {
                newBuilder.mBtRight!!.text = newBuilder.mRightTitle
            }
            if (newBuilder.mTvTitle != null) {
                newBuilder.mTitle = context.getString(R.string.notification)
                newBuilder.mTvTitle!!.visibility = View.GONE
            }
            if (newBuilder.mIvAlert != null) {
                newBuilder.mAlertIcon = -1
                newBuilder.mIvAlert!!.visibility = View.GONE
            }
            return newBuilder
        }
    }
    private var mContent: String? = null
    private var mLeftTitle: String? = null
    private var mRightTitle: String? = null
    private var mTitle: String? = null
    private var mAlertIcon = -1
    private var mBtLeft: TextView? = null
    private var mBtRight: TextView? = null
    private var mTvContent: TextView? = null
    private var mTvTitle: TextView? = null
    private var mIvAlert: ImageView? = null
    private var needActionAll = false
    private var leftOnClick: () -> Unit? = {}
    private var rightOnClick: () -> Unit? = {}
    private var isCancelable = true
    override fun build(): ConfirmDialog {
        val dialog = ConfirmDialog(mContext)
        dialog.mContent = mContent
        dialog.mLeftTitle = mLeftTitle
        dialog.mRightTitle = mRightTitle
        dialog.mTitle = mTitle
        dialog.mAlertIcon = mAlertIcon
        dialog.mBtLeft = mBtLeft
        dialog.mBtRight = mBtRight
        dialog.mTvContent = mTvContent
        dialog.mTvTitle = mTvTitle
        dialog.mIvAlert = mIvAlert
        dialog.needActionAll = needActionAll
        dialog.leftOnClick = leftOnClick
        dialog.rightOnClick = rightOnClick
        dialog.setCancelable(isCancelable)
        dialog.setCanceledOnTouchOutside(isCancelable)
        return dialog
    }

    override fun setAction(needActionAll: Boolean): IDialogBuilder {
        this.needActionAll = needActionAll
        return this
    }

    override fun setCanTouchOutside(b: Boolean): IDialogBuilder {
        isCancelable = b
        return this
    }

    override fun setNotice(content: String?): IDialogBuilder {
        setContent(content)
        return this
    }

    override fun setNotice(idContent: Int): IDialogBuilder {
        mContent = mContext.getString(idContent)
        setContent(mContent)
        return this
    }

    override fun setContent(content: String?): IDialogBuilder {
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
        return this
    }

    override fun addButtonLeft(id: Int): IDialogBuilder {
        return addButtonLeft(mContext.getString(id))
    }

    override fun addButtonLeft(onLeftOnClick: View.OnClickListener?): IDialogBuilder {
        if (mBtLeft != null) {
            mBtLeft!!.visibility = View.VISIBLE
            mBtLeft!!.text = mContext.getString(R.string.agree)
        } else {
            mLeftTitle = mContext.getString(R.string.cancel)
        }
        return this
    }

    override fun addButtonLeft(title: String?): IDialogBuilder {
        if (mBtLeft != null) {
            mBtLeft!!.visibility = View.VISIBLE
            mBtLeft!!.text = title
        } else {
            mLeftTitle = title
        }
        return this
    }

    override fun addButtonLeft(title: Int, onLeftOnClick: () -> Unit): IDialogBuilder {
        addButtonLeft(mContext.getString(title), onLeftOnClick)
        return this
    }

    override fun addButtonLeft(title: String?, onLeftOnClick: () -> Unit): IDialogBuilder {
        leftOnClick = onLeftOnClick
        if (mBtLeft != null) {
            mBtLeft!!.visibility = View.VISIBLE
            mBtLeft!!.text = title
        } else {
            mLeftTitle = title
        }
        return this
    }

    override fun addButtonRight(onRightClick: () -> Unit): IDialogBuilder {
        rightOnClick = onRightClick
        if (mBtRight != null) {
            mBtRight!!.setText(R.string.agree)
        } else {
            mRightTitle = mContext.getString(R.string.agree)
        }
        return this
    }

    override fun addButtonRight(title: Int, onRightClick: () -> Unit): IDialogBuilder {
        addButtonRight(mContext.getString(title), onRightClick)
        return this
    }

    override fun addButtonRight(title: Int): IDialogBuilder {
        addButtonRight(mContext.getString(title), {})
        return this
    }

    override fun addButtonRight(title: String?, onRightClick: () -> Unit): IDialogBuilder {
        rightOnClick = onRightClick
        if (mBtRight != null) {
            mBtRight!!.text = title
        } else {
            mRightTitle = title
        }
        return this
    }

    override fun setIcon(icon: Int): IDialogBuilder {
        if (mIvAlert != null) {
            mIvAlert!!.setImageDrawable(ContextCompat.getDrawable(mContext, icon))
            mIvAlert!!.visibility = View.VISIBLE
        } else {
            mAlertIcon = icon
        }
        return this
    }

    override fun setNoticeTitle(idTitle: Int): IDialogBuilder {
        setNoticeTitle(mContext.getString(idTitle))
        return this
    }

    override fun setNoticeTitle(title: String?): IDialogBuilder {
        if (mTvTitle != null) {
            mTvTitle!!.text = title
            mTvTitle!!.visibility = View.VISIBLE
        } else {
            mTitle = title
        }
        return this
    }

    override fun removeActionAll(): IDialogBuilder {
        needActionAll = false
        return this
    }
}
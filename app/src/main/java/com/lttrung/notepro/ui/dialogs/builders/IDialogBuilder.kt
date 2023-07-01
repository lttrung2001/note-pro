package com.lttrung.notepro.ui.dialogs.builders

import android.view.View
import androidx.annotation.StringRes
import com.lttrung.notepro.ui.dialogs.ConfirmDialog

interface IDialogBuilder {
    fun build(): ConfirmDialog
    fun setAction(needActionAll: Boolean): IDialogBuilder
    fun setCanTouchOutside(b: Boolean): IDialogBuilder
    fun setNotice(content: String?): IDialogBuilder
    fun setContent(content: String?): IDialogBuilder
    fun setNotice(@StringRes idContent: Int): IDialogBuilder
    fun addButtonLeft(@StringRes id: Int): IDialogBuilder
    fun addButtonLeft(onLeftOnClick: View.OnClickListener?): IDialogBuilder
    fun addButtonLeft(title: String?): IDialogBuilder
    fun addButtonLeft(title: Int, onLeftOnClick: () -> Unit): IDialogBuilder
    fun addButtonLeft(title: String?, onLeftOnClick: () -> Unit): IDialogBuilder
    fun addButtonRight(onRightClick: () -> Unit): IDialogBuilder
    fun addButtonRight(title: Int, onRightClick: () -> Unit): IDialogBuilder
    fun addButtonRight(title: Int): IDialogBuilder
    fun addButtonRight(title: String?, onRightClick: () -> Unit): IDialogBuilder
    fun setIcon(icon: Int): IDialogBuilder
    fun setNoticeTitle(idTitle: Int): IDialogBuilder
    fun setNoticeTitle(title: String?): IDialogBuilder
    fun removeActionAll(): IDialogBuilder
}
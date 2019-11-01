package com.play.accompany.utils

import android.content.Context

import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction

object DialogUtils {

    fun showCommonDialog(context: Context, content: String, dialogListener: (Boolean) -> Unit) {
        QMUIDialog.MessageDialogBuilder(context).setMessage(content).addAction("取消") { dialog, _ ->
            dialogListener(false)
            dialog.dismiss()
        }.addAction("确认"){dialog, _ ->
            dialogListener(true)
            dialog.dismiss()
        }.create().show()
    }
}

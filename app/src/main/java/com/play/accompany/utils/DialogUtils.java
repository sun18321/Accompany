package com.play.accompany.utils;

import android.content.Context;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

public class DialogUtils {

    public static void showCommonDialog(Context context, String content, QMUIDialogAction.ActionListener cancelListener,QMUIDialogAction.ActionListener confirmListener) {
        new QMUIDialog.MessageDialogBuilder(context).setMessage(content).addAction("取消", cancelListener).addAction("确认", confirmListener)
                .create().show();

    }
}

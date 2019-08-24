package com.play.accompany.design;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.play.accompany.R;

public class BottomDialog extends Dialog {
    private Context mContext;

    public BottomDialog(@NonNull Context context) {
        super(context);

        mContext = context;
    }


//    public BottomDialog(@NonNull Context context, int themeResId) {
//        super(context, themeResId);
//
//        mContext = context;
//        mView = LayoutInflater.from(mContext).inflate(themeResId, null);
//    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Window window = this.getWindow();
//        window.setGravity(Gravity.BOTTOM);
//        WindowManager.LayoutParams params = window.getAttributes();
//        params.width = WindowManager.LayoutParams.MATCH_PARENT;
//        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        window.setAttributes(params);
//        window.setWindowAnimations(R.style.BottomDialog);

        setCanceledOnTouchOutside(true);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        View decorView = window.getDecorView();
        decorView.setPadding(0, 0, 0, 0);
        // 设置 dialog 的宽和高
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // 设置 dialog 的位置
        layoutParams.gravity = Gravity.BOTTOM;

        //23会有bug，必须加上这一句!
        window.setBackgroundDrawable(null);
        window.setWindowAnimations(R.style.BottomDialog);
        window.setAttributes(layoutParams);
    }
}

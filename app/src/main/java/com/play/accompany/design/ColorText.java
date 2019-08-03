package com.play.accompany.design;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.play.accompany.R;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

public class ColorText extends android.support.v7.widget.AppCompatTextView {
    private Context mContext;

    public ColorText(Context context) {
        this(context,null);
    }

    public ColorText(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ColorText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        mContext = context;
    }


    public void setColor(String textColor, String backgroundColor, String word) {
        if (TextUtils.isEmpty(textColor) || TextUtils.isEmpty(backgroundColor)) {
            return;
        }
        int padding = QMUIDisplayHelper.dp2px(mContext, 8);
        setPadding(padding, 0, padding, 0);
        GradientDrawable gradientDrawable = (GradientDrawable) mContext.getResources().getDrawable(R.drawable.colorful_frame);
        gradientDrawable.setStroke(QMUIDisplayHelper.dp2px(mContext, 1), Color.parseColor(textColor));
        gradientDrawable.setColor(Color.parseColor(backgroundColor));
        setTextColor(Color.parseColor(textColor));
        setText(word);
        setBackground(gradientDrawable);

    }
}

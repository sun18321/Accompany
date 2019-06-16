package com.play.accompany.design;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.play.accompany.R;
import com.play.accompany.utils.LogUtils;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

public class ColorfulTitle extends FrameLayout {

    private View mViewLine;
    private TextView mTvTitle;
    private Context mContext;

    public ColorfulTitle(@NonNull Context context) {
        this(context, null);
    }

    public ColorfulTitle(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorfulTitle(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        mContext = context;
        View view = View.inflate(context, R.layout.colorful_title, this);
        mViewLine = view.findViewById(R.id.view_line);
        mTvTitle = view.findViewById(R.id.tv_title);
    }

    public void setColor(String lineColor, String solidColor,String word) {
        LogUtils.d("color", "line:" + lineColor + "solid:" + solidColor + "word:" + word);
        if (TextUtils.isEmpty(lineColor) || TextUtils.isEmpty(solidColor) || TextUtils.isEmpty(word)) {
            return;
        }
        int intLineColor = Color.parseColor(lineColor);
        int intSolidColor = Color.parseColor(solidColor);
        GradientDrawable gradientDrawable = (GradientDrawable) mTvTitle.getBackground();
        gradientDrawable.setStroke(QMUIDisplayHelper.dp2px(mContext, 1), intLineColor);
        gradientDrawable.setColor(intSolidColor);
        mViewLine.setBackgroundColor(intLineColor);
        mTvTitle.setTextColor(intLineColor);
        mTvTitle.setText(word);
    }
}

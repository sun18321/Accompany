package com.play.accompany.design;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.play.accompany.R;

public class DetailLayout extends FrameLayout {
    private TextView mTvTitle;
    private TextView mTvDetail;
    private RelativeLayout mRelativeLayout;
    private ImageView mImgArrow;

    public DetailLayout(@NonNull Context context) {
        this(context, null);
    }

    public DetailLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DetailLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs) {
        View view = View.inflate(context, R.layout.design_detail_layout, this);
        mTvTitle = view.findViewById(R.id.tv_title);
        mTvDetail = view.findViewById(R.id.tv_detail);
        mRelativeLayout = view.findViewById(R.id.rl_top);
        mImgArrow = findViewById(R.id.img);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DetailLayout);
        String title = typedArray.getString(R.styleable.DetailLayout_title);
        mTvTitle.setText(title);
        String detail = typedArray.getString(R.styleable.DetailLayout_detail);
        mTvDetail.setText(detail);
        typedArray.recycle();
    }

    public void setOnClickListener(final OnClickListener listener) {
        mRelativeLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(DetailLayout.this);
                }
            }
        });
    }

    public void setArrowInvisiable() {
        if (mImgArrow != null) {
            mImgArrow.setVisibility(INVISIBLE);
        }
    }

    public void setTvDetail(String detail) {
        mTvDetail.setText(detail);
    }
}

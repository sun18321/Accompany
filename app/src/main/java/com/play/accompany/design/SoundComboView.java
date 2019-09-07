package com.play.accompany.design;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.play.accompany.R;
import com.play.accompany.utils.SoundManager;
import com.play.accompany.utils.ToastUtils;

import java.io.File;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class SoundComboView extends FrameLayout {

    private TextView mTvTime;
    private AnimationDrawable mSoundDrawable;
    private String mUrl;
    private boolean mPlay = false;
    private File mFile = null;

    public SoundComboView(@NonNull Context context) {
        this(context,null);
    }

    public SoundComboView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SoundComboView(@NonNull Context context,  @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.sound_combo, this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SoundSize);
        float imageSize = typedArray.getDimension(R.styleable.SoundSize_image_size, 30);
        float textSize = typedArray.getDimension(R.styleable.SoundSize_text_size, 14);
        typedArray.recycle();

        ImageView imgSound = findViewById(R.id.img_sound);
        mTvTime = findViewById(R.id.tv_time);
        mSoundDrawable = (AnimationDrawable) imgSound.getBackground();

        mTvTime.setTextSize(textSize);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imgSound.getLayoutParams();
        params.height = (int) imageSize;
        params.width = (int) imageSize;
        imgSound.setLayoutParams(params);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlay) {
                    stopPlay();
                } else {
                    startAudio();
                }
            }
        });
    }

    public void setData(String url, int length) {
        String time = length + "″";
        mTvTime.setText(time);
        mUrl = url;
    }

    public void setData(File file, int length) {
        mFile = file;
        String time = length + "″";
        mTvTime.setText(time);
        mUrl = null;
    }

    public void stopPlay() {
        SoundManager.Companion.getInstance().stopPlay();
        stopAudio();
    }

    public void startAudio() {
        if (TextUtils.isEmpty(mUrl) && mFile == null) {
            ToastUtils.showCommonToast("数据错误,请稍后重试");
            return;
        }
        mPlay = true;
        if (mUrl == null) {
            SoundManager.Companion.getInstance().playAudio(mFile, new Function0<Unit>() {
                @Override
                public Unit invoke() {
                    stopAudio();
                    return null;
                }
            });
        } else {
            SoundManager.Companion.getInstance().playAudio(mUrl, new Function0<Unit>() {
                @Override
                public Unit invoke() {
                    stopAudio();
                    return null;
                }
            });

        }
        mSoundDrawable.start();
    }

    public void destroy() {
        SoundManager.Companion.getInstance().destroy();
    }

    private void stopAudio() {
        mSoundDrawable.stop();
        mSoundDrawable.selectDrawable(0);
        mPlay = false;
    }
}

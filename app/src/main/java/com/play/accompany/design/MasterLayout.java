package com.play.accompany.design;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class MasterLayout extends FrameLayout {
    public MasterLayout( @NonNull Context context) {
        this(context, null);
    }

    public MasterLayout( @NonNull Context context,  AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MasterLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {

    }
}

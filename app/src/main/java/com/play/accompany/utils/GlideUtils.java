package com.play.accompany.utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class GlideUtils {

    public static void commonLoad(Context context, String url, ImageView imageView) {
        Glide.with(context).load(url).into(imageView);
    }
}

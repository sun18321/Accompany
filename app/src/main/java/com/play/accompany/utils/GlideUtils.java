package com.play.accompany.utils;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class GlideUtils {

    public static void commonLoad(Context context, String url, ImageView imageView) {
        Glide.with(context).load(url).into(imageView);
    }

    public static void commonLoad(Context context, Uri url, ImageView imageView) {
        Glide.with(context).load(url).into(imageView);
    }

    public static void loadFuzzy(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(50, 5)))
                .into(imageView);
    }
}

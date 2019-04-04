package com.play.accompany.design;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.play.accompany.constant.OtherConstant;
import com.youth.banner.loader.ImageLoader;

public class BannerImageLoader extends ImageLoader {


    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        Glide.with(context).load(path).into(imageView);
    }

    @Override
    public ImageView createImageView(Context context) {
        RoundedImageView roundedImageView = new RoundedImageView(context);
        roundedImageView.setCornerRadius(OtherConstant.BANNER_CORNER);
        return roundedImageView;
    }
}

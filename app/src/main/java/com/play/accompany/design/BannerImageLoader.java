package com.play.accompany.design;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.play.accompany.R;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.view.AccompanyApplication;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.youth.banner.loader.ImageLoader;

public class BannerImageLoader extends ImageLoader {


    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        Glide.with(context).load(path).into(imageView);
    }

    @Override
    public ImageView createImageView(Context context) {
        RoundedImageView roundedImageView = new RoundedImageView(context);
        float corner = AccompanyApplication.getContext().getResources().getDimension(R.dimen.dp_5);
        int px = QMUIDisplayHelper.dp2px(AccompanyApplication.getContext(), (int) corner);
        LogUtils.d("corner", "corner:" + corner + "----px:" + px);
        roundedImageView.setCornerRadius(corner);
        return roundedImageView;
    }
}

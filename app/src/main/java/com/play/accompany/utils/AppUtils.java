package com.play.accompany.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.play.accompany.R;
import com.play.accompany.view.AccompanyApplication;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.regex.PatternSyntaxException;

public class AppUtils {
    /**
     * 验证手机号码是否合法
     * 176, 177, 178;
     * 180, 181, 182, 183, 184, 185, 186, 187, 188, 189;
     * 145, 147;
     * 130, 131, 132, 133, 134, 135, 136, 137, 138, 139;
     * 150, 151, 152, 153, 155, 156, 157, 158, 159;
     *
     * "13"代表前两位为数字13,
     * "[0-9]"代表第二位可以为0-9中的一个,
     * "[^4]" 代表除了4
     * "\\d{8}"代表后面是可以是0～9的数字, 有8位。
     */
    public static boolean isMobileNumber(String mobiles) throws PatternSyntaxException {
        String telRegex = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(147,145))\\d{8}$";
        return !TextUtils.isEmpty(mobiles) && mobiles.matches(telRegex);
    }

    /**
     * 返回当前程序版本号
     */
    public static String getAppVersionCode() {
        int versioncode = 0;
        try {
            PackageManager pm = AccompanyApplication.getContext().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(AccompanyApplication.getContext().getPackageName(), 0);
            // versionName = pi.versionName;
            versioncode = pi.versionCode;
        } catch (Exception e) {
        }
        return versioncode + "";
    }

    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName() {
        String versionName=null;
        try {
            PackageManager pm = AccompanyApplication.getContext().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(AccompanyApplication.getContext().getPackageName(), 0);
            versionName = pi.versionName;
        } catch (Exception e) {
        }
        return versionName;
    }

    public static void goCrop(Uri imgUri, AppCompatActivity activity) {
        UCrop.Options options = new UCrop.Options();
        options.setToolbarTitle(AccompanyApplication.getContext().getResources().getString(R.string.crop_image));
        options.setToolbarColor(ActivityCompat.getColor(AccompanyApplication.getContext(), R.color.colorPrimary));
        options.setStatusBarColor(ActivityCompat.getColor(AccompanyApplication.getContext(), R.color.colorPrimary));
//        options.setHideBottomControls(true);
        File outDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        File outFile = new File(outDir, System.currentTimeMillis() + ".png");
        Uri destinationUri = Uri.fromFile(outFile);
        UCrop.of(imgUri,destinationUri).withOptions(options).withAspectRatio(1,1).start(activity);
    }
}

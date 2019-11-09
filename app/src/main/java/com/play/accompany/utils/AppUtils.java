package com.play.accompany.utils;

import android.app.ActivityManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.play.accompany.R;
import com.play.accompany.bean.FilterAudioBean;
import com.play.accompany.bean.VersionBean;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.present.FilterOverListener;
import com.play.accompany.present.FilterProgressListerner;
import com.play.accompany.view.AccompanyApplication;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class AppUtils {
    private static long mCurrentTime = 0;
    private static long mTimeSpace = 300;

    /**
     * 验证手机号码是否合法
     * 176, 177, 178;
     * 180, 181, 182, 183, 184, 185, 186, 187, 188, 189;
     * 145, 147;
     * 130, 131, 132, 133, 134, 135, 136, 137, 138, 139;
     * 150, 151, 152, 153, 155, 156, 157, 158, 159;
     * <p>
     * "13"代表前两位为数字13,
     * "[0-9]"代表第二位可以为0-9中的一个,
     * "[^4]" 代表除了4
     * "\\d{8}"代表后面是可以是0～9的数字, 有8位。
     */
    public static boolean isMobileNumber(String mobiles) throws PatternSyntaxException {
        String telRegex = "^\\d{11}$";
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
        String versionName = null;
        try {
            PackageManager pm = AccompanyApplication.getContext().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(AccompanyApplication.getContext().getPackageName(), 0);
            versionName = pi.versionName;
        } catch (Exception e) {
        }
        return versionName;
    }

    public static boolean isSinglePhone(String s) {
        if (TextUtils.isEmpty(s)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        boolean length = s.length() == 11;
        return pattern.matcher(s).matches() && length;
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
        UCrop.of(imgUri, destinationUri).withOptions(options).withAspectRatio(1, 1).start(activity);
    }

    public static boolean isIdentity(String code) {
        int IDENTITYCODE_OLD = 15;
        int IDENTITYCODE_NEW = 18;
        if (TextUtils.isEmpty(code)) {
            return false;
        }

        String birthDay = "";
        code = code.trim().toUpperCase();

        // 长度只有15和18两种情况
        if ((code.length() != IDENTITYCODE_OLD) && (code.length() != IDENTITYCODE_NEW)) {
            return false;
        }

        // 身份证号码必须为数字(18位的新身份证最后一位可以是x)
        Pattern pt = Pattern.compile("(^\\d{15}$)|(\\d{17}(?:\\d|x|X)$)");
        Matcher mt = pt.matcher(code);
        if (!mt.find()) {
            return false;
        }
        return true;
    }


    /**
     * 判断程序是否在前台运行（当前运行的程序）
     */
    public boolean isRunForeground() {
        ActivityManager activityManager = (ActivityManager) AccompanyApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return false;
        }
        String packageName = AccompanyApplication.getContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;// 程序运行在前台
            }
        }
        return false;
    }

    public static int shouldUpdate(VersionBean bean) {
        String name = getAppVersionName();
        String[] versionName = name.split("\\.");

        LogUtils.d("update", "name:" + name + "length:" + versionName.length);
        if (versionName.length != 3) {
            return OtherConstant.MUST_UPDATE;
        }
        int first = Integer.parseInt(versionName[0]);
        int second = Integer.parseInt(versionName[1]);
        int third = Integer.parseInt(versionName[2]);

        if (first < bean.getMajor()) {
            return OtherConstant.MUST_UPDATE;
        }
        if (second < bean.getMinor()) {
            return OtherConstant.MUST_UPDATE;
        }
        if (third < bean.getPatch()) {
            return OtherConstant.COMMON_UPDATE;
        }
        return OtherConstant.NOT_UPDATE;
    }

    public static int getStatusBarHeight() {
        Resources resources = AccompanyApplication.getContext().getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    public static String getImagePath(Uri uri) {
        String imagePath = null;
        if (uri == null) {
            ToastUtils.showCommonToast(AccompanyApplication.getContext().getResources().getString(R.string.data_error));
            return null;
        }
        if (DocumentsContract.isDocumentUri(AccompanyApplication.getContext(),uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        return imagePath;
    }

    private static String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = AccompanyApplication.getContext().getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private static HashSet<FilterAudioBean> mList = new HashSet<>();
    private static List<String> mAllFormat = Arrays.asList(AccompanyApplication.getContext().getResources().getStringArray(R.array.audio_format));

    public static void getAllAudio(File filePath, FilterProgressListerner progressListener, FilterOverListener overListener) {
        File[] files = filePath.listFiles();

        for (File file : files) {
            if (file.isDirectory()) {
                getAllAudio(file, progressListener,null);
            } else {
                String fileName = file.getName();
                progressListener.onProgress(fileName);
                if (!TextUtils.isEmpty(fileName)) {
                    int index = fileName.lastIndexOf(".");
                    if (index != -1) {
                        String format = fileName.substring(index);
                        if (mAllFormat.contains(format)) {
                            String path = file.getAbsolutePath();
                            long sizeLong = file.length();
                            String sizeString = FormatFileSize(sizeLong);
                            FilterAudioBean bean = new FilterAudioBean(fileName, path, sizeLong, sizeString, 0,"",false);
                            mList.add(bean);
                        }
                    }
                }
            }
        }
        if (overListener != null) {
            overListener.onOver(mList);
        }
    }

    public static String FormatFileSize(long fileS) {// 转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024)
        {
            fileSizeString = df.format((double) fileS) + "B";
        }
        else if (fileS < 1048576)
        {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        }
        else if (fileS < 1073741824)
        {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        }
        else
        {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    public static String timeParse(long duration) {
        String time = "" ;
        long minute = duration / 60000 ;
        long seconds = duration % 60000 ;
        long second = Math.round((float)seconds/1000) ;
        if( minute < 10 ){
            time += "0" ;
        }
        time += minute+":" ;
        if( second < 10 ){
            time += "0" ;
        }
        time += second ;
        return time ;
    }

    public static boolean isQuickCLick() {
        long currentTime = System.currentTimeMillis();
        long timeSpace = currentTime - mCurrentTime;
        mCurrentTime = currentTime;
        if (timeSpace > mTimeSpace) {
            return false;
        }
        return true;
    }
}

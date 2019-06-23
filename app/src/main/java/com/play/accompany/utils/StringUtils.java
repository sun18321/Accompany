package com.play.accompany.utils;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Toast;

import com.play.accompany.R;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.view.AccompanyApplication;

import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Provider;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StringUtils {
    /**
     * id-type
     * 1000-王者荣耀   1001-绝地求生  1002-英雄联盟  1003-刀塔2  1004-第五人格  1005-刺激战场  1006-守望先锋 9999-其他
     *
     *
     */
    public static String getGameString(List<Integer> list) {
        if (list.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i =0;i<list.size();i++) {
            String s = integer2String(list.get(i));
            if (i == 2) {
                sb.append(AccompanyApplication.getContext().getResources().getString(R.string.vertical));
                sb.append("...");
                break;
            }
            if (sb.length() > 0) {
                sb.append(AccompanyApplication.getContext().getResources().getString(R.string.vertical));
            }
                sb.append(s);
        }
        return sb.toString();
    }

    public static List<String> getGameList(List<Integer> list) {
        List<String> mList = new ArrayList<>();
        if (list.isEmpty()) {
            return null;
        }
        for (Integer integer : list) {
            String s = integer2String(integer);
            mList.add(s);
        }
        return mList;
    }

    public static String getGameString(int i) {
        return integer2String(i);
    }

    public static String GameList2String(List<String> list) {
        if (list.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            if (i == 2) {
                sb.append(AccompanyApplication.getContext().getResources().getString(R.string.vertical));
                sb.append("...");
                break;
            }
            if (sb.length() > 0) {
                sb.append(AccompanyApplication.getContext().getResources().getString(R.string.vertical));
            }
            sb.append(s);
        }
        return sb.toString();
    }

    private static String integer2String(int i) {
        String s;
        switch (i) {
            case 1000:
                s = "王者荣耀";
                break;
            case 1001:
                s = "绝地求生";
                break;
            case 1002:
                s = "英雄联盟";
                break;
            case 1003:
                s = "刀塔2";
                break;
            case 1004:
                s = "第五人格";
                break;
            case 1005:
                s = "刺激战场";
                break;
            case 1006:
                s = "守望先锋";
                break;
            case 9999:
                s = "其他";
                break;
            default:
                s = "";
        }
        return s;
    }

    public static String m2Km(double instance, String city) {
        if (instance == 0) {
            return "<0.1km";
        } else if (instance > 100 || instance < 0) {
            return cutCityWord(city);
        }
        return instance + "km";
    }

    /**
     * 101: sign错误
     * <p>
     * 401: 用户重复
     * 402: 用户不存在
     * 403: 密码错误
     * 404: token错误
     * 405: 主播玩家不能下订单
     * 406: 金币不够
     * 407: 订单重复
     * 408: 订单不存在
     * 409：订单开始时间不对
     * 410: 验证码错误
     * 411: 验证码过期
     * 412: 验证码发送过于频繁
     * 413: 邀请码无效
     * 414: 邀请码 已经使用
     * 415: 订单状态 不对
     * 416: 订单完成的过早
     * 417: 无法取消 关注
     * 418: 图片URL过期
     * 419：头像过大
     * 420：提现金额过小
     * 421：提现金额过大
     *
     * 600：融云用户注册 无效
     * 601：融云用户更新 无效
     * 700: 微信登录错误
     * <p>
     * 998：非管理员权限，无法修改
     * 999: 服务器其他错误
     */

    public static String getErrorInfo(int errorCode) {
        String errorInfo;
        switch (errorCode) {
            case 401:
                errorInfo = "用户重复";
                break;
            case 402:
                errorInfo = "用户不存在";
                break;
            case 403:
                errorInfo = "密码错误";
                break;
            case 404:
                errorInfo = "登录过期";
                break;
            case 405:
                errorInfo = "主播玩家不能下单";
                break;
            case 406:
                errorInfo = "金币不够";
                break;
            case 407:
                errorInfo = "订单重复";
                break;
            case 408:
                errorInfo = "订单不存在";
                break;
            case 409:
                errorInfo = "订单开始时间不对";
                break;
            case 410:
                errorInfo = "验证码错误";
                break;
            case 411:
                errorInfo = "验证码过期";
                break;
            case 412:
                errorInfo = "验证码发送频繁";
                break;
            case 413:
                errorInfo = "无效邀请码";
                break;
            case 414:
                errorInfo = "已填写邀请码";
                break;
            case 415:
                errorInfo = "订单状态无效";
                break;
            case 416:
                errorInfo = "未到订单完成时间";
                break;
            case 417:
                errorInfo = "无法取消关注";
                break;
            case 419:
                errorInfo = "头像过大";
                break;
            case 420:
                errorInfo = "提现金额过小";
                break;
            case 421:
                errorInfo = "提现金额过大";
                break;
            case 422:
                errorInfo = "微信支付下单失败";
                break;
            case 423:
                errorInfo = "支付金额超过限制";
                break;
            case 424:
                errorInfo = "果币操作异常";
                break;
            case 425:
                errorInfo = "订单已经无法取消";
                break;
            case 426:
                errorInfo = "今天提现的次数已满";
                break;
            case 427:
                errorInfo = "订单无法申请提前完成";
                break;
            case 428:
                errorInfo = "未到提前结束时间";
                break;
            case 600:
                errorInfo = "聊天系统注册错误";
                break;
            case 601:
                errorInfo = "聊天系统更新错误";
                break;
            case 700:
                errorInfo = "微信登录错误";
                break;
            default:
                errorInfo = "未知错误";
        }
        return errorInfo;
    }

    /**
     * 订单状态 1：完成下单，未付款 3:已经付款 5：开始服务 7：服务完成 9：确认完成 未评价 11：评价完成
     *                                 退单状态 2:未付款 时间到自动取消 订单 4:完成下单 后退款 6：开始服务 后退款 8：服务完成 后退款
     *
     */

//    public static String getStateInfo(int state) {
//        String info;
//        switch (state) {
//            case 1:
//                info = "未支付";
//                break;
//            case 3:
//                info = "已支付";
//                break;
//            case 5:
//                info = "服务中";
//                break;
//            case 7:
//                info = "服务结束";
//                break;
//            case 1:
//                info = "未付款";
//                break;
//            case 1:
//                info = "未付款";
//                break;
//            case 1:
//                info = "未付款";
//                break;
//            case 1:
//                info = "未付款";
//                break;
//            case 1:
//                info = "未付款";
//                break;
//            case 1:
//                info = "未付款";
//                break;
//            case 1:
//                info = "未付款";
//                break;
//
//        }
//    }

    /**
     * 将图片转换成Base64编码的字符串
     */
    public static String imageToBase64(String path){
        if(TextUtils.isEmpty(path)){
            return null;
        }
        InputStream is = null;
        byte[] data = null;
        String result = null;
        try{
            is = new FileInputStream(path);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            result = Base64.encodeToString(data,Base64.NO_WRAP);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null !=is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }

    public static String imageChangeUpload(String image) {
        StringBuilder sb = new StringBuilder();
        String s = imageToBase64(image);
        sb.append(OtherConstant.IMAGE_HEAD).append(s);
        return sb.toString();
    }

    public static String moneyExchange(int money) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(money);
    }

    public static String moneyExchange(double money) {
        DecimalFormat df = new DecimalFormat("#,###.###");
        return df.format(money);
    }

    private final static int[] dayArr = new int[] { 20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22 };
    private final static String[] constellationArr = new String[] { "摩羯座", "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座" };

    public static String getConstellation(int month, int day) {
        return day < dayArr[month - 1] ? constellationArr[month - 1] : constellationArr[month];
    }

    public static String getConstellationByString(String birthday) {
        if (TextUtils.isEmpty(birthday)) {
            return "";
        }
        String date = dateFormat(birthday);
        String[] split = date.split("-");
        if (split.length > 2) {
           return getConstellation(Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        }
        return "";
    }

    private static String dateFormat(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = null;
        try {
            parse = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sdf.format(parse);
    }

    public static String cutCityWord(String city) {
        if (city == null || city.length() == 0) {
            return "";
        }
        if (city.contains("市") && TextUtils.equals("市", city.substring(city.length() - 1))) {
            return city.substring(0, city.length() - 1);
        } else {
            return city;
        }
    }

    public static String uri2Path(Uri uri) {
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
}

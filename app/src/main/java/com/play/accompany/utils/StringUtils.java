package com.play.accompany.utils;

import android.text.TextUtils;
import android.util.Base64;

import com.play.accompany.R;
import com.play.accompany.view.AccompanyApplication;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
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

    public static String m2Km(int m) {
        if (m == 0) {
            return "0km";
        }
        double km = (double) m / (double) 1000;
        return km + "km";
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

    public static String moneyExchange(int money) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(money);
    }

    private final static int[] dayArr = new int[] { 20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22 };
    private final static String[] constellationArr = new String[] { "摩羯座", "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座" };

    public static String getConstellation(int month, int day) {
        return day < dayArr[month - 1] ? constellationArr[month - 1] : constellationArr[month];
    }

    public static String getConstellationByString(String birthday) {
        String[] split = birthday.split("-");
        if (split.length > 2) {
           return getConstellation(Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        }
        return "";
    }
}

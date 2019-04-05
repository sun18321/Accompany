package com.play.accompany.constant;

import com.play.accompany.R;
import com.play.accompany.view.AccompanyApplication;

public class OrderConstant {

    public static final int NO_PAY = 1;
    public static final int PAY = 3;
    public static final int START_SERVICE = 5;
    public static final int SERVICE_COMPLETE = 7;
    public static final int CONFIRM_COMPLETE = 9;
    public static final int COMMENT_COMPLETE = 11;
    public static final int QUIT_NO_PAY = 2;
    public static final int QUIT_PAY = 4;
    public static final int QUIT_START_SERVICE = 6;
    public static final int QUIT_SERVICE_COMPLETE = 8;


    public static String getOrderState(int orderCode) {
        String orderState;
        switch (orderCode) {
            case NO_PAY:
            case QUIT_NO_PAY:
                orderState = AccompanyApplication.getContext().getResources().getString(R.string.order_no_pay);
                break;
            case PAY:
                orderState = AccompanyApplication.getContext().getResources().getString(R.string.order_pay_complete);
                break;
//            case
            default:
                orderState = AccompanyApplication.getContext().getResources().getString(R.string.order_error);
        }
        return orderState;
    }

}

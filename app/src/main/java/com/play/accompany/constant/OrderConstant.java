package com.play.accompany.constant;

import com.play.accompany.R;
import com.play.accompany.bean.AllOrderBean;
import com.play.accompany.bean.OrderState;
import com.play.accompany.view.AccompanyApplication;

public class OrderConstant {

    public static final int NO_PAY = 1;
    public static final int PAY = 3;
    public static final int ACCEPT_ORDER = 5;
    public static final int START_SERVICE = 7;
    public static final int SERVICE_COMPLETE = 9;
    public static final int COMMENT_COMPLETE = 11;
    public static final int QUIT_NO_PAY = 2;
    public static final int MASTER_NO_ACCEPT = 4;
    public static final int QUIT_START_SERVICE = 6;
    public static final int QUIT_SERVICE_COMPLETE = 8;

    public static final int CLICK_JUMP_PAY = 1001;
    public static final int CLICK_JUMP_WAIT = 1002;
    public static final int CLICK_JUMP_SERVICE = 1003;
    public static final int CLICK_JUMP_COMMENT = 1004;
    public static final int CLICK_JUMP_COMPLETE = 1005;
    public static final int CLICK_JUMP_ACCEPT = 1006;
    public static final int CLICK_JUMP_SUBMIT = 1007;
    public static final int CLICK_JUMP_ERROR = 9999;


    public static OrderState getOrderState(AllOrderBean bean, int orderCode, boolean isHost) {
        OrderState orderState = new OrderState();
        switch (orderCode) {
            case NO_PAY:
                orderState.setStateText(AccompanyApplication.getContext().getResources().getString(R.string.order_state_no_pay));
                orderState.setStateBackground(R.drawable.green_background);
                orderState.setStateAction(CLICK_JUMP_PAY);
                orderState.setTip(AccompanyApplication.getContext().getResources().getString(R.string.order_tip_pay));
                break;
            case PAY:
                if (isHost) {
                    orderState.setStateText(AccompanyApplication.getContext().getResources().getString(R.string.order_state_start_click));
                    orderState.setStateBackground(R.drawable.blue_background);
                    orderState.setStateAction(CLICK_JUMP_ACCEPT);
                    orderState.setTip(AccompanyApplication.getContext().getResources().getString(R.string.order_tip_wait));
                } else {
                    orderState.setStateText(AccompanyApplication.getContext().getResources().getString(R.string.order_state_pay));
                    orderState.setStateBackground(R.drawable.green_background);
                    orderState.setStateAction(CLICK_JUMP_WAIT);
                    orderState.setTip(AccompanyApplication.getContext().getResources().getString(R.string.order_tip_wait));
                }

                break;
            case ACCEPT_ORDER:
                orderState.setStateText(AccompanyApplication.getContext().getResources().getString(R.string.order_state_click));
                orderState.setStateBackground(R.drawable.green_background);

                ////////
//                long currentTime = System.currentTimeMillis();
//                long endTime = startTime + OtherConstant.SUBMIT_SPACE_TIME;
//                if (isHost) {
//                    if (currentTime > endTime) {
//                        orderState.setStateText(AccompanyApplication.getContext().getResources().getString(R.string.order_state_click_complete));
//                        orderState.setStateBackground(R.drawable.blue_background);
//                        orderState.setStateAction(CLICK_JUMP_SUBMIT);
//                    } else if (currentTime > startTime) {
//                        orderState.setStateText(AccompanyApplication.getContext().getResources().getString(R.string.order_state_service));
//                        orderState.setStateBackground(R.drawable.green_background);
//                    } else {
//                        orderState.setStateText(AccompanyApplication.getContext().getResources().getString(R.string.order_state_click));
//                        orderState.setStateBackground(R.drawable.green_background);
//                    }
//
//                }else {
//                    orderState.setStateText(AccompanyApplication.getContext().getResources().getString(R.string.order_state_service));
//                    orderState.setStateBackground(R.drawable.green_background);
//                    orderState.setStateAction(CLICK_JUMP_SERVICE);
//                }
                break;
            case QUIT_NO_PAY:
                orderState.setStateText(AccompanyApplication.getContext().getResources().getString(R.string.order_state_cancel));
                orderState.setStateBackground(R.drawable.gray_background);
                orderState.setTip(AccompanyApplication.getContext().getResources().getString(R.string.order_tip_no_pay));
                break;
            case MASTER_NO_ACCEPT:
                orderState.setStateText(AccompanyApplication.getContext().getResources().getString(R.string.order_state_cancel));
                orderState.setStateBackground(R.drawable.gray_background);
                orderState.setTip(AccompanyApplication.getContext().getResources().getString(R.string.order_tip_no_wait));
                break;
            case START_SERVICE:
                orderState.setStateText(AccompanyApplication.getContext().getResources().getString(R.string.order_state_service));
                orderState.setStateBackground(R.drawable.green_background);
                break;
            case SERVICE_COMPLETE:
                if (isHost) {
                    orderState.setStateText(AccompanyApplication.getContext().getResources().getString(R.string.order_state_complete));
                    orderState.setTip(AccompanyApplication.getContext().getString(R.string.order_tip_to_comment));
                    orderState.setStateBackground(R.drawable.gray_background);
                } else {
                    orderState.setStateText(AccompanyApplication.getContext().getResources().getString(R.string.order_state_comment));
                    orderState.setStateBackground(R.drawable.blue_background);
                    orderState.setStateAction(CLICK_JUMP_COMMENT);
                }
                break;
            case COMMENT_COMPLETE:
                if (isHost) {
                    orderState.setTip(AccompanyApplication.getContext().getString(R.string.order_tip_comment_finish));
                }
                orderState.setStateText(AccompanyApplication.getContext().getResources().getString(R.string.order_state_complete));
                orderState.setStateBackground(R.drawable.gray_background);
                orderState.setStateAction(CLICK_JUMP_COMPLETE);
                break;
            case QUIT_START_SERVICE:
            case QUIT_SERVICE_COMPLETE:

                break;
            default:
                orderState.setStateText(AccompanyApplication.getContext().getResources().getString(R.string.order_state_error));
                orderState.setStateBackground(R.drawable.gray_background);
                orderState.setStateAction(CLICK_JUMP_ERROR);
        }
        int all = bean.getNum() * bean.getPrice();
        if (orderCode == MASTER_NO_ACCEPT) {
            orderState.setSpend("0");
        } else {
            if (isHost) {
                orderState.setSpend("+" + all);
            } else {
                orderState.setSpend("-" + all);
            }
        }
        return orderState;
    }

}
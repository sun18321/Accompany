package com.play.accompany.constant;

import com.play.accompany.R;
import com.play.accompany.bean.OrderState;
import com.play.accompany.view.AccompanyApplication;

public class OrderConstant {

    private static final int NO_PAY = 1;
    private static final int PAY = 3;
    private static final int START_SERVICE = 5;
    private static final int SERVICE_COMPLETE = 7;
    private static final int CONFIRM_COMPLETE = 9;
    private static final int COMMENT_COMPLETE = 11;
    private static final int QUIT_NO_PAY = 2;
    private static final int QUIT_PAY = 4;
    private static final int QUIT_START_SERVICE = 6;
    private static final int QUIT_SERVICE_COMPLETE = 8;

    public static final int CLICK_JUMP_PAY = 1001;
    public static final int CLICK_JUMP_WAIT = 1002;
    public static final int CLICK_JUMP_SERVICE = 1003;
    public static final int CLICK_JUMP_COMMENT = 1004;
    public static final int CLICK_JUMP_COMPLETE = 1005;
    public static final int CLICK_JUMP_ACCEPT = 1006;
    public static final int CLICK_JUMP_SUBMIT = 1007;
    public static final int CLICK_JUMP_ERROR = 9999;


    public static OrderState getOrderState(long startTime, int orderCode, boolean isHost) {
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
            case START_SERVICE:
                long currentTime = System.currentTimeMillis();
                long endTime = startTime + OtherConstant.SUBMIT_SPACE_TIME;
                if (isHost) {
                    if (currentTime > endTime) {
                        orderState.setStateText(AccompanyApplication.getContext().getResources().getString(R.string.order_state_click_complete));
                        orderState.setStateBackground(R.drawable.blue_background);
                        orderState.setStateAction(CLICK_JUMP_SUBMIT);
                    } else if (currentTime > startTime) {
                        orderState.setStateText(AccompanyApplication.getContext().getResources().getString(R.string.order_state_service));
                        orderState.setStateBackground(R.drawable.green_background);
                    } else {
                        orderState.setStateText(AccompanyApplication.getContext().getResources().getString(R.string.order_state_click));
                        orderState.setStateBackground(R.drawable.green_background);
                    }

                }else {
                    orderState.setStateText(AccompanyApplication.getContext().getResources().getString(R.string.order_state_service));
                    orderState.setStateBackground(R.drawable.green_background);
                    orderState.setStateAction(CLICK_JUMP_SERVICE);
                }
                break;
            case QUIT_NO_PAY:
                orderState.setStateText(AccompanyApplication.getContext().getResources().getString(R.string.order_state_cancel));
                orderState.setStateBackground(R.drawable.gray_background);
                orderState.setTip(AccompanyApplication.getContext().getResources().getString(R.string.order_tip_no_pay));
                break;
            case QUIT_PAY:
                orderState.setStateText(AccompanyApplication.getContext().getResources().getString(R.string.order_state_cancel));
                orderState.setStateBackground(R.drawable.gray_background);
                orderState.setTip(AccompanyApplication.getContext().getResources().getString(R.string.order_tip_no_wait));
                break;
            case COMMENT_COMPLETE:
            case QUIT_SERVICE_COMPLETE:
            case QUIT_START_SERVICE:
            case SERVICE_COMPLETE:
                orderState.setStateText(AccompanyApplication.getContext().getResources().getString(R.string.order_state_comment));
                if (!isHost) {
                    orderState.setStateBackground(R.drawable.blue_background);
                    orderState.setStateAction(CLICK_JUMP_COMMENT);
                } else {
                    orderState.setStateBackground(R.drawable.green_background);
                }
                break;
            case CONFIRM_COMPLETE:
                orderState.setStateText(AccompanyApplication.getContext().getResources().getString(R.string.order_state_complete));
                orderState.setStateBackground(R.drawable.gray_background);
                orderState.setStateAction(CLICK_JUMP_COMPLETE);
                break;
            default:
                orderState.setStateText(AccompanyApplication.getContext().getResources().getString(R.string.order_state_error));
                orderState.setStateBackground(R.drawable.gray_background);
                orderState.setStateAction(CLICK_JUMP_ERROR);
        }
        return orderState;
    }

}

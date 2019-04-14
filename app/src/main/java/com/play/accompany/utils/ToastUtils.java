package com.play.accompany.utils;

import android.view.Gravity;
import android.widget.Toast;

import com.play.accompany.R;
import com.play.accompany.view.AccompanyApplication;

public class ToastUtils {

    public static void showCommonToast(String message) {
        Toast.makeText(AccompanyApplication.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void showCenterToast(String message) {
        Toast toast = Toast.makeText(AccompanyApplication.getContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showErrorToast() {
        Toast.makeText(AccompanyApplication.getContext(), AccompanyApplication.getContext().getResources().getString(R.string.data_error), Toast.LENGTH_SHORT).show();
    }

}

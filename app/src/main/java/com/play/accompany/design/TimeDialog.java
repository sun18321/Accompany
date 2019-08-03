package com.play.accompany.design;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.play.accompany.R;
import com.play.accompany.utils.DateUtils;
import com.play.accompany.utils.LogUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimeDialog {

    public void showDialog(Context context, final TimeListener listener) {
        final BottomDialog dialog = new BottomDialog(context);
        String[] dateArray = context.getResources().getStringArray(R.array.date_array);
        String[] minuteArray = context.getResources().getStringArray(R.array.minute_array);
        final String[] hourArray = context.getResources().getStringArray(R.array.hour_array);
        int currentHour = DateUtils.getCurrentHour();
        final List<String> hourList = new ArrayList<>();
        for (int i = currentHour; i < 24; i++) {
            int index = i + 1;
            if (index < hourArray.length) {
                hourList.add(hourArray[index]);
            }
        }
        View view = LayoutInflater.from(context).inflate(R.layout.time_pick, null);
        final WheelView wheelDay = view.findViewById(R.id.wheel_day);
        final WheelView wheelHour = view.findViewById(R.id.wheel_hour);
        final WheelView wheelMinute = view.findViewById(R.id.wheel_minute);

        List<String> dayList = new ArrayList<>(Arrays.asList(dateArray));
        if (hourList.size() == 0) {
            dayList.remove(0);
            wheelDay.setItems(dayList);
            wheelHour.setItems(Arrays.asList(hourArray));
        } else {
            wheelDay.setItems(dayList);
            wheelHour.setItems(hourList);
        }
        wheelMinute.setItems(Arrays.asList(minuteArray));

        wheelDay.setOnWheelViewListener(new WheelView.OnWheelViewListener(){
            @Override
            public void onSelected(int selectedIndex, String item) {
                super.onSelected(selectedIndex, item);
                LogUtils.d("wheel", "index:" + selectedIndex);

                if (selectedIndex == 1 && TextUtils.equals("今天", item)) {
                    wheelHour.setItems(hourList);
                    LogUtils.d("wheel", "origin size:" + hourList.size() + " string:" + item);
                } else {
                    wheelHour.setItems(Arrays.asList(hourArray));
                    wheelHour.setSeletion(0);
                }
            }
        });

        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String day = wheelDay.getSeletedItem();
                String hour = wheelHour.getSeletedItem();
                String minute = wheelMinute.getSeletedItem();

                String stringTime = day + " " + hour + ":" + minute;
                long longTime = getLongTime(day, hour, minute);
                if (listener != null) {
                    listener.onTime(stringTime, longTime);
                }

                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }

    private long getLongTime(String day, String hour, String minute) {
        String dayTime;
        if (TextUtils.equals("今天", day)) {
            dayTime = DateUtils.getToday();
        } else if (TextUtils.equals("明天", day)) {
            dayTime = DateUtils.getTomorrow();
        } else {
            dayTime = DateUtils.getAfterTomoorrow();
        }
        String completeTime = dayTime + " " + hour + ":" + minute;
        try {
            return DateUtils.date2Time(completeTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return System.currentTimeMillis();
        }
    }


    public interface TimeListener{
        void onTime(String stringTime,long longTime);
    }
}

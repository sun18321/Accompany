package com.play.accompany.view;

import android.content.Intent;
import android.support.design.widget.BottomSheetDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;
import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.IntentPayInfo;
import com.play.accompany.bean.OrderBean;
import com.play.accompany.bean.ResponseCreateOrder;
import com.play.accompany.bean.UserInfo;
import com.play.accompany.constant.AppConstant;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.design.BottomDialog;
import com.play.accompany.design.WheelView;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.utils.DateUtils;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.utils.SPUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.RequestBody;

public class OrderActivity extends BaseActivity implements View.OnClickListener {

    private RoundedImageView mHeadView;
    private TextView mTvName;
    private TextView mTvPrice;
    private RelativeLayout mRlType;
    private TextView mTvType;
    private RelativeLayout mRlTime;
    private TextView mTvTime;
    private TextView mTvAmount;
    private EditText mEditMarks;
    private TextView mTvAll;
    private UserInfo mHome;
    private BottomDialog mTimeDialog;
    private int mCount = 1;
    private int mPrice;
    private BottomDialog mTypeDialog;
    private String[] mDateArray;
    private String[] mHourArray;
    private String[] mMinuteArray;
    private long mServiceTime = 0;
    private String mTargetId;
    private int mgameType = 0;
    private String mGameName = null;


    @Override
    protected int getLayout() {
        return R.layout.activity_order;
    }

    @Override
    protected String getTag() {
        return "OrderActivity";
    }

    @Override
    protected void initViews() {
        initToolbar(getResources().getString(R.string.confirm_order));
        mHeadView = findViewById(R.id.img_head);
        mTvName = findViewById(R.id.tv_name);
        mTvPrice = findViewById(R.id.tv_price);
        mRlType = findViewById(R.id.rl_type);
        mTvType = findViewById(R.id.tv_type);
        mRlTime = findViewById(R.id.rl_time);
        mTvTime = findViewById(R.id.tv_time);
        mTvAmount = findViewById(R.id.tv_amount);
        mEditMarks = findViewById(R.id.edit_marks);
        mTvAll = findViewById(R.id.tv_all);

        mRlTime.setOnClickListener(this);
        mRlType.setOnClickListener(this);
        findViewById(R.id.btn_submit).setOnClickListener(this);
        findViewById(R.id.img_reduce).setOnClickListener(this);
        findViewById(R.id.img_add).setOnClickListener(this);

        if (mHome != null) {
            setViews();
        } else {
            Toast.makeText(this, getResources().getString(R.string.data_error), Toast.LENGTH_SHORT).show();
            this.finish();
        }

        mDateArray = getResources().getStringArray(R.array.date_array);
        mHourArray = getResources().getStringArray(R.array.hour_array);
        mMinuteArray = getResources().getStringArray(R.array.minute_array);
    }

    private void setViews() {
        int price = mHome.getPrice();
        mPrice = price;
        mTargetId = mHome.getUserId();
        mTvName.setText(mHome.getName());
        mTvPrice.setText(price + getResources().getString(R.string.price));
        String url = mHome.getUrl();
        if (!TextUtils.isEmpty(url)) {
            Glide.with(this).load(url).into(mHeadView);
        }
        mTvAll.setText(price + "");
    }

    @Override
    protected void parseIntent() {
        super.parseIntent();
        Intent intent = getIntent();
        if (intent != null) {
            UserInfo bean = (UserInfo) intent.getSerializableExtra(IntentConstant.INTENT_USER);
            mHome = bean;
        }
        if (mHome == null) {
            Toast.makeText(this, getResources().getString(R.string.data_error), Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }

    private void showTimeDialog() {
        if (mTimeDialog == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.time_pick, null);
            final WheelView wheelDay = view.findViewById(R.id.wheel_day);
            final WheelView wheelHour = view.findViewById(R.id.wheel_hour);
            final WheelView wheelMinute = view.findViewById(R.id.wheel_minute);
            wheelDay.setItems(Arrays.asList(mDateArray));
            wheelHour.setItems(Arrays.asList(mHourArray));
            wheelMinute.setItems(Arrays.asList(mMinuteArray));

            view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTimeDialog.dismiss();
                }
            });

            view.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int dayIndex = wheelDay.getSeletedIndex();
                    int hourIndex = wheelHour.getSeletedIndex();
                    int minuteIndex = wheelMinute.getSeletedIndex();
                    mTvTime.setText(mDateArray[dayIndex] + " " + mHourArray[hourIndex] + ":" + mMinuteArray[minuteIndex]);
                    convertOrderTime(dayIndex, hourIndex, minuteIndex);
                    mTimeDialog.dismiss();
                }
            });
            mTimeDialog = new BottomDialog(this);
            mTimeDialog.setContentView(view);
        }
        mTimeDialog.show();
    }

    private void showTypeDialog() {
        if (mTypeDialog == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.type_pick, null);
            final WheelView wheelView = view.findViewById(R.id.wheel);
            TextView tvConfirm = view.findViewById(R.id.tv_confirm);
            final List<String> listGame = mHome.getGameTypeName();
            final List<Integer> gameType = mHome.getGameType();
            if (listGame.size() > 2) {
                wheelView.setOffset(1);
            } else {
                wheelView.setOffset(0);
            }
            wheelView.setItems(listGame);

            tvConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = wheelView.getSeletedIndex();
                    if (listGame.size() == 1 && index == 1) {
                        index = 0;
                    }
                    mGameName = listGame.get(index);
                    mTvType.setText(mGameName);
                    mgameType = gameType.get(index);
                    mTypeDialog.dismiss();
                }
            });

            view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTypeDialog.dismiss();
                }
            });
            mTypeDialog = new BottomDialog(this);
            mTypeDialog.setContentView(view);

        }
        mTypeDialog.show();
    }

    private void convertOrderTime(int dayIndex, int hourIndex, int minuteIndex) {
        String day;
        switch (dayIndex) {
            case 0:
                day = DateUtils.getToday();
                break;
            case 1:
                day = DateUtils.getTomorrow();
                break;
            case 2:
                day = DateUtils.getAfterTomoorrow();
                break;
            default:
                day = "";
        }
        String hour = mHourArray[hourIndex] + ":" + mMinuteArray[minuteIndex];
        String completeTime = day + " " + hour;
        try {
            mServiceTime = DateUtils.date2Time(completeTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void reduceCount() {
        if (mCount == 1) {
            Toast.makeText(this, getResources().getString(R.string.no_more_less), Toast.LENGTH_SHORT).show();
            return;
        } else {
            mCount--;
        }
        mTvAmount.setText(mCount + "");
        mTvAll.setText(mCount * mPrice + "");
    }

    private void addCount() {
        mCount++;
        mTvAmount.setText(mCount + "");
        mTvAll.setText(mCount * mPrice + "");
    }

    private void submitOrder() {
        if (mgameType == 0) {
            Toast.makeText(this, getResources().getString(R.string.skill_please), Toast.LENGTH_SHORT).show();
            return;
        }

        if (mServiceTime == 0) {
            Toast.makeText(this, getResources().getString(R.string.time_please), Toast.LENGTH_SHORT).show();
            return;
        }

        if (mServiceTime < System.currentTimeMillis()) {
            Toast.makeText(this, getResources().getString(R.string.time_to_early), Toast.LENGTH_SHORT).show();
            return;
        }
        showDialog();
        final OrderBean bean = new OrderBean();
        bean.setToken(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
        bean.setNum(mCount);
        bean.setPrice(mPrice);
        bean.setTargetId(mTargetId);
        String marks = mEditMarks.getText().toString();
        bean.setComment(marks);
        bean.setgameType(mgameType);
        bean.setStartTime(mServiceTime);
        String json = GsonUtils.toJson(bean);
        LogUtils.d(getTag(), "json:" + json);
        RequestBody body = EncodeUtils.encodeInBody(json);
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().createOrder(body), new TypeToken<BaseDecodeBean<List<ResponseCreateOrder>>>() {
        }.getType(), new NetListener<List<ResponseCreateOrder>>() {
            @Override
            public void onSuccess(List<ResponseCreateOrder> list) {
                dismissDialog();
                ResponseCreateOrder order = list.get(0);
                String orderId = order.getOrderId();
                String detail = mPrice + getResources().getString(R.string.price) + "*" + mCount;
                int all = mPrice * mCount;
                Intent intent = new Intent(OrderActivity.this, OrderPayActivity.class);
                IntentPayInfo info = new IntentPayInfo(mHome.getUrl(), mHome.getName(), mGameName, detail,
                        all, orderId);
                intent.putExtra(IntentConstant.INTENT_PAY_INFO, info);
                startActivity(intent);
                OrderActivity.this.finish();
            }

            @Override
            public void onFailed(int errCode) {
                dismissDialog();
            }

            @Override
            public void onError() {

            }

            @Override
            public void onComplete() {
                dismissDialog();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_time:
                showTimeDialog();
                break;
            case R.id.rl_type:
                showTypeDialog();
                break;
            case R.id.btn_submit:
                submitOrder();
                break;
            case R.id.img_reduce:
                reduceCount();
                break;
            case R.id.img_add:
                addCount();
                break;
        }
    }
}

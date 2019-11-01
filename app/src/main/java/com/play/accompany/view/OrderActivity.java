package com.play.accompany.view;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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
import com.play.accompany.bean.GameProperty;
import com.play.accompany.bean.IntentPayInfo;
import com.play.accompany.bean.OrderBean;
import com.play.accompany.bean.ResponseCreateOrder;
import com.play.accompany.bean.UserInfo;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.design.BottomDialog;
import com.play.accompany.design.TimeDialog;
import com.play.accompany.design.WheelView;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.utils.DateUtils;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.EventUtils;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.StringUtils;
import com.play.accompany.utils.ToastUtils;

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
    private UserInfo mUserInfo;
    private int mCount = 1;
    //单价
    private int mPrice;
    private BottomDialog mTypeDialog;
    private long mServiceTime = 0;
    private String mTargetId;
    private int mGameType = 0;
    private String mGameName = null;
    private TextView mTvWordCount;


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
        mTvWordCount = findViewById(R.id.tv_word_count);
        mEditMarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mTvWordCount.setText(s.length() + "/50");
            }
        });

        mRlTime.setOnClickListener(this);
        mRlType.setOnClickListener(this);
        findViewById(R.id.btn_submit).setOnClickListener(this);
        findViewById(R.id.img_reduce).setOnClickListener(this);
        findViewById(R.id.img_add).setOnClickListener(this);

        if (mUserInfo != null) {
            setViews();
        } else {
            Toast.makeText(this, getResources().getString(R.string.data_error), Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }

    private void setViews() {
        mTargetId = mUserInfo.getUserId();
        mTvName.setText(mUserInfo.getName());
        String url = mUserInfo.getUrl();
        if (!TextUtils.isEmpty(url)) {
            Glide.with(this).load(url).into(mHeadView);
        }
        mTvType.setText(mUserInfo.getGameType().get(mUserInfo.getSelectedPosition()).getName());
    }

    @Override
    protected void parseIntent() {
        super.parseIntent();
        Intent intent = getIntent();
        if (intent != null) {
            UserInfo bean = (UserInfo) intent.getSerializableExtra(IntentConstant.INTENT_USER);
            mUserInfo = bean;
        }
        if (mUserInfo == null) {
            Toast.makeText(this, getResources().getString(R.string.data_error), Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }

    private void showTimeDialog() {
            new TimeDialog().showDialog(this, new TimeDialog.TimeListener() {
                @Override
                public void onTime(String stringTime, long longTime) {
                    mTvTime.setText(stringTime);
                    mServiceTime = longTime;

                    LogUtils.d(getTag(), "time:" + mServiceTime);
                }
            });
    }

    private void showTypeDialog() {
        if (mTypeDialog == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.type_pick, null);
            final WheelView wheelView = view.findViewById(R.id.wheel);
            TextView tvConfirm = view.findViewById(R.id.tv_confirm);
            final List<GameProperty> gameType = mUserInfo.getGameType();
            final List<String> listGame = new ArrayList<>();
            for (GameProperty property : gameType) {
                listGame.add(property.getName());
            }

//            if (listGame.size() > 2) {
//                wheelView.setOffset(1);
//            } else {
//                wheelView.setOffset(0);
//            }

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
                    mGameType = gameType.get(index).getType();
                    mTypeDialog.dismiss();
                    mTvPrice.setText(mUserInfo.getGameType().get(index).toString());
                    mPrice = gameType.get(index).getPrice();
                    mTvAll.setText(mCount * mPrice + "");
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
        if (mGameType == 0) {
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
        String marks = mEditMarks.getText().toString();
        if (!TextUtils.isEmpty(marks) && marks.length() > 50) {
            ToastUtils.showCommonToast("备注过长");
            return;
        }

        showDialog();
        final OrderBean bean = new OrderBean();
        bean.setToken(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
        bean.setNum(mCount);
        bean.setPrice(mPrice);
        bean.setTargetId(mTargetId);
        bean.setComment(marks);
        bean.setgameType(mGameType);
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

                EventUtils.getInstance().upCreateOrder(DateUtils.time2Date(System.currentTimeMillis()), DateUtils.time2Date(mServiceTime), orderId, mTargetId, mGameName, String.valueOf(all), String.valueOf(mCount), mEditMarks.getText().toString());

                Intent intent = new Intent(OrderActivity.this, OrderPayActivity.class);
                IntentPayInfo info = new IntentPayInfo(mUserInfo.getUrl(), mUserInfo.getName(), mGameName, detail,
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

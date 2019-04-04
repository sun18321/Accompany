package com.play.accompany.view;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.bean.UserInfo;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.db.AccompanyDatabase;
import com.play.accompany.utils.DateUtils;
import com.play.accompany.utils.SPUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class UserCenterActivity extends BaseActivity implements View.OnClickListener {

    private boolean mIsMe;
    private ImageView mImgHead;
    private TextView mTvSign;
    private LinearLayout mLinGender;
    private ImageView mImgGender;
    private TextView mTvAge;
    private TextView mTvName;
    private Button mBtnAttention;
    private TextView mTvFans;
    private TextView mTvBottomName;
    private TextView mTvId;
    private TextView mTvInterest;
    private Button mBtnChat;
    private Button mBtnOrder;
    private RelativeLayout mRlEdit;
    private LinearLayout mLinOperation;
    private UserInfo mUserInfo;
    private Disposable mDisposable;

    @Override
    protected int getLayout() {
        return R.layout.activity_user_center;
    }

    @Override
    protected String getTag() {
        return "UserCenterActivity";
    }

    @Override
    protected void parseIntent() {
        super.parseIntent();
        Intent intent = getIntent();
        if (intent != null) {
            UserInfo bean = (UserInfo) intent.getSerializableExtra(IntentConstant.INTENT_USER);
            mUserInfo = bean;
            if (bean != null) {
                String id = bean.getUserId();
                String myId = SPUtils.getInstance().getString(SpConstant.MY_USER_ID);
                if (TextUtils.equals(id, myId)) {
                    mIsMe = true;
                } else {
                    mIsMe = false;
                }
            } else {
                mIsMe = true;
            }
        }
    }

    @Override
    protected void initViews() {
        mImgHead = findViewById(R.id.img_head);
        mTvSign = findViewById(R.id.tv_sign);
        mLinGender = findViewById(R.id.lin_gender);
        mImgGender = findViewById(R.id.img_gender);
        mTvAge = findViewById(R.id.tv_age);
        mTvName = findViewById(R.id.tv_name);
        mBtnAttention = findViewById(R.id.btn_attention);
        mTvFans = findViewById(R.id.tv_fans);
        mTvBottomName = findViewById(R.id.tv_bottom_name);
        mTvId = findViewById(R.id.tv_id);
        mTvInterest = findViewById(R.id.tv_interest);
        mBtnChat = findViewById(R.id.btn_chat);
        mBtnOrder = findViewById(R.id.btn_order);
        mRlEdit = findViewById(R.id.rl_edit);
        mLinOperation = findViewById(R.id.lin_operation);

        findViewById(R.id.rl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserCenterActivity.this.finish();
            }
        });

        if (mIsMe) {
            mBtnAttention.setVisibility(View.INVISIBLE);
            mLinOperation.setVisibility(View.INVISIBLE);
        } else {
            mRlEdit.setVisibility(View.INVISIBLE);
        }

        mBtnOrder.setOnClickListener(this);
        mBtnAttention.setOnClickListener(this);
        mBtnChat.setOnClickListener(this);
        mRlEdit.setOnClickListener(this);

        if (mUserInfo != null) {
            setViews();
        }
    }

    private void setViews() {
        mTvSign.setText(mUserInfo.getSign());
        int gender = mUserInfo.getGender();
        if (gender == OtherConstant.GENDER_FEMALE) {
            mLinGender.setBackgroundResource(R.drawable.female_bg);
            mImgGender.setImageResource(R.drawable.female);
        } else {
            mLinGender.setBackgroundResource(R.drawable.male_bg);
            mImgGender.setImageResource(R.drawable.male);
        }
        String date = mUserInfo.getDate();
        try {
            int age = DateUtils.getAge(date);
            mTvAge.setText("" + age);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mTvName.setText(mUserInfo.getName());
        mTvBottomName.setText(mUserInfo.getName());
        mTvId.setText(mUserInfo.getUserId());
        String url = mUserInfo.getUrl();
        if (!TextUtils.isEmpty(url)) {
            Glide.with(this).load(url).into(mImgHead);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean isEdit = SPUtils.getInstance().getBoolean(SpConstant.IS_USER_EDIT, false);
        if (mIsMe && isEdit) {
            updateData();
        }
    }

    private void updateData() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                mUserInfo = AccompanyDatabase.getInstance(UserCenterActivity.this).getUserDao().getUserInfo(SPUtils.getInstance().getString(SpConstant.MY_USER_ID));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                mDisposable = d;
            }

            @Override
            public void onNext(String s) {
                if (mUserInfo != null) {
                    setViews();
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_order:
                if (mUserInfo == null) {
                    Toast.makeText(this, getResources().getString(R.string.data_error), Toast.LENGTH_SHORT).show();
                    this.finish();
                } else {
                    Intent intent = new Intent(this, OrderActivity.class);
                    intent.putExtra(IntentConstant.INTENT_USER, mUserInfo);
                    startActivity(intent);
                }
                break;
            case R.id.btn_attention:
                break;
            case R.id.btn_chat:
                break;
            case R.id.rl_edit:
                Intent intent = new Intent(this, EditUserActivity.class);
                intent.putExtra(IntentConstant.INTENT_CODE, EditUserActivity.INTENT_EDIT);
                intent.putExtra(IntentConstant.INTENT_USER, mUserInfo);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }
}

package com.play.accompany.view;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.bean.AttentionBean;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.OnlyCodeBean;
import com.play.accompany.bean.UserInfo;
import com.play.accompany.constant.AppConstant;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.db.AccompanyDatabase;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.utils.DateUtils;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.StringUtils;
import com.play.accompany.utils.ToastUtils;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

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
    private LinearLayout mLinOperation;
    private UserInfo mUserInfo;
    private Disposable mDisposable;
    private LinearLayout mLinRate;
    private TextView mTvRate;
    private TextView mTvProfession;
    private boolean mAttention;
    private boolean mIsNet = false;
    private TextView mTvConstellation;

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
        mLinOperation = findViewById(R.id.lin_operation);
        mLinRate = findViewById(R.id.lin_rate);
        mTvRate = findViewById(R.id.tv_rating);
        mTvProfession = findViewById(R.id.tv_profession);
        mTvConstellation = findViewById(R.id.tv_constellation);

        findViewById(R.id.rl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserCenterActivity.this.finish();
            }
        });

        if (mIsMe) {
            mBtnAttention.setVisibility(View.INVISIBLE);
            mLinOperation.setVisibility(View.INVISIBLE);
            mLinRate.setVisibility(View.INVISIBLE);
        }

        mBtnOrder.setOnClickListener(this);
        mBtnAttention.setOnClickListener(this);
        mBtnChat.setOnClickListener(this);

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
        if (!mIsMe) {
            Double grade = mUserInfo.getGrade();
            mTvRate.setText("" + grade);
            Boolean attention = mUserInfo.getAttention();
            if (attention != null && attention) {
                mAttention = true;
                mBtnAttention.setText(getResources().getString(R.string.attention_already));
            } else {
                mAttention = false;
                mBtnAttention.setText(getResources().getString(R.string.attention));
            }
        }
        mTvInterest.setText(mUserInfo.getInterest());
        mTvProfession.setText(mUserInfo.getProfession());
        Integer favor = mUserInfo.getFavor();
        if (favor != null) {
            mTvFans.setText(favor + "");
        } else {
            mTvFans.setText("0");
        }
        LogUtils.d(getTag(), "date:" + mUserInfo.getDate());
//        mTvConstellation.setText(StringUtils.getConstellationByString(mUserInfo.getDate()));
    }

    @Override
    protected void onResume() {
        super.onResume();

//        boolean isEdit = SPUtils.getInstance().getBoolean(SpConstant.IS_USER_EDIT, false);
//        if (mIsMe && isEdit) {
//            updateData();
//        }
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

    private void dealAttention() {
        if (mAttention) {
            new QMUIDialog.MessageDialogBuilder(this).setTitle(getResources().getString(R.string.tips))
                    .setMessage(getResources().getString(R.string.tips_attention))
                    .addAction(getResources().getString(R.string.cancel), new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                        }
                    }).addAction(getResources().getString(R.string.confirm), new QMUIDialogAction.ActionListener() {
                @Override
                public void onClick(QMUIDialog dialog, int index) {
                    dialog.dismiss();
                    attention();
                }
            }).create().show();
        } else {
            attention();
        }
    }

    private void attention() {
        if (mIsNet) {
            return;
        }
        showDialog();
        mIsNet = true;
        AttentionBean bean = new AttentionBean();
        bean.setToken(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
        bean.setTargetId(mUserInfo.getUserId());
        if (mAttention) {
            bean.setFlag(OtherConstant.ATTEMTION_FLAG_CANCEL);
        } else {
            bean.setFlag(OtherConstant.ATTENTION_FLAG);
        }
        String json = GsonUtils.toJson(bean);
        RequestBody body = EncodeUtils.encodeInBody(json);
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().attention(body), new TypeToken<BaseDecodeBean>() {
                }.getType(), new NetListener() {
                    @Override
                    public void onSuccess(Object o) {
                        mIsNet = false;
                        dismissDialog();
                        if (mAttention) {
                            ToastUtils.showCommonToast(getResources().getString(R.string.attention_cancel_success));
                            mBtnAttention.setText(getResources().getString(R.string.attention));
                        } else {
                            ToastUtils.showCommonToast(getResources().getString(R.string.attention_success));
                            mBtnAttention.setText(getResources().getString(R.string.attention_already));
                        }
                        mAttention = !mAttention;
                        Intent intent = new Intent(AppConstant.BROADCAST_ATTENTION);
                        intent.putExtra(IntentConstant.INTENT_USER_ID, mUserInfo.getUserId());
                        sendBroadcast(intent);
                    }

                    @Override
                    public void onFailed(int errCode) {

                    }

                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onComplete() {
                        dismissDialog();
                        mIsNet = false;
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
                dealAttention();
                break;
            case R.id.btn_chat:
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

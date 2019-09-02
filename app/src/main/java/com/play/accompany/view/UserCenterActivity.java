package com.play.accompany.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.bean.AttentionBean;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.FindUserBean;
import com.play.accompany.bean.GameProperty;
import com.play.accompany.bean.TopGameBean;
import com.play.accompany.bean.UserInfo;
import com.play.accompany.constant.AppConstant;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.db.AccompanyDatabase;
import com.play.accompany.design.SoundComboView;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.utils.DateUtils;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.EventUtils;
import com.play.accompany.utils.FileSaveUtils;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.StringUtils;
import com.play.accompany.utils.ThreadPool;
import com.play.accompany.utils.ToastUtils;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.zhy.view.flowlayout.FlowLayout;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.rong.imkit.RongIM;
import okhttp3.RequestBody;

public class UserCenterActivity extends BaseActivity implements View.OnClickListener {

    private boolean mIsMe;
    private ImageView mImgHead;
    private TextView mTvSign;
    private LinearLayout mLinGender;
    private ImageView mImgGender;
    private TextView mTvAge;
    private TextView mTvName;
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
    private FlowLayout mFlowLayout;
    private LinearLayout mLinAttention;
    private TextView mTvAttention;
    private ImageView mImgAttention;
    private List<String> mAttentionList = new ArrayList<>();
    private LinearLayout mLinGame;
    private SoundComboView mSoundView;

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
        mLinAttention = findViewById(R.id.lin_attention);
        mImgAttention = findViewById(R.id.img_attention);
        mTvAttention = findViewById(R.id.tv_attention);
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
        mFlowLayout = findViewById(R.id.flowlayout);
        mLinGame = findViewById(R.id.lin_game);
        mSoundView = findViewById(R.id.sound_view);

        findViewById(R.id.rl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(OtherConstant.IS_ATTENTION, mAttention);
                setResult(RESULT_OK,intent);
                UserCenterActivity.this.finish();
            }
        });

        if (mIsMe) {
            mLinAttention.setVisibility(View.INVISIBLE);
            mLinOperation.setVisibility(View.INVISIBLE);
            mLinRate.setVisibility(View.INVISIBLE);
        } else {
            mBtnOrder.setOnClickListener(this);
            mLinAttention.setOnClickListener(this);
            mBtnChat.setOnClickListener(this);
        }
        if (mUserInfo.getFromChat()) {
            requestUserInfo();
        } else {
            setViews();
        }
    }

    private void setViews(){
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
        mTvId.setText(mUserInfo.getUserName());
        String url = mUserInfo.getUrl();
        if (!TextUtils.isEmpty(url)) {
            Glide.with(this).load(url).into(mImgHead);
        }
        if (!mIsMe) {
            Double grade = mUserInfo.getGrade();
            mTvRate.setText("" + grade);

            //展示关注
            getAllAttentionList();

            Integer type = mUserInfo.getType();
            if (type != null) {
                if (type != OtherConstant.USER_TYPE_ACCOMPANY) {
                    mBtnOrder.setVisibility(View.GONE);
                }
            }
        }
        //展示游戏
        getAllGameList();

        mTvInterest.setText(mUserInfo.getInterest());
        mTvProfession.setText(mUserInfo.getProfession());
        Integer favor = mUserInfo.getFavor();
        if (favor != null) {
            mTvFans.setText(favor + "");
        } else {
            mTvFans.setText("0");
        }
        LogUtils.d(getTag(), "date:" + mUserInfo.getDate());
        mTvConstellation.setText(StringUtils.getConstellationByString(mUserInfo.getDate()));
        String audioUrl = mUserInfo.getAudioUrl();
        if (!TextUtils.isEmpty(audioUrl)) {
            mSoundView.setVisibility(View.VISIBLE);
            mSoundView.setData(mUserInfo.getAudioUrl(), mUserInfo.getAudioLen());
        }

    }

    private void displayGame(List<TopGameBean> allList) {
        List<GameProperty> list = mUserInfo.getGameType();
//        List<TopGameBean> allList = AccompanyApplication.getGameList();
        if (list == null || list.isEmpty() || allList == null || allList.isEmpty()) {
            return;
        }
        ViewGroup.MarginLayoutParams marginLayoutParams;
        for (GameProperty property : list) {
            for (TopGameBean topGameBean : allList) {
                if (property.getType() == topGameBean.getTypeId()) {
                    TextView tv = new TextView(this);
                    int frameColor = Color.parseColor(topGameBean.getTagBg());
                    int wordColor = Color.parseColor(topGameBean.getTagFront());

                    LogUtils.d("color", "color:" + topGameBean.getTagBg() + "int:" + wordColor);

                    GradientDrawable drawable = (GradientDrawable) ActivityCompat.getDrawable(this, R.drawable.colorful_frame);
                    drawable.setColor(frameColor);
                    drawable.setStroke(QMUIDisplayHelper.dp2px(this, 1), wordColor);
                    tv.setTextColor(wordColor);
                    tv.setText(topGameBean.getName());
                    tv.setBackground(drawable);
                    tv.setTextSize(12);
                    tv.setPadding(QMUIDisplayHelper.dp2px(this, 12), QMUIDisplayHelper.dp2px(this, 2), QMUIDisplayHelper.dp2px(this, 12), QMUIDisplayHelper.dp2px(this, 2));
                    ViewGroup.LayoutParams layoutParams = tv.getLayoutParams();
                    if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                        marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
                    } else {
                        marginLayoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    }
                    marginLayoutParams.setMargins(QMUIDisplayHelper.dp2px(this, 2), QMUIDisplayHelper.dp2px(this, 2), QMUIDisplayHelper.dp2px(this, 2), QMUIDisplayHelper.dp2px(this, 2));
                    tv.setLayoutParams(marginLayoutParams);
                    mFlowLayout.addView(tv);
                }
            }
        }
    }

    private void getAllGameList() {
        List<GameProperty> list = mUserInfo.getGameType();
        if (list == null || list.isEmpty()) {
            mLinGame.setVisibility(View.GONE);
            return;
        }
        List<TopGameBean> allList = AccompanyApplication.getGameList();
        if (allList == null || allList.isEmpty()) {
            getGameListFromDisk();
        } else {
            displayGame(allList);
        }
    }

    private void getGameListFromDisk() {
        ThreadPool.newInstance().add(new Runnable() {
            @Override
            public void run() {
                String data = FileSaveUtils.getInstance().getData(OtherConstant.FILE_GAME);
                if (data == null || TextUtils.isEmpty(data)) {
                    return;
                }
                final List<TopGameBean> list = GsonUtils.fromJson(data, new TypeToken<List<TopGameBean>>() {
                }.getType());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayGame(list);
                    }
                });
            }
        });
    }

    private void disPlayAttention(List<String> allList) {
        String userId = mUserInfo.getUserId();
        if (allList != null) {
            mAttentionList = allList;
        }
        if (mAttentionList.contains(userId)) {
            setAttentionUI();
        } else {
            setDisAttentionUI();
        }
    }

    private void getAllAttentionList() {
        if (mAttentionList != null && mAttentionList.size() > 0) {
            disPlayAttention(mAttentionList);
        }
        List<String> list = AccompanyApplication.getAttentionList();
        if (list == null || list.isEmpty()) {
            getAttentionListFromDisk();
        } else {
            disPlayAttention(list);
        }
    }

    private void getAttentionListFromDisk() {
        ThreadPool.newInstance().add(new Runnable() {
            @Override
            public void run() {
                String data = FileSaveUtils.getInstance().getData(OtherConstant.FILE_ATTENTION);
                final List<String> allList = GsonUtils.fromJson(data, new TypeToken<List<String>>() {
                }.getType());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        disPlayAttention(allList);
                    }
                });
            }
        });
    }

    private void setAttentionUI() {
        mAttention = true;
        mTvAttention.setText(getResources().getString(R.string.attention_already));
        mLinAttention.setBackgroundResource(R.drawable.shape_cancel_attention);
        mImgAttention.setBackgroundResource(R.mipmap.cross);
    }

    private void setDisAttentionUI() {
        mAttention = false;
        mTvAttention.setText(getResources().getString(R.string.attention));
        mLinAttention.setBackgroundResource(R.drawable.shape_attention);
        mImgAttention.setBackgroundResource(R.mipmap.plus);
    }

    private void saveAttention() {
        String userId = mUserInfo.getUserId();
        if (mAttentionList.contains(userId)) {
            mAttentionList.remove(userId);
        } else {
            mAttentionList.add(userId);
        }
        AccompanyApplication.setAttentionList(mAttentionList);
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
                            setDisAttentionUI();
                        } else {
                            ToastUtils.showCommonToast(getResources().getString(R.string.attention_success));
                            setAttentionUI();
                        }
                        saveAttention();
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
                    EventUtils.getInstance().upClickOrder(mUserInfo.getUserId());
                    Intent intent = new Intent(this, OrderActivity.class);
                    intent.putExtra(IntentConstant.INTENT_USER, mUserInfo);
                    startActivity(intent);
                }
                break;
            case R.id.lin_attention:
                dealAttention();
                break;
            case R.id.btn_chat:
                if (mUserInfo != null) {
                    RongIM.getInstance().startPrivateChat(this, mUserInfo.getUserId(), mUserInfo.getName());
                }
                break;
        }
    }

    private void requestUserInfo() {
//        showDialog();
        FindUserBean bean = new FindUserBean();
        bean.setToken(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
        bean.setFindId(mUserInfo.getUserId());
        RequestBody body = EncodeUtils.encodeInBody(GsonUtils.toJson(bean));
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().getUserInfo(body), new TypeToken<BaseDecodeBean<List<UserInfo>>>() {
        }.getType(), new NetListener<List<UserInfo>>() {
            @Override
            public void onSuccess(List<UserInfo> list) {
                dismissDialog();
                if (list.isEmpty()) {
                    return;
                }
                mUserInfo = list.get(0);
                setViews();
                //刷新聊天头像
                updateChatUser();
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
            }
        });
    }

    private void updateChatUser() {
        if (mUserInfo == null) {
            return;
        }
        io.rong.imlib.model.UserInfo chatInfo = new io.rong.imlib.model.UserInfo(mUserInfo.getUserId(), mUserInfo.getName(), Uri.parse(mUserInfo.getUrl()));
        RongIM.getInstance().refreshUserInfoCache(chatInfo);

        if (TextUtils.equals((SPUtils.getInstance().getString(SpConstant.MY_USER_ID)),mUserInfo.getUserId())) {
            return;
        }
        Intent intent = new Intent(OtherConstant.CONVERSATION_ACTIVITY_RECEIVER);
        intent.putExtra(IntentConstant.INTENT_CONVERSATION_RECEIVER_TYPE, OtherConstant.CONVERSATION_UPDATE_NAME);
        intent.putExtra(IntentConstant.INTENT_USER_NAME, mUserInfo.getName());
        sendBroadcast(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(OtherConstant.IS_ATTENTION, mAttention);
        setResult(RESULT_OK,intent);
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mSoundView != null) {
            mSoundView.stopPlay();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mSoundView != null) {
            mSoundView.destroy();
        }

        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }
}

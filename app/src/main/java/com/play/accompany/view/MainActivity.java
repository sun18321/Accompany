package com.play.accompany.view;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.base.BaseFragment;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.BaseResponse;
import com.play.accompany.bean.ChatBean;
import com.play.accompany.bean.ChatInfo;
import com.play.accompany.bean.MainReceiverMessage;
import com.play.accompany.bean.OrderUnreadBean;
import com.play.accompany.bean.TopGameBean;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.fragment.HomeFragment;
import com.play.accompany.fragment.MessageFragment;
import com.play.accompany.fragment.MyFragment;
import com.play.accompany.fragment.TopLivingSoundFragment;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.present.BottomListener;
import com.play.accompany.service.HeartService;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.LocationUtils;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.StringUtils;
import com.play.accompany.utils.ToastUtils;
import java.util.List;

import io.reactivex.Observable;
import io.rong.imkit.RongIM;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;
import okhttp3.RequestBody;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends BaseActivity implements View.OnClickListener, BottomListener {

    private HomeFragment mHomeFragment;
//    private ConversationListFragment mConversationListFragment;
    private MyFragment mMyFragment;
    private final String TAG_HOME = "home";
    private final String TAG_MESSAGE = "message";
    private final String TAG_MY = "my";
    private final String TAG_MIC = "mic";
    private String mCurrentTag;
    private final String mChatTag = "white_sign";
    private boolean mTokenRequest = false;
    private MessageFragment mMessageFragment;
    private MainReceiver mReceiver;
    private ImageView mImgHome;
    private TextView mTvHome;
    private TextView mTvHomeMessage;
    private ImageView mImgMessage;
    private TextView mTvMessage;
    private TextView mTvMessageMessage;
    private ImageView mImgMy;
    private ImageView mImgSpeak;
    private TextView mTvMy;
    private TextView mTvMyMessage;
    private TextView mCurrentTextView = null;
    private AnimationDrawable mAnimMy;
    private AnimationDrawable mAnimMessage;
    private AnimationDrawable mAnimHome;
    private AnimationDrawable mAnimSpeak;
    private AnimationDrawable mCurrentAnim = null;
    private IUnReadMessageObserver mUnReadListener;
    private long mCLickbbackTime = 0;
    private Animator mAnimIn;
    private Animator mAnimOut;
    private boolean mBottomShow = true;
    private int mAllUnRead;
    private int mOrderUnRead = 0;
    private TextView mTvMic;
    private TopLivingSoundFragment mLivingFragment;
    private boolean mIsAnim = false;

    public static void launch(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        registerMessageListener();
        registerReceiver();
        startHeart();
    }

    private void startHeart() {
        Intent intent = new Intent(this, HeartService.class);
        startService(intent);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected String getTag() {
        return "mainActivity";
    }

    @Override
    protected void onPause() {
        super.onPause();

        Glide.with(this).pauseRequests();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        LogUtils.d("life", "main activity resume");

        Glide.with(this).resumeRequests();

        if (getIntent() != null) {
            int extra = getIntent().getIntExtra(OtherConstant.MAIN_INTNET, -1);
            if (extra == OtherConstant.GO_MESSAGE) {
                LogUtils.d("anim", "on resume switch");

                animMessage();
                switchText(mTvMessage);
                switchFragment(mHomeFragment, TAG_HOME);
            }
        }
        int newCount = AccompanyApplication.getMessageUnread();
        mAllUnRead = mAllUnRead - mOrderUnRead + newCount;
        mOrderUnRead = newCount;
        showRedPoint();
        setUnreadMessageCount();
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        if (mUnReadListener != null) {
            RongIM.getInstance().removeUnReadMessageCountChangedObserver(mUnReadListener);
        }

        Intent intent = new Intent(this, HeartService.class);
        stopService(intent);
    }

    @Override
    protected void initViews() {
        if (SPUtils.getInstance().getBoolean(SpConstant.SHOW_LOCATION, true)) {
            MainActivityPermissionsDispatcher.requestPermissionWithPermissionCheck(this);
        }
        requestCacheData();
        initFragment();
        getRedPoint();

//        testDownload();
    }


    //传入所有未读消息数
    private void setUnreadMessageCount() {
        if (mAllUnRead <= 0) {
            mTvMessageMessage.setVisibility(View.GONE);
        } else {
            String count = StringUtils.unReadCount(mAllUnRead);
            mTvMessageMessage.setVisibility(View.VISIBLE);
            mTvMessageMessage.setText(count);
        }
    }

    private void registerMessageListener() {
        mUnReadListener = new IUnReadMessageObserver() {
            @Override
            public void onCountChanged(int i) {
                LogUtils.d("message", "unread:" + i);
                mAllUnRead = mOrderUnRead + i;
                setUnreadMessageCount();
            }
        };
        RongIM.getInstance().addUnReadMessageCountChangedObserver(mUnReadListener, Conversation.ConversationType.PRIVATE);
    }

    private void registerReceiver() {
        mReceiver = new MainReceiver();
        IntentFilter filter = new IntentFilter(OtherConstant.FILTER_MAIN_RECEIVER);
        registerReceiver(mReceiver, filter);
    }

    private void initFragment() {
        findViewById(R.id.rel_home).setOnClickListener(this);
        findViewById(R.id.rel_message).setOnClickListener(this);
        findViewById(R.id.rel_my).setOnClickListener(this);
        findViewById(R.id.rel_mic).setOnClickListener(this);

        mImgHome = findViewById(R.id.img_home);
        mTvHome = findViewById(R.id.tv_home);
        mTvHomeMessage = findViewById(R.id.tv_home_message);
        mImgMessage = findViewById(R.id.img_message);
        mTvMessage = findViewById(R.id.tv_message);
        mTvMessageMessage = findViewById(R.id.tv_message_message);
        mImgMy = findViewById(R.id.img_my);
        mTvMy = findViewById(R.id.tv_my);
        mTvMyMessage = findViewById(R.id.tv_my_message);
        mImgSpeak = findViewById(R.id.img_mic);
        mAnimMy = (AnimationDrawable) mImgMy.getBackground();
        mAnimMessage = (AnimationDrawable) mImgMessage.getBackground();
        mAnimHome = (AnimationDrawable) mImgHome.getBackground();
        mAnimSpeak = (AnimationDrawable) mImgSpeak.getBackground();
        mTvMic = findViewById(R.id.tv_mic);


        mHomeFragment = HomeFragment.newInstance(this);
//        if (mConversationListFragment == null) {
//            mConversationListFragment = new ConversationListFragment();
//            Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
//                    .appendPath("conversationlist")
//                    .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话，非聚合
//                    .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")//设置群组会话，该会话非聚合显示
//                    .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//设置讨论组会话非聚合显示
//                    .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//设置系统会话非聚合显示
//                    .build();
//            mConversationListFragment.setUri(uri);
//        }

        mMessageFragment = MessageFragment.newInstance();
        mMyFragment = MyFragment.newInstance();
        if (mLivingFragment == null) {
            mLivingFragment = new TopLivingSoundFragment(this);
        }

        String token = SPUtils.getInstance().getString(SpConstant.CHAT_TOKEN);
        if (TextUtils.isEmpty(token)) {
            getChatToken();
        }else {
            chatConnect(token);
        }

        animHome();
        switchText(mTvHome);
        switchFragment(mHomeFragment, TAG_HOME);
    }



    private void requestCacheData() {
        RequestBody body = EncodeUtils.encodeToken();
        Observable<BaseResponse> observable = NetFactory.getNetRequest().getNetService().getAllGame(body);
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(observable, new TypeToken<BaseDecodeBean<List<TopGameBean>>>() {}.getType(), new NetListener<List<TopGameBean>>() {
            @Override
            public void onSuccess(List<TopGameBean> list) {
                if (list.isEmpty()) {
                    return;
                }
                AccompanyApplication.setGameList(list);
            }

            @Override
            public void onFailed(int errCode) {

            }

            @Override
            public void onError() {

            }

            @Override
            public void onComplete() {

            }
        });

    }


    private void getChatToken() {
        if (mTokenRequest) {
            return;
        }
        mTokenRequest = true;
        RequestBody body = EncodeUtils.encodeToken();
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().getChatToken(body), new TypeToken<BaseDecodeBean<List<ChatBean>>>() {
        }.getType(), new NetListener<List<ChatBean>>() {
            @Override
            public void onSuccess(List<ChatBean> list) {
                if (list.isEmpty()) {
                    return;
                }
                ChatBean bean = list.get(0);
                String token = bean.getRcToken();
                SPUtils.getInstance().put(SpConstant.CHAT_TOKEN, token);
                chatConnect(token);
            }

            @Override
            public void onFailed(int errCode) {

            }

            @Override
            public void onError() {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void chatConnect(String token) {
        if (getApplicationInfo().packageName.equals(AccompanyApplication.getCurProcessName(getApplicationContext()))){
            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {
                    LogUtils.d(mChatTag, "token failed");
                    getChatToken();
                }

                @Override
                public void onSuccess(String s) {
                    LogUtils.d(mChatTag, "connect success");

                    RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
                        @Override
                        public UserInfo getUserInfo(String s) {
                            Uri uri = Uri.parse(SPUtils.getInstance().getString(SpConstant.MY_USER_URL));

                            LogUtils.d(mChatTag, "res:" + s);

                            if (TextUtils.equals(s, SPUtils.getInstance().getString(SpConstant.MY_USER_ID))) {
                                return new UserInfo(SPUtils.getInstance().getString(SpConstant.MY_USER_ID), SPUtils.getInstance().getString(SpConstant.MY_USER_NAME), uri);
                            } else {
                                getChatInfo(s);
                            }
                            return null;
                        }
                    }, true);
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    LogUtils.d(mChatTag, "error:" + errorCode);
                }
            });
        }
    }


    private void animMy() {
        setCurrentAnim(mAnimMy);
//        mAnimMy.setVisible(true, true);
//        mAnimMy.start();
    }

    private void animMessage() {
        setCurrentAnim(mAnimMessage);
//        mAnimMessage.setVisible(true, true);
//        mAnimMessage.start();
    }

    private void animHome() {
        setCurrentAnim(mAnimHome);
//        mAnimHome.setVisible(true, true);
//        mAnimHome.start();
    }

    private void setCurrentAnim(AnimationDrawable drawable) {
        if (mCurrentAnim != null) {
            mCurrentAnim.selectDrawable(0);
            mCurrentAnim.stop();
            mCurrentAnim.setVisible(true, true);
        }
        mCurrentAnim = drawable;
        mCurrentAnim.setVisible(true, true);
        mCurrentAnim.start();
    }

    private void switchFragment(Fragment fragment, String tag) {
        if (TextUtils.equals(mCurrentTag, tag)) {
            return;
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (getSupportFragmentManager().findFragmentByTag(tag) == null) {
            fragmentTransaction.add(R.id.nav_container,fragment, tag);
        }
        BaseFragment oldFragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(mCurrentTag);
        if (oldFragment != null && oldFragment.isAdded()) {
            fragmentTransaction.hide(oldFragment);
        }
        fragmentTransaction.show(fragment).commitAllowingStateLoss();
        mCurrentTag = tag;
    }

    private void switchText(TextView textView) {
        if (mCurrentTextView == textView) {
            return;
        }
        if (mCurrentTextView != null) {
            mCurrentTextView.setSelected(false);
        }
        mCurrentTextView = textView;
        mCurrentTextView.setSelected(true);
    }

    private void getChatInfo(String userId) {
        LogUtils.d(mChatTag, "get data from service:" + userId);

        ChatInfo info = new ChatInfo(userId);
        String json = GsonUtils.toJson(info);
        RequestBody body = EncodeUtils.encodeInBody(json);
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().getChaterInfo(body), new TypeToken<BaseDecodeBean<List<com.play.accompany.bean.UserInfo>>>() {
        }.getType(), new NetListener<List<com.play.accompany.bean.UserInfo>>() {
            @Override
            public void onSuccess(List<com.play.accompany.bean.UserInfo> list) {
                if (list.isEmpty()) {
                    return;
                }
                com.play.accompany.bean.UserInfo userInfo = list.get(0);
                if (userInfo != null) {
                    String url = userInfo.getUrl();
                    UserInfo chatInfo = new UserInfo(userInfo.getUserId(), userInfo.getName(), Uri.parse(url));
                    RongIM.getInstance().refreshUserInfoCache(chatInfo);
                }
            }

            @Override
            public void onFailed(int errCode) {

            }

            @Override
            public void onError() {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION})
    public void requestPermission() {
        LocationUtils.startLocate();
    }

    @OnShowRationale({Manifest.permission.ACCESS_COARSE_LOCATION})
    public void showReason(final PermissionRequest request) {
        new AlertDialog.Builder(this).setMessage(getResources().getString(R.string.location_msg))
                .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                request.cancel();
            }
        }).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    private void goOrder() {
        Intent intent = new Intent(this, AllOrderActivity.class);
        startActivity(intent);
    }

    private void getRedPoint() {
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().getRedPointCount(EncodeUtils.encodeToken()), new TypeToken<BaseDecodeBean<List<OrderUnreadBean>>>() {
                }.getType(), new NetListener<List<OrderUnreadBean>>() {
                    @Override
                    public void onSuccess(List<OrderUnreadBean> list) {
                        if (list.isEmpty()) {
                            return;
                        }

                        OrderUnreadBean bean = list.get(0);
                        if (bean != null) {
                            int count = bean.getOrderNewNum();
                            AccompanyApplication.setMessageUnread(count);
                            mOrderUnRead = count;
                            registerMessageListener();
                            showRedPoint();
                        }
                    }

                    @Override
                    public void onFailed(int errCode) {

                    }

                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    //传入订单消息的红点数
    public void showRedPoint() {
        mMessageFragment.setUnread(mOrderUnRead);
        if (mTvMyMessage != null) {
            if (mOrderUnRead == 0) {
                mTvMyMessage.setText("");
                mTvMyMessage.setVisibility(View.GONE);
            } else {
                mTvMyMessage.setVisibility(View.VISIBLE);
                mTvMyMessage.setText(String.valueOf(mOrderUnRead));
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        initAnim();
    }

    private void initAnim() {
        final LinearLayout linBottom = findViewById(R.id.lin_bottom);

        LogUtils.d(getTag(),"height:" + linBottom.getHeight());
        mAnimIn = ObjectAnimator.ofFloat(linBottom, "translationY", linBottom.getHeight(), 0).setDuration(300);
        mAnimIn.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mIsAnim = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mBottomShow = true;
                mIsAnim = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimOut = ObjectAnimator.ofFloat(linBottom, "translationY", 0, linBottom.getHeight()).setDuration(300);
        mAnimOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mIsAnim = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mBottomShow = false;
                mIsAnim = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rel_home:
                animHome();
                switchText(mTvHome);
                switchFragment(mHomeFragment,TAG_HOME);
                break;
            case R.id.rel_message:
                animMessage();
                switchText(mTvMessage);
                switchFragment(mMessageFragment, TAG_MESSAGE);
                break;
            case R.id.rel_my:
                animMy();
                switchText(mTvMy);
                switchFragment(mMyFragment, TAG_MY);
                break;
            case R.id.rel_mic:
                setCurrentAnim(mAnimSpeak);
                switchText(mTvMic);
                switchFragment(mLivingFragment, TAG_MIC);
                break;
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (!TextUtils.equals(mCurrentTag, TAG_HOME)) {
            animHome();
            switchText(mTvHome);
            switchFragment(mHomeFragment, TAG_HOME);
            if (!mBottomShow) {
                mAnimIn.start();
            }
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        long space = currentTimeMillis - mCLickbbackTime;
        if (space > 5000) {
            ToastUtils.showCommonToast("再按一次退出程序");
            mCLickbbackTime = currentTimeMillis;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onShow() {
        if (mBottomShow) {
            return;
        }

        if (mIsAnim) {
            return;
        }

        if (mAnimIn == null) {
            initAnim();
        } else {
            mAnimIn.start();
        }
    }

    @Override
    public void onHide() {
        if (!mBottomShow) {
            return;
        }

        if (mIsAnim) {
            return;
        }

        if (mAnimOut == null) {
            initAnim();
        } else {
            mAnimOut.start();
        }
    }

    @Override
    public boolean isShow() {
        return mBottomShow;
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            moveTaskToBack(false);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    class MainReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            MainReceiverMessage message = (MainReceiverMessage) intent.getSerializableExtra(OtherConstant.MAIN_RECEIVER);
            if (message.getMessageType() == MainReceiverMessage.TYPE_ORDER) {
                goOrder();
            } else if (message.getMessageType() == MainReceiverMessage.TYPE_REMAIN) {
                mOrderUnRead = message.getRemainMessage();
                AccompanyApplication.setMessageUnread(mOrderUnRead);
                showRedPoint();
            }
        }
    }
}

package com.play.accompany.view;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.base.BaseFragment;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.ChatBean;
import com.play.accompany.bean.ChatInfo;
import com.play.accompany.bean.Token;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.db.AccompanyDatabase;
import com.play.accompany.fragment.HomeFragment;
import com.play.accompany.fragment.MessageFragment;
import com.play.accompany.fragment.MyFragment;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.LocationUtils;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.ThreadPool;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;
import okhttp3.RequestBody;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends BaseActivity {

    private BottomNavigationView mNavigationView;
    private HomeFragment mHomeFragment;
//    private ConversationListFragment mConversationListFragment;
    private MyFragment mMyFragment;
    private final String TAG_HOME = "home";
    private final String TAG_MESSAGE = "message";
    private final String TAG_MY = "my";
    private String mCurrentTag;
    private final String mChatTag = "chat";
    private boolean mTokenRequest = false;
    private MessageFragment mMessageFragment;

    public static void launch(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
    }

    private void initData() {

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
    protected void onResume() {
        super.onResume();

        Glide.with(this).resumeRequests();
    }

    @Override
    protected void initViews() {
        if (SPUtils.getInstance().getBoolean(SpConstant.SHOW_LOCATION, true)) {
            MainActivityPermissionsDispatcher.requestPermissionWithPermissionCheck(this);
        }

        mNavigationView = findViewById(R.id.navigation);
        mNavigationView.setItemIconTintList(null);
        mHomeFragment = HomeFragment.newInstance();
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

        mNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_home:
                        switchFragment(mHomeFragment, TAG_HOME);
                        return true;
                    case R.id.menu_message:
                        switchFragment(mMessageFragment, TAG_MESSAGE);
                        return true;
                    case R.id.menu_my:
                        switchFragment(mMyFragment, TAG_MY);
                        return true;
                }
                return false;
            }
        });

        String token = SPUtils.getInstance().getString(SpConstant.CHAT_TOKEN);
        if (TextUtils.isEmpty(token)) {
            getChatToken();
        }else {
            chatConnect(token);
        }

        switchFragment(mHomeFragment, TAG_HOME);
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

    private void switchFragment(Fragment fragment, String tag) {
        if (TextUtils.equals(mCurrentTag, tag)) {
            return;
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (!fragment.isAdded() && getSupportFragmentManager().findFragmentByTag(tag) == null) {
            fragmentTransaction.add(R.id.nav_container,fragment, tag);
        }
        BaseFragment oldFragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(mCurrentTag);
        if (oldFragment != null && oldFragment.isAdded()) {
            fragmentTransaction.hide(oldFragment);
        }
        fragmentTransaction.show(fragment).commitAllowingStateLoss();
        mCurrentTag = tag;
    }

    private void getChatInfo(String userId) {
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

}

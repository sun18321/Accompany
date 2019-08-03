package com.play.accompany.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.play.accompany.R;
import com.play.accompany.base.BaseFragment;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.OrderUnreadBean;
import com.play.accompany.bean.UserInfo;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.db.AccompanyDatabase;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.StringUtils;
import com.play.accompany.view.AccompanyApplication;
import com.play.accompany.view.AllOrderActivity;
import com.play.accompany.view.InviteCodeActivity;
import com.play.accompany.view.KotlinActivity;
import com.play.accompany.view.MainActivity;
import com.play.accompany.view.MasterActivity;
import com.play.accompany.view.ServiceActivity;
import com.play.accompany.view.SettingActivity;
import com.play.accompany.view.SingleEditActivity;
import com.play.accompany.view.UserCenterActivity;
import com.play.accompany.view.WalletActivity;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class MyFragment extends BaseFragment implements View.OnClickListener {
    private static MyFragment sMyFragment;
    private TextView mTvId;
    private TextView mTvName;
    private TextView mTvAttention;
    private TextView mTvFans;
    private ImageView mImgHead;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private UserInfo mUserInfo;
    private TextView mTvOrder;
    private boolean mLoad = false;

    public static MyFragment newInstance() {
        if (sMyFragment == null) {
            sMyFragment = new MyFragment();
            return sMyFragment;
        }
        return sMyFragment;
    }


    @Override
    protected int getLayout() {
        return R.layout.fragment_my;
    }

    @Override
    protected void initViews(View view) {
//        getRedPoint();

        mTvId = view.findViewById(R.id.tv_id);
        mTvName = view.findViewById(R.id.tv_name);
        mTvAttention = view.findViewById(R.id.tv_attention);
        mTvFans = view.findViewById(R.id.tv_fans);
        mImgHead = view.findViewById(R.id.img_head);
        view.findViewById(R.id.lin_wallet).setOnClickListener(this);
        view.findViewById(R.id.lin_master).setOnClickListener(this);
        view.findViewById(R.id.lin_service).setOnClickListener(this);
        view.findViewById(R.id.lin_invite).setOnClickListener(this);
        view.findViewById(R.id.lin_setting).setOnClickListener(this);
        view.findViewById(R.id.rl_order).setOnClickListener(this);
        mTvOrder = view.findViewById(R.id.tv_order);

        mImgHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUserInfo != null) {
                    Intent intent = new Intent(mContext, UserCenterActivity.class);
                    mUserInfo.setFromChat(true);
                    intent.putExtra(IntentConstant.INTENT_USER, mUserInfo);
                    mContext.startActivity(intent);
                } else {
                    getUserInfo();
                }

            }
        });

        view.findViewById(R.id.rl_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SingleEditActivity.class);
                intent.putExtra(IntentConstant.INTENT_USER, mUserInfo);
                startActivity(intent);
            }
        });

        mTvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, KotlinActivity.class));
            }
        });

    }

    @Override
    protected String getFragmentName() {
        return "MyFragment";
    }

    @Override
    public void onResume() {
        super.onResume();

        LogUtils.d("life","my fragment resume");

        //关注需要时刻刷新
        if (mTvAttention != null) {
            mTvAttention.setText(SPUtils.getInstance().getInt(SpConstant.ATTENTION_COUNT, 0) + "");
        }

        boolean isEdit = SPUtils.getInstance().getBoolean(SpConstant.IS_USER_EDIT, false);
        if (isEdit) {
            getUserInfo();
            SPUtils.getInstance().put(SpConstant.IS_USER_EDIT, false);
        }

//        LogUtils.d(getFragmentName(), "id:" + mUserInfo.getUserId() + "sp id:" + SPUtils.getInstance().getString(SpConstant.MY_USER_ID));
        //切换账号
        if (mUserInfo == null || !TextUtils.equals(SPUtils.getInstance().getString(SpConstant.MY_USER_ID), mUserInfo.getUserId())) {
            getUserInfo();
        }
//        if (mUserInfo != null) {
//            LogUtils.d(getFragmentName(), "resume id:" + mUserInfo.getUserId() + "sp id:" + SPUtils.getInstance().getString(SpConstant.MY_USER_ID));
//        } else {
//            LogUtils.d(getFragmentName(), "resume user is null");
//        }

        int count = AccompanyApplication.getMessageUnread();
        LogUtils.d("life", "count:" + count);
        if (count == 0) {
            clearRedPoint();
        } else {
            showRedPoint(count);
        }

    }

    private void getUserInfo() {
        if (mLoad) {
            return;
        }
        mLoad = true;
        mCompositeDisposable.add(Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                mUserInfo = AccompanyDatabase.getInstance(mContext).getUserDao().getUserInfo(SPUtils.getInstance().getString(SpConstant.MY_USER_ID));
                if (mUserInfo == null) {
                    emitter.onNext(false);
                    LogUtils.d(getTag(),"没有读取到数据");
                } else {
                    emitter.onNext(true);
                    LogUtils.d(getTag(),"读取到数据了！");
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean b) throws Exception {
                mLoad = false;
                if (b) {
                    displayUser();
//                    ToastUtils.showCommonToast("有名字");
                } else {
//                    ToastUtils.showCommonToast("名字是空的");
                }
            }
        }));
    }

    private void displayUser() {
        String url = mUserInfo.getUrl();
        if (!TextUtils.isEmpty(url)) {
            glideFuzzy(url);
        }
        String name = mUserInfo.getName();
        String showId = mUserInfo.getUserName();
        mTvName.setText(name);
        mTvId.setText(getResources().getString(R.string.id) + ":" + showId);
        Integer attention_followed = mUserInfo.getFavor();
        if (attention_followed != null) {
            mTvFans.setText(String.valueOf(attention_followed));
        }
    }

    private void glideFuzzy(String url) {
        LogUtils.d("glide", "url:" + url);
        Glide.with(mContext).load(url).into(mImgHead);
    }

    private void showRedPoint(int count) {
        if (mTvOrder != null) {
            mTvOrder.setVisibility(View.VISIBLE);
            String unReadCount = StringUtils.unReadCount(count);
            mTvOrder.setText(unReadCount);
        }
    }

    private void clearRedPoint() {
        if (mTvOrder != null) {
            mTvOrder.setText("");
            mTvOrder.setVisibility(View.GONE);
        }
    }

//    private void getRedPoint() {
//        AccompanyRequest request = new AccompanyRequest();
//        request.beginRequest(NetFactory.getNetRequest().getNetService().getRedPointCount(EncodeUtils.encodeToken()), new TypeToken<BaseDecodeBean<List<OrderUnreadBean>>>() {
//                }.getType(), new NetListener<List<OrderUnreadBean>>() {
//                    @Override
//                    public void onSuccess(List<OrderUnreadBean> list) {
//                        if (list.isEmpty()) {
//                            return;
//                        }
//                        OrderUnreadBean bean = list.get(0);
//                        if (bean != null) {
//                            int num = bean.getOrderNewNum();
//                            if (num > 0) {
//                                showRedPoint(num);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFailed(int errCode) {
//
//                    }
//
//                    @Override
//                    public void onError() {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//    }

    private void readRedPoint() {
        clearRedPoint();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
//            getRedPoint();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_wallet:
                startActivity(new Intent(mContext, WalletActivity.class));
                break;
            case R.id.rl_order:
                if (mTvOrder != null && mTvOrder.getVisibility() == View.VISIBLE) {
                    readRedPoint();
                }
                startActivity(new Intent(mContext,AllOrderActivity.class));
                break;
            case R.id.lin_master:
                startActivity(new Intent(mContext,MasterActivity.class));
                break;
            case R.id.lin_service:
                startActivity(new Intent(mContext,ServiceActivity.class));
                break;
            case R.id.lin_invite:
                startActivity(new Intent(mContext,InviteCodeActivity.class));
                break;
            case R.id.lin_setting:
                startActivity(new Intent(mContext,SettingActivity.class));
                break;
        }
    }
}

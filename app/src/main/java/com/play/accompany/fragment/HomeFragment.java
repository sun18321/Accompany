package com.play.accompany.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.play.accompany.R;
import com.play.accompany.adapter.HomeAdapter;
import com.play.accompany.base.BaseFragment;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.BaseResponse;
import com.play.accompany.bean.FavoriteInfo;
import com.play.accompany.bean.Token;
import com.play.accompany.bean.TopGameBean;
import com.play.accompany.bean.TypeQueryBean;
import com.play.accompany.bean.UserInfo;
import com.play.accompany.constant.AppConstant;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.db.AccompanyDatabase;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.net.StringListener;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.ThreadPool;
import com.play.accompany.utils.ToastUtils;
import com.play.accompany.view.AccountActivity;
import com.play.accompany.view.RankActivity;
import com.play.accompany.view.UserCenterActivity;
import com.play.accompany.view.WebViewActivity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;

public class HomeFragment extends BaseFragment {
    public static final int REQUEST_CODE = 1001;

    private static HomeFragment sHomeFragment;
    private RecyclerView mRecyclerView;
    private HomeAdapter mAdapter;
    private View mLoadingView;
    private SmartRefreshLayout mRefreshLayout;
    private boolean isRequsting = false;
    private List<String> mAttentionList = new ArrayList<>();
    private List<FavoriteInfo> mInfoList = new ArrayList<>();
    private boolean mLoad = false;
    private AttentionReceiver mReceiver;

    public static HomeFragment newInstance() {
        if (sHomeFragment == null) {
            sHomeFragment = new HomeFragment();
            return sHomeFragment;
        }
        return sHomeFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter filter = new IntentFilter(AppConstant.BROADCAST_ATTENTION);
        mReceiver = new AttentionReceiver();
        mContext.registerReceiver(mReceiver, filter);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initViews(View view) {
        ThreadPool.newInstance().add(new Runnable() {
            @Override
            public void run() {
                mInfoList = AccompanyDatabase.getInstance(mContext).getFavriteDao().getAllFavoriteByUserId(SPUtils.getInstance().getString(SpConstant.MY_USER_ID));
                if (!mInfoList.isEmpty()) {
                    for (FavoriteInfo favoriteInfo : mInfoList) {
                        mAttentionList.add(favoriteInfo.getFavoriteId());
                    }
                }
                mLoad = true;
            }
        });

        mRecyclerView = view.findViewById(R.id.recycler);
        mLoadingView = view.findViewById(R.id.loading_view);
        mRefreshLayout = view.findViewById(R.id.refresh_layout);

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                requestData(getToken());
            }
        });

        String token = getToken();
        requestData(token);
    }

    @Override
    protected String getFragmentName() {
        return "HomeFragment";
    }

    private void requestData(String token) {
        Token requestToken = new Token(token);
        String json = GsonUtils.toJson(requestToken);
        RequestBody body = EncodeUtils.encodeInBody(json);
        Observable<BaseResponse> observable = NetFactory.getNetRequest().getNetService().getHomePage(body);
//        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<BaseResponse>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(BaseResponse baseResponse) {
//                if (mRefreshLayout.isRefreshing()) {
//                    mRefreshLayout.finishRefresh();
//                }
//                if (mLoadingView.getVisibility() == View.VISIBLE) {
//                    mLoadingView.setVisibility(View.GONE);
//                }
//                String recEncode = baseResponse.getRecEncode();
//                try {
//                    String s = CipherUtil.desDecrypt(recEncode);
//                    BaseDecodeBean bean = GsonUtils.fromJson(s, new TypeToken<BaseDecodeBean<ResponseHome>>() {
//                    }.getType());
//                    List<ResponseHome> msgList = bean.getMsgList();
//                    if (msgList.isEmpty()) {
//                        Toast.makeText(mContext, getResources().getString(R.string.data_error), Toast.LENGTH_SHORT).show();
//                    } else {
//                        if (mList.size() > 0) {
//                            mList.clear();
//                        }
//                        mList.addAll(msgList);
//                        showHome(mList);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//                if (mLoadingView.getVisibility() == View.VISIBLE) {
//                    mLoadingView.setVisibility(View.GONE);
//                }
//            }
//        });

        AccompanyRequest request = new AccompanyRequest();
        request.requestBackString(observable, new StringListener() {
            @Override
            public void onSuccess(String s) {
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.finishRefresh();
                }
                if (mLoadingView.getVisibility() == View.VISIBLE) {
                    mLoadingView.setVisibility(View.GONE);
                }
                showHome(s);
            }

            @Override
            public void onFailed(int errorCode) {

            }

            @Override
            public void onError() {

            }

            @Override
            public void onComplete() {
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.finishRefresh();
                }
                if (mLoadingView.getVisibility() == View.VISIBLE) {
                    mLoadingView.setVisibility(View.GONE);
                }
            }
        });

    }

    private void showHome(String homeJson) {
        mAdapter = new HomeAdapter(mContext, homeJson);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayout.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setHomeListener(new HomeAdapter.HomeListener() {
            @Override
            public void onTopCLick(TopGameBean bean) {
                if (bean != null) {
                    int typeId = bean.getTypeId();
                    if (typeId == OtherConstant.TYPE_OTHER) {
                        goTopGame();
                    } else {
                        queryType(typeId);
                    }
                }
            }

            @Override
            public void onItemClick(UserInfo info) {
                if (!mLoad) {
                    ToastUtils.showCommonToast(mContext.getResources().getString(R.string.data_loading));
                    return;
                }
                boolean attention = mAttentionList.contains(info.getUserId());
                info.setAttention(attention);
                Intent intent = new Intent(mContext, UserCenterActivity.class);
                intent.putExtra(IntentConstant.INTENT_USER, info);
                mContext.startActivity(intent);
            }

            @Override
            public void onBannerClick(String url) {
                Intent intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra(IntentConstant.INTENT_URL, url);
                intent.putExtra(IntentConstant.INTENT_TITLE, mContext.getResources().getString(R.string.webview_title_activity));
                startActivity(intent);
            }
        });
    }

    private void queryType(int id) {
        if (isRequsting) {
            return;
        }
        isRequsting = true;
        TypeQueryBean bean = new TypeQueryBean();
        bean.setQueryId(id);
        bean.setToken(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
        String json = GsonUtils.toJson(bean);
        RequestBody body = EncodeUtils.encodeInBody(json);
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().getType(body), new TypeToken<BaseDecodeBean<List<UserInfo>>>() {
        }.getType(), new NetListener<List<UserInfo>>() {
            @Override
            public void onSuccess(List<UserInfo> list) {
                if (list.isEmpty()) {
                    return;
                }
                if (mAdapter != null) {
                    mAdapter.setContentList(list);
                }
                isRequsting = false;
            }

            @Override
            public void onFailed(int errCode) {

            }

            @Override
            public void onError() {

            }

            @Override
            public void onComplete() {
                isRequsting = false;
            }
        });
    }

    private void goTopGame() {
        Intent intent = new Intent(mContext, RankActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    private String getToken() {
        String token = SPUtils.getInstance().getString(SpConstant.APP_TOKEN);
        if (TextUtils.isEmpty(token)) {
            Toast.makeText(mContext, getResources().getString(R.string.no_login), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(mContext, AccountActivity.class));
            mActivity.finish();
            return null;
        } else {
            return token;
        }
    }

    private void attentionChange(String id) {
        if (TextUtils.isEmpty(id)) {
            return;
        }

        if (mAttentionList.contains(id)) {
            mAttentionList.remove(id);
            SPUtils.getInstance().put(SpConstant.ATTENTION_COUNT, mAttentionList.size());
            FavoriteInfo info = null;
            for (FavoriteInfo favoriteInfo : mInfoList) {
                if (TextUtils.equals(favoriteInfo.getFavoriteId(), id)) {
                    info = favoriteInfo;
                    break;
                }
            }
            if (info != null) {
                mInfoList.remove(info);
                final FavoriteInfo finalInfo = info;
                ThreadPool.newInstance().add(new Runnable() {
                    @Override
                    public void run() {
                        AccompanyDatabase.getInstance(mContext).getFavriteDao().delete(finalInfo);
                    }
                });
            }
        } else {
            mAttentionList.add(id);
            SPUtils.getInstance().put(SpConstant.ATTENTION_COUNT, mAttentionList.size());
            final FavoriteInfo info = new FavoriteInfo();
            info.setUserId(SPUtils.getInstance().getString(SpConstant.MY_USER_ID));
            info.setFavoriteId(id);
            ThreadPool.newInstance().add(new Runnable() {
                @Override
                public void run() {
                    long row = AccompanyDatabase.getInstance(mContext).getFavriteDao().insertSingle(info);
                    info.setId(row);
                    mInfoList.add(info);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            int extra = data.getIntExtra(IntentConstant.INTENT_GAME_ID, 0);
            if (extra == 0) {
                return;
            }
            queryType(extra);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (getActivity() != null) {
            getActivity().unregisterReceiver(mReceiver);
        }
    }

    class AttentionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String id = intent.getStringExtra(IntentConstant.INTENT_USER_ID);
                attentionChange(id);
            }
        }
    }

}

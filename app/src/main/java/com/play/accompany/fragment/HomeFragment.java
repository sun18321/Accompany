package com.play.accompany.fragment;

import android.content.Intent;
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
import com.play.accompany.bean.Token;
import com.play.accompany.bean.TopGameBean;
import com.play.accompany.bean.TypeQueryBean;
import com.play.accompany.bean.UserInfo;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.net.StringListener;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.SPUtils;
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
    private static HomeFragment sHomeFragment;
    private RecyclerView mRecyclerView;
    private HomeAdapter mAdapter;
    private View mLoadingView;
    private SmartRefreshLayout mRefreshLayout;
    private boolean isRequsting = false;

    public static HomeFragment newInstance() {
        if (sHomeFragment == null) {
            sHomeFragment = new HomeFragment();
            return sHomeFragment;
        }
        return sHomeFragment;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initViews(View view) {

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
                    String name = bean.getName();
                    int typeId = bean.getTypeId();
                    if (typeId == OtherConstant.TYPE_OTHER) {
                        goTopGame(name);
                    } else {
                        queryType(typeId);
                    }
                }
            }

            @Override
            public void onItemClick(UserInfo info) {
                Intent intent = new Intent(mContext, UserCenterActivity.class);
                intent.putExtra(IntentConstant.INTENT_USER, info);
                mContext.startActivity(intent);
            }

            @Override
            public void onBannerClick(String url) {
                Intent intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra(IntentConstant.INTENT_URL, url);
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

            }
        });
    }

    private void goTopGame(String name) {
        Intent intent = new Intent(mContext, RankActivity.class);
        intent.putExtra(RankActivity.ACTIVITY_TITLE, name);
        mContext.startActivity(intent);
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

}

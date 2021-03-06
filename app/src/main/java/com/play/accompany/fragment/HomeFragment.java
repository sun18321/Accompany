package com.play.accompany.fragment;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.play.accompany.present.BottomListener;
import com.play.accompany.utils.DateUtils;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.EventUtils;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.ToastUtils;
import com.play.accompany.view.AccountActivity;
import com.play.accompany.view.CollapseUserCenterActivity;
import com.play.accompany.view.RankActivity;
import com.play.accompany.view.UserCenterActivity;
import com.play.accompany.view.WebViewActivity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

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
    private int mCurrentType = -1;
    private static BottomListener mListener;

    public static HomeFragment newInstance(BottomListener listener) {
        if (sHomeFragment == null) {
            mListener = listener;
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
                if (mCurrentType == -1) {
                    requestData(getToken());
                } else {
                    queryType(mCurrentType,true);
                }
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (mRecyclerView.getScrollState() == RecyclerView.SCROLL_STATE_SETTLING && dy > 0) {
                    if (mListener != null) {
                        mListener.onHide();
                    }
                }else if (mRecyclerView.getScrollState() == RecyclerView.SCROLL_STATE_SETTLING && dy < 0) {
                    if (mListener != null) {
                        mListener.onShow();
                    }
                }
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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setHomeListener(new HomeAdapter.HomeListener() {
            @Override
            public void onTopCLick(TopGameBean bean) {
                if (bean != null) {
                    int typeId = bean.getTypeId();
                    if (typeId == OtherConstant.TYPE_OTHER) {
                        goTopGame();
                    } else {
                        queryType(typeId, false);
                    }
                }
            }

            @Override
            public void onItemClick(UserInfo info,int index) {
//                EventUtils.getInstance().upMasterClick(String.valueOf(index), info.getUserId(), String.valueOf(mCurrentType), DateUtils.time2Date(System.currentTimeMillis()));
//
//                info.setFromChat(true);
//                Intent intent = new Intent(mContext, UserCenterActivity.class);
//                intent.putExtra(IntentConstant.INTENT_USER, info);
//                mContext.startActivity(intent);

                mContext.startActivity(new Intent(mContext, CollapseUserCenterActivity.class).putExtra(IntentConstant.INTENT_USER_ID,info.getUserId()));
            }

            @Override
            public void onBannerClick(String url) {
                EventUtils.getInstance().upOpenActivity(DateUtils.time2Date(System.currentTimeMillis()));

                Intent intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra(IntentConstant.INTENT_URL, url);
                intent.putExtra(IntentConstant.INTENT_TITLE, mContext.getResources().getString(R.string.webview_title_activity));
                startActivity(intent);
            }
        });
    }

    private void queryType(final int id, final boolean isRefresh) {
        if (isRequsting) {
            return;
        }
        if (!isRefresh && mCurrentType == id) {
            return;
        }

        mCurrentType = id;
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
                if (isRefresh) {
                    mRefreshLayout.finishRefresh();
                }

                EventUtils.getInstance().upClickGameType(String.valueOf(id), DateUtils.time2Date(System.currentTimeMillis()));

                if (list.isEmpty()) {
                    ToastUtils.showCommonToast(getResources().getString(R.string.no_master));
                    return;
                }
                if (mAdapter != null) {
                    mAdapter.setGameQuery(list, id);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            int extra = data.getIntExtra(IntentConstant.INTENT_GAME_ID, 0);
            if (extra == 0) {
                return;
            }

            queryType(extra,false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mListener = null;
    }
}

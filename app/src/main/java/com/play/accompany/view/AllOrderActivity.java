package com.play.accompany.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.play.accompany.R;
import com.play.accompany.adapter.OrderAdapter;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.bean.AcceptOrderBean;
import com.play.accompany.bean.AllOrderBean;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.CommentBean;
import com.play.accompany.bean.IntentPayInfo;
import com.play.accompany.bean.OnlyCodeBean;
import com.play.accompany.bean.Token;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;

public class AllOrderActivity extends BaseActivity implements OrderAdapter.OrderListener {

    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private View mLoadingView;
    private List<AllOrderBean> mList = new ArrayList<>();
    private OrderAdapter mAdapter;

    @Override
    protected int getLayout() {
        return R.layout.activity_all_order;
    }

    @Override
    protected String getTag() {
        return "AllOrderActivity";
    }

    @Override
    protected void initViews() {
        initToolbar(getResources().getString(R.string.my_order));
        mRefreshLayout = findViewById(R.id.refresh_layout);
        mRecyclerView = findViewById(R.id.recycler);
        mLoadingView = findViewById(R.id.loading_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                requestData();
            }
        });

//        requestData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mRefreshLayout != null) {
            mRefreshLayout.autoRefresh();
        }
    }

    private void requestData() {
        Token token = new Token(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
        String json = GsonUtils.toJson(token);
        RequestBody body = EncodeUtils.encodeInBody(json);
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().getAllOrder(body), new TypeToken<BaseDecodeBean<List<AllOrderBean>>>() {
        }.getType(), new NetListener<List<AllOrderBean>>() {
            @Override
            public void onSuccess(List<AllOrderBean> list) {
                removeLoading();
                mList.clear();
                mList.addAll(list);
                if (mAdapter == null) {
                    mAdapter = new OrderAdapter(AllOrderActivity.this, mList);
                    mAdapter.setOrderListener(AllOrderActivity.this);
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    mAdapter.notifyDataSetChanged();
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
                removeLoading();
            }
        });
    }

    private void removeLoading() {
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.finishRefresh();
        }

        if (mLoadingView != null && mLoadingView.getVisibility() == View.VISIBLE) {
            mLoadingView.setVisibility(View.INVISIBLE);
        }
    }

    private void sendComment(CommentBean bean) {
        String json = GsonUtils.toJson(bean);
        RequestBody body = EncodeUtils.encodeInBody(json);
        AccompanyRequest request = new AccompanyRequest();
        request.requestDealToast(NetFactory.getNetRequest().getNetService().sendComment(body), getResources().getString(R.string.comment_success), getResources().getString(R.string.comment_failed));
    }

    private void orderNext(String id, final String success, final String failed) {
        AcceptOrderBean bean = new AcceptOrderBean();
        bean.setId(id);
        bean.setToken(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
        String json = GsonUtils.toJson(bean);
        RequestBody body = EncodeUtils.encodeInBody(json);
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().acceptOrder(body), new TypeToken<BaseDecodeBean<List<OnlyCodeBean>>>() {
        }.getType(), new NetListener<List<OnlyCodeBean>>() {
            @Override
            public void onSuccess(List<OnlyCodeBean> list) {
                ToastUtils.showCommonToast(success);
                if (mRefreshLayout != null) {
                    mRefreshLayout.autoRefresh();
                }
            }

            @Override
            public void onFailed(int errCode) {
                ToastUtils.showCommonToast(failed);
            }

            @Override
            public void onError() {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void onItemClick(IntentPayInfo info) {
        Intent intent = new Intent(this, OrderPayActivity.class);
        intent.putExtra(IntentConstant.INTENT_PAY_INFO, info);
        startActivity(intent);
    }

    @Override
    public void onCommentClick(CommentBean bean) {
        sendComment(bean);
    }

    @Override
    public void onOrderNext(String id,String success,String failed) {
        orderNext(id,success,failed);
    }
}

package com.play.accompany.view;

import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.play.accompany.bean.OrderUnreadBean;
import com.play.accompany.bean.Token;
import com.play.accompany.bean.UserInfo;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.utils.DateUtils;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.EventUtils;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.ToastUtils;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
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
    private TextView mTvNoOrder;
    private boolean mNeting = false;

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
        mTvNoOrder = findViewById(R.id.tv_no_order);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                requestData();
            }
        });

        requestData();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        if (mRefreshLayout != null) {
//            mRefreshLayout.autoRefresh();
//        }
//    }

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

                clearRed();

                mList.clear();
                if (list == null || list.isEmpty()) {
                    mTvNoOrder.setVisibility(View.VISIBLE);
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                    return;
                }
                if (mTvNoOrder != null && mTvNoOrder.getVisibility() == View.VISIBLE) {
                    mTvNoOrder.setVisibility(View.INVISIBLE);
                }
                mList.addAll(list);
                if (mAdapter == null) {
                    mAdapter = new OrderAdapter(AllOrderActivity.this, mList);
                    mAdapter.setOrderListener(AllOrderActivity.this);
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.addItemDecoration(new DividerItemDecoration(AllOrderActivity.this, LinearLayout.VERTICAL));
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

    private void orderNext(final AllOrderBean allOrderBean, final String success, final String failed) {
        if (mNeting) {
            return;
        }
        showDialog();
        mNeting = true;
        final AcceptOrderBean bean = new AcceptOrderBean();
        bean.setId(allOrderBean.getId());
        bean.setToken(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
        String json = GsonUtils.toJson(bean);
        RequestBody body = EncodeUtils.encodeInBody(json);
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().acceptOrder(body), new TypeToken<BaseDecodeBean<List<OnlyCodeBean>>>() {
        }.getType(), new NetListener<List<OnlyCodeBean>>() {
            @Override
            public void onSuccess(List<OnlyCodeBean> list) {
                dismissDialog();
                ToastUtils.showCommonToast(success);
                if (mRefreshLayout != null) {
                    mRefreshLayout.autoRefresh();
                }
                int all = allOrderBean.getNum() * allOrderBean.getPrice();
                EventUtils.getInstance().upAcceptOrder(allOrderBean.getTargetId(), allOrderBean.getId(), allOrderBean.getGameTypeName(), DateUtils.time2Date(allOrderBean.getStartTime()
                ), String.valueOf(allOrderBean.getNum()), String.valueOf(all));
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
                dismissDialog();
                mNeting = false;
            }
        });
    }

    private void showConfirmDialog(final AllOrderBean bean, final String success, final String failed) {
        new QMUIDialog.MessageDialogBuilder(this).setMessage("确定要接受此订单吗？").addAction(getResources().getString(R.string.cancel),
                new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                }).addAction(getResources().getString(R.string.confirm), new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                orderNext(bean, success, failed);
                dialog.dismiss();
            }
        }).create().show();
    }


    private void clearRed() {
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().clearRedPoint(EncodeUtils.encodeToken()), new TypeToken<BaseDecodeBean<List<OrderUnreadBean>>>() {
        }.getType(), new NetListener<List<OrderUnreadBean>>() {
            @Override
            public void onSuccess(List<OrderUnreadBean> list) {
                AccompanyApplication.setMessageUnread(0);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == IntentConstant.INTENT_CODE_PAY_SUCCESS || resultCode == IntentConstant.INTENT_CODE_ACCEPT_SUCESS) {
            if (mRefreshLayout != null) {
                mRefreshLayout.autoRefresh();
            }
        }
    }

    @Override
    public void onItemClick(AllOrderBean bean) {
        Intent intent = new Intent(this, OrderDetailActivity.class);
        intent.putExtra(IntentConstant.INTENT_ALL_ORDER, bean);
        startActivityForResult(intent,1);
    }

    @Override
    public void onCommentClick(CommentBean bean) {
        sendComment(bean);
    }

    @Override
    public void onOrderNext(AllOrderBean bean, String success, String failed) {
        showConfirmDialog(bean, success, failed);
//        orderNext(id,success,failed);
    }

    @Override
    public void onPayClick(IntentPayInfo info) {
        Intent intent = new Intent(this, OrderPayActivity.class);
        intent.putExtra(IntentConstant.INTENT_PAY_INFO, info);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onHeadClick(String uid) {
        UserInfo info = new UserInfo();
        info.setUserId(uid);
        info.setFromChat(true);
        Intent intent = new Intent(AllOrderActivity.this, UserCenterActivity.class);
        intent.putExtra(IntentConstant.INTENT_USER, info);
        startActivity(intent);
    }

}

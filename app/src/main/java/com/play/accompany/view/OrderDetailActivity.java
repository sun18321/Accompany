package com.play.accompany.view;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;
import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.bean.AcceptOrderBean;
import com.play.accompany.bean.AllOrderBean;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.CancelOrder;
import com.play.accompany.bean.CommentBean;
import com.play.accompany.bean.IntentPayInfo;
import com.play.accompany.bean.OnlyCodeBean;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.constant.OrderConstant;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.design.CommentDialog;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.utils.DateUtils;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.GlideUtils;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.ToastUtils;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import java.util.List;

import io.rong.imkit.RongIM;
import okhttp3.RequestBody;

public class OrderDetailActivity extends BaseActivity implements View.OnClickListener {
    private AllOrderBean mBean;
    private boolean mIsHost;
    private LinearLayout mLinReason;
    private TextView mTvReason;
    private LinearLayout mLinRate;
    private RatingBar mRatingBar;
    private LinearLayout mLinOperation;
    private TextView mTvState;
    private TextView mTvNegative;
    private Button mBtnPositive;
    private TextView mTvComment;
    private RelativeLayout mRelButton;

    @Override
    protected int getLayout() {
        return R.layout.activity_order_detail;
    }

    @Override
    protected String getTag() {
        return "OrderDetailActivity";
    }

    @Override
    protected void initViews() {
        initToolbar("订单详情");
        RoundedImageView imgHead = findViewById(R.id.img_head);
        TextView tvName = findViewById(R.id.tv_name);
        LinearLayout linGender = findViewById(R.id.lin_gender);
        ImageView imgGender = findViewById(R.id.img_gender);
        TextView tvAge = findViewById(R.id.tv_age);
        TextView tvRating = findViewById(R.id.tv_rating);
        TextView tvIntroduction = findViewById(R.id.tv_introduction);
        TextView tvType = findViewById(R.id.tv_type);
        TextView tvTime = findViewById(R.id.tv_time);
        TextView tvMoney = findViewById(R.id.tv_money);
        TextView tvCount = findViewById(R.id.tv_count);
        TextView tvAll = findViewById(R.id.tv_all);
        TextView tvMark = findViewById(R.id.tv_mark);
        mLinReason = findViewById(R.id.lin_reason);
        mTvReason = findViewById(R.id.tv_reason);
        mLinRate = findViewById(R.id.layout_rate);
        mRatingBar = findViewById(R.id.rate);
        mLinOperation = findViewById(R.id.lin_operation);
        mTvNegative = findViewById(R.id.tv_negative);
        mBtnPositive = findViewById(R.id.btn_positive);
        mTvState = findViewById(R.id.tv_state);
        mTvComment = findViewById(R.id.tv_comment);
        mRelButton = findViewById(R.id.rel_button);

//        mTvNegative.setOnClickListener(this);
//        mBtnPositive.setOnClickListener(this);
        findViewById(R.id.lin_chat).setOnClickListener(this);
        GlideUtils.commonLoad(this, mBean.getUrl(), imgHead);
        tvName.setText(mBean.getName());
        String date = mBean.getDate();
        try {
            int age = DateUtils.getAge(date);
            tvAge.setText(String.valueOf(age));
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvRating.setText(String.valueOf(mBean.getGrade()/2));
        tvIntroduction.setText(mBean.getSign());
        tvType.setText(mBean.getGameTypeName());
        tvTime.setText(DateUtils.time2Date(mBean.getStartTime()));
        int money = mBean.getPrice() * mBean.getNum();
        tvMoney.setText(String.valueOf(mBean.getPrice()));
        tvCount.setText("X" + mBean.getNum());
        tvAll.setText(money + getResources().getString(R.string.money));
        tvMark.setText(mBean.getComment());
        int state = mBean.getState();
        int gender = mBean.getGender();
        if (gender == OtherConstant.GENDER_MALE) {
            linGender.setBackgroundResource(R.drawable.male_bg);
            imgGender.setBackgroundResource(R.drawable.male);
        } else {
            linGender.setBackgroundResource(R.drawable.female_bg);
            imgGender.setBackgroundResource(R.drawable.female);
        }
        aboutState(state);
    }

    private void aboutState(int state) {
        LogUtils.d("state", "state:" + state);
        switch (state) {
            case OrderConstant.NO_PAY:
                mTvNegative.setText(getResources().getString(R.string.cancel));
                mTvNegative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelOrder();
                    }
                });
                mBtnPositive.setText(getResources().getString(R.string.button_pay));
                mBtnPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String detail = mBean.getPrice() + AccompanyApplication.getContext().getResources().getString(R.string.price) + "*" + mBean.getNum();
                        int all = (mBean.getPrice()) * (mBean.getNum());
                        IntentPayInfo info = new IntentPayInfo(mBean.getUrl(), mBean.getName(), mBean.getGameTypeName(), detail, all, mBean.getId());
                        Intent intent = new Intent(OrderDetailActivity.this, OrderPayActivity.class);
                        intent.putExtra(IntentConstant.INTENT_PAY_INFO, info);
                        startActivity(intent);
                    }
                });
                break;
            case OrderConstant.QUIT_NO_PAY:
                mLinOperation.setVisibility(View.INVISIBLE);
                mTvState.setVisibility(View.VISIBLE);
                mLinReason.setVisibility(View.VISIBLE);
                mTvReason.setText(getResources().getString(R.string.order_tip_no_pay));
                mTvState.setText("已取消");
                mTvState.setTextColor(getResources().getColor(R.color.red));
                break;
            case OrderConstant.PAY:
                if (mIsHost) {
                    mBtnPositive.setText("接单");
                    mTvNegative.setText("拒接");
                    mBtnPositive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showAcceptDialog();
                        }
                    });

                    mTvNegative.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showCancelDialog();
                        }
                    });
                } else {
                    mLinOperation.setVisibility(View.INVISIBLE);
                    mTvState.setVisibility(View.VISIBLE);
                    mTvState.setTextColor(getResources().getColor(R.color.color_order_green));
                    mTvState.setText("已支付");
                }
                break;
            case OrderConstant.MASTER_NO_ACCEPT:
                mLinOperation.setVisibility(View.INVISIBLE);
                mTvState.setVisibility(View.VISIBLE);
                mLinReason.setVisibility(View.VISIBLE);
                mTvReason.setText(getResources().getString(R.string.order_tip_no_wait));
                mTvState.setVisibility(View.VISIBLE);
                mTvState.setText("已取消");
                mTvState.setTextColor(getResources().getColor(R.color.red));
                break;
            case OrderConstant.ACCEPT_ORDER:
                mLinOperation.setVisibility(View.INVISIBLE);
                mTvState.setVisibility(View.VISIBLE);
                mTvState.setText("已接单");
                mTvState.setTextColor(getResources().getColor(R.color.color_order_green));
                break;
            case OrderConstant.START_SERVICE:
                mLinOperation.setVisibility(View.INVISIBLE);
                mTvState.setVisibility(View.VISIBLE);
                mTvState.setText("服务中");
                mTvState.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
            case OrderConstant.SERVICE_COMPLETE:
                if (mIsHost) {
                    mLinOperation.setVisibility(View.INVISIBLE);
                    mTvState.setVisibility(View.VISIBLE);
                    mTvState.setTextColor(getResources().getColor(R.color.color_order_gray));
                    mTvState.setText("已完成");
                } else {
                    mBtnPositive.setText("去评价");
                    mBtnPositive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showCommentDialog();
                        }
                    });
                    mTvNegative.setText("申诉");
                    mTvNegative.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            goService();
                        }
                    });
                }
                break;
            case OrderConstant.COMMENT_COMPLETE:
                if (mIsHost) {
                    mLinOperation.setVisibility(View.INVISIBLE);
                    mTvState.setVisibility(View.VISIBLE);
                    mTvState.setTextColor(getResources().getColor(R.color.color_order_gray));
                    mTvState.setText("已完成");
                } else {
                    mTvNegative.setText("申诉");
                    mTvNegative.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            goService();
                        }
                    });
                    mRelButton.setVisibility(View.GONE);
                }
                LogUtils.d("state", "vis:" + mLinRate.getVisibility() + "ope:" + mLinOperation.getVisibility() + "rea:" + mLinReason.getVisibility());
                mLinRate.setVisibility(View.VISIBLE);
//                mLinReason.setVisibility(View.VISIBLE);
                LogUtils.d("state", "vis:" + mLinRate.getVisibility());
                mRatingBar.setIsIndicator(true);
                float floatGrade = (float) mBean.getEvaluateGrade() / 2;
                mRatingBar.setRating(floatGrade);
                mTvComment.setText(mBean.getEvaluate());
                break;
        }
    }

    private void sendComment(final CommentBean bean) {
        String json = GsonUtils.toJson(bean);
        RequestBody body = EncodeUtils.encodeInBody(json);
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().sendComment(body), new TypeToken<BaseDecodeBean<OnlyCodeBean>>() {
                }.getType(),
                new NetListener<List<OnlyCodeBean>>() {
                    @Override
                    public void onSuccess(List<OnlyCodeBean> list) {
                        ToastUtils.showCommonToast(getResources().getString(R.string.comment_success));
                        mLinOperation.setVisibility(View.INVISIBLE);
                        mLinRate.setVisibility(View.VISIBLE);
                        mRatingBar.setIsIndicator(true);
                        mRatingBar.setRating(bean.getEvaluateGrade()/2);
                        mTvState.setVisibility(View.VISIBLE);
                        mTvState.setText("已完成");
                        mTvState.setTextColor(getResources().getColor(R.color.color_order_complete));
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

    private void goService() {
        Intent intent = new Intent(this, ServiceActivity.class);
        startActivity(intent);
    }

    public void callPhone(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        startActivity(intent);
    }


    private void showCommentDialog() {
        CommentDialog dialog = new CommentDialog(this, mBean);
        dialog.show();
        dialog.setCommentListener(new CommentDialog.CommentListener() {
            @Override
            public void onComment(CommentBean bean) {
                sendComment(bean);
            }
        });
    }

    private void showAcceptDialog() {
        new QMUIDialog.MessageDialogBuilder(this).setMessage("确定要接受此订单吗？").addAction(getResources().getString(R.string.cancel),
                new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                }).addAction(getResources().getString(R.string.confirm), new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                acceptOrder();
                dialog.dismiss();
            }
        }).create().show();
    }

    private void showCancelDialog() {
        new QMUIDialog.MessageDialogBuilder(this).setMessage("确定要拒接此订单吗？").addAction(getResources().getString(R.string.cancel), new
                QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                }).addAction(getResources().getString(R.string.confirm), new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                cancelOrder();
                dialog.dismiss();
            }
        }).create().show();
    }

    private void acceptOrder() {
        showDialog();
        AcceptOrderBean bean = new AcceptOrderBean();
        bean.setId(mBean.getId());
        bean.setToken(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
        String json = GsonUtils.toJson(bean);
        RequestBody body = EncodeUtils.encodeInBody(json);
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().acceptOrder(body), new TypeToken<BaseDecodeBean<List<OnlyCodeBean>>>() {
        }.getType(), new NetListener<List<OnlyCodeBean>>() {
            @Override
            public void onSuccess(List<OnlyCodeBean> list) {
                dismissDialog();
                ToastUtils.showCommonToast("接单成功");
                mLinOperation.setVisibility(View.INVISIBLE);
                mTvState.setVisibility(View.VISIBLE);
                mTvState.setTextColor(getResources().getColor(R.color.color_order_green));
                mTvState.setText("已接单");
            }

            @Override
            public void onFailed(int errCode) {
                dismissDialog();
                ToastUtils.showCommonToast("接单失败");
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


    private void cancelOrder() {
        showDialog();
        CancelOrder cancelOrder = new CancelOrder();
        cancelOrder.setId(mBean.getId());
        cancelOrder.setToken(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
        String json = GsonUtils.toJson(cancelOrder);
        RequestBody body = EncodeUtils.encodeInBody(json);
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().cancelOrder(body), new TypeToken<BaseDecodeBean<List<OnlyCodeBean>>>() {
        }.getType(), new NetListener<List<OnlyCodeBean>>() {
            @Override
            public void onSuccess(List<OnlyCodeBean> list) {
                dismissDialog();
                ToastUtils.showCommonToast("订单已取消");
                mLinOperation.setVisibility(View.INVISIBLE);
                mTvState.setVisibility(View.VISIBLE);
                mTvState.setTextColor(getResources().getColor(R.color.red));
                mTvState.setText("已取消");
            }

            @Override
            public void onFailed(int errCode) {
                dismissDialog();
                ToastUtils.showCommonToast("取消订单失败!");
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

    @Override
    protected void parseIntent() {
        super.parseIntent();

        if (getIntent() != null) {
            AllOrderBean bean = (AllOrderBean) getIntent().getSerializableExtra(IntentConstant.INTENT_ALL_ORDER);
            if (bean != null) {
                String targetId = bean.getTargetId();
                mIsHost = TextUtils.equals(targetId, SPUtils.getInstance().getString(SpConstant.MY_USER_ID));
                mBean = bean;
            } else {
                ToastUtils.showCommonToast(getResources().getString(R.string.data_error));
                this.finish();
            }
        } else {
            ToastUtils.showCommonToast(getResources().getString(R.string.data_error));
            this.finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_chat:
                if (mIsHost) {
                    RongIM.getInstance().startPrivateChat(this, mBean.getUserId(), mBean.getName());
                } else {
                    RongIM.getInstance().startPrivateChat(this, mBean.getTargetId(), mBean.getName());
                }
                break;
        }
    }
}

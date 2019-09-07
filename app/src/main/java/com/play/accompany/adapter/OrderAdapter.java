package com.play.accompany.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.play.accompany.R;
import com.play.accompany.bean.AllOrderBean;
import com.play.accompany.bean.CommentBean;
import com.play.accompany.bean.IntentPayInfo;
import com.play.accompany.bean.OrderState;
import com.play.accompany.bean.TopGameBean;
import com.play.accompany.constant.OrderConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.design.ColorText;
import com.play.accompany.design.CommentDialog;
import com.play.accompany.present.ApplicationListener;
import com.play.accompany.utils.DateUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.view.AccompanyApplication;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderHolder> {
    private Context mContext;
    private List<AllOrderBean> mList;
    private OrderListener mListener;
    private List<TopGameBean> mAllList;

    public OrderAdapter(Context context, List<AllOrderBean> list) {
        mContext = context;
        mList = list;
    }

    public void setOrderListener(OrderListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_order, viewGroup, false);
        return new OrderHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHolder orderHolder, int i) {
        orderHolder.bindItem(mList.get(i));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class OrderHolder extends RecyclerView.ViewHolder {
        RoundedImageView imgHead;
        TextView tvName;
        ColorText tvType;
        TextView tvTime;
        TextView tvPrice;
        TextView tvState;
        TextView tvTips;
        ImageView imgGet;
        TextView tvSpend;

         OrderHolder(@NonNull View itemView) {
            super(itemView);

            imgHead = itemView.findViewById(R.id.img_head);
            tvName = itemView.findViewById(R.id.tv_name);
            tvType = itemView.findViewById(R.id.tv_type);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvState = itemView.findViewById(R.id.tv_state);
            tvTips = itemView.findViewById(R.id.tv_tips);
            imgGet = itemView.findViewById(R.id.img_get);
            tvSpend = itemView.findViewById(R.id.tv_spend);
        }

        void bindItem(final AllOrderBean bean) {
            final boolean isHost;
            String targetId = bean.getTargetId();
            String id = SPUtils.getInstance().getString(SpConstant.MY_USER_ID);
            if (TextUtils.equals(id, targetId)) {
                isHost = true;
            } else {
                isHost = false;
            }
            String url = bean.getUrl();
            if (!TextUtils.isEmpty(url)) {
                Glide.with(itemView.getContext()).load(url).into(imgHead);
                imgHead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            if (isHost) {
                                mListener.onHeadClick(bean.getUserId());
                            } else {
                                mListener.onHeadClick(bean.getTargetId());
                            }
                        }
                    }
                });
            }
            tvName.setText(bean.getName());
            if (mAllList == null || mAllList.isEmpty()) {
                checkList(tvType, bean.getgameType());
            } else {
                setType(tvType, bean.getgameType());
            }
            tvTime.setText(DateUtils.time2Date(bean.getTime()));
            int price = bean.getPrice();
            final int num = bean.getNum();
            int all = price * num;
            tvPrice.setText("【" + all + AccompanyApplication.getContext().getResources().getString(R.string.money) + "】");
            int state = bean.getState();
            OrderState orderState = OrderConstant.getOrderState(bean,state, isHost);
            if (orderState != null) {
                final int stateAction = orderState.getStateAction();
                String tip = orderState.getTip();
                if (!TextUtils.isEmpty(tip)) {
                    tvTips.setVisibility(View.VISIBLE);
                    tvTips.setText(tip);
                } else {
                    tvTips.setVisibility(View.GONE);
                }
                tvState.setBackgroundResource(orderState.getStateBackground());
                tvState.setText(orderState.getStateText());
                tvState.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickAction(itemView.getContext(),bean,stateAction);
                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onItemClick(bean);
                        }
                    }
                });
                tvSpend.setText(orderState.getSpend());
            }
            if (TextUtils.equals(SPUtils.getInstance().getString(SpConstant.MY_USER_ID), bean.getUserId())) {
                imgGet.setVisibility(View.GONE);
            } else {
                imgGet.setVisibility(View.VISIBLE);
            }
        }

        void clickAction(Context context,AllOrderBean bean, int action) {
            switch (action) {
                case OrderConstant.CLICK_JUMP_PAY:
                    goPay(bean);
                    break;
                case OrderConstant.CLICK_JUMP_WAIT:
                    pleaseWait();
                    break;
                case OrderConstant.CLICK_JUMP_SERVICE:
                    servicing();
                    break;
                case OrderConstant.CLICK_JUMP_COMMENT:
                    showCommentDialog(context,bean);
                    break;
                case OrderConstant.CLICK_JUMP_COMPLETE:
//                    Toast.makeText(itemView.getContext(), "评价已完成，后续开发中", Toast.LENGTH_SHORT).show();
                    showCommentDialog(context, bean);
                    break;
                case OrderConstant.CLICK_JUMP_ACCEPT:
                    orderNext(bean,AccompanyApplication.getContext().getResources()
                            .getString(R.string.accept_success),AccompanyApplication.getContext().getResources().getString(R.string.accept_failed));
                    break;
                case OrderConstant.CLICK_JUMP_SUBMIT:
                    orderNext(bean, AccompanyApplication.getContext().getResources()
                            .getString(R.string.submit_success), AccompanyApplication.getContext().getResources().getString(R.string.submit_failed));
                    break;
                case OrderConstant.CLICK_JUMP_ERROR:
                    Toast.makeText(AccompanyApplication.getContext(),AccompanyApplication.getContext().getResources()
                    .getString(R.string.order_state_error),Toast.LENGTH_SHORT).show();
                    break;
                default:

            }
        }

        void goPay(AllOrderBean bean) {
            String detail = bean.getPrice() + AccompanyApplication.getContext().getResources().getString(R.string.price) + "*" + bean.getNum();
            int all = (bean.getPrice()) * (bean.getNum());
            IntentPayInfo info = new IntentPayInfo(bean.getUrl(), bean.getName(), bean.getGameTypeName(), detail, all, bean.getId());
            if (mListener != null) {
                mListener.onPayClick(info);
            }
        }

        void pleaseWait() {
            Toast.makeText(AccompanyApplication.getContext(), AccompanyApplication.getContext().getResources().getString(R.string.order_tips_pay)
                    , Toast.LENGTH_SHORT).show();


        }

        void servicing() {
            Toast.makeText(AccompanyApplication.getContext(), AccompanyApplication.getContext().getResources().getString(R.string.order_tips_click)
                    , Toast.LENGTH_SHORT).show();
        }

        void showCommentDialog(Context context,AllOrderBean bean) {
            CommentDialog dialog = new CommentDialog(context, bean);
            dialog.show();
            dialog.setCommentListener(new CommentDialog.CommentListener() {
                @Override
                public void onComment(CommentBean bean) {
                    if (mListener != null) {
                        mListener.onCommentClick(bean);
                    }
                }
            });
        }

        void orderNext(AllOrderBean bean,String success,String failed) {
            if (mListener != null) {
                mListener.onOrderNext(bean, success, failed);
            }
        }
    }

    private void setType(ColorText textView, int type) {
        for (TopGameBean bean : mAllList) {
            if (type == bean.getTypeId()) {
                textView.setColor(bean.getTagFront(), bean.getTagBg(), bean.getName());
                break;
            }
        }
    }

    private void checkList(final ColorText text, final int type) {
        if (mAllList == null || mAllList.isEmpty()) {
            AccompanyApplication.getGameList(new ApplicationListener.GameListListener() {
                @Override
                public void onGameListener(List<TopGameBean> list) {
                    mAllList = list;
                    setType(text, type);
                }
            });
        }
    }

    public interface OrderListener {
        void onItemClick(AllOrderBean bean);

        void onCommentClick(CommentBean bean);

        void onOrderNext(AllOrderBean bean, String success, String failed);

        void onPayClick(IntentPayInfo info);

        void onHeadClick(String uid);
    }

}


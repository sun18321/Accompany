package com.play.accompany.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.play.accompany.R;
import com.play.accompany.bean.AllOrderBean;
import com.play.accompany.bean.CommentBean;
import com.play.accompany.bean.IntentPayInfo;
import com.play.accompany.bean.OrderState;
import com.play.accompany.constant.OrderConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.design.CommentDialog;
import com.play.accompany.utils.DateUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.view.AccompanyApplication;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderHolder> {
    private Context mContext;
    private List<AllOrderBean> mList;
    private OrderListener mListener;

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
        TextView tvType;
        TextView tvTime;
        TextView tvPrice;
        TextView tvState;
        TextView tvTips;

        public OrderHolder(@NonNull View itemView) {
            super(itemView);

            imgHead = itemView.findViewById(R.id.img_head);
            tvName = itemView.findViewById(R.id.tv_name);
            tvType = itemView.findViewById(R.id.tv_type);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvState = itemView.findViewById(R.id.tv_state);
            tvTips = itemView.findViewById(R.id.tv_tips);
        }

        void bindItem(final AllOrderBean bean) {
            boolean isHost;
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
                            mListener.onHeadClick(bean.getTargetId());
                        }
                    }
                });
            }
            tvName.setText(bean.getName());
            tvType.setText(AccompanyApplication.getGameString(bean.getgameType()));
            tvTime.setText(DateUtils.time2Date(bean.getTime()));
            int price = bean.getPrice();
            final int num = bean.getNum();
            int all = price * num;
            String content = "(" + price + itemView.getContext().getResources().getString(R.string.price) + " * " + num + ")";
            String detail = itemView.getContext().getResources().getString(R.string.price_detail_place);
            String format = String.format(detail, content);
            String text = itemView.getContext().getResources().getString(R.string.all_money) + all + itemView.getContext().getResources().getString(R.string.money) + format;

            tvPrice.setText(Html.fromHtml(text));
            int state = bean.getState();
            OrderState orderState = OrderConstant.getOrderState(bean.getStartTime(),state, isHost);
            if (orderState != null) {
                final int stateAction = orderState.getStateAction();
                String tip = orderState.getTip();
                tvTips.setText(tip);
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
                    orderNext(bean.getId(),AccompanyApplication.getContext().getResources()
                            .getString(R.string.accept_success),AccompanyApplication.getContext().getResources().getString(R.string.accept_failed));
                    break;
                case OrderConstant.CLICK_JUMP_SUBMIT:
                    orderNext(bean.getId(), AccompanyApplication.getContext().getResources()
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
            IntentPayInfo info = new IntentPayInfo(bean.getUrl(), bean.getName(), AccompanyApplication.getGameString(bean.getgameType()),
                    detail, all, bean.getId());
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

        void orderNext(String id,String success,String failed) {
            if (mListener != null) {
                mListener.onOrderNext(id, success, failed);
            }
        }
    }

    public interface OrderListener {
        void onItemClick(AllOrderBean bean);

        void onCommentClick(CommentBean bean);

        void onOrderNext(String id, String success, String failed);

        void onPayClick(IntentPayInfo info);

        void onHeadClick(String uid);
    }

}


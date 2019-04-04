package com.play.accompany.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.play.accompany.R;
import com.play.accompany.bean.AllOrderBean;
import com.play.accompany.utils.DateUtils;
import com.play.accompany.utils.StringUtils;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderHolder> {
    private Context mContext;
    private List<AllOrderBean> mList;


    public OrderAdapter(Context context, List<AllOrderBean> list) {
        mContext = context;
        mList = list;
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

        public OrderHolder(@NonNull View itemView) {
            super(itemView);

            imgHead = itemView.findViewById(R.id.img_head);
            tvName = itemView.findViewById(R.id.tv_name);
            tvType = itemView.findViewById(R.id.tv_type);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvState = itemView.findViewById(R.id.tv_state);
        }

        void bindItem(AllOrderBean bean) {
            String url = bean.getUrl();
            if (!TextUtils.isEmpty(url)) {
                Glide.with(itemView.getContext()).load(url).into(imgHead);
            }
            tvName.setText(bean.getName());
            tvType.setText(StringUtils.getGameString(bean.getTypeGame()));
            tvTime.setText(DateUtils.time2Date(bean.getTime()));
            int price = bean.getPrice();
            int num = bean.getNum();
            int all = price * num;
            tvPrice.setText(all + itemView.getContext().getResources().getString(R.string.money) + "(" + price + itemView.getContext().getResources().getString(R.string.price) + " * " + num + ")");
            int state = bean.getState();
            if (state == 1) {
                tvState.setText("未支付");
            } else if (state == 3) {
                tvState.setText("已支付");
            } else if (state == 4) {
                tvState.setText("完成下单");
            }
        }
    }
}


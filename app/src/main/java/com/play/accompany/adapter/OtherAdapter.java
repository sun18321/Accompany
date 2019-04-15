package com.play.accompany.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.play.accompany.R;
import com.play.accompany.bean.TopGameBean;

import java.util.List;

public class OtherAdapter extends RecyclerView.Adapter<OtherAdapter.OtherHolder> {
    private List<TopGameBean> mList;
    private Context mContext;
    private OtherItemListener mListener;

    public OtherAdapter(Context context, List<TopGameBean> list) {
        mContext = context;
        mList = list;
    }

    public void setOtherListener(OtherItemListener listener) {
        mListener = listener;
    }


    @NonNull
    @Override
    public OtherHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_other_game, viewGroup, false);
        return new OtherHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OtherHolder otherHolder, int i) {
        otherHolder.bindItem(mList.get(i));
    }

    @Override
    public int getItemCount() {
        return mList == null || mList.isEmpty() ? 0 : mList.size();
    }

    class OtherHolder extends RecyclerView.ViewHolder {
        ImageView imgGame;
        TextView tvName;

         OtherHolder(@NonNull View itemView) {
            super(itemView);

             imgGame = itemView.findViewById(R.id.img_game);
             tvName = itemView.findViewById(R.id.tv_name);
        }

        void bindItem(final TopGameBean bean) {
            Glide.with(itemView.getContext()).load(bean.getUrl()).into(imgGame);
            tvName.setText(bean.getName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onItemClick(bean.getTypeId());
                    }
                }
            });
        }
    }

    public interface OtherItemListener {
        void onItemClick(int id);
    }
}


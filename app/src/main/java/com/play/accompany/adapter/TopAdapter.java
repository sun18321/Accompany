package com.play.accompany.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.play.accompany.R;
import com.play.accompany.bean.TopGameBean;

import java.util.List;

public class TopAdapter extends RecyclerView.Adapter<TopAdapter.TopGridHolder> {
    private Context mContext;
    private List<TopGameBean> mList;
    private topListener mListener;

    public TopAdapter(Context Context, List<TopGameBean> list) {
        mList = list;
        mContext = Context;
    }

    @NonNull
    @Override
    public TopGridHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_top, viewGroup, false);
        return new TopGridHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull TopGridHolder topGridHolder,  int i) {
        final int index = i;
        Glide.with(mContext).load(mList.get(i).getUrl()).into(topGridHolder.img);
        topGridHolder.text.setText(mList.get(i).getName());
        topGridHolder.lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    TopGameBean bean = mList.get(index);
                    mListener.itemClick(bean);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setTopListener(topListener listener) {
        mListener = listener;
    }

      class TopGridHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView text;
        LinearLayout lin;

         TopGridHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_game);
            text = itemView.findViewById(R.id.tv_game);
            lin = itemView.findViewById(R.id.top_lin);
        }
    }

    public interface topListener {
        void itemClick(TopGameBean bean);
    }


}

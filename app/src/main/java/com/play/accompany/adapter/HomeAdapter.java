package com.play.accompany.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.play.accompany.R;
import com.play.accompany.bean.BannerBean;
import com.play.accompany.bean.HomeBean;
import com.play.accompany.bean.TopGameBean;
import com.play.accompany.bean.UserInfo;
import com.play.accompany.design.BannerImageLoader;
import com.play.accompany.design.ColorfulTitle;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.StringUtils;
import com.play.accompany.view.AccompanyApplication;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;


public class HomeAdapter extends RecyclerView.Adapter {
    //除了陪玩列表，其他都算other 总共3个条目，other为2，以此类推
    private final int mOtherTypeCount = 2;

    private Context mContext;
    private final int mTypeTop = 1001;
    private final int mTypeContent = 1002;
    private final int mTypeBanner = 1003;
    private final int mGridCount = 5;
    private List<TopGameBean> mGameList = new ArrayList<>();
    private List<UserInfo> mContentList = new ArrayList<>();
    private List<BannerBean> mBannerList = new ArrayList<>();
    private List<String> mBannerImgList = new ArrayList<>();
    private HomeListener mListener;
    private int mSelectedGame = 0;

    public HomeAdapter(Context context, String json) {
        mContext = context;

        HomeBean homeBean = GsonUtils.fromJson(json, HomeBean.class);
        if (homeBean == null) {
            return;
        }
        HomeBean.OtherBean other = homeBean.getOther();
        if (other == null) {
            return;
        }
        mBannerList = other.getActivity();
        mGameList = other.getGameType();
        mContentList = homeBean.getMsg();
        if (!mBannerList.isEmpty()) {
            mBannerImgList.clear();
            for (BannerBean bannerBean : mBannerList) {
                mBannerImgList.add(bannerBean.getImgurl());
            }
        }
    }

    private TopGameBean getGameIndex() {
        if (mSelectedGame == 0) {
            return mGameList.get(0);
        }
        int index = 0;
        List<TopGameBean> list = AccompanyApplication.getGameList();
        if (list == null || list.isEmpty()) {
            list = mGameList;
        }

        for (int i = 0; i < list.size(); i++) {
            int typeId = list.get(i).getTypeId();
            if (typeId == mSelectedGame) {
                index = i;
                break;
            }
        }
        return list.get(index);
    }

    public void setGameQuery(List<UserInfo> list, int gameId) {
        mContentList = list;
        mSelectedGame = gameId;
        notifyDataSetChanged();

//        notifyItemRangeChanged(mOtherTypeCount, 1);
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == mTypeBanner) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_banner, viewGroup, false);
            return new BannerHolder(view);
        } else if (i == mTypeTop) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_many_top, viewGroup, false);
            return new TopViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_home, viewGroup, false);
            return new ContentViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof BannerHolder) {
            BannerHolder bannerHolder = (BannerHolder) viewHolder;
            bannerHolder.bindItem();
        } else if (viewHolder instanceof TopViewHolder) {
            TopViewHolder topViewHolder = (TopViewHolder) viewHolder;
            topViewHolder.recyclerView.setLayoutManager(new GridLayoutManager(mContext, mGridCount, GridLayoutManager.VERTICAL, false));
            TopAdapter topAdapter = new TopAdapter(mContext, mGameList);
            topViewHolder.recyclerView.setAdapter(topAdapter);
            topAdapter.setTopListener(new TopAdapter.topListener() {
                @Override
                public void itemClick(TopGameBean bean) {
                    if (mListener != null) {
                        mListener.onTopCLick(bean);
                    }
                }
            });
            TopGameBean gameBean  = getGameIndex();
            topViewHolder.colorfulTitle.setColor(gameBean.getTagFront(), gameBean.getTagBg(), gameBean.getName());
        } else {
            ContentViewHolder contentViewHolder = (ContentViewHolder) viewHolder;
            contentViewHolder.bindItem(mContentList.get(i - mOtherTypeCount));
        }
    }

    @Override
    public int getItemCount() {
        return mContentList.size() + mOtherTypeCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return mTypeBanner;
        } else if (position == 1) {
            return mTypeTop;
        } else {
            return mTypeContent;
        }
    }

    public void setHomeListener(HomeListener listener) {
        mListener = listener;
    }

    public class BannerHolder extends RecyclerView.ViewHolder {
        private Banner mBanner;

        BannerHolder(@NonNull View itemView) {
            super(itemView);
            mBanner = itemView.findViewById(R.id.banner);
        }

        void bindItem() {
            mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
            mBanner.setImageLoader(new BannerImageLoader());
            mBanner.setImages(mBannerImgList);
            mBanner.setBannerAnimation(Transformer.DepthPage);
            mBanner.isAutoPlay(true);
            mBanner.setDelayTime(5000);
            mBanner.setIndicatorGravity(BannerConfig.RIGHT);
            mBanner.setOnBannerListener(new OnBannerListener() {
                @Override
                public void OnBannerClick(int position) {
                    if (mListener != null) {
                        mListener.onBannerClick(mBannerList.get(position).getUrl());
                    }
                }
            });
            mBanner.start();
        }
    }



    public static class TopViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        ColorfulTitle colorfulTitle;

         TopViewHolder(@NonNull View itemView) {
            super(itemView);
             recyclerView = itemView.findViewById(R.id.recycler);
             colorfulTitle = itemView.findViewById(R.id.color_title);
         }
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder{
        ImageView imgHead;
        TextView tvName;
        TextView tvIntroduction;
        TextView tvPrice;
        TextView tvDistance;
        TextView tvGrade;
        TextView tvCount;
         ContentViewHolder(@NonNull View itemView) {
            super(itemView);
             imgHead = itemView.findViewById(R.id.head_image);
             tvName= itemView.findViewById(R.id.tv_name);
             tvIntroduction = itemView.findViewById(R.id.tv_introduction);
             tvPrice = itemView.findViewById(R.id.tv_price);
             tvDistance = itemView.findViewById(R.id.tv_distance);
             tvGrade = itemView.findViewById(R.id.tv_grade);
             tvCount = itemView.findViewById(R.id.tv_count);
        }

        void bindItem(final UserInfo info) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onItemClick(info);
                    }
                }
            });
            tvName.setText(info.getName());
            tvPrice.setText(info.getPrice() + mContext.getResources().getString(R.string.price));
            tvIntroduction.setText(info.getSign());
            Glide.with(itemView.getContext()).load(info.getUrl()).into(imgHead);
            tvDistance.setText(StringUtils.m2Km(info.getLbs(), info.getAddress()));
            tvGrade.setText(String.valueOf(info.getGrade()/2));
            tvCount.setText(String.valueOf(info.getOrderNum()) + "单");
        }
    }

    public interface HomeListener {
        void onTopCLick(TopGameBean bean);

        void onItemClick(UserInfo info);

        void onBannerClick(String url);
    }
}


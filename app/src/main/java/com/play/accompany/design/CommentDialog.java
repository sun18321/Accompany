package com.play.accompany.design;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.play.accompany.R;
import com.play.accompany.bean.AllOrderBean;
import com.play.accompany.bean.CommentBean;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.view.AccompanyApplication;

public class CommentDialog extends Dialog {
    private Context mContext;
    private String mId;
    private CommentListener mListener;
    private AllOrderBean mBean;

    public CommentDialog(@NonNull Context context, AllOrderBean bean) {
        super(context);

        mContext = context;
        mBean = bean;
        init();
    }

//    public CommentDialog(@NonNull Context context, String id) {
//        super(context);
//        mContext = context;
//        mId = id;
//        init();
//    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_comment, null);
        RoundedImageView headImg = view.findViewById(R.id.img_head);
        TextView tvName = view.findViewById(R.id.tv_name);
        TextView tvDetail= view.findViewById(R.id.tv_details);
        final EditText editComment = view.findViewById(R.id.edit_comment);
        final RatingBar ratingBar = view.findViewById(R.id.rate);


        if (mBean != null) {
            String url = mBean.getUrl();
            if (!TextUtils.isEmpty(url)) {
                Glide.with(mContext).load(url).into(headImg);
            }
            tvName.setText(mBean.getName());
            int all = (mBean.getNum()) * (mBean.getPrice());
            tvDetail.setText(all + AccompanyApplication.getContext().getResources().getString(R.string.money));
            mId = mBean.getId();
        }


        view.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    CommentBean bean = new CommentBean();
                    bean.setId(mId);
                    bean.setToken(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
                    float rating = ratingBar.getRating();
                    int score = (int) (rating * 2);
                    bean.setEvaluateGrade(score);
                    String comment = editComment.getText().toString();
                    bean.setEvaluate(comment);

                    mListener.onComment(bean);
                }

                if (CommentDialog.this.isShowing()) {
                    CommentDialog.this.dismiss();
                }
            }
        });
        view.findViewById(R.id.img_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommentDialog.this.isShowing()) {
                    CommentDialog.this.dismiss();
                }
            }
        });

        setContentView(view);
    }

    public void setCommentListener(CommentListener listener) {
        mListener = listener;
    }

    public interface CommentListener {
        void onComment(CommentBean bean);
    }
}

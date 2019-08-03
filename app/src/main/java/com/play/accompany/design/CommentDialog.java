package com.play.accompany.design;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.play.accompany.R;
import com.play.accompany.bean.AllOrderBean;
import com.play.accompany.bean.CommentBean;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.utils.EventUtils;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.ToastUtils;
import com.play.accompany.view.AccompanyApplication;

public class CommentDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private String mId;
    private CommentListener mListener;
    private AllOrderBean mBean;
    private EditText mEditComment;
    private RatingBar mRatingBar;
    private TextView mTvComment;
    private TextView mTvComplete;
    private Button mButtonSubmit;
    private boolean mIsComplete;


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
        mEditComment = view.findViewById(R.id.edit_comment);
        mRatingBar = view.findViewById(R.id.rate);
        mTvComment = view.findViewById(R.id.tv_comment);
        mTvComplete = view.findViewById(R.id.tv_complete);
        mButtonSubmit = view.findViewById(R.id.btn_submit);

        if (mBean == null) {
            ToastUtils.showErrorToast();
            return;
        }
        int grade = mBean.getEvaluateGrade();
        if (grade != 0) {
            mIsComplete = true;
        }

        if (mIsComplete) {
            mRatingBar.setIsIndicator(true);
            mTvComplete.setVisibility(View.VISIBLE);
            mTvComment.setVisibility(View.VISIBLE);
            mEditComment.setVisibility(View.GONE);
            mButtonSubmit.setText(mContext.getResources().getString(R.string.appeal));
            String evaluate = mBean.getEvaluate();
            float floatGrade = (float) grade / 2;
            mRatingBar.setRating(floatGrade);
            mTvComment.setText(evaluate);
        }else {
            mRatingBar.setIsIndicator(false);
            mTvComplete.setVisibility(View.GONE);
            mTvComment.setVisibility(View.GONE);
            mEditComment.setVisibility(View.VISIBLE);
            mButtonSubmit.setText(mContext.getResources().getString(R.string.submit_only));
        }
        String url = mBean.getUrl();
        if (!TextUtils.isEmpty(url)) {
            Glide.with(mContext).load(url).into(headImg);
        }
        tvName.setText(mBean.getName());
        int all = (mBean.getNum()) * (mBean.getPrice());
        tvDetail.setText(all + AccompanyApplication.getContext().getResources().getString(R.string.money));
        mId = mBean.getId();
        mButtonSubmit.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        if (mIsComplete) {
            ToastUtils.showCommonToast(mContext.getResources().getString(R.string.go_service));
        } else {
            if (mListener != null) {
                CommentBean bean = new CommentBean();
                bean.setId(mId);
                bean.setToken(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
                float rating = mRatingBar.getRating();
                int score = (int) (rating * 2);
                bean.setEvaluateGrade(score);
                String comment = mEditComment.getText().toString();
                bean.setEvaluate(comment);
                mListener.onComment(bean);

                EventUtils.getInstance().upOrderComment(SPUtils.getInstance().getString(SpConstant.MY_USER_ID), mBean.getTargetId(), mBean.getId(),
                        mBean.getName(), comment, String.valueOf(score), String.valueOf((mBean.getPrice() * mBean.getNum())), String.valueOf(mBean.getNum()));
            }
        }
        if (CommentDialog.this.isShowing()) {
            CommentDialog.this.dismiss();
        }
    }

    public void setCommentListener(CommentListener listener) {
        mListener = listener;
    }

    public interface CommentListener {
        void onComment(CommentBean bean);
    }
}

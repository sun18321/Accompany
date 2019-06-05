package com.play.accompany.view;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.bean.AllOrderBean;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.InviteCodeBean;
import com.play.accompany.bean.OnlyCodeBean;
import com.play.accompany.constant.AppConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.ToastUtils;

import java.util.List;

import okhttp3.RequestBody;

public class InviteCodeActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEditInvite;
    private String mInviteCode;
    private ImageView mImgSwitch;
    private RelativeLayout mLinExpand;
    private boolean mAnimating = false;
    private final long mAnimTime = 500;
    private boolean mIsExpand = false;
    private int mCurrentAngle = 0;
    private final int mRotateAngle = 180;
    private Animator mAnimatorExpand;
    private Animator mAnimatorCollapse;
    private TextView mTvTest;
    private int mHeight = 0;
    private TextView mTvSwitch;

    @Override
    protected int getLayout() {
        return R.layout.activity_invite_code;
    }

    @Override
    protected String getTag() {
        return "InviteCodeActivity";
    }

    @Override
    protected void initViews() {
        initToolbar(getResources().getString(R.string.invite_code));

        TextView tvInviteCode = findViewById(R.id.tv_invite);
        mInviteCode = SPUtils.getInstance().getString(SpConstant.MY_INVITE_CODE);
        tvInviteCode.setText(mInviteCode);
        tvInviteCode.setOnClickListener(this);

        mEditInvite = findViewById(R.id.edit_invite);
        findViewById(R.id.btn_submit).setOnClickListener(this);
        mImgSwitch = findViewById(R.id.img_switch);
        mLinExpand = findViewById(R.id.lin_expand);
        mTvSwitch = findViewById(R.id.tv_switch);
        mTvTest = findViewById(R.id.test);

        if (SPUtils.getInstance().getBoolean(SpConstant.INVITE_FLAG)) {
            mImgSwitch.setImageResource(R.drawable.invite_arrow_up);
            mLinExpand.setVisibility(View.INVISIBLE);
            mIsExpand = false;
        } else {
            mImgSwitch.setImageResource(R.drawable.invite_arrow_down);
            mLinExpand.setVisibility(View.VISIBLE);
            mIsExpand = true;
        }
        doAnimEnd();
        mImgSwitch.setOnClickListener(this);
        LogUtils.d("anim", "start expand:" + mIsExpand);


        mLinExpand.setPivotY(0);
        mAnimatorExpand = AnimatorInflater.loadAnimator(this, R.animator.invite_expand);
        mAnimatorExpand.setTarget(mLinExpand);
        mAnimatorExpand.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mAnimating = true;
                mLinExpand.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimating = false;
                mIsExpand = !mIsExpand;
                mLinExpand.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        mAnimatorCollapse = AnimatorInflater.loadAnimator(this, R.animator.invite_collapse);
        mAnimatorCollapse.setTarget(mLinExpand);
        mAnimatorCollapse.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimating = false;
                mIsExpand = !mIsExpand;
                mLinExpand.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void startAnim() {
        if (mCurrentAngle == 360) {
            mCurrentAngle = 0;
        }
        ObjectAnimator rotation = ObjectAnimator.ofFloat(mImgSwitch, "rotation", mCurrentAngle, mCurrentAngle + mRotateAngle).setDuration(mAnimTime);
        rotation.start();
        if (mIsExpand) {
//            mAnimatorCollapse.start();
            collapseView();
        } else {
//            mAnimatorExpand.start();
            expandView();
        }
        mCurrentAngle += mRotateAngle;
    }

    private void expandView() {
        ObjectAnimator expand = ObjectAnimator.ofFloat(mLinExpand, "translationY", -mHeight, 0).setDuration(1500);
        expand.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mAnimating = true;
                mLinExpand.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimating = false;
                mIsExpand = !mIsExpand;
                doAnimEnd();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        expand.setInterpolator(new BounceInterpolator());
        expand.start();
    }

    private void collapseView() {
        ObjectAnimator collapse = ObjectAnimator.ofFloat(mLinExpand, "translationY", 0, -mHeight);
        collapse.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimating = false;
                mIsExpand = !mIsExpand;
                mLinExpand.setVisibility(View.INVISIBLE);
                doAnimEnd();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        collapse.start();
    }

    private void doAnimEnd() {
        if (mIsExpand) {
            mTvSwitch.setText("点击收起");
        } else {
            mTvSwitch.setText("显示隐藏");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (mLinExpand != null) {
            mHeight = mLinExpand.getHeight();
        }

        LogUtils.d("expand", "resume height:" + mHeight);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        if (mLinExpand != null) {
            mHeight = mLinExpand.getHeight();
        }

        LogUtils.d("expand", "post resume height:" + mHeight);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_invite:
                copyInviteCode();
                break;
            case R.id.btn_submit:
                submitCode();
                break;
            case R.id.img_switch:
                if (mLinExpand != null) {
                    mHeight = mLinExpand.getHeight();
                }

                LogUtils.d("expand", "click height:" + mHeight);

                if (!mAnimating) {
                    startAnim();
                }
                break;
        }
    }

    private void copyInviteCode() {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Label", mInviteCode);
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(clip);
            ToastUtils.showCommonToast(getResources().getString(R.string.copy_invite_success));
        }
    }

    private void submitCode() {
        String code = mEditInvite.getText().toString();
        if (TextUtils.equals(code, mInviteCode)) {
            ToastUtils.showCommonToast(getResources().getString(R.string.invite_is_me));
            return;
        }

        if (TextUtils.isEmpty(code)) {
            ToastUtils.showCommonToast(getResources().getString(R.string.not_null_invite));
            return;
        }

        boolean flag = SPUtils.getInstance().getBoolean(SpConstant.INVITE_FLAG);
        if (flag) {
            ToastUtils.showCommonToast(getResources().getString(R.string.invite_inputed));
            return;
        }

        String token = SPUtils.getInstance().getString(SpConstant.APP_TOKEN);
        InviteCodeBean bean = new InviteCodeBean(token, code);
        String json = GsonUtils.toJson(bean);
        RequestBody body = EncodeUtils.encodeInBody(json);
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().inviteCode(body), new TypeToken<BaseDecodeBean<List<OnlyCodeBean>>>() {
        }.getType(), new NetListener<List<OnlyCodeBean>>() {
            @Override
            public void onSuccess(List<OnlyCodeBean> list) {
                if (list.isEmpty()) {
                    return;
                }
                OnlyCodeBean codeBean = list.get(0);
                if (codeBean.getCode() == AppConstant.RESPONSE_SUCCESS) {
                    ToastUtils.showCommonToast(getResources().getString(R.string.invite_success));
                    SPUtils.getInstance().put(SpConstant.INVITE_FLAG, true);
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

            }
        });

    }
}

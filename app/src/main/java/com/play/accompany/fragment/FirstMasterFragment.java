package com.play.accompany.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.reflect.TypeToken;
import com.play.accompany.R;
import com.play.accompany.base.BaseFragment;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.MasterBean;
import com.play.accompany.bean.OnlyCodeBean;
import com.play.accompany.bean.TopGameBean;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.design.TypeDialog;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.utils.AppUtils;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.FileSaveUtils;
import com.play.accompany.utils.GlideUtils;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.StringUtils;
import com.play.accompany.utils.ThreadPool;
import com.play.accompany.utils.ToastUtils;
import com.play.accompany.view.AccompanyApplication;
import com.play.accompany.view.MasterActivity;
import com.play.accompany.view.RuleActivity;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

import java.util.List;
import okhttp3.RequestBody;

public class FirstMasterFragment extends BaseFragment implements View.OnClickListener {

    private EditText mEditName;
    private EditText mEditId;
    private EditText mEditWeChat;
    private TextView mTvType;
    private List<TopGameBean> mDataList;
    private EditText mEditPhone;
    private TextView mTvMaster;
    private ImageView mImageAddSkill;
    private static FirstMasterFragment mFragment;
    private int mType = -1;
    private TextView mTvWait;
    private Button mBtnSubmit;
    private String mMasterImage = null;

    public static FirstMasterFragment getInstance(int type) {
        if (mFragment == null) {
            mFragment = new FirstMasterFragment();
        }
        Bundle bundle = new Bundle();
        bundle.putInt(OtherConstant.FIRST_MASTER_FRAGMENT_TYPE, type);
        mFragment.setArguments(bundle);
        return mFragment;
    }


    @Override
    protected int getLayout() {
        return R.layout.fragment_first_master;
    }

    @Override
    protected void initViews(View view) {
        if (getArguments() != null) {
            mType = getArguments().getInt(OtherConstant.FIRST_MASTER_FRAGMENT_TYPE);
        }
        mBtnSubmit = view.findViewById(R.id.btn_submit);
        mBtnSubmit.setOnClickListener(this);
        view.findViewById(R.id.lin_type).setOnClickListener(this);
        mTvMaster = view.findViewById(R.id.tv_master);
        mEditName = view.findViewById(R.id.edit_name);
        mEditId = view.findViewById(R.id.edit_id);
        mEditWeChat = view.findViewById(R.id.edit_wechat);
        mEditPhone = view.findViewById(R.id.edit_phone);
        mTvType = view.findViewById(R.id.tv_type);
        mDataList = AccompanyApplication.getGameList();
        mTvWait = view.findViewById(R.id.tv_wait);
        mImageAddSkill = view.findViewById(R.id.add_skill);
        mImageAddSkill.setOnClickListener(this);

        getData();
        initTip();

        LogUtils.d("master", "type:" + mType);
        if (mType == OtherConstant.USER_TYPE_WAIT) {
            getCheckInfo();
        }
    }

    private void setAfterView(MasterBean bean) {
        mEditName.setText(bean.getApplyName());
        mEditPhone.setText(bean.getApplyPhone());
        mEditId.setText(bean.getApplyIdentity());
        mEditWeChat.setText(bean.getApplyAccount());
        mTvType.setText(bean.getGameTypeName());
        GlideUtils.commonLoad(mContext, bean.getUrlGameType(), mImageAddSkill);
        setComplete();
    }

    private void setComplete() {
        mTvWait.setVisibility(View.VISIBLE);
        mEditWeChat.setEnabled(false);
        mEditId.setEnabled(false);
        mEditName.setEnabled(false);
        mEditPhone.setEnabled(false);
        mTvType.setEnabled(false);
        mBtnSubmit.setText("已提交");
        mBtnSubmit.setBackgroundResource(R.drawable.already_gray_background);
        mBtnSubmit.setClickable(false);
    }

    @Override
    protected String getFragmentName() {
        return "FirstMasterFragment";
    }

    private void getData() {
        mDataList = AccompanyApplication.getGameList();
        if (mDataList == null || mDataList.isEmpty()) {
            getListFromDisk();
        }
    }

    private void getListFromDisk() {
        ThreadPool.newInstance().add(new Runnable() {
            @Override
            public void run() {
                String data = FileSaveUtils.getInstance().getData(OtherConstant.FILE_GAME);
                mDataList = GsonUtils.fromJson(data, new TypeToken<List<TopGameBean>>() {
                }.getType());
            }
        });
    }

    private void initTip() {
        String stringAll = getResources().getString(R.string.tips_master);
        final String stringIndex = getResources().getString(R.string.master_index);
        int index = stringAll.indexOf(stringIndex);
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(mContext, RuleActivity.class);
                intent.putExtra(IntentConstant.INTENT_TITLE, getResources().getString(R.string.master_index));
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);

                ds.setColor(getResources().getColor(R.color.colorPrimary));
            }
        };
        SpannableString spannableString = new SpannableString(stringAll);
        spannableString.setSpan(span, index, index + stringIndex.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvMaster.setText(spannableString);
        mTvMaster.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setData(TopGameBean bean) {
        mType = bean.getTypeId();
        mTvType.setText(bean.getName());
    }

    private void upData() {
        String name = mEditName.getText().toString();
        String phone = mEditPhone.getText().toString();
        String weChat = mEditWeChat.getText().toString();
        String id = mEditId.getText().toString();

        if (mMasterImage == null) {
            ToastUtils.showCommonToast(getResources().getString(R.string.master_image_first));
            return;
        }

        if (mType == -1) {
            ToastUtils.showCommonToast(getResources().getString(R.string.game_type_first));
            return;
        }

        if (TextUtils.isEmpty(name)) {
            ToastUtils.showCommonToast(getResources().getString(R.string.real_name_first));
            return;
        }

        if (!AppUtils.isMobileNumber(phone)) {
            ToastUtils.showCommonToast(getResources().getString(R.string.phone_first));
            return;
        }

        if (TextUtils.isEmpty(weChat)) {
            ToastUtils.showCommonToast(getResources().getString(R.string.wechat_first));
            return;
        }

        if (!AppUtils.isIdentity(id)) {
            ToastUtils.showCommonToast(getResources().getString(R.string.id_first));
            return;
        }

        MasterBean bean = new MasterBean();
        bean.setToken(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
        bean.setApplyName(name);
        bean.setApplyAccount(weChat);
        bean.setApplyIdentity(id);
        bean.setApplyPhone(phone);
        bean.setGameType(mType);
        bean.setUrlGameType(mMasterImage);
        String json = GsonUtils.toJson(bean);
        RequestBody body = EncodeUtils.encodeInBody(json);
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().applyMaster(body), new TypeToken<BaseDecodeBean<List<OnlyCodeBean>>>() {
                }.getType(),
                new NetListener<List<OnlyCodeBean>>() {
                    @Override
                    public void onSuccess(List<OnlyCodeBean> list) {
                        ToastUtils.showCommonToast(AccompanyApplication.getContext().getResources().getString(R.string.submit_success));
                        SPUtils.getInstance().put(SpConstant.USER_TYPE, OtherConstant.USER_TYPE_WAIT);
                        mType = OtherConstant.USER_TYPE_WAIT;
                        setComplete();
                    }

                    @Override
                    public void onFailed(int errCode) {
                        ToastUtils.showCommonToast(AccompanyApplication.getContext().getResources().getString(R.string.submit_failed));
                    }

                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getMasterPicture() {
        MasterActivity activity = (MasterActivity) mActivity;
        activity.getPicture(new MasterActivity.PictureListener() {
            @Override
            public void picturePath(String path) {
                GlideUtils.commonLoad(mContext, path, mImageAddSkill);
                mMasterImage = StringUtils.imageChangeUpload(path);
            }
        });
    }

    private void getCheckInfo() {
        RequestBody body = EncodeUtils.encodeToken();
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().getCheckMasterInfo(body), new TypeToken<BaseDecodeBean<List<MasterBean>>>() {
                }.getType(), new NetListener<List<MasterBean>>() {
                    @Override
                    public void onSuccess(List<MasterBean> list) {
                        if (list.isEmpty()) {
                            return;
                        }
                        MasterBean masterBean = list.get(0);
                        setAfterView(masterBean);
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

    @Override
    public void onClick(View v) {
        if (mType == OtherConstant.USER_TYPE_WAIT) {
            ToastUtils.showCommonToast("等待审核中");
            return;
        }
        switch (v.getId()) {
            case R.id.lin_type:
                if (mDataList == null || mDataList.isEmpty()) {
                    ToastUtils.showCommonToast(getResources().getString(R.string.data_error));
                    return;
                }
                TopGameBean bean = mDataList.get(mDataList.size() - 1);
                if (bean.getTypeId() == OtherConstant.TYPE_OTHER) {
                    mDataList.remove(mDataList.size() - 1);
                }
                final TypeDialog dialogBuild = new TypeDialog(mContext, mDataList, new TypeDialog.SelectListenr() {
                    @Override
                    public void onSelect(TopGameBean bean) {
                        setData(bean);
                    }
                });
                dialogBuild.create().show();
                break;
            case R.id.btn_submit:
                upData();
                break;
            case R.id.add_skill:
                getMasterPicture();
                break;
        }
    }

}

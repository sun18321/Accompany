package com.play.accompany.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.MasterBean;
import com.play.accompany.bean.OnlyCodeBean;
import com.play.accompany.bean.Token;
import com.play.accompany.bean.TopGameBean;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.design.TypeDialog;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.StringUtils;
import com.play.accompany.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.RequestBody;

public class MasterActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEditName;
    private EditText mEditId;
    private EditText mEditWeChat;
    private TextView mTvType;
    private RelativeLayout mRlRequest;
    private RelativeLayout mRlWait;
    private List<TopGameBean> mDataList;
    private Set<Integer> mSelectSet = new HashSet<>();
    private List<Integer> mTypeList = new ArrayList<>();
    private List<String> mDisplayList = new ArrayList<>();
    private EditText mEditPhone;
    private TextView mTvMaster;

    @Override
    protected int getLayout() {
        return R.layout.activity_master;
    }

    @Override
    protected String getTag() {
        return "MasterActivity";
    }

    @Override
    protected void initViews() {
        initToolbar(getResources().getString(R.string.master));

        findViewById(R.id.btn_submit).setOnClickListener(this);
        findViewById(R.id.lin_type).setOnClickListener(this);
        mTvMaster = findViewById(R.id.tv_master);
        mEditName = findViewById(R.id.edit_name);
        mEditId = findViewById(R.id.edit_id);
        mEditWeChat = findViewById(R.id.edit_wechat);
        mEditPhone = findViewById(R.id.edit_phone);
        mTvType = findViewById(R.id.tv_type);
        mRlRequest = findViewById(R.id.rl_request);
        mRlWait = findViewById(R.id.rl_wait);

        initTip();

        int type = SPUtils.getInstance().getInt(SpConstant.USER_TYPE, 1);
        if (type == OtherConstant.USER_TYPE_COMMON) {
            mRlWait.setVisibility(View.INVISIBLE);
            mDataList = AccompanyApplication.getmGameList();
            if (mDataList == null || mDataList.isEmpty()) {
                getData();
            }
        } else if (type == OtherConstant.USER_TYPE_WAIT) {
            mRlWait.setVisibility(View.VISIBLE);
        }
    }

    private void initTip() {
        String stringAll = getResources().getString(R.string.tips_master);
        final String stringIndex = getResources().getString(R.string.master_index);
        int index = stringAll.indexOf(stringIndex);
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(MasterActivity.this, RuleActivity.class);
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

    private void getData() {
        showDialog();
        Token token = new Token(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
        String json = GsonUtils.toJson(token);
        RequestBody body = EncodeUtils.encodeInBody(json);
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().getAllGame(body), new TypeToken<BaseDecodeBean<List<TopGameBean>>>() {
        }.getType(), new NetListener<List<TopGameBean>>() {
            @Override
            public void onSuccess(List<TopGameBean> list) {
                dismissDialog();
                if (list.isEmpty()) {
                    return;
                }
                mDataList = list;
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

    private void setData() {
        mTypeList.clear();
        mDisplayList.clear();
        for (Integer integer : mSelectSet) {
            mTypeList.add(mDataList.get(integer).getTypeId());
            mDisplayList.add(mDataList.get(integer).getName());
        }
        mTvType.setText(StringUtils.GameList2String(mDisplayList));
    }

    private void upData() {
        String name = mEditName.getText().toString();
        String phone = mEditPhone.getText().toString();
        String weChat = mEditWeChat.getText().toString();
        String id = mEditId.getText().toString();

        if (mTypeList.isEmpty()) {
            ToastUtils.showCommonToast(getResources().getString(R.string.game_type_first));
            return;
        }

        if (TextUtils.isEmpty(name)) {
            ToastUtils.showCommonToast(getResources().getString(R.string.real_name_first));
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showCommonToast(getResources().getString(R.string.phone_first));
            return;
        }

        if (TextUtils.isEmpty(weChat)) {
            ToastUtils.showCommonToast(getResources().getString(R.string.wechat_first));
            return;
        }

        if (TextUtils.isEmpty(id)) {
            ToastUtils.showCommonToast(getResources().getString(R.string.id_first));
            return;
        }

        MasterBean bean = new MasterBean();
        bean.setToken(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
        bean.setApplyName(name);
        bean.setApplyAccount(weChat);
        bean.setApplyIdentity(id);
        bean.setApplyPhone(phone);
        bean.setGameType(mTypeList);
        String json = GsonUtils.toJson(bean);
        RequestBody body = EncodeUtils.encodeInBody(json);
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().applyMaster(body), new TypeToken<BaseDecodeBean<List<OnlyCodeBean>>>() {
                }.getType(),
                new NetListener<List<OnlyCodeBean>>() {
                    @Override
                    public void onSuccess(List<OnlyCodeBean> list) {
                        ToastUtils.showCommonToast(MasterActivity.this.getResources().getString(R.string.submit_success));
                        SPUtils.getInstance().put(SpConstant.USER_TYPE, OtherConstant.USER_TYPE_WAIT);
                        mRlRequest.setVisibility(View.VISIBLE);
                        mRlWait.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailed(int errCode) {
                        ToastUtils.showCommonToast(MasterActivity.this.getResources().getString(R.string.submit_failed));
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
        switch (v.getId()) {
            case R.id.lin_type:
                if (mDataList == null || mDataList.isEmpty()) {
                    ToastUtils.showCommonToast(getResources().getString(R.string.data_error));
                    return;
                }
                final TypeDialog dialogBuild = new TypeDialog(this, mDataList,mSelectSet);
                dialogBuild.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSelectSet = dialogBuild.getSelectSet();
                        setData();
                    }
                });

                dialogBuild.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialogBuild.create().show();
                break;
            case R.id.btn_submit:
                upData();
                break;
        }
    }
}

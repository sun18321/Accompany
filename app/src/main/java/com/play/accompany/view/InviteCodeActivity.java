package com.play.accompany.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
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
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.ToastUtils;

import java.util.List;

import okhttp3.RequestBody;

public class InviteCodeActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEditInvite;
    private String mInviteCode;

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

        mEditInvite = findViewById(R.id.edit_invite);
        findViewById(R.id.btn_copy).setOnClickListener(this);
        findViewById(R.id.btn_submit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_copy:
                copyInviteCode();
                break;
            case R.id.btn_submit:
                submitCode();
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

package com.play.accompany.view;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;

import com.google.gson.reflect.TypeToken;
import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.FindUserBean;
import com.play.accompany.bean.Token;
import com.play.accompany.bean.TopGameBean;
import com.play.accompany.bean.UserInfo;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.fragment.FirstMasterFragment;
import com.play.accompany.fragment.SecondMasterFragment;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.utils.AppUtils;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.StringUtils;
import com.play.accompany.utils.ToastUtils;
import com.yalantis.ucrop.UCrop;

import java.util.List;

import okhttp3.RequestBody;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MasterActivity extends BaseActivity {

    private PictureListener mListener;

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
        getUserType();
    }

    private void initFragment(int type) {

        SPUtils.getInstance().put(SpConstant.USER_TYPE, type);

        if (type == OtherConstant.USER_TYPE_ACCOMPANY) {
            SecondMasterFragment secondMasterFragment = SecondMasterFragment.getInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame,secondMasterFragment).commitAllowingStateLoss();
        } else {
            FirstMasterFragment firstFragment = FirstMasterFragment.getInstance(type);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame,firstFragment).commitAllowingStateLoss();
        }
    }

    public void getPicture(PictureListener listener) {
        mListener = listener;
        checkPermission();
    }

    private void getUserType() {
        FindUserBean bean = new FindUserBean();
        bean.setFindId(SPUtils.getInstance().getString(SpConstant.MY_USER_ID));
        bean.setToken(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
        RequestBody body = EncodeUtils.encodeInBody(GsonUtils.toJson(bean));
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().getUserInfo(body), new TypeToken<BaseDecodeBean<List<UserInfo>>>() {
        }.getType(), new NetListener<List<UserInfo>>() {
            @Override
            public void onSuccess(List<UserInfo> list) {
                if (list.isEmpty()) {
                    ToastUtils.showCommonToast(getResources().getString(R.string.data_error));
                    MasterActivity.this.finish();
                    return;
                }
                UserInfo info = list.get(0);
                Integer type = info.getType();
                initFragment(type);
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

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void checkPermission() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, OtherConstant.PICTURE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MasterActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == OtherConstant.PICTURE_REQUEST_CODE) {
                if (data != null) {
                    AppUtils.goCrop(data.getData(), this);
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                if (data == null) {
                    return;
                }
                Uri output = UCrop.getOutput(data);
                String path = StringUtils.uri2Path(output);
                if (mListener != null) {
                    mListener.picturePath(path);
                }
            }
        }
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

    public interface PictureListener {
        void picturePath(String path);
    }
}

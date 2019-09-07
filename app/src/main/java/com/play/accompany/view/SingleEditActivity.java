package com.play.accompany.view;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.core.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;
import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.HeadImageBean;
import com.play.accompany.bean.ResponseImage;
import com.play.accompany.bean.UserInfo;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.design.DetailLayout;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.utils.AppUtils;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.GlideUtils;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.StringUtils;
import com.play.accompany.utils.ToastUtils;
import com.play.accompany.utils.UserInfoDatabaseUtils;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.List;

import okhttp3.RequestBody;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class SingleEditActivity extends BaseActivity implements View.OnClickListener {
    private final int request_code_album = 1001;
    private final int request_code_camera = 1002;
    private final int request_code_edit = 1003;

    private UserInfo mUserInfo;
    private DetailLayout mDetailName;
    private DetailLayout mDetailId;
    private DetailLayout mDetailGender;
    private DetailLayout mDetailBirthday;
    private DetailLayout mDetailMore;
    private RoundedImageView mHeadImg;
    private BottomSheetDialog mImgDialog;
    private File mFile;

    @Override
    protected int getLayout() {
        return R.layout.activity_single_edit;
    }

    @Override
    protected String getTag() {
        return "SingleEditActivity";
    }

    @Override
    protected void initViews() {
        initToolbar("编辑");

        mDetailName = findViewById(R.id.detail_name);
        mDetailId = findViewById(R.id.detail_id);
        mDetailGender = findViewById(R.id.detail_gender);
        mDetailBirthday = findViewById(R.id.detail_birth);
        mDetailMore = findViewById(R.id.detail_more);
        mHeadImg = findViewById(R.id.img_head);

        mDetailName.setOnClickListener(this);
        mDetailId.setOnClickListener(this);
        mDetailGender.setOnClickListener(this);
        mDetailBirthday.setOnClickListener(this);
        mDetailMore.setOnClickListener(this);
        mHeadImg.setOnClickListener(this);
        findViewById(R.id.detail_sound).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SingleEditActivity.this,SoundSettingActivity.class));
            }
        });

        setViews();
    }

    private void setViews() {
        GlideUtils.commonLoad(this, mUserInfo.getUrl(), mHeadImg);
        mDetailName.setTvDetail(mUserInfo.getName());
        mDetailId.setTvDetail(mUserInfo.getUserName());
        mDetailBirthday.setTvDetail(mUserInfo.getDate());
        int gender = mUserInfo.getGender();
        String sex;
        if (gender == OtherConstant.GENDER_MALE) {
            sex = "男";
        } else {
            sex = "女";
        }
        mDetailGender.setTvDetail(sex);
        if (SPUtils.getInstance().getBoolean(SpConstant.ID_IS_EDITED, true)) {
            mDetailId.setArrowInvisiable();
        }
    }

    @Override
    protected void parseIntent() {
        super.parseIntent();

        if (getIntent() == null) {
            ToastUtils.showCommonToast(getResources().getString(R.string.data_error));
            this.finish();
            return;
        }
        mUserInfo = (UserInfo) getIntent().getSerializableExtra(IntentConstant.INTENT_USER);
    }

    @Override
    public void onClick(View v) {
        LogUtils.d(getTag(), "click:" + v.getId() + "name:" + R.id.detail_name);

        if (v.getId() == R.id.detail_more) {
            Intent intent = new Intent(this, MoreDetailActivity.class);
            intent.putExtra(IntentConstant.INTENT_USER, mUserInfo);
            startActivity(intent);
            return;
        } else if (v.getId() == R.id.img_head) {
            showImgSelect();
            return;
        }

        Intent intent = new Intent(this, EditDetailActivity.class);
        intent.putExtra(IntentConstant.INTENT_USER, mUserInfo);
        switch (v.getId()) {
            case R.id.detail_name:
                intent.putExtra(OtherConstant.DETAIL_TYPE, EditDetailActivity.TYPE_NAME);
                break;
            case R.id.detail_id:
                if (SPUtils.getInstance().getBoolean(SpConstant.ID_IS_EDITED, true)) {
                    ToastUtils.showCommonToast("您已修改过ID，不可再次更改");
                    return;
                }
                intent.putExtra(OtherConstant.DETAIL_TYPE, EditDetailActivity.TYPE_ID);
                break;
            case R.id.detail_birth:
                intent.putExtra(OtherConstant.DETAIL_TYPE, EditDetailActivity.TYPE_BIRTHDAY);
                break;
            case R.id.detail_gender:
                intent.putExtra(OtherConstant.DETAIL_TYPE, EditDetailActivity.TYPE_GENDER);
                break;
        }
        startActivityForResult(intent, request_code_edit);
    }

    private void showImgSelect() {
        if (mImgDialog == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.head_select, null);
            view.findViewById(R.id.tv_album).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mImgDialog != null && mImgDialog.isShowing()) {
                        mImgDialog.dismiss();
                    }
                    SingleEditActivityPermissionsDispatcher.selectAlbumWithPermissionCheck(SingleEditActivity.this);
                }
            });

            view.findViewById(R.id.tv_camera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SingleEditActivityPermissionsDispatcher.selectCameraWithPermissionCheck(SingleEditActivity.this);
                    if (mImgDialog != null && mImgDialog.isShowing()) {
                        mImgDialog.dismiss();
                    }
                }
            });

            view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mImgDialog != null && mImgDialog.isShowing()) {
                        mImgDialog.dismiss();
                    }
                }
            });

            mImgDialog = new BottomSheetDialog(this);
            mImgDialog.setContentView(view);
        }
        mImgDialog.show();
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void selectAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, request_code_album);
    }

    @NeedsPermission({Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void selectCamera() {
        takePhoto();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SingleEditActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    private void takePhoto() {
        // 步骤一：创建存储照片的文件
        mFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/accompany", System.currentTimeMillis() + ".png");
        if(!mFile.getParentFile().exists()){
            mFile.getParentFile().mkdirs();
        }
        Uri uri;
        LogUtils.d(getTag(), "file path:" + Environment.getExternalStorageDirectory().getAbsolutePath() + "/accompany");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //步骤二：Android 7.0及以上获取文件 Uri
            uri = FileProvider.getUriForFile(this, getPackageName() + OtherConstant.FILE_PROVIDER_NAME, mFile);

            LogUtils.d(getTag(), "uri path:" + uri.toString());

        } else {
            //步骤三：获取文件Uri
            uri = Uri.fromFile(mFile);
        }
        //步骤四：调取系统拍照
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, request_code_camera);
    }

    private void goCrop(Uri imgUri) {
        AppUtils.goCrop(imgUri,this);
    }

    private void uploadImage(String image) {
        HeadImageBean bean = new HeadImageBean();
        bean.setToken(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
        bean.setUrl(image);
        String json = GsonUtils.toJson(bean);
        RequestBody body = EncodeUtils.encodeInBody(json);
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().upLoadImage(body), new TypeToken<BaseDecodeBean<List<ResponseImage>>>() {
        }.getType(), new NetListener<List<ResponseImage>>() {
            @Override
            public void onSuccess(List<ResponseImage> list) {
                if (list.isEmpty()) {
                    return;
                }
                ResponseImage bean = list.get(0);
                String url = bean.getUrl();
                LogUtils.d("aboutimage", "up image:" + url);
                ToastUtils.showCommonToast(getResources().getString(R.string.upload_image_success));
                Glide.with(SingleEditActivity.this).load(url).into(mHeadImg);
                UserInfoDatabaseUtils.getInstance().updateUrl(url);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == request_code_edit && resultCode == RESULT_OK && data != null) {
            mUserInfo = (UserInfo) data.getSerializableExtra(IntentConstant.INTENT_USER);
            setViews();
        } else if (requestCode == request_code_album && data != null) {
            String imagePath = AppUtils.getImagePath(data.getData());
            LogUtils.d(getTag(),"string2uri:" + Uri.parse(imagePath));
            Uri uriForFile = FileProvider.getUriForFile(this, (getPackageName() + OtherConstant.FILE_PROVIDER_NAME), new File(imagePath));
            LogUtils.d(getTag(), "file_provider:" + uriForFile.toString());
            goCrop(data.getData());
        }else if (requestCode == UCrop.REQUEST_CROP && data != null) {
            Uri output = UCrop.getOutput(data);
            String path = AppUtils.getImagePath(output);
            LogUtils.d("aboutimage", "path:" + path);
            LogUtils.d("aboutimage", "output:" + output);
            String image = StringUtils.imageToBase64(path);
            LogUtils.d("aboutimage", "64:" + image);

            StringBuilder sb = new StringBuilder();
            sb.append(OtherConstant.IMAGE_HEAD);
            sb.append(image);
            LogUtils.d("aboutimage", "append:" + sb.toString());
            uploadImage(sb.toString());
        } else if (requestCode == request_code_camera) {
            LogUtils.d(getTag(), "camera result");
//            LogUtils.d(getTag(),"uri:" + FileProvider.getUriForFile(this, getPackageName() + OtherConstant.FILE_PROVIDER_NAME, mFile));
            LogUtils.d(getTag(), "uri string:" + FileProvider.getUriForFile(this, getPackageName() + OtherConstant.FILE_PROVIDER_NAME, mFile).toString());
            if (mFile.exists()) {
                goCrop(FileProvider.getUriForFile(this, getPackageName() + OtherConstant.FILE_PROVIDER_NAME, mFile));
            } else {
                LogUtils.d(getTag(), "no photo");
            }
        }
    }
}

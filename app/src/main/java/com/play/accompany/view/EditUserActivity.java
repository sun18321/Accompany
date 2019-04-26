package com.play.accompany.view;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;
import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.HeadImageBean;
import com.play.accompany.bean.ResponseImage;
import com.play.accompany.bean.UpUser;
import com.play.accompany.bean.UserInfo;
import com.play.accompany.constant.AppConstant;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.net.StringListener;
import com.play.accompany.utils.DateUtils;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.StringUtils;
import com.play.accompany.utils.UserInfoDatabaseUtils;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.Date;
import java.util.List;

import okhttp3.RequestBody;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class EditUserActivity extends BaseActivity implements View.OnClickListener {
    public static final int INTENT_EDIT = 0;
    public static final int INTENT_REGISTER = 1;
    private final int request_code_album = 1001;
    private final int request_code_camera = 1002;

    private int mIntentCode = 0;
    private UserInfo mInfo;
    private RoundedImageView mHeadImg;
    private EditText mEditName;
    private RadioGroup mRadioGroup;
    private LinearLayout mLinBirthday;
    private TextView mTvBirthday;
    private EditText mEditSign;
    private RadioButton mRadioMale;
    private RadioButton mRadioFemale;
    BottomSheetDialog mImgDialog;
    private int mGender = -1;
    private String mBirthday = null;
    private String mUrl;
    private TextView mTvConstellation;
    private String mConstellation;
    private EditText mEditInterest;
    private EditText mEditProfession;
    private EditText mEditOtherGame;
    private File mFile;

    @Override
    protected int getLayout() {
        return R.layout.activity_edit_user;
    }

    @Override
    protected String getTag() {
        return "EditUserActivity";
    }

    @Override
    protected void initViews() {
        setStatusColor(R.color.colorPrimary);
        if (mIntentCode == INTENT_EDIT) {
            initToolbar(getResources().getString(R.string.edit));
        } else {
            initToolbar(getResources().getString(R.string.edit_first));
        }
        mHeadImg = findViewById(R.id.img_head);
        mEditName = findViewById(R.id.edit_name);
        mRadioGroup = findViewById(R.id.gender_group);
        mRadioMale = findViewById(R.id.gender_male);
        mRadioFemale = findViewById(R.id.gender_female);
        mLinBirthday = findViewById(R.id.lin_birthday);
        mTvBirthday = findViewById(R.id.tv_birthday);
        mEditSign = findViewById(R.id.edit_sign);
        mEditInterest = findViewById(R.id.edit_interest);
        mEditProfession = findViewById(R.id.edit_profession);
        mTvConstellation = findViewById(R.id.tv_constellation);
        mEditOtherGame = findViewById(R.id.edit_other_game);
        mHeadImg.setOnClickListener(this);
        mLinBirthday.setOnClickListener(this);

        findViewById(R.id.btn_save).setOnClickListener(this);

        if (mInfo != null) {
            setViews();
        }
    }

    private void setViews() {
        mUrl = mInfo.getUrl();
        Glide.with(this).load(mUrl).into(mHeadImg);
        mEditName.setText(mInfo.getName());
        mGender = mInfo.getGender();
        if (mGender == OtherConstant.GENDER_FEMALE) {
            mRadioFemale.setChecked(true);
        } else {
            mRadioMale.setChecked(true);
        }
        mBirthday = mInfo.getDate();
        mTvBirthday.setText(mBirthday);
        mEditSign.setText(mInfo.getSign());
        mConstellation = StringUtils.getConstellationByString(mBirthday);
        mTvConstellation.setText(mConstellation);
        mEditInterest.setText(mInfo.getInterest());
        mEditProfession.setText(mInfo.getProfession());
        mEditOtherGame.setText(mInfo.getOtherGame());
    }

    private void saveUser() {
        if (TextUtils.isEmpty(mUrl)) {
            Toast.makeText(this, getResources().getString(R.string.head_image_first), Toast.LENGTH_SHORT).show();
            return;
        }

        String name = mEditName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, getResources().getString(R.string.name_first), Toast.LENGTH_SHORT).show();
            return;
        }

        if (mRadioMale.isChecked()) {
            mGender = OtherConstant.GENDER_MALE;
        }
        if (mRadioFemale.isChecked()) {
            mGender = OtherConstant.GENDER_FEMALE;
        }

        if (mGender == -1) {
            Toast.makeText(this, getResources().getString(R.string.gender_first), Toast.LENGTH_SHORT).show();
            return;
        }

        if (mBirthday == null) {
            Toast.makeText(this, getResources().getString(R.string.birthday_first), Toast.LENGTH_SHORT).show();
            return;
        }
        String sign = mEditSign.getText().toString();
        if (TextUtils.isEmpty(sign)) {
            sign = getResources().getString(R.string.default_sign);
        }
        String interest = mEditInterest.getText().toString();
        String profession = mEditProfession.getText().toString();
        String otherGame = mEditOtherGame.getText().toString();

        final UserInfo userInfo = new UserInfo();
        userInfo.setName(name);
        userInfo.setGender(mGender);
        userInfo.setSign(sign);
        userInfo.setDate(mBirthday);
        userInfo.setToken(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
        userInfo.setGameZone(1);
        userInfo.setInterest(interest);
        userInfo.setProfession(profession);
        userInfo.setOtherGame(otherGame);
        final String json = GsonUtils.toJson(userInfo);
        LogUtils.d("json", "json:" + json);

        RequestBody body = EncodeUtils.encodeInBody(json);
        AccompanyRequest request = new AccompanyRequest();
        request.requestBackString(NetFactory.getNetRequest().getNetService().saveUser(body), new StringListener() {
            @Override
            public void onSuccess(String s) {
                Toast.makeText(EditUserActivity.this, getResources().getString(R.string.user_update), Toast.LENGTH_SHORT).show();
                //写入数据库
                UserInfoDatabaseUtils.updateUserInfo(userInfo);
                if (mIntentCode == INTENT_REGISTER) {
                    MainActivity.launch(EditUserActivity.this);
                }
                EditUserActivity.this.finish();
            }

            @Override
            public void onFailed(int errorCode) {

            }

            @Override
            public void onError() {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void selectAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, request_code_album);
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void writeDeny() {
        Toast.makeText(this, "需要读写权限才能选择照片", Toast.LENGTH_SHORT).show();
    }

    @NeedsPermission({Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void selectCamera() {
        takePhoto();
    }

    @OnPermissionDenied({Manifest.permission.CAMERA})
    public void cameraDeny() {
        Toast.makeText(this, "没有权限无法打开相机", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EditUserActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == request_code_album && data != null) {
            goCrop(data.getData());
        } else if (requestCode == UCrop.REQUEST_CROP && data != null) {
            Uri output = UCrop.getOutput(data);
            String path = getImagePath(output);
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
            LogUtils.d(getTag(), "go crop path:" + FileProvider.getUriForFile(this, getPackageName() + ".FileProvider", mFile).toString());

            if (mFile.exists()) {
                goCrop(FileProvider.getUriForFile(this, getPackageName() + ".FileProvider", mFile));
            } else {
                LogUtils.d(getTag(), "no photo");
            }
        }

    }

    private void takePhoto() {
        // 步骤一：创建存储照片的文件
        mFile = new File(Environment.getExternalStorageDirectory().getPath() + "/Accompany", System.currentTimeMillis() + ".png");
        if(!mFile.getParentFile().exists()){
            mFile.getParentFile().mkdirs();
        }
        Uri uri;
        LogUtils.d(getTag(), "file path:" + Environment.getExternalStorageDirectory().getPath() + "/Accompany");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //步骤二：Android 7.0及以上获取文件 Uri
            uri = FileProvider.getUriForFile(this, getPackageName() + ".FileProvider", mFile);

            LogUtils.d(getTag(), "uri path:" + uri.toString());

        } else {
            //步骤三：获取文件Uri
            uri = Uri.fromFile(mFile);
        }
        //步骤四：调取系统拍照
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, request_code_camera);
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
                Toast.makeText(EditUserActivity.this, getResources().getString(R.string.upload_image_success), Toast.LENGTH_SHORT).show();
                mUrl = url;
                Glide.with(EditUserActivity.this).load(url).into(mHeadImg);
                UserInfoDatabaseUtils.updateUrl(url);
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

    private void goCrop(Uri imgUri) {
        UCrop.Options options = new UCrop.Options();
        options.setToolbarTitle(getResources().getString(R.string.crop_image));
        options.setToolbarColor(ActivityCompat.getColor(this, R.color.colorPrimary));
        options.setStatusBarColor(ActivityCompat.getColor(this, R.color.colorPrimary));
//        options.setHideBottomControls(true);
        File outDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File outFile = new File(outDir, System.currentTimeMillis() + ".png");
        Uri destinationUri = Uri.fromFile(outFile);
        UCrop.of(imgUri,destinationUri).withOptions(options).withAspectRatio(1,1).start(this);
    }

    private String getImagePath(Uri uri) {
        String imagePath = null;
        if (uri == null) {
            Toast.makeText(this, getResources().getString(R.string.data_error), Toast.LENGTH_SHORT).show();
            return null;
        }
        if (DocumentsContract.isDocumentUri(this,uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        return imagePath;
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
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
                    EditUserActivityPermissionsDispatcher.selectAlbumWithPermissionCheck(EditUserActivity.this);
                }
            });

            view.findViewById(R.id.tv_camera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditUserActivityPermissionsDispatcher.selectCameraWithPermissionCheck(EditUserActivity.this);
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

    private void showDataPick() {
        int[] ymd = DateUtils.getYMD(mBirthday);
        DatePickerDialog dialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mBirthday = year + "-" + (month + 1) + "-" + dayOfMonth;
                mTvBirthday.setText(mBirthday);
                mConstellation = StringUtils.getConstellation(month + 1, dayOfMonth);
                mTvConstellation.setText(mConstellation);

            }
        }, ymd[0], ymd[1] - 1, ymd[2]);
        dialog.getDatePicker().setMaxDate(OtherConstant.MAX_DAY);
        dialog.getDatePicker().setMinDate(OtherConstant.MIN_DAY);
        dialog.show();
    }

    @Override
    protected void parseIntent() {
        super.parseIntent();
        Intent intent = getIntent();
        if (intent != null) {
            mIntentCode = intent.getIntExtra(IntentConstant.INTENT_CODE, 0);
            mInfo = (UserInfo) intent.getSerializableExtra(IntentConstant.INTENT_USER);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_birthday:
                showDataPick();
                break;
            case R.id.img_head:
                showImgSelect();
                break;
            case R.id.btn_save:
                saveUser();
                break;
        }
    }
}

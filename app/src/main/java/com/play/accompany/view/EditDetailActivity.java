package com.play.accompany.view;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.services.nearby.UploadInfo;
import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.bean.EditIdBean;
import com.play.accompany.bean.UserInfo;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.StringListener;
import com.play.accompany.utils.DateUtils;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.StringUtils;
import com.play.accompany.utils.ToastUtils;
import com.play.accompany.utils.UserInfoDatabaseUtils;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditDetailActivity extends BaseActivity {
    public static final int TYPE_NAME = 1001;
    public static final int TYPE_ID = 1002;
    public static final int TYPE_BIRTHDAY = 1003;
    public static final int TYPE_GENDER = 1004;
    public static final int TYPE_GAME = 1005;
    public static final int TYPE_SIGN = 1006;
    public static final int TYPE_INTEREST = 1007;
    public static final int TYPE_WORD = 1008;

    private final int mNameLength = 16;
    private final int mIdLength = 16;
    private final int mGameLength = 15;
    private final int mSignLength = 15;
    private final int mInterestLength = 28;
    private final int mWordLength = 28;

    private int mType;
    private UserInfo mUserInfo;
    private EditText mEditDetail;

    @Override
    protected int getLayout() {
        return R.layout.activity_edit_detail;
    }

    @Override
    protected String getTag() {
        return "EditDetailActivity";
    }

    @Override
    protected void initViews() {
        TextView tvTip = findViewById(R.id.tv_tip);

        String title;
        String detail;
        switch (mType) {
            case TYPE_NAME:
                title = "修改昵称";
                detail = mUserInfo.getName();
                tvTip.setVisibility(View.VISIBLE);
                tvTip.setText(getResources().getString(R.string.tip_nick_name));
                break;
            case TYPE_ID:
                title = "修改ID";
                detail = mUserInfo.getUserName();
                tvTip.setVisibility(View.VISIBLE);
                tvTip.setText(getResources().getString(R.string.tip_id));
                break;
            case TYPE_BIRTHDAY:
                title = "修改生日";
                detail = mUserInfo.getDate();
                break;
            case TYPE_GENDER:
                title = "修改性别";
                int gender = mUserInfo.getGender();
                if (gender == OtherConstant.GENDER_MALE) {
                    detail = "男";
                } else {
                    detail = "女";
                }
                break;
            case TYPE_GAME:
                title = "修改游戏";
                detail = mUserInfo.getOtherGame();
                break;
            case TYPE_SIGN:
                title = "修改签名";
                detail = mUserInfo.getSign();
                break;
            case TYPE_INTEREST:
                title = "修改兴趣";
                detail = mUserInfo.getInterest();
                break;
            case TYPE_WORD:
                title = "修改格言";
                detail = mUserInfo.getProfession();
                break;
            default:
                title = "";
                detail = "";
        }
        initToolbar(title, "保存", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showKeyBoard(mEditDetail);
                saveDetail();
            }
        });

        mEditDetail = findViewById(R.id.detail_edit);
        mEditDetail.setText(detail);
        mEditDetail.setSelection(mEditDetail.getText().length());
        mEditDetail.requestFocus();
//        showKeyBoard(mEditDetail);

        if (mType == TYPE_BIRTHDAY) {
            mEditDetail.setFocusable(false);
            mEditDetail.setFocusableInTouchMode(false);
            final String date = detail;
            mEditDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDataPick(date);
                }
            });
        } else if (mType == TYPE_GENDER) {
            mEditDetail.setFocusable(false);
            mEditDetail.setFocusableInTouchMode(false);
            mEditDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showGenderPick();
                }
            });
        } else {
            mEditDetail.setFocusable(true);
            mEditDetail.setFocusableInTouchMode(true);
            mEditDetail.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showKeyBoard(mEditDetail);
                }
            }, 300);
        }
    }

    private void showGenderPick() {
        int index;
        if (mUserInfo.getGender() == OtherConstant.GENDER_MALE) {
            index = 0;
        } else {
            index = 1;
        }
        final CharSequence[] items = {"男", "女"};
        new QMUIDialog.CheckableDialogBuilder(this).addItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mEditDetail.setText(items[which]);
            }
        }).setCheckedIndex(index).create().show();
    }

    private void showDataPick(String birthday) {
        int[] ymd = DateUtils.getYMD(birthday);
        DatePickerDialog dialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String newDay = year + "-" + (month + 1) + "-" + dayOfMonth;
                String constellation = StringUtils.getConstellation(month + 1, dayOfMonth);
                mEditDetail.setText(newDay);
            }
        }, ymd[0], ymd[1] - 1, ymd[2]);
        dialog.getDatePicker().setMaxDate(OtherConstant.MAX_DAY);
        dialog.getDatePicker().setMinDate(OtherConstant.MIN_DAY);
        dialog.show();
    }

    private void saveDetail() {
        switch (mType) {
            case TYPE_NAME:
                if (detailCheck(mNameLength, "昵称不可为空", "昵称过长,不能超过" + mNameLength + "个字符")) {
                    mUserInfo.setName(mEditDetail.getText().toString());
                    uploadInfo();
                }
                break;
            case TYPE_ID:
                if (detailCheck(mIdLength, "ID不可为空", "ID过长,不能超过" + mIdLength + "个字符")) {
                    if (checkID(mEditDetail.getText().toString())) {
                        mUserInfo.setUserName(mEditDetail.getText().toString());
                        uploadID();
                    }
                }
                break;
            case TYPE_BIRTHDAY:
                mUserInfo.setDate(mEditDetail.getText().toString());
                uploadInfo();
                break;
            case TYPE_GENDER:
                String s = mEditDetail.getText().toString();
                int gender;
                if (TextUtils.equals(s, "男")) {
                    gender = OtherConstant.GENDER_MALE;
                } else {
                    gender = OtherConstant.GENDER_FEMALE;
                }
                mUserInfo.setGender(gender);
                uploadInfo();
                break;
            case TYPE_GAME:
                if (detailCheck(mGameLength, "所填游戏为空", "游戏内容过长,不能超过" + mGameLength + "个字符")) {
                    mUserInfo.setOtherGame(mEditDetail.getText().toString());
                    uploadInfo();
                }
                break;
            case TYPE_SIGN:
                if (detailCheck(mSignLength, "签名为空", "签名内容过长,不能超过" + mSignLength + "个字符")) {
                    mUserInfo.setOtherGame(mEditDetail.getText().toString());
                    uploadInfo();
                }
                break;
            case TYPE_INTEREST:
                if (detailCheck(mNameLength, "兴趣为空", "兴趣内容过长,不能超过" + mInterestLength + "个字符")) {
                    mUserInfo.setInterest(mEditDetail.getText().toString());
                    uploadInfo();
                }
                break;
            case TYPE_WORD:
                if (detailCheck(mWordLength, "格言为空", "格言内容过长,不能超过" + mWordLength + "个字符")) {
                    mUserInfo.setProfession(mEditDetail.getText().toString());
                    uploadInfo();
                }
                break;
            default:
        }


    }

    private boolean detailCheck(int maxLength, String isEmpty, String tooLong) {
        if (mEditDetail.getText().length() == 0) {
            ToastUtils.showCommonToast(isEmpty);
            return false;
        }
        if (mEditDetail.getText().length() > maxLength) {
            ToastUtils.showCommonToast(tooLong);
            return false;
        }

        return true;
    }

    private boolean checkID(String id) {
        Pattern pt = Pattern.compile("^[0-9a-zA-Z_]+$");
        Matcher mt = pt.matcher(id);
        boolean b = mt.matches();
        if (!b) {
            ToastUtils.showCommonToast("ID不合法");
        }
        return b;
    }

    private void uploadInfo() {
        AccompanyRequest request = new AccompanyRequest();
        UserInfo info = new UserInfo();
        info.setName(mUserInfo.getName());
        info.setGender(mUserInfo.getGender());
        info.setDate(mUserInfo.getDate());
        info.setSign(mUserInfo.getSign());
        info.setOtherGame(mUserInfo.getOtherGame());
        info.setInterest(mUserInfo.getInterest());
        info.setProfession(mUserInfo.getProfession());
        info.setToken(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
        String json = GsonUtils.toJson(info);
        request.requestBackString(NetFactory.getNetRequest().getNetService().saveUser(EncodeUtils.encodeInBody(json)), new StringListener() {
            @Override
            public void onSuccess(String s) {
                ToastUtils.showCommonToast("修改成功");
                //写入数据库
                UserInfoDatabaseUtils.getInstance().updateUserInfo(mUserInfo);

                Intent intent = new Intent();
                intent.putExtra(IntentConstant.INTENT_USER, mUserInfo);
                setResult(RESULT_OK, intent);
                EditDetailActivity.this.finish();
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

    private void uploadID() {
        EditIdBean bean = new EditIdBean();
        bean.setUserName(mUserInfo.getUserName());
        String json = GsonUtils.toJson(bean);
        AccompanyRequest request = new AccompanyRequest();
        request.requestBackString(NetFactory.getNetRequest().getNetService().editId(EncodeUtils.encodeInBody(json)), new StringListener() {
            @Override
            public void onSuccess(String s) {
                ToastUtils.showCommonToast("修改成功");
                //写入数据库
                UserInfoDatabaseUtils.getInstance().updateUserInfo(mUserInfo);

                Intent intent = new Intent();
                intent.putExtra(IntentConstant.INTENT_USER, mUserInfo);
                setResult(RESULT_OK, intent);
                EditDetailActivity.this.finish();
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

    @Override
    protected void parseIntent() {
        super.parseIntent();

        if (getIntent() == null) {
            ToastUtils.showCommonToast(getResources().getString(R.string.data_error));
            this.finish();
            return;
        }
        mType = getIntent().getIntExtra(OtherConstant.DETAIL_TYPE, -1);
        mUserInfo = (UserInfo) getIntent().getSerializableExtra(IntentConstant.INTENT_USER);
        LogUtils.d(getTag(), "name:" + mUserInfo.getName() + "type:" + mType);
    }
}

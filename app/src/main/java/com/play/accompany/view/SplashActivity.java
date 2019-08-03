package com.play.accompany.view;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.Token;
import com.play.accompany.bean.UserInfo;
import com.play.accompany.bean.VersionBean;
import com.play.accompany.constant.AppConstant;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.utils.AppUtils;
import com.play.accompany.utils.DownloadUtils;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.EventUtils;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.ToastUtils;
import com.play.accompany.utils.UserInfoDatabaseUtils;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class SplashActivity extends BaseActivity {
    private long mRemainTimes = 3000;
    private int mRepeatCount = 3;
    private long mUnitTimes = 1000;
    private Disposable mDisposable;
    //页面停留总时间,从验证token开始计算
    private final int mDelay = 2500;
    private long mCurrentTimes;
    private File mInstallPath = null;

    private String json = "{\"code\":1,\"msg\":{\"userId\":\"12555999996\",\"token\":\"1c0a4b11e03c42a62c3be2823af08678\",\"time\":1552941667073}}";

    private String listJson = "{\"code\":1,\"msgList\":[{\"id\":\"5c4c67d3e382ae5a218dbdf2\",\"userId\":\"10000000001\",\"name\":\"池鱼\",\"gender\":0,\"date\":\"2000-01-31T16:00:00.000Z\",\"type\":0,\"gameType\":[1000,1001,1002],\"price\":10,\"gold\":100,\"gameZone\":0,\"sign\":\"软甜又凶\"},{\"id\":\"5c4c67e3e382ae5a218dbdf3\",\"userId\":\"10000000002\",\"name\":\"久久\",\"gender\":0,\"date\":\"2000-01-31T16:00:00.000Z\",\"type\":0,\"gameType\":[1000,1001,1003],\"price\":20,\"gold\":200,\"gameZone\":0,\"sign\":\"萌妹\"},{\"id\":\"5c4c67ebe382ae5a218dbdf4\",\"userId\":\"10000000003\",\"name\":\"超可爱罗罗\",\"gender\":0,\"date\":\"1998-01-31T16:00:00.000Z\",\"type\":0,\"gameType\":[1000,1001,1002,1003],\"price\":30,\"gold\":300,\"gameZone\":0,\"sign\":\"纯萌凶凶\"},{\"id\":\"5c4c67f4e382ae5a218dbdf5\",\"userId\":\"10000000004\",\"name\":\"吱吱\",\"gender\":0,\"date\":\"1997-01-31T16:00:00.000Z\",\"type\":0,\"gameType\":[1000,1001,1002,1003],\"price\":40,\"gold\":400,\"gameZone\":0,\"sign\":\"小可爱\"},{\"id\":\"5c4c67fae382ae5a218dbdf6\",\"userId\":\"10000000005\",\"name\":\"果er\",\"gender\":0,\"date\":\"2000-01-31T16:00:00.000Z\",\"type\":0,\"gameType\":[1003],\"price\":20,\"gold\":200,\"gameZone\":0,\"sign\":\"文御贴心\"}]}";
    private RelativeLayout mRlLogo;
    private TextView mTvTime;
    private RelativeLayout mRlAdvertise;
    private ImageView mImgLogo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawable(null);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_splash;
    }

    @Override
    protected String getTag() {
        return "splashActivity";
    }

    @Override
    protected void initViews() {

        SplashActivityPermissionsDispatcher.requestPermissionWithPermissionCheck(this);
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE})
    public void requestPermission() {

        mRlLogo = findViewById(R.id.rl_logo);
        mTvTime = findViewById(R.id.time);
        mRlAdvertise = findViewById(R.id.rl_advertise);
        mImgLogo = findViewById(R.id.splash_log);

        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 0, 1);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 0, 1);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 0, 1);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(mImgLogo, alpha, scaleX, scaleY);
        animator.setDuration(500);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                getNewVersion();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();

        mTvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                goMain();
            }
        });

    }

    @OnShowRationale({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE})
    public void showReason(final PermissionRequest request) {
        new AlertDialog.Builder(this).setMessage(getResources().getString(R.string.permission_request))
                .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                request.cancel();
            }
        }).show();
    }

    @OnPermissionDenied({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void permissionDeny() {
        Toast.makeText(this, getResources().getString(R.string.permission_deny), Toast.LENGTH_SHORT).show();
        this.finish();
    }

    @OnNeverAskAgain({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void permissionNeverAsk() {
        Toast.makeText(this, getResources().getString(R.string.permission_never_ask), Toast.LENGTH_SHORT).show();
        this.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SplashActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    private void getNewVersion() {
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().getVersion(), new TypeToken<BaseDecodeBean<List<VersionBean>>>() {
                }.getType(), new NetListener<List<VersionBean>>() {
                    @Override
                    public void onSuccess(List<VersionBean> list) {
                        if (list.isEmpty()) {
                            checkToken();
                            return;
                        }
                        VersionBean bean = list.get(0);
                        compareVersion(bean);
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

    private void compareVersion(VersionBean bean) {
        int update = AppUtils.shouldUpdate(bean);
        switch (update) {
            case OtherConstant.MUST_UPDATE:
                showMustUpdate(bean.getVerUrl());
                break;
            case OtherConstant.COMMON_UPDATE:
                showCommonUpdate(bean.getVerUrl());
                break;
            case OtherConstant.NOT_UPDATE:
                checkToken();
                break;
            default:
                checkToken();
                break;
        }
    }

    private void showMustUpdate(final String url) {
        new QMUIDialog.MessageDialogBuilder(this).setTitle("版本更新").setMessage("有新的版本更新，此版本已不可使用，请更新").addAction("取消", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                dialog.dismiss();
                SplashActivity.this.finish();
            }
        }).addAction("确定", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                dialog.dismiss();
                downloadNew(url);
            }
        }).setCancelable(false).setCanceledOnTouchOutside(false).create().show();
    }


    private void showCommonUpdate(final String url) {
        new QMUIDialog.MessageDialogBuilder(this).setTitle("版本更新").setMessage("有新的版本啦，是否选择更新").addAction("取消", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                dialog.dismiss();
                checkToken();
            }
        }).addAction("确定", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                dialog.dismiss();
                downloadNew(url);
            }
        }).setCancelable(false).setCanceledOnTouchOutside(false).create().show();
    }


    private void downloadNew(String url) {
        DownloadUtils.getInstance().downloadFile(url, this, new DownloadUtils.DownloadListener() {
            @Override
            public void downloadFailed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showCommonToast("下载失败");
                    }
                });
            }

            @Override
            public void downloadComplete(File filePath) {
                willInstall(filePath);
            }
        });
    }

    private void willInstall(File path) {
        mInstallPath = path;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (getPackageManager().canRequestPackageInstalls()) {
                installApp();
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1001);
            }
        } else {
            installApp();
        }
    }

    private void installApp() {
        if (mInstallPath == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri uriForFile = FileProvider.getUriForFile(this, getPackageName() + OtherConstant.FILE_PROVIDER_NAME, mInstallPath);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(uriForFile, "application/vnd.android.package-archive");
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(mInstallPath), "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }


    private void checkToken() {
        String token = SPUtils.getInstance().getString(SpConstant.APP_TOKEN);
        if (TextUtils.isEmpty(token)) {
            startActivity(new Intent(this, AccountActivity.class));
            this.finish();
        } else {
            verifyToken(token);
        }
    }

    private void verifyToken(String s) {
        mCurrentTimes = System.currentTimeMillis();
        Token token = new Token(s);
        String json = GsonUtils.toJson(token);
        RequestBody body = EncodeUtils.encodeInBody(json);
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().verifyToken(body), new TypeToken<BaseDecodeBean<List<UserInfo>>>() {
        }.getType(), new NetListener<List<UserInfo>>() {
            @Override
            public void onSuccess(List<UserInfo> list) {
                if (list.isEmpty()) {
                    startActivity(new Intent(SplashActivity.this, AccountActivity.class));
                    SplashActivity.this.finish();
                } else {
                    UserInfo userInfo = list.get(0);
                    String name = userInfo.getName();
                    if (TextUtils.isEmpty(name)) {
                        Intent intent = new Intent(SplashActivity.this, EditUserActivity.class);
                        intent.putExtra(IntentConstant.INTENT_CODE, EditUserActivity.INTENT_REGISTER);
                        startActivity(intent);
                        SplashActivity.this.finish();
                    } else {
                        UserInfoDatabaseUtils.getInstance().saveUserInfo(list.get(0));
                        delay();
                    }
                }
            }

            @Override
            public void onFailed(int errCode) {
                if (errCode == AppConstant.ERROR_TOKEN || errCode == AppConstant.ERROR_NO_USER) {
                    startActivity(new Intent(SplashActivity.this, AccountActivity.class));
                    SplashActivity.this.finish();
                }
            }

            @Override
            public void onError() {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void delay() {
        long time = System.currentTimeMillis() - mCurrentTimes;
        if (time < mDelay) {
            long timeSpace = mDelay - time;
            mRlLogo.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goMain();
                }
            }, timeSpace);
        } else {
            goMain();
        }
    }

    private void startCountdown() {
        mRlLogo.setVisibility(View.INVISIBLE);
        mRlAdvertise.setVisibility(View.VISIBLE);
        mTvTime.setText(getResources().getString(R.string.skip) + (mRemainTimes) / mUnitTimes + "S");

        Observable.interval(mUnitTimes, TimeUnit.MILLISECONDS)
                .take(mRepeatCount)
                .observeOn(AndroidSchedulers.mainThread())
                .onTerminateDetach()
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(Long aLong) {
                        if (mDisposable == null || mDisposable.isDisposed()) {
                            return;
                        }
                        mRemainTimes -= mUnitTimes;
                        mTvTime.setText(getResources().getString(R.string.skip) + (mRemainTimes) / mUnitTimes + "S");
                    }

                    @Override
                    public void onError(Throwable e) {
                        goMain();
                    }

                    @Override
                    public void onComplete() {
                        goMain();
                    }
                });
    }

    private void goMain() {
        String string = SPUtils.getInstance().getString(SpConstant.APP_TOKEN);
        if (TextUtils.isEmpty(string)) {
            startActivity(new Intent(this, AccountActivity.class));
        } else {
            EventUtils.getInstance().upUserType();

            MainActivity.launch(SplashActivity.this);
        }
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            installApp();
        }
    }


    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }
}

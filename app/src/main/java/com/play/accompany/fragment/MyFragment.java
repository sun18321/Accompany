package com.play.accompany.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.play.accompany.R;
import com.play.accompany.base.BaseFragment;
import com.play.accompany.bean.UserInfo;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.db.AccompanyDatabase;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.view.AllOrderActivity;
import com.play.accompany.view.MainActivity;
import com.play.accompany.view.UserCenterActivity;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.glide.transformations.BlurTransformation;


public class MyFragment extends BaseFragment {
    private static MyFragment sMyFragment;
    private QMUIGroupListView mGroupListView;
    private TextView mTvId;
    private TextView mTvName;
    private TextView mTvAttention;
    private TextView mTvFans;
    private ImageView mImgHead;
    private ImageView mImgBg;
    private boolean mFirst = true;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private UserInfo mUserInfo;

    public static MyFragment newInstance() {
        if (sMyFragment == null) {
            sMyFragment = new MyFragment();
            return sMyFragment;
        }
        return sMyFragment;
    }


    @Override
    protected int getLayout() {
        return R.layout.fragment_my;
    }

    @Override
    protected void initViews(View view) {
        mGroupListView = view.findViewById(R.id.group_list);
        mImgBg = view.findViewById(R.id.img_bg);
        mTvId = view.findViewById(R.id.tv_id);
        mTvName = view.findViewById(R.id.tv_name);
        mTvAttention = view.findViewById(R.id.tv_attention);
        mTvFans = view.findViewById(R.id.tv_fans);
        mImgHead = view.findViewById(R.id.img_head);

        mImgHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUserInfo != null) {
                    Intent intent = new Intent(mContext, UserCenterActivity.class);
                    intent.putExtra(IntentConstant.INTENT_USER, mUserInfo);
                    mContext.startActivity(intent);
                } else {
                    getUserInfo();
                }

            }
        });
        initItems();
    }

    @Override
    protected String getFragmentName() {
        return "MyFragment";
    }

    @Override
    public void onResume() {
        super.onResume();

        boolean isEdit = SPUtils.getInstance().getBoolean(SpConstant.IS_USER_EDIT, false);
        if (isEdit) {
            getUserInfo();
            SPUtils.getInstance().put(SpConstant.IS_USER_EDIT, false);
            return;
        }

        if (mFirst) {
            willDisplay();
            mFirst = false;
        }
    }

    private void willDisplay() {
        if (mUserInfo == null) {
            getUserInfo();
        } else {
            displayUser();
        }
     }

    private void getUserInfo() {
        mCompositeDisposable.add(Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                mUserInfo = AccompanyDatabase.getInstance(mContext).getUserDao().getUserInfo(SPUtils.getInstance().getString(SpConstant.MY_USER_ID));
                if (mUserInfo == null) {
                    emitter.onNext(false);
                } else {
                    emitter.onNext(true);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean b) throws Exception {
                if (b) {
                    displayUser();
                }
            }
        }));
    }

    private void displayUser() {
        String url = mUserInfo.getUrl();
        if (!TextUtils.isEmpty(url)) {
            glideFuzzy(url);
        }

    }

    private void glideFuzzy(String url) {
        Glide.with(mContext).load(url).into(mImgHead);
        Glide.with(mContext)
                .load(url)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(50, 5)))
                .into(mImgBg);
    }

    private void initItems() {
//        QMUICommonListItemView item_member = mGroupListView.createItemView(ContextCompat.getDrawable(mContext, R.drawable.member), getResources().getString(R.string.member), "",
//                QMUICommonListItemView.HORIZONTAL, QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
//        QMUICommonListItemView item_service = mGroupListView.createItemView(ContextCompat.getDrawable(mContext, R.drawable.service), getResources().getString(R.string.service), "",
//                QMUICommonListItemView.HORIZONTAL, QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
//        QMUICommonListItemView item_master = mGroupListView.createItemView(ContextCompat.getDrawable(mContext, R.drawable.master), getResources().getString(R.string.master), "",
//                QMUICommonListItemView.HORIZONTAL, QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
//        QMUICommonListItemView item_setting = mGroupListView.createItemView(ContextCompat.getDrawable(mContext, R.drawable.setting), getResources().getString(R.string.setting), "",
//                QMUICommonListItemView.HORIZONTAL, QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUICommonListItemView itemOrder = mGroupListView.createItemView(getResources().getString(R.string.my_order));
        itemOrder.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.order));
        itemOrder.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUICommonListItemView itemMember = mGroupListView.createItemView(getResources().getString(R.string.member));
        itemMember.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.member));
        itemMember.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUICommonListItemView itemServices = mGroupListView.createItemView(getResources().getString(R.string.service));
        itemServices.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.service));
        itemServices.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUICommonListItemView itemMaster = mGroupListView.createItemView(getResources().getString(R.string.master));
        itemMaster.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.master));
        itemMaster.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUICommonListItemView itemSetting = mGroupListView.createItemView(getResources().getString(R.string.setting));
        itemSetting.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.setting));
        itemSetting.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUIGroupListView.newSection(mContext).addItemView(itemOrder, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, AllOrderActivity.class));
            }
        }).addItemView(itemMember, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).addItemView(itemServices, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).addItemView(itemMaster, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).addItemView(itemSetting, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).addTo(mGroupListView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }
}

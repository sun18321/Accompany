package com.play.accompany.view;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;

import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.ToastUtils;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

public class PrivacyActivity extends BaseActivity {
    QMUIGroupListView mGroupListView;
    private ImageView mImageOpen;
    private ImageView mImageClose;

    @Override
    protected int getLayout() {
        return R.layout.activity_privacy;
    }

    @Override
    protected String getTag() {
        return "PrivacyActivity";
    }

    @Override
    protected void initViews() {

        initToolbar(getResources().getString(R.string.setting_privacy));
        mGroupListView = findViewById(R.id.group_list);
        boolean showLocation = SPUtils.getInstance().getBoolean(SpConstant.SHOW_LOCATION, true);

        QMUICommonListItemView itemOpen = mGroupListView.createItemView(getResources().getString(R.string.privacy_default));
        itemOpen.setOrientation(QMUICommonListItemView.VERTICAL);
        itemOpen.setDetailText(getResources().getText(R.string.privacy_open));
        itemOpen.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
        mImageOpen = new ImageView(this);
        mImageOpen.setImageResource(R.drawable.check);
        itemOpen.addAccessoryCustomView(mImageOpen);

        QMUICommonListItemView itemClose = mGroupListView.createItemView(getResources().getString(R.string.privacy_close));
        itemClose.setOrientation(QMUICommonListItemView.VERTICAL);
        itemClose.setDetailText(getResources().getText(R.string.privacy_close_detail));
        itemClose.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
        mImageClose = new ImageView(this);
        mImageClose.setImageResource(R.drawable.check);
        itemClose.addAccessoryCustomView(mImageClose);

        if (showLocation) {
            mImageOpen.setVisibility(View.VISIBLE);
            mImageClose.setVisibility(View.INVISIBLE);
        } else {
            mImageOpen.setVisibility(View.INVISIBLE);
            mImageClose.setVisibility(View.VISIBLE);
        }

        QMUIGroupListView.newSection(this).addItemView(itemOpen, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dealOpen();
            }
        }).addItemView(itemClose, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dealClose();
            }
        }).addTo(mGroupListView);
    }

    private void dealOpen() {
        if (mImageOpen.getVisibility() == View.INVISIBLE) {
            mImageOpen.setVisibility(View.VISIBLE);
            mImageClose.setVisibility(View.INVISIBLE);
            SPUtils.getInstance().put(SpConstant.SHOW_LOCATION, true);
            ToastUtils.showCommonToast(getResources().getString(R.string.location_open));
        }
    }

    private void dealClose() {
        if (mImageClose.getVisibility() == View.INVISIBLE) {
            mImageOpen.setVisibility(View.INVISIBLE);
            mImageClose.setVisibility(View.VISIBLE);
            SPUtils.getInstance().put(SpConstant.SHOW_LOCATION,false);
            ToastUtils.showCommonToast(getResources().getString(R.string.location_close));
        }
    }
}

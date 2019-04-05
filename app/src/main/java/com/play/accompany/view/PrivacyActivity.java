package com.play.accompany.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

public class PrivacyActivity extends BaseActivity {
    QMUIGroupListView mGroupListView;

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

        QMUICommonListItemView itemOpen = mGroupListView.createItemView(getResources().getString(R.string.privacy_default));
        itemOpen.setOrientation(QMUICommonListItemView.VERTICAL);
        itemOpen.setDetailText(getResources().getText(R.string.privacy_open));
        itemOpen.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
        ImageView imageOpen = new ImageView(this);
        imageOpen.setImageResource(R.drawable.check);
        itemOpen.addAccessoryCustomView(imageOpen);

        QMUICommonListItemView itemClose = mGroupListView.createItemView(getResources().getString(R.string.privacy_close));
        itemClose.setOrientation(QMUICommonListItemView.VERTICAL);
        itemClose.setDetailText(getResources().getText(R.string.privacy_close_detail));
        itemClose.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
        ImageView imageClose = new ImageView(this);
        imageClose.setImageResource(R.drawable.check);
        itemClose.addAccessoryCustomView(imageClose);

        QMUIGroupListView.newSection(this).addItemView(itemOpen, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).addItemView(itemClose, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).addTo(mGroupListView);
    }
}

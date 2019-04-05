package com.play.accompany.view;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

public class SettingActivity extends BaseActivity {
    QMUIGroupListView mGroupListView;

    @Override
    protected int getLayout() {
        return R.layout.activity_setting;
    }

    @Override
    protected String getTag() {
        return "SettingActivity";
    }

    @Override
    protected void initViews() {
        mGroupListView = findViewById(R.id.group_list);
        initToolbar(getResources().getString(R.string.setting));

        QMUICommonListItemView itemSound = mGroupListView.createItemView(getResources().getString(R.string.setting_sound));
        itemSound.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUICommonListItemView itemPrivacy = mGroupListView.createItemView(getResources().getString(R.string.setting_privacy));
        itemPrivacy.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUICommonListItemView itemNotice = mGroupListView.createItemView(getResources().getString(R.string.setting_notice));
        itemNotice.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUIGroupListView.newSection(this).addItemView(itemSound, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).addItemView(itemPrivacy, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this,PrivacyActivity.class));
            }
        }).addItemView(itemNotice, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).addTo(mGroupListView);
    }

}

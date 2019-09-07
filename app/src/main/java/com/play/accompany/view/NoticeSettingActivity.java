package com.play.accompany.view;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.utils.SPUtils;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

public class NoticeSettingActivity extends BaseActivity {
    QMUIGroupListView mGroupListView;

    @Override
    protected int getLayout() {
        return R.layout.activity_notice_setting;
    }

    @Override
    protected String getTag() {
        return "NoticeSettingActivity";
    }

    @Override
    protected void initViews() {
        initToolbar(getResources().getString(R.string.setting_notice));
        mGroupListView = findViewById(R.id.group_list);

        QMUICommonListItemView itemNotice = mGroupListView.createItemView("接收新消息通知");
        itemNotice.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        boolean b = SPUtils.getInstance().getBoolean(SpConstant.ACCEPT_NEW_NOTICE, true);
        CheckBox checkBox =itemNotice.getSwitch();
        if (b) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SPUtils.getInstance().put(SpConstant.ACCEPT_NEW_NOTICE, isChecked);
                } else {
                    SPUtils.getInstance().put(SpConstant.ACCEPT_NEW_NOTICE, isChecked);
                }
            }
        });

        QMUIGroupListView.newSection(this).setTitle("消息提醒").addItemView(itemNotice, null).addTo(mGroupListView);

    }
}

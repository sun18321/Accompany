package com.play.accompany.view;

import android.content.Intent;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.TextView;

import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.utils.AppUtils;
import com.play.accompany.utils.SPUtils;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import io.rong.imkit.RongIM;

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
        TextView tvVersion = findViewById(R.id.tv_version);
        tvVersion.setText(AppUtils.getAppVersionName());
        mGroupListView = findViewById(R.id.group_list);
        initToolbar(getResources().getString(R.string.setting));

        QMUICommonListItemView itemSound = mGroupListView.createItemView(getResources().getString(R.string.setting_sound));
        itemSound.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUICommonListItemView itemPrivacy = mGroupListView.createItemView(getResources().getString(R.string.setting_privacy));
        itemPrivacy.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUICommonListItemView itemNotice = mGroupListView.createItemView(getResources().getString(R.string.setting_notice));
        itemNotice.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUICommonListItemView itemQuit = mGroupListView.createItemView(getResources().getString(R.string.quit_login));
        itemQuit.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUIGroupListView.newSection(this).addItemView(itemPrivacy, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this,PrivacyActivity.class));
            }
        }).addItemView(itemNotice, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, NoticeSettingActivity.class));
            }
        }).addItemView(itemQuit, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new QMUIDialog.MessageDialogBuilder(SettingActivity.this).setTitle(getResources().getString(R.string.tips))
                        .setMessage(getResources().getString(R.string.tips_login)).addAction(getResources().getString(R.string.cancel), new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                }).addAction(getResources().getString(R.string.confirm), new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        quitLogin();
                        dialog.dismiss();
                    }
                }).create().show();
            }
        }).addTo(mGroupListView);

    }

    private void quitLogin() {
        SPUtils.getInstance().clear();
        RongIM.getInstance().logout();
        startActivity(new Intent(this, AccountActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        this.finish();
    }


}

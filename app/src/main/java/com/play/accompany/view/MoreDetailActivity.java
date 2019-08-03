package com.play.accompany.view;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.bean.UserInfo;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.design.DetailLayout;
import com.play.accompany.utils.ToastUtils;

import java.io.Serializable;

public class MoreDetailActivity extends BaseActivity implements View.OnClickListener {
    private UserInfo mUserInfo;
    private DetailLayout mDetailGame;
    private DetailLayout mDetailSign;
    private DetailLayout mDetailInterest;
    private DetailLayout mDetailWord;

    @Override
    protected int getLayout() {
        return R.layout.activity_more_detail;
    }

    @Override
    protected String getTag() {
        return "MoreDetailActivity";
    }

    @Override
    protected void initViews() {

        initToolbar("更多信息");
        mDetailGame = findViewById(R.id.detail_game);
        mDetailSign = findViewById(R.id.detail_sign);
        mDetailInterest = findViewById(R.id.detail_interest);
        mDetailWord = findViewById(R.id.detail_word);

        mDetailGame.setOnClickListener(this);
        mDetailSign.setOnClickListener(this);
        mDetailInterest.setOnClickListener(this);
        mDetailWord.setOnClickListener(this);

        setViews();
    }

    private void setViews() {
        mDetailGame.setTvDetail(mUserInfo.getOtherGame());
        mDetailSign.setTvDetail(mUserInfo.getSign());
        mDetailInterest.setTvDetail(mUserInfo.getInterest());
        mDetailWord.setTvDetail(mUserInfo.getProfession());
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            mUserInfo = (UserInfo) data.getSerializableExtra(IntentConstant.INTENT_USER);
            setViews();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, EditDetailActivity.class);
        intent.putExtra(IntentConstant.INTENT_USER, mUserInfo);
        switch (v.getId()) {
            case R.id.detail_game:
                intent.putExtra(OtherConstant.DETAIL_TYPE, EditDetailActivity.TYPE_GAME);
                break;
            case R.id.detail_sign:
                intent.putExtra(OtherConstant.DETAIL_TYPE, EditDetailActivity.TYPE_SIGN);
                break;
            case R.id.detail_interest:
                intent.putExtra(OtherConstant.DETAIL_TYPE, EditDetailActivity.TYPE_INTEREST);
                break;
            case R.id.detail_word:
                intent.putExtra(OtherConstant.DETAIL_TYPE, EditDetailActivity.TYPE_WORD);
                break;
        }
        startActivityForResult(intent, 100);
    }
}

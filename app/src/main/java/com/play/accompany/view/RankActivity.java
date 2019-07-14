package com.play.accompany.view;

import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.play.accompany.R;
import com.play.accompany.adapter.OtherAdapter;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.OtherGameBean;
import com.play.accompany.bean.TopGameBean;
import com.play.accompany.constant.IntentConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.ToastUtils;

import java.util.List;

import okhttp3.RequestBody;

public class RankActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    //写死了，只有其他了
    private final int mOtherGame = 9999;
    private final int mGridCount = 3;

    @Override
    protected int getLayout() {
        return R.layout.activity_rank;
    }

    @Override
    protected String getTag() {
        return "rankActivity";
    }

    @Override
    protected void parseIntent() {
        super.parseIntent();
    }

    @Override
    protected void initViews() {
        initToolbar(getResources().getString(R.string.other));
        mRecyclerView = findViewById(R.id.recycler);
        requestData();
    }

    private void setAdapter(List<TopGameBean> list) {
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, mGridCount, GridLayoutManager.VERTICAL, false));
        OtherAdapter adapter = new OtherAdapter(this, list);
        adapter.setOtherListener(new OtherAdapter.OtherItemListener() {
            @Override
            public void onItemClick(int id) {
                Intent intent = new Intent();
                intent.putExtra(IntentConstant.INTENT_GAME_ID, id);
                setResult(RESULT_OK, intent);
                RankActivity.this.finish();
            }
        });
        mRecyclerView.setAdapter(adapter);
    }

    private void requestData() {
//        showDialog();
        OtherGameBean bean = new OtherGameBean();
        bean.setToken(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
        bean.setParent(mOtherGame);
        String json = GsonUtils.toJson(bean);
        RequestBody body = EncodeUtils.encodeInBody(json);
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().getOtherGame(body), new TypeToken<BaseDecodeBean<List<TopGameBean>>>() {
        }.getType(), new NetListener<List<TopGameBean>>() {
            @Override
            public void onSuccess(List<TopGameBean> list) {
                if (list.isEmpty()) {
                    return;
                }
                setAdapter(list);
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
}

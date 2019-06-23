package com.play.accompany.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.MasterCheckBean;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.GlideUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;

import okhttp3.RequestBody;

public class MasterDetailActivity extends BaseActivity {

    private TextView mTvGame;
    private ImageView mImgMaster;

    @Override
    protected int getLayout() {
        return R.layout.activity_master_detail;
    }

    @Override
    protected String getTag() {
        return "MasterDetailActivity";
    }

    @Override
    protected void initViews() {
        initToolbar(getResources().getString(R.string.master_wait));
        mTvGame = findViewById(R.id.tv_game);
        mImgMaster = findViewById(R.id.img_master);

        requestData();
    }

    private void setView(MasterCheckBean bean) {
        mTvGame.setText(bean.getName());
        GlideUtils.commonLoad(this, bean.getUrlGameType(), mImgMaster);
    }

    private void requestData() {
        RequestBody body = EncodeUtils.encodeToken();
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().getCheckMaterDetail(body), new TypeToken<BaseDecodeBean<List<MasterCheckBean>>>() {
                }.getType(), new NetListener<List<MasterCheckBean>>() {
                    @Override
                    public void onSuccess(List<MasterCheckBean> list) {
                        if (list.isEmpty()) {
                            return;
                        }
                        setView(list.get(0));
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

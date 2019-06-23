package com.play.accompany.fragment;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.play.accompany.R;
import com.play.accompany.base.BaseFragment;
import com.play.accompany.bean.BaseDecodeBean;
import com.play.accompany.bean.MasterBean;
import com.play.accompany.bean.MasterCheckBean;
import com.play.accompany.bean.OnlyCodeBean;
import com.play.accompany.bean.TopGameBean;
import com.play.accompany.constant.OtherConstant;
import com.play.accompany.constant.SpConstant;
import com.play.accompany.design.MasterLayout;
import com.play.accompany.design.TypeDialog;
import com.play.accompany.net.AccompanyRequest;
import com.play.accompany.net.NetFactory;
import com.play.accompany.net.NetListener;
import com.play.accompany.utils.EncodeUtils;
import com.play.accompany.utils.GlideUtils;
import com.play.accompany.utils.GsonUtils;
import com.play.accompany.utils.LogUtils;
import com.play.accompany.utils.SPUtils;
import com.play.accompany.utils.StringUtils;
import com.play.accompany.utils.ToastUtils;
import com.play.accompany.view.AccompanyApplication;
import com.play.accompany.view.MasterActivity;
import com.play.accompany.view.MasterDetailActivity;

import java.util.Iterator;
import java.util.List;

import okhttp3.RequestBody;

public class SecondMasterFragment extends BaseFragment implements View.OnClickListener {

    private ScrollView mScrollView;
    private LinearLayout mLinSkill;
    private LinearLayout mLinAdd;
    private ImageView mImgAdd;
    private static SecondMasterFragment mFragment;
    private List<MasterCheckBean> mList;
    private TextView mTvGame;
    private Button mBtnSubmit;
    private MasterActivity mMasterActivity;
    private String mMasterPicture = null;
    private TopGameBean mBean = null;
    private List<TopGameBean> mAllList;
    private MasterLayout mLastLayout;


    public static SecondMasterFragment getInstance() {
        if (mFragment == null) {
            synchronized (SecondMasterFragment.class) {
                if (mFragment == null) {
                    mFragment = new SecondMasterFragment();
                }
            }
        }
        return mFragment;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_second_master;
    }

    @Override
    protected void initViews(View view) {
        mMasterActivity = (MasterActivity) mActivity;

        mScrollView = view.findViewById(R.id.scroll);
        mLinSkill = view.findViewById(R.id.lin_skill);
        mLinAdd = view.findViewById(R.id.lin_add);
        mImgAdd = view.findViewById(R.id.img_add);
        mTvGame = view.findViewById(R.id.tv_game);
        mBtnSubmit = view.findViewById(R.id.btn_submit);
        mImgAdd.setOnClickListener(this);
        mBtnSubmit.setOnClickListener(this);
        mTvGame.setOnClickListener(this);

        requestMasterInfo();
    }

    @Override
    protected String getFragmentName() {
        return "SecondMasterFragment";
    }

    private void setViews(List<MasterCheckBean> list) {
        setRemainList(list);

        mList = list;
        for (int i = 0; i < mList.size() - 1; i++) {
            MasterLayout layout = new MasterLayout(mContext);
            layout.setCommon(mList.get(i));
            mLinSkill.addView(layout);
        }
        MasterCheckBean lastBean = mList.get(mList.size() - 1);
        addLast(lastBean);
    }

    private void addLast(MasterCheckBean bean) {
        int lastState = bean.getIsApply();
        mLastLayout = new MasterLayout(mContext);
        if (lastState == OtherConstant.MASTER_CHECKING) {
            mLastLayout.setLast(bean.getName(), new MasterLayout.MasterListener() {
                @Override
                public void onChecking() {
                    goWait();
                }

                @Override
                public void onAddNew() {

                }
            });
        } else if (lastState == OtherConstant.MATSER_CHECKED) {
            MasterLayout commonLayout = new MasterLayout(mContext);
            commonLayout.setCommon(bean);
            mLinSkill.addView(commonLayout);

            mLastLayout.setAddLayout(new MasterLayout.MasterListener() {
                @Override
                public void onChecking() {

                }

                @Override
                public void onAddNew() {
                    mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    mLinAdd.setVisibility(View.VISIBLE);
                }
            });
        }
        mLinSkill.addView(mLastLayout);
    }




    private void requestMasterInfo() {
        RequestBody body = EncodeUtils.encodeToken();
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().getAfterMasterInfo(body), new TypeToken<BaseDecodeBean<List<MasterCheckBean>>>() {
        }.getType(), new NetListener<List<MasterCheckBean>>() {
            @Override
            public void onSuccess(List<MasterCheckBean> list) {
                if (list.isEmpty()) {
                    return;
                }
                setViews(list);
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

    private void setRemainList(List<MasterCheckBean> list) {
        mAllList = AccompanyApplication.getGameList();
        if (mAllList == null || mAllList.isEmpty()) {
            return;
        }
        for (MasterCheckBean masterCheckBean : list) {
            Iterator<TopGameBean> iterator = mAllList.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getTypeId() == masterCheckBean.getTypeId()) {
                    iterator.remove();
                }
            }
        }
    }

    private void showGameDialog() {
        if (mAllList.isEmpty()) {
            ToastUtils.showCommonToast("您已经申请全部大神");
            return;
        }

        new TypeDialog(mContext, mAllList, new TypeDialog.SelectListenr() {
            @Override
            public void onSelect(TopGameBean bean) {
                mTvGame.setText(bean.getName());
                mBean = bean;
            }
        }).create().show();
    }

    private void updateLast() {
        mLinAdd.setVisibility(View.GONE);
        if (mLastLayout != null) {
            LogUtils.d("layout", "last layout is not null");
//            mLastLayout.setLast(mBean.getName(), new MasterLayout.MasterListener() {
//                @Override
//                public void onChecking() {
//                    goWait();
//                }
//
//                @Override
//                public void onAddNew() {
//
//                }
//            });
//            mLinSkill.notifyAll();

            mLinSkill.removeView(mLastLayout);
            MasterLayout layout = new MasterLayout(mContext);
            layout.setLast(mBean.getName(), new MasterLayout.MasterListener() {
                @Override
                public void onChecking() {
                    goWait();
                }

                @Override
                public void onAddNew() {

                }
            });
            mLinSkill.addView(layout);
        } else {
            LogUtils.d("layout", "last layout is null");
        }
    }

    private void goWait() {
        startActivity(new Intent(mContext, MasterDetailActivity.class));
    }

    private void addNew() {
        if (mBean == null) {
            ToastUtils.showCommonToast(getResources().getString(R.string.game_type_first));
            return;
        }

        if (mMasterPicture == null) {
            ToastUtils.showCommonToast(getResources().getString(R.string.master_image_first));
            return;
        }
        MasterBean bean = new MasterBean();
        bean.setToken(SPUtils.getInstance().getString(SpConstant.APP_TOKEN));
        bean.setUrlGameType(mMasterPicture);
        bean.setGameType(mBean.getTypeId());
        RequestBody body = EncodeUtils.encodeInBody(GsonUtils.toJson(bean));
        AccompanyRequest request = new AccompanyRequest();
        request.beginRequest(NetFactory.getNetRequest().getNetService().addMasterItem(body), new TypeToken<BaseDecodeBean<OnlyCodeBean>>() {
                }.getType(), new NetListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        updateLast();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_add:
                if (mMasterActivity != null) {
                    mMasterActivity.getPicture(new MasterActivity.PictureListener() {
                        @Override
                        public void picturePath(String path) {
                            GlideUtils.commonLoad(mContext, path, mImgAdd);
                            mMasterPicture = StringUtils.imageChangeUpload(path);
                        }
                    });
                }

                break;
            case R.id.btn_submit:
                addNew();
                break;
            case R.id.tv_game:
                showGameDialog();
                break;
        }
    }
}

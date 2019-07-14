package com.play.accompany.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.play.accompany.R;
import com.play.accompany.utils.LogUtils;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseFragment extends Fragment {
    protected AppCompatActivity mActivity;
    protected Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mActivity = (AppCompatActivity) context;
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.d("fragment","on create");

        View view = inflater.inflate(getLayout(), container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d("fragment","on resume");

        MobclickAgent.onPageStart(getFragmentName());
    }

    @Override
    public void onPause() {
        LogUtils.d("fragment","on pause");
        super.onPause();
        MobclickAgent.onPageEnd(getFragmentName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LogUtils.d("fragment","on destroy");
        mActivity = null;
        mContext = null;
    }

    protected abstract int getLayout();

    protected abstract void initViews(View view);

    protected abstract String getFragmentName();
}

package com.play.accompany.base;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
//        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
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

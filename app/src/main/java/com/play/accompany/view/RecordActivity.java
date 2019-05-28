package com.play.accompany.view;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;

public class RecordActivity extends BaseActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected int getLayout() {
        return R.layout.activity_record;
    }

    @Override
    protected String getTag() {
        return "RecordActivity";
    }

    @Override
    protected void initViews() {
        initToolbar(getResources().getString(R.string.history));
        mTabLayout = findViewById(R.id.tablayout);
        mViewPager = findViewById(R.id.viewpager);

    }
}

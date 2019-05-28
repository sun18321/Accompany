package com.play.accompany.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.play.accompany.base.BaseFragment;

import java.util.List;

public class AccompanyFragmentAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> mFragmentList;
    private String[] mStringTitle;

    public AccompanyFragmentAdapter(FragmentManager fm, List<BaseFragment> fragmentList, String[] title) {
        super(fm);

        mFragmentList = fragmentList;
        mStringTitle = title;
    }



    @Override
    public Fragment getItem(int i) {
        return mFragmentList != null && mFragmentList.size() > i ? mFragmentList.get(i) : null;
    }

    @Override
    public int getCount() {
        return mFragmentList != null ? mFragmentList.size() : 0;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mStringTitle != null && mStringTitle.length > position ? mStringTitle[position] : null;
    }
}

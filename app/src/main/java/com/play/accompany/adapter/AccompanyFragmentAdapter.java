package com.play.accompany.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

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

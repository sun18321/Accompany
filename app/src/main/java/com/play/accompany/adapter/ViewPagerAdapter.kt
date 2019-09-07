package com.play.accompany.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.play.accompany.base.BaseFragment

class ViewPagerAdapter(fm: androidx.fragment.app.FragmentManager?, titleList:ArrayList<String>, fragmentList:ArrayList<BaseFragment>) : FragmentStatePagerAdapter(fm) {
    private var mFragmentList = fragmentList
    private var mTitleList = titleList

    override fun getItem(p0: Int): androidx.fragment.app.Fragment {
        return mFragmentList[p0]
    }

    override fun getCount(): Int = mFragmentList.size

    override fun getPageTitle(position: Int): CharSequence? = mTitleList[position]

}
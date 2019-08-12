package com.play.accompany.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.play.accompany.base.BaseFragment

class ViewPagerAdapter(fm: FragmentManager?,titleList:ArrayList<String>,fragmentList:ArrayList<BaseFragment>) : FragmentStatePagerAdapter(fm) {
    private var mFragmentList = fragmentList
    private var mTitleList = titleList

    override fun getItem(p0: Int): Fragment {
        return mFragmentList[p0]
    }

    override fun getCount(): Int = mFragmentList.size

    override fun getPageTitle(position: Int): CharSequence? = mTitleList[position]

    fun setTitle(title: String, position: Int) {
        if (position >= 0 && position < mTitleList.size) {
            mTitleList[position] = title
        }
        notifyDataSetChanged()
    }
}
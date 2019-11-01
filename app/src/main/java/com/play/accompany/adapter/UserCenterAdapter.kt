package com.play.accompany.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import kotlin.math.min

class UserCenterAdapter(fm:FragmentManager,private val mTitleList:ArrayList<String>,private val mFragmentList:ArrayList<Fragment>) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return if (mTitleList.size == mFragmentList.size) {
            mTitleList.size
        } else {
            min(mTitleList.size, mFragmentList.size)
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mTitleList[position]
    }

}
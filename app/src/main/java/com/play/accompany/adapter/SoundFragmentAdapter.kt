package com.play.accompany.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class SoundFragmentAdapter(context: FragmentActivity, private val mList: ArrayList<Fragment>) : FragmentStateAdapter(context) {
    override fun getItemCount(): Int {
        return mList.size
    }

    override fun createFragment(position: Int): Fragment {
        return mList[position]
    }
}
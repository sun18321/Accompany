package com.play.accompany.fragment

import androidx.viewpager2.widget.ViewPager2
import com.play.accompany.fragment.LivingSoundFragment
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.play.accompany.R
import com.play.accompany.adapter.SoundFragmentAdapter
import com.play.accompany.base.BaseFragment
import kotlinx.android.synthetic.main.top_living_fragment.*

class TopLivingSoundFragment : BaseFragment() {
    private lateinit var mView: View

    override fun getLayout(): Int {
        return R.layout.top_living_fragment
    }

    override fun initViews(view: View?) {
        mView = view!!
        val list = ArrayList<Fragment>()
        for (i in 0..5) {
            list.add(LivingSoundFragment())
        }
        second_viewpager.adapter = SoundFragmentAdapter(mContext as FragmentActivity, list)
        second_viewpager.registerOnPageChangeCallback(object :  ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })
    }
    override fun getFragmentName(): String {
        return "TopLivingSoundFragment"
    }
}
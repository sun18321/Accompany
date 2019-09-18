package com.play.accompany.view

import androidx.viewpager.widget.ViewPager
import android.view.View
import com.play.accompany.R
import com.play.accompany.adapter.ViewPagerAdapter
import com.play.accompany.base.BaseActivity
import com.play.accompany.base.BaseFragment
import com.play.accompany.constant.IntentConstant
import com.play.accompany.fragment.AttentionFragment
import com.play.accompany.fragment.FansFragment
import com.play.accompany.utils.LogUtils
import com.play.accompany.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_record.*
import kotlinx.android.synthetic.main.activity_record.view.*

class ViewPagerActivity : BaseActivity() {
    private val titleList = arrayListOf("关注", "粉丝")

    override fun getLayout(): Int {
        return R.layout.activity_record
    }

    override fun getTag(): String {
       return "ViewPagerActivity"
    }

    override fun initViews() {
        initToolbar("关注")
        val fragmentList = arrayListOf(AttentionFragment.newInstance { i -> updateAttention(i) }, FansFragment.newInstance { i -> updateFans(i) })
        val pagerAdapter = ViewPagerAdapter(supportFragmentManager, titleList, fragmentList)
        viewpager.adapter = pagerAdapter
        tablayout.setViewPager(viewpager)

        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {

            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

            }

            override fun onPageSelected(p0: Int) {
                initToolbar(titleList[p0])
            }

        })
    }

    private fun updateAttention(count: Int) {
        val title = titleList[0] + "($count)"
        tablayout.getTitleView(0).text = title
    }

    private fun updateFans(count: Int) {
        val title = titleList[1] + "($count)"
        tablayout.getTitleView(1).text = title
    }

}

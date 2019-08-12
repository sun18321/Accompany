package com.play.accompany.view

import android.support.v4.view.ViewPager
import com.play.accompany.R
import com.play.accompany.adapter.ViewPagerAdapter
import com.play.accompany.base.BaseActivity
import com.play.accompany.base.BaseFragment
import com.play.accompany.constant.IntentConstant
import com.play.accompany.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_record.*

class ViewPagerActivity : BaseActivity() {
    override fun getLayout(): Int {
        return R.layout.activity_record
    }

    override fun getTag(): String {
       return "ViewPagerActivity"
    }

    override fun initViews() {
        val titleList = arrayListOf("关注", "粉丝")
//        arrayListOf()
//        ViewPagerAdapter(supportFragmentManager,titleList,)
        viewpager.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(p0: Int) {

            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

            }

            override fun onPageSelected(p0: Int) {

            }
        })
    }

    override fun parseIntent() {
        super.parseIntent()
        val code = intent!!.getIntExtra(IntentConstant.INTENT_CODE, -1)
        if (code == -1) {
            ToastUtils.showCommonToast(resources.getString(R.string.data_error))
            this.finish()
            return
        }

    }

}

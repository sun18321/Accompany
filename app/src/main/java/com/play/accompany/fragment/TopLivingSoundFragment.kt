package com.play.accompany.fragment

import android.media.MediaPlayer
import androidx.viewpager2.widget.ViewPager2
import android.view.View
import android.widget.Adapter
import androidx.fragment.app.Fragment
import com.play.accompany.R
import com.play.accompany.adapter.SoundAdapter
import com.play.accompany.base.BaseFragment
import com.play.accompany.present.BottomListener
import com.play.accompany.present.ClickListener
import com.play.accompany.utils.LogUtils
import kotlinx.android.synthetic.main.top_living_fragment.*

class TopLivingSoundFragment : BaseFragment,ClickListener {
    private lateinit var mView: View
    private var mListener: BottomListener? = null
    private val mPLayer = MediaPlayer()

    constructor()

    constructor(listener: BottomListener) {
        mListener = listener
    }

    override fun getLayout(): Int {
        return R.layout.top_living_fragment
    }

    override fun initViews(view: View?) {
        mView = view!!
        val list = ArrayList<Fragment>()
        for (i in 0..5) {
            list.add(LivingSoundFragment(this))
        }
        val soundAdapter = SoundAdapter()
        second_viewpager.adapter = soundAdapter
        second_viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })


        second_viewpager.setOnClickListener {
            LogUtils.d("click", "viewpager click")
        }




    }

    override fun getFragmentName(): String {
        return "TopLivingSoundFragment"
    }

    override fun onViewClick() {
        val isShow = mListener?.isShow
        if (isShow == true) {
            mListener?.onHide()
        }else if (isShow == false) {
            mListener?.onShow()
        }

    }

}
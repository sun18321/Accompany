package com.play.accompany.fragment

import android.view.View
import androidx.fragment.app.Fragment
import com.play.accompany.R
import com.play.accompany.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_living_sound.*
import kotlinx.android.synthetic.main.fragment_living_sound.view.*

class LivingSoundFragment : BaseFragment() {
    private lateinit var mView: View

    override fun getLayout(): Int {
        return R.layout.fragment_living_sound
    }

    override fun initViews(view: View?) {
        mView = view!!
    }

    override fun getFragmentName(): String {
        return "LivingSoundFragment"
    }

    override fun onPause() {
        super.onPause()

    }

    override fun onDestroy() {
//        dashabi.destoryALlAnim()
        mView.living_bg.visibility = View.INVISIBLE
        mView.living_sound.destoryALlAnim()

        super.onDestroy()

    }

}
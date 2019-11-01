package com.play.accompany.fragment

import android.view.View
import com.play.accompany.R
import com.play.accompany.base.BaseFragment
import com.play.accompany.present.ClickListener
import com.play.accompany.utils.LogUtils
import kotlinx.android.synthetic.main.fragment_living_sound.view.*

class LivingSoundFragment : BaseFragment {
    private lateinit var mView: View
    private var mListener: ClickListener? = null

    constructor()

    constructor(listener: ClickListener) {
        mListener = listener
    }

    override fun getLayout(): Int {
        return R.layout.fragment_living_sound
    }

    override fun initViews(view: View?) {
        mView = view!!

        mView.setOnClickListener {
            mListener?.onViewClick()
        }
    }

    override fun getFragmentName(): String {
        return "LivingSoundFragment"
    }

    override fun onPause() {
        super.onPause()
    }


    override fun onDestroy() {
        mView.living_sound.destroy()

        super.onDestroy()
    }

}
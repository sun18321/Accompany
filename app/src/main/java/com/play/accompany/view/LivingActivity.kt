package com.play.accompany.view

import android.content.Intent
import android.os.Bundle
import com.play.accompany.R
import com.play.accompany.base.BaseActivity
import com.play.accompany.constant.IntentConstant
import com.play.accompany.fragment.TopLivingSoundFragment
import com.play.accompany.utils.ToastUtils

class LivingActivity : BaseActivity() {
    private var mIntent: Intent? = null

    override fun getLayout(): Int {
        return R.layout.activity_living
    }

    override fun getTag(): String {
        return "LivingActivity"
    }


    override fun initViews() {
        val soundFragment = TopLivingSoundFragment()
        val bundle = Bundle()
        bundle.putBundle(IntentConstant.INTENT_BUNDLE, mIntent?.getBundleExtra(IntentConstant.INTENT_BUNDLE))
        soundFragment.arguments = bundle

        supportFragmentManager.beginTransaction().replace(R.id.frame, soundFragment).commitNowAllowingStateLoss()
    }

    override fun parseIntent() {
        super.parseIntent()

        if (intent == null) {
            ToastUtils.showCommonToast("数据错误")
            this.finish()
        } else {
            mIntent = intent
        }
    }
}

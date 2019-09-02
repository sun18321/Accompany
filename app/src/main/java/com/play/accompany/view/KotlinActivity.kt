package com.play.accompany.view

import com.play.accompany.R
import com.play.accompany.base.BaseActivity
import com.play.accompany.utils.DateUtils
import kotlinx.android.synthetic.main.activity_kotlin.*

class KotlinActivity : BaseActivity() {
    override fun getLayout(): Int {
        return R.layout.activity_kotlin
    }

    override fun getTag(): String {
        return "KotlinActivity"
    }

    override fun initViews() {
        initToolbar("彩蛋")

        val date = DateUtils.time2Date(System.currentTimeMillis())
        tv_kotlin.text = date
    }

}

package com.play.accompany.design

import android.animation.ValueAnimator
import android.graphics.PointF
import com.play.accompany.bean.MusicAnimBean
import kotlin.math.cos
import kotlin.math.sin

class MusicAnim(private val mBean: MusicAnimBean, private val mListener: (position: Int, anim: ValueAnimator) -> Unit) {
    init {
        createAnim()
    }

    private fun createAnim() {
//        ValueAnimator.ofObject()
    }




}
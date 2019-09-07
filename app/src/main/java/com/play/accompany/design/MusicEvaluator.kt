package com.play.accompany.design

import android.animation.TypeEvaluator
import android.graphics.PointF

class MusicEvaluator(private val firstPath: PointF,private val secondPath: PointF):TypeEvaluator<PointF> {
    override fun evaluate(fraction: Float, startValue: PointF?, endValue: PointF?): PointF {
        val currentPoint = PointF()
        var fractionLeft = 1 - fraction

        currentPoint.x = fractionLeft*fractionLeft*fractionLeft*startValue!!.x + 3*fractionLeft*fractionLeft*fraction*firstPath.x
        +3 * fractionLeft * fraction * fraction * secondPath.x + fraction * fraction * fraction * endValue!!.x
        currentPoint.y =  fractionLeft*fractionLeft*fractionLeft*startValue.y + 3*fractionLeft*fractionLeft*fraction*firstPath.y
        +3 * fractionLeft * fraction * fraction * secondPath.y + fraction * fraction * fraction * endValue.y

        return currentPoint
    }
}
package com.play.accompany.utils

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView

object  SpanUtils {
    fun setSpan(tv: TextView, content: String, startIndex: Int, endIndex: Int, color: Int, action: () -> Unit) {
        val clickSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                action()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)

                ds.color = color
            }
        }


        val spanString = SpannableString(content)
        spanString.setSpan(clickSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv.text = spanString
        tv.movementMethod = LinkMovementMethod.getInstance()
    }
}
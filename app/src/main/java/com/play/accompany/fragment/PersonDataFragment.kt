package com.play.accompany.fragment

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.play.accompany.R
import com.play.accompany.base.BaseFragment
import com.play.accompany.bean.TopGameBean
import com.play.accompany.bean.UserInfo
import com.play.accompany.constant.IntentConstant
import com.play.accompany.utils.LogUtils
import com.play.accompany.utils.StringUtils
import com.play.accompany.view.AccompanyApplication
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import kotlinx.android.synthetic.main.activity_collapse_user_center.*
import kotlinx.android.synthetic.main.fragment_person_data.*

class PersonDataFragment:BaseFragment() {
    private lateinit var mInfo: UserInfo
    private lateinit var mAllList: List<TopGameBean>

    override fun getLayout(): Int {
        return R.layout.fragment_person_data
    }

    override fun initViews(view: View?) {
        mInfo= arguments?.getSerializable(IntentConstant.INTENT_USER) as UserInfo
        tv_bottom_name.text = mInfo.name
        tv_id.text = mInfo.userId
        tv_constellation.text = StringUtils.getConstellationByString(mInfo.date)
        tv_interest.text = mInfo.interest
        tv_profession.text = mInfo.profession

        AccompanyApplication.getGameList{
            mAllList = it
            displayGame()
        }

        if (!TextUtils.isEmpty(mInfo.audioUrl)) {
            sound_view.visibility = View.VISIBLE
            sound_view.setData(mInfo.audioUrl, mInfo.audioLen)
        }
    }

    private fun displayGame() {
            val list = mInfo.gameType
            if (list == null || list.isEmpty() || mAllList.isEmpty()) {
                return
            }
            var marginLayoutParams: ViewGroup.MarginLayoutParams
            for (property in list) {
                for (topGameBean in mAllList) {
                    if (property.type == topGameBean.typeId) {
                        val tv = TextView(mContext)
                        val frameColor = Color.parseColor(topGameBean.tagBg)
                        val wordColor = Color.parseColor(topGameBean.tagFront)

//                        LogUtils.d("color", "color:" + topGameBean.tagBg + "int:" + wordColor)

                        val drawable = ActivityCompat.getDrawable(mContext, R.drawable.colorful_frame) as GradientDrawable?
                        drawable!!.setColor(frameColor)
                        drawable.setStroke(QMUIDisplayHelper.dp2px(mContext, 1), wordColor)
                        tv.setTextColor(wordColor)
                        tv.text = topGameBean.name
                        tv.background = drawable
                        tv.textSize = 12f
                        tv.setPadding(QMUIDisplayHelper.dp2px(mContext, 12), QMUIDisplayHelper.dp2px(mContext, 2), QMUIDisplayHelper.dp2px(mContext, 12), QMUIDisplayHelper.dp2px(mContext, 2))
                        val layoutParams = tv.layoutParams
                        marginLayoutParams = if (layoutParams is ViewGroup.MarginLayoutParams) {
                            layoutParams
                        } else {
                            ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                        }
                        marginLayoutParams.setMargins(QMUIDisplayHelper.dp2px(mContext, 2), QMUIDisplayHelper.dp2px(mContext, 2), QMUIDisplayHelper.dp2px(mContext, 2), QMUIDisplayHelper.dp2px(mContext, 2))
                        tv.layoutParams = marginLayoutParams
                        flowlayout.addView(tv)
                    }
                }
            }
    }

    override fun getFragmentName(): String {
        return "PersonDataFragment"
    }

    override fun onStop() {
        super.onStop()
        sound_view?.stopPlay()
    }

    override fun onDestroy() {
        super.onDestroy()
        sound_view?.destroy()
    }
}
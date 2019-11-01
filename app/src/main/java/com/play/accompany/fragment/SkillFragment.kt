package com.play.accompany.fragment

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.play.accompany.R
import com.play.accompany.adapter.SkillAdapter
import com.play.accompany.base.BaseFragment
import com.play.accompany.bean.UserInfo
import com.play.accompany.constant.IntentConstant
import com.play.accompany.constant.SpConstant
import com.play.accompany.utils.SPUtils
import com.play.accompany.utils.ToastUtils
import com.play.accompany.view.OrderActivity
import kotlinx.android.synthetic.main.fragment_skill.*

class SkillFragment:BaseFragment() {
    private lateinit var mInfo:UserInfo

    override fun getLayout(): Int {
        return R.layout.fragment_skill
    }

    override fun initViews(view: View?) {
        mInfo = arguments?.getSerializable(IntentConstant.INTENT_USER) as UserInfo
        recycler.run {
            layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL))
            adapter = SkillAdapter(mContext,mInfo.gameType){
                goOrder(it)
            }
        }

    }

    private fun goOrder(position: Int) {
        if (SPUtils.getInstance().getString(SpConstant.MY_USER_ID) == mInfo.userId) {
            ToastUtils.showCommonToast("这是你自己哦！")
            return
        }

        mInfo.selectedPosition = position
        startActivity(Intent(mContext, OrderActivity::class.java).putExtra(IntentConstant.INTENT_USER, mInfo))
    }

    override fun getFragmentName(): String {
        return "SkillFragment"
    }
}
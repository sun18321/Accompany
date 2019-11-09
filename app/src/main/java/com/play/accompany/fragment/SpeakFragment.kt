package com.play.accompany.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.reflect.TypeToken
import com.play.accompany.R
import com.play.accompany.adapter.SpeakAdapter
import com.play.accompany.base.BaseFragment
import com.play.accompany.bean.BaseDecodeBean
import com.play.accompany.bean.RequestSpeakBean
import com.play.accompany.bean.ResponseSpeakBean
import com.play.accompany.constant.IntentConstant
import com.play.accompany.constant.OtherConstant
import com.play.accompany.constant.SpConstant
import com.play.accompany.net.AccompanyRequest
import com.play.accompany.net.NetFactory
import com.play.accompany.net.NetListener
import com.play.accompany.utils.*
import com.play.accompany.view.LivingActivity
import com.play.accompany.view.MasterActivity
import com.play.accompany.view.ReleaseSpeakActivity
import kotlinx.android.synthetic.main.fragment_speak.*

class SpeakFragment:BaseFragment() {
    private var mIsMe = false
    private var mUserId: String? = null
    private var mCurrentPage = 0
    private val mPageMax = 20
    private var mAdapter: SpeakAdapter? = null
    private val mList = mutableListOf<ResponseSpeakBean>()

    override fun getLayout(): Int {
        return R.layout.fragment_speak
    }

    override fun initViews(view: View?) {
        mUserId = arguments?.getString(IntentConstant.INTENT_USER_ID)
        mIsMe = TextUtils.equals(mUserId, SPUtils.getInstance().getString(SpConstant.MY_USER_ID))

        refresh_layout.setOnRefreshListener {
            mCurrentPage = 0
            requestData()
        }

        refresh_layout.setOnLoadMoreListener {
            mCurrentPage++
            requestData()
        }
        updateView()
    }

    override fun onResume() {
        super.onResume()

        mAdapter?.notifyDataSetChanged()

        if (mIsMe && SPUtils.getInstance().getInt(SpConstant.USER_TYPE) == OtherConstant.USER_TYPE_ACCOMPANY) {
            tv_no_permission.visibility = View.GONE
            updateView()
        }
    }

    private fun updateView() {
        if (mIsMe) {
            if (SPUtils.getInstance().getInt(SpConstant.USER_TYPE) == OtherConstant.USER_TYPE_ACCOMPANY) {
                setAdapter()
                refresh_layout.autoRefresh()
            } else {
                go2master()
            }
        } else {
            setAdapter()
            refresh_layout.autoRefresh()
        }
    }

    override fun getFragmentName(): String {
        return "SpeakFragment"
    }

    private fun setAdapter() {
        recycler.layoutManager = GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false)
        mAdapter = SpeakAdapter(mIsMe){
            position ->
            run {
                if (mIsMe) {
                    if (position == 0) {
                        startActivityForResult(Intent(mContext, ReleaseSpeakActivity::class.java), 100)
                    }else{
                        val bundle = Bundle()
                        bundle.putSerializable(IntentConstant.INTENT_SPEAK_LIST, ArrayList(mList))
                        bundle.putInt(IntentConstant.INTENT_SPEAK_POSITION, position - 1)
                        bundle.putBoolean(IntentConstant.INTENT_IS_ME,mIsMe)
                        mContext.startActivity(Intent(mContext, LivingActivity::class.java).putExtra(IntentConstant.INTENT_BUNDLE, bundle))
                    }
                }else{
                    val bundle = Bundle()
                    bundle.putSerializable(IntentConstant.INTENT_SPEAK_LIST, ArrayList(mList))
                    bundle.putInt(IntentConstant.INTENT_SPEAK_POSITION, position)
                    bundle.putBoolean(IntentConstant.INTENT_IS_ME,mIsMe)
                    mContext.startActivity(Intent(mContext, LivingActivity::class.java).putExtra(IntentConstant.INTENT_BUNDLE, bundle))
                }
            }
        }
        recycler.adapter = mAdapter
    }

    private fun requestData() {
        val bean = RequestSpeakBean(SPUtils.getInstance().getString(SpConstant.APP_TOKEN), mUserId!!, mCurrentPage)
        val json = GsonUtils.toJson(bean)
        val request = AccompanyRequest()
        request.beginRequest(NetFactory.getNetRequest().netService.querySpeak(EncodeUtils.encodeInBody(json)),
                object :TypeToken<BaseDecodeBean<List<ResponseSpeakBean>>>(){}.type,object :NetListener<List<ResponseSpeakBean>>{
            override fun onSuccess(t: List<ResponseSpeakBean>?) {
                refresh_layout.finishRefresh()
                refresh_layout.finishLoadMore()

                if (t == null || t.size < mPageMax) {
//                    refresh_layout.finishLoadMoreWithNoMoreData()
                }
                if (mCurrentPage == 0) {
                    mList.clear()
                }
                t?.let { mList.addAll(it) }
                mAdapter!!.setData(mList)
            }

            override fun onFailed(errCode: Int) {
            }

            override fun onError() {
            }

            override fun onComplete() {
            }

        })
    }

    private fun go2master() {
        tv_no_permission.visibility = View.VISIBLE
        SpanUtils.setSpan(tv_no_permission,resources.getString(R.string.guide_apply_master),2,8, Color.parseColor("#FF63A0")){
            startActivity(Intent(mContext, MasterActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            refresh_layout.autoRefresh()
        }
    }
}
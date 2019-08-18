package com.play.accompany.fragment

import android.app.Application
import android.content.Intent
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.google.gson.reflect.TypeToken
import com.play.accompany.R
import com.play.accompany.adapter.AttentionAdapter
import com.play.accompany.base.BaseFragment
import com.play.accompany.bean.AttentionBean
import com.play.accompany.bean.BaseDecodeBean
import com.play.accompany.bean.FansBean
import com.play.accompany.bean.UserInfo
import com.play.accompany.constant.IntentConstant
import com.play.accompany.constant.OtherConstant
import com.play.accompany.constant.SpConstant
import com.play.accompany.net.AccompanyRequest
import com.play.accompany.net.NetFactory
import com.play.accompany.net.NetListener
import com.play.accompany.utils.EncodeUtils
import com.play.accompany.utils.GsonUtils
import com.play.accompany.utils.SPUtils
import com.play.accompany.utils.ToastUtils
import com.play.accompany.view.AccompanyApplication
import com.play.accompany.view.UserCenterActivity
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import kotlinx.android.synthetic.main.refresh_layout.*

class FansFragment : BaseFragment() {
    private lateinit var mList: ArrayList<FansBean>
    private var mAdapter: AttentionAdapter? = null
    private val code = 1002
    private var startPosition = -1

    companion object{
        private lateinit var mListener: (Int) -> Unit
        fun newInstance(listener: (Int) -> Unit): FansFragment {
            mListener = listener
            return FansFragment()
        }
    }

    override fun getLayout(): Int = R.layout.refresh_layout

    override fun initViews(view: View?) {
        recycler.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        recycler.addItemDecoration(DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL))
        refresh_layout.setOnRefreshListener {
            requestData()
            it.finishRefresh()
        }
        requestData()
    }

    private fun setRecycler() {
        getAttentionList()
        if (mAdapter == null) {
            mAdapter = AttentionAdapter(mContext,mList)
            mAdapter!!.setListener(object :AttentionAdapter.AttentionListener{
                override fun goUser(id: String,position: Int) {
                    goUserCenter(id, position)
                }

                override fun goAttention(attention: Boolean, position: Int, id: String) {
                    dealAttention(attention, position, id)
                }
            })
            recycler.adapter = mAdapter
        } else {
            mAdapter!!.setData(ArrayList(mList))
        }
        mListener!!(mList.size)
    }

    override fun getFragmentName(): String = "FansFragment"



    private fun getAttentionList() {
        AccompanyApplication.getAttentionList { list ->
            for (fansBean in mList) {
                fansBean.attention = list.contains(fansBean.userId)
            }
        }
    }

    private fun dealAttention(attention: Boolean, position: Int, id: String) {
        if (attention) {
            QMUIDialog.MessageDialogBuilder(mContext).setMessage("确定要取消关注吗？").addAction("取消") { dialog, _ -> dialog.dismiss() }.addAction("确定") { dialog, _ ->
                dialog!!.dismiss()
                attention(attention,id, position)
            }.create().show()
        } else {
            attention(attention, id, position)
        }
    }

    private fun attention(attention: Boolean, id: String, position: Int) {
        val bean = AttentionBean()
        bean.token = SPUtils.getInstance().getString(SpConstant.APP_TOKEN)
        bean.targetId = id
        if (attention) {
            bean.flag = OtherConstant.ATTEMTION_FLAG_CANCEL
        } else {
            bean.flag = OtherConstant.ATTENTION_FLAG
        }
        AccompanyRequest().beginRequest(NetFactory.getNetRequest().netService.attention(EncodeUtils.encodeInBody(GsonUtils.toJson(bean))),object:TypeToken<BaseDecodeBean<*>>(){}.type,object :NetListener<String>{
            override fun onSuccess(t: String?) {
                if (attention) {
                    ToastUtils.showCommonToast("取消关注")
                } else {
                    ToastUtils.showCommonToast("关注成功")
                }
                mList[position].attention = !attention
                mAdapter!!.notifyItemChanged(position)
                AccompanyApplication.setAttentionChange(id)
            }

            override fun onFailed(errCode: Int) {
            }

            override fun onError() {
            }

            override fun onComplete() {
            }

        })
    }


    private fun goUserCenter(id: String, position: Int) {
        val intent = Intent(mContext, UserCenterActivity::class.java)
        val info = UserInfo()
        info.userId = id
        info.fromChat = true
        intent.putExtra(IntentConstant.INTENT_USER, info)
        startPosition = position
        startActivityForResult(intent, code)
    }

    private fun setDataChange(attention: Boolean) {
        if (startPosition == -1) {
            return
        }
        if (mList[startPosition].attention == attention) {
            return
        }
        mList[startPosition].attention = attention
        mAdapter!!.notifyItemChanged(startPosition)
        AccompanyApplication.setAttentionChange(mList[startPosition].userId)
    }


    private fun requestData() {
        AccompanyRequest().beginRequest(NetFactory.getNetRequest().netService.getFansDetail(EncodeUtils.encodeToken()),object :TypeToken<BaseDecodeBean<List<FansBean>>>(){}.type, object : NetListener<List<FansBean>>{
            override fun onSuccess(list: List<FansBean>?) {
                SPUtils.getInstance().put(SpConstant.FANS_COUNT, list!!.size)
                mList = ArrayList(list)
                setRecycler()
            }

            override fun onFailed(errCode: Int) {
            }

            override fun onError() {
            }

            override fun onComplete() {
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == code){
            val extra = data!!.getBooleanExtra(OtherConstant.IS_ATTENTION, false)
            setDataChange(extra)
        }
    }


}
package com.play.accompany.utils

import com.google.gson.reflect.TypeToken
import com.play.accompany.bean.AttentionBean
import com.play.accompany.bean.BaseDecodeBean
import com.play.accompany.bean.OnlyCodeBean
import com.play.accompany.constant.SpConstant
import com.play.accompany.net.AccompanyRequest
import com.play.accompany.net.NetFactory
import com.play.accompany.net.NetListener
import com.play.accompany.view.AccompanyApplication

object RequestUtils {
    //1关注0取消

    fun dealAttention(id: String, event: Int, callback: (Boolean) -> Unit) {
        val bean = AttentionBean()
        bean.token = SPUtils.getInstance().getString(SpConstant.APP_TOKEN)
        bean.targetId = id
        bean.flag = event
        AccompanyRequest().beginRequest(NetFactory.getNetRequest().netService.attention(EncodeUtils.encodeInBody(GsonUtils.toJson(bean)))
        ,object:TypeToken<BaseDecodeBean<OnlyCodeBean>>(){}.type,object :NetListener<OnlyCodeBean>{
            override fun onSuccess(t: OnlyCodeBean?) {
                val attention: Boolean
                if (event == 1) {
                    ToastUtils.showCommonToast("关注成功")
                    attention = true
                } else {
                    ToastUtils.showCommonToast("取消关注")
                    attention = false
                }
                callback(attention)
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
}
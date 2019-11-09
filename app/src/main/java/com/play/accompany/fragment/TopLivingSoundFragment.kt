package com.play.accompany.fragment

import androidx.viewpager2.widget.ViewPager2
import android.view.View
import com.google.gson.reflect.TypeToken
import com.play.accompany.R
import com.play.accompany.adapter.SoundAdapter
import com.play.accompany.base.BaseFragment
import com.play.accompany.bean.*
import com.play.accompany.constant.*
import com.play.accompany.design.LivingSoundPLayer
import com.play.accompany.design.SPEAK_TYPE_CHANGE_PAUSE
import com.play.accompany.design.SPEAK_TYPE_DELETE
import com.play.accompany.design.SPEAK_TYPE_SHOW_HIDE
import com.play.accompany.net.AccompanyRequest
import com.play.accompany.net.NetFactory
import com.play.accompany.net.NetListener
import com.play.accompany.present.BottomListener
import com.play.accompany.present.ClickListener
import com.play.accompany.utils.*
import com.play.accompany.view.LivingActivity
import kotlinx.android.synthetic.main.top_living_fragment.*

class TopLivingSoundFragment : BaseFragment,ClickListener {
    private var mListener: BottomListener? = null
    private var mPaging = 0
    private var mAdapter: SoundAdapter? = null
    private var mViewPagerIndex = 0
    private var mInit = true
    private val mPlayer = LivingSoundPLayer()
    private var mIsMe = false

    constructor()

    constructor(listener: BottomListener) {
        mListener = listener
    }

    override fun getLayout(): Int {
        return R.layout.top_living_fragment
    }

    override fun initViews(view: View?) {


        LogUtils.d("hash_code","fragment:${hashCode()}-----player:${mPlayer.hashCode()}")

        second_viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                LogUtils.d("top_live","view pager selected")

                if (mInit) {
                    mInit = false
                } else {
                    LogUtils.d("select", "select:$position----index:$mViewPagerIndex")

                    if (position > mViewPagerIndex) {
                        LogUtils.d("select", "play next")
                        mPlayer.playNext()
                    } else {
                        LogUtils.d("select", "play previous")
                        mPlayer.playPrevious()
                    }
                    mAdapter?.getLivingView(mViewPagerIndex)?.pause()
                    mViewPagerIndex = position
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })

        second_viewpager.setOnClickListener {
            LogUtils.d("click", "viewpager click")
        }
        val data = arguments?.getBundle(IntentConstant.INTENT_BUNDLE)
        if (data == null) {
            getSoundData()
        } else {
            mViewPagerIndex = data.getInt(IntentConstant.INTENT_SPEAK_POSITION)
            val sound = data.getSerializable(IntentConstant.INTENT_SPEAK_LIST) as List<ResponseSpeakBean>
            mIsMe = data.getBoolean(IntentConstant.INTENT_IS_ME, false)
            LogUtils.d("person","is me:$mIsMe")
            setData(sound)
        }
    }

    fun setData(list: List<ResponseSpeakBean>) {
        mAdapter = SoundAdapter(list,mIsMe){
            when (it.type) {
                SPEAK_TYPE_DELETE -> {
                    showDialog(it.info!!)
                }
                SPEAK_TYPE_CHANGE_PAUSE -> {
                    playerOnPauseChange()
                }
                SPEAK_TYPE_SHOW_HIDE ->{
                    onViewClick()
                }
            }
        }
        second_viewpager.adapter = mAdapter

        second_viewpager.setCurrentItem(mViewPagerIndex, false)
        mPlayer.setSource(list, mViewPagerIndex) {
            when (it.getType()) {
                PLAYER_STATUS_LOADING -> {

                }

                PLAYER_STATUS_START -> {
                    mAdapter?.getLivingView(mViewPagerIndex)?.startAllAnim()
                }

                PLAYER_STATUS_PAUSE -> {
                    mAdapter?.getLivingView(mViewPagerIndex)?.pause()
                }

                PLAYER_STATUS_PLAYING -> {
                    second_viewpager.post {
                        mAdapter?.getLivingView(mViewPagerIndex)?.setProgress(it.getProgress()!!, it.getTotalLength()!!)
                    }
                }
                PLAYER_STATUS_ERROR ->{
                    second_viewpager.post {
                        ToastUtils.showCommonToast("播放失败")
                    }
                }
            }
        }
    }

    private fun showDialog(info: ResponseSpeakBean) {
        DialogUtils.showCommonDialog(mContext,"确定要删除这条说说吗?"){
            if (it) {
                deleteSpeak(info)
            }
        }
    }

    private fun deleteSpeak(info: ResponseSpeakBean) {
        val bean = DeleteSpeakBean(SPUtils.getInstance().getString(SpConstant.APP_TOKEN), info.id)
        AccompanyRequest().beginRequest(NetFactory.getNetRequest().netService.deleteVoice(EncodeUtils.encodeInBody(GsonUtils.toJson(bean))),
                object :TypeToken<BaseDecodeBean<OnlyCodeBean>>(){}.type,object:NetListener<OnlyCodeBean>{
            override fun onSuccess(t: OnlyCodeBean?) {
                LogUtils.d("code", "code:${t?.code}")

                    ToastUtils.showCommonToast("删除成功")
                    if (activity is LivingActivity) {
                        val livingActivity = activity as LivingActivity
                        livingActivity.finish()
                }
            }

            override fun onFailed(errCode: Int) {
            }

            override fun onError() {
            }

            override fun onComplete() {
            }

        })

    }

    private fun playerOnPauseChange() {
        if (mPlayer.getPause()) {
            mPlayer.reStart()
        } else {
            mPlayer.pause()
        }
    }

    private fun getSoundData() {
        val bean = RecommandSpeakBean(SPUtils.getInstance().getString(SpConstant.APP_TOKEN), mPaging)
        val request = AccompanyRequest()
        request.beginRequest(NetFactory.getNetRequest().netService.getRecommendSound(EncodeUtils.encodeInBody(GsonUtils.toJson(bean))),
                object :TypeToken<BaseDecodeBean<List<ResponseSpeakBean>>>(){}.type,object :NetListener<List<ResponseSpeakBean>>{
            override fun onSuccess(t: List<ResponseSpeakBean>?) {
                LogUtils.d("sound","sound:$t")
                 t?.run {
                     setData(t)
                 }
            }

            override fun onFailed(errCode: Int) {

            }

            override fun onError() {

            }

            override fun onComplete() {

            }
        })
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (hidden) {
            mPlayer.pause()
        }else if (!hidden && !mInit) {
            mPlayer.reStart()
        }
    }

    override fun onPause() {
        super.onPause()

        mPlayer.pause()
    }

    override fun onResume() {
        super.onResume()

        if (mPlayer.getPause()&&!isHidden) {
            mPlayer.reStart()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        mPlayer.destroy()
        mAdapter?.getLivingView(mViewPagerIndex)?.destroy()
    }

    override fun getFragmentName(): String {
        return "TopLivingSoundFragment"
    }

    override fun onViewClick() {
        val isShow = mListener?.isShow
        if (isShow == true) {
            mListener?.onHide()
        }else if (isShow == false) {
            mListener?.onShow()
        }
    }
}
package com.play.accompany.view

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.gson.reflect.TypeToken
import com.play.accompany.R
import com.play.accompany.adapter.UserCenterAdapter
import com.play.accompany.base.BaseActivity
import com.play.accompany.bean.*
import com.play.accompany.constant.IntentConstant
import com.play.accompany.constant.OtherConstant
import com.play.accompany.constant.SpConstant
import com.play.accompany.fragment.PersonDataFragment
import com.play.accompany.fragment.SkillFragment
import com.play.accompany.fragment.SpeakFragment
import com.play.accompany.net.AccompanyRequest
import com.play.accompany.net.NetFactory
import com.play.accompany.net.NetListener
import com.play.accompany.utils.*
import kotlinx.android.synthetic.main.activity_collapse_user_center.*

class CollapseUserCenterActivity : BaseActivity() {
    private var mIsMe = false
    private var mPersonDataFragment: PersonDataFragment? = null
    private var mSkillFragment: SkillFragment? = null
    private var mSpeakFragment: SpeakFragment? = null
    private lateinit var mUserId: String
    private var mAttention = false
    private var mDealAttention = false
    private lateinit var mAttentionList: MutableList<String>


    override fun getLayout(): Int {
        return R.layout.activity_collapse_user_center
    }

    override fun getTag(): String {
       return "CollapseUserCenterActivity"
    }

    override fun parseIntent() {
        super.parseIntent()

        mUserId = intent?.getStringExtra(IntentConstant.INTENT_USER_ID)!!
        if (mUserId == SPUtils.getInstance().getString(SpConstant.MY_USER_ID)) {
            mIsMe = true
        }
    }

    override fun initViews() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        collapse.setCollapsedTitleTextColor(resources.getColor(R.color.white))
        toolbar.setNavigationOnClickListener {
            this.finish()
        }
        getUserData()
    }

    private fun otherViews(info: UserInfo) {
        if (mIsMe) {
            button_chat.visibility = View.INVISIBLE
            button_attention.visibility = View.INVISIBLE
        }
        GlideUtils.commonLoad(this,info.url,top_head)
        collapse_name.text = info.name
        toolbar.title = info.name
        if (info.gender == OtherConstant.GENDER_FEMALE) {
            lin_gender.setBackgroundResource(R.drawable.female_bg)
            img_gender.setImageResource(R.drawable.female)
        } else {
            lin_gender.setBackgroundResource(R.drawable.male_bg)
            img_gender.setImageResource(R.drawable.male)
        }
        val age = DateUtils.getAge(info.date)
        tv_age.text = age.toString()
        tv_rating.text = info.grade.toString()
        tv_sign.text = info.sign
        button_chat.setOnClickListener {
            ChatManager.instance.startChat(this, info.userId, info.name)
        }

        button_attention.setOnClickListener {
            if (mAttention) {
                DialogUtils.showCommonDialog(this,"确定要取消关注吗？"){
                    if (it) {
                        dealAttention(info.userId)
                    }
                }
            } else {
                dealAttention(info.userId)
            }
        }

        AccompanyApplication.getAttentionList {
            mAttentionList = it
            when {
                mAttentionList.isNullOrEmpty() -> setDisAttention()
                mAttentionList.contains(info.userId) -> setAttention()
                else -> setDisAttention()
            }
        }

        if (mPersonDataFragment == null) {
            mPersonDataFragment = PersonDataFragment()
            val bundle = Bundle()
            bundle.putSerializable(IntentConstant.INTENT_USER, info)
            mPersonDataFragment?.arguments = bundle
        }

        if (mSkillFragment == null) {
            mSkillFragment = SkillFragment()
            val bundle = Bundle()
            bundle.putSerializable(IntentConstant.INTENT_USER, info)
            mSkillFragment?.arguments = bundle
        }

        if (mSpeakFragment == null) {
            mSpeakFragment = SpeakFragment()
            val bundle = Bundle()
            bundle.putString(IntentConstant.INTENT_USER_ID, mUserId)
            mSpeakFragment?.arguments = bundle
        }


        val titleList = arrayListOf("资料", "技能", "说说")
        val fragmentList = arrayListOf<Fragment>(mPersonDataFragment!!, mSkillFragment!!, mSpeakFragment!!)
        val adapter = UserCenterAdapter(supportFragmentManager, titleList, fragmentList)
        viewpager.offscreenPageLimit = 2
        viewpager.adapter = adapter
        tab_layout.setViewPager(viewpager)
    }

    private fun getUserData() {
        if (TextUtils.isEmpty(mUserId)) {
            ToastUtils.showCommonToast(resources.getString(R.string.data_error))
            this.finish()
            return
        }
        val bean = FindUserBean()
        bean.token = SPUtils.getInstance().getString(SpConstant.APP_TOKEN)
        bean.findId = mUserId
        val json = GsonUtils.toJson(bean)
        val body = EncodeUtils.encodeInBody(json)
        val request = AccompanyRequest()
        request.beginRequest(NetFactory.getNetRequest().netService.getUserInfo(body),object : TypeToken<BaseDecodeBean<List<UserInfo>>>(){}.type,
                object : NetListener<List<UserInfo>> {
                    override fun onSuccess(list: List<UserInfo>?) {
                        if (!list.isNullOrEmpty()) {
                            otherViews(list[0])
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

    private fun dealAttention(id:String) {
        if (mDealAttention) {
            return
        }
        mDealAttention = true
        val bean = AttentionBean()
        bean.token = SPUtils.getInstance().getString(SpConstant.APP_TOKEN)
        bean.targetId = id
        if (mAttention) {
            bean.flag = OtherConstant.ATTEMTION_FLAG_CANCEL
        } else {
            bean.flag = OtherConstant.ATTENTION_FLAG
        }
        val json = GsonUtils.toJson(bean)
        val body = EncodeUtils.encodeInBody(json)
        val request = AccompanyRequest()
        request.beginRequest(NetFactory.getNetRequest().netService.attention(body),object :TypeToken<BaseDecodeBean<OnlyCodeBean>>() {}.type,
                object : NetListener<OnlyCodeBean> {
            override fun onSuccess(o: OnlyCodeBean?) {
                mDealAttention = false
                dismissDialog()
                if (mAttention) {
                    ToastUtils.showCommonToast(resources.getString(R.string.attention_cancel_success))
                    setDisAttention()
                } else {
                    ToastUtils.showCommonToast(resources.getString(R.string.attention_success))
                    setAttention()
                }
                saveAttention(id)
            }

            override fun onFailed(errCode: Int) {

            }

            override fun onError() {

            }

            override fun onComplete() {
                dismissDialog()
                mDealAttention = false
            }
        })

    }

    private fun setAttention() {
        mAttention = true
        button_attention.background = ActivityCompat.getDrawable(this, R.drawable.button_black)
        button_attention.text = "已关注"
        button_attention.setTextColor(ActivityCompat.getColor(this, R.color.text_dis_attention))
    }

    private fun setDisAttention() {
        mAttention = false
        button_attention.background = ActivityCompat.getDrawable(this, R.drawable.ripple_blue_button)
        button_attention.text = "关注"
        button_attention.setTextColor(ActivityCompat.getColor(this, R.color.white))
    }

    private fun saveAttention(id:String) {
        if (mAttentionList.contains(id)) {
            mAttentionList.remove(id)
        } else {
            mAttentionList.add(id)
        }
        AccompanyApplication.setAttentionList(mAttentionList)
    }
}

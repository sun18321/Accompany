package com.play.accompany.design

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.PointF
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.play.accompany.R
import com.play.accompany.bean.*
import com.play.accompany.constant.SpConstant
import com.play.accompany.net.AccompanyRequest
import com.play.accompany.net.NetFactory
import com.play.accompany.net.NetListener
import com.play.accompany.utils.*
import com.play.accompany.view.AccompanyApplication
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import io.rong.imkit.RongIM
import io.rong.imlib.RongIMClient
import kotlinx.android.synthetic.main.view_living_sound.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class LivingSoundView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var mContext: Context = context
    private var mDiameter = 0
    private var mAnimWidth = 0
    private lateinit var mCenter: PointF
    private val mAnimPeriod = 5000L
    private val mMusicAnimPeriod = 2500L
    private val mMusicCount = 5
    private var mSpreadAnimOne: ObjectAnimator? = null
    private var mSpreadAnimTwo: ObjectAnimator? = null
    private var mRotateAnim: ObjectAnimator? = null
    private var mMusicAnim: ValueAnimator? = null
    private var mRandom = Random()
    private val mMusicResourceList = arrayListOf(R.mipmap.music_one, R.mipmap.music_two, R.mipmap.music_three)
    private var mMusicDataList = ArrayList<MusicAnimBean>()
    private var mMusicAnimCancel = false
    private val mAttentionText = "已关注"
    private val mDisAttentionText = "+关注"
    private var mLike = false
    private var mNet = false

    init {
        initView()
        initAnim()
    }

    private fun initView() {
        val view = View.inflate(mContext, R.layout.view_living_sound, this)
        val mAnimWidth = QMUIDisplayHelper.getScreenWidth(mContext)
        val parentParams: RelativeLayout.LayoutParams = view.anim_parent.layoutParams as LayoutParams
        parentParams.width = mAnimWidth
        parentParams.height = mAnimWidth
        view.anim_parent.layoutParams = parentParams

        val dishParams: RelativeLayout.LayoutParams = view.img_dish.layoutParams as LayoutParams
        mDiameter = mAnimWidth / 2
        val dishDiameter = (mDiameter * 1.1).toInt()

        dishParams.height = dishDiameter
        dishParams.width = dishDiameter
        view.img_dish.cornerRadius = dishDiameter / 2.toFloat()
        view.img_dish.layoutParams = dishParams

        val params = dish_bg.layoutParams as LayoutParams
        params.height = dishDiameter + QMUIDisplayHelper.dpToPx(8)
        params.width = dishDiameter + QMUIDisplayHelper.dpToPx(8)
        dish_bg.layoutParams = params

        val spreadParams: RelativeLayout.LayoutParams = view.img_spread_one.layoutParams as LayoutParams
        spreadParams.height = mDiameter - 10
        spreadParams.width = mDiameter - 10
        view.img_spread_one.layoutParams = spreadParams
        view.img_spread_two.layoutParams = spreadParams

        mCenter = PointF(view.anim_parent.x + mDiameter, view.anim_parent.y + mDiameter)

        initMusicData()

        img_dish.setOnClickListener {
            if (!AppUtils.isQuickCLick()) {
                changePlay()
            }
        }

        img_play.setOnClickListener {
            if (!AppUtils.isQuickCLick()) {
                changePlay()
            }
        }
    }

    private fun changePlay() {
        if (img_play.visibility == View.GONE) {

        }
    }

    private fun initMusicData() {
        for (i in 1..mMusicCount) {
            val image = ImageView(mContext)
            val index = mRandom.nextInt(3)
            image.setImageResource(mMusicResourceList[index])
            image.visibility = View.INVISIBLE
            anim_parent.addView(image)
            val bean = MusicAnimBean()
            bean.img = image
            val pathList = createMusicAnimPath()
            bean.startPonit = pathList[0]
            bean.firstPath = pathList[1]
            bean.secondPath = pathList[2]
            bean.endPonit = pathList[3]
            mMusicDataList.add(bean)
        }
    }

    private fun createNewMusicData() {
        for (bean in mMusicDataList) {
            val path = createMusicAnimPath()
            bean.startPonit = path[0]
            bean.firstPath = path[1]
            bean.secondPath = path[2]
            bean.endPonit = path[3]
        }

        mMusicAnim?.start()
    }


    private fun initAnim() {
        mRotateAnim = ObjectAnimator.ofFloat(img_dish, "rotation", 0f, 360f)
        mRotateAnim?.duration = mAnimPeriod
        mRotateAnim?.interpolator = LinearInterpolator()
        mRotateAnim?.repeatCount = ValueAnimator.INFINITE

        val alpha = PropertyValuesHolder.ofFloat("alpha", 1f, 0f)
        val scaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 2f)
        val scaleY = PropertyValuesHolder.ofFloat("scaleY", 1f, 2f)
        mSpreadAnimOne = ObjectAnimator.ofPropertyValuesHolder(img_spread_one, alpha, scaleX, scaleY)
        mSpreadAnimOne?.duration = mAnimPeriod
        mSpreadAnimOne?.interpolator = LinearInterpolator()
        mSpreadAnimOne?.repeatCount = ValueAnimator.INFINITE

        mSpreadAnimTwo = ObjectAnimator.ofPropertyValuesHolder(img_spread_two, alpha, scaleX, scaleY)
        mSpreadAnimTwo?.duration = mAnimPeriod
        mSpreadAnimTwo?.interpolator = LinearInterpolator()
        mSpreadAnimTwo?.repeatCount = ValueAnimator.INFINITE
        mSpreadAnimTwo?.startDelay = 2500

        mMusicAnim = ValueAnimator.ofInt(0, 1)
        mMusicAnim?.interpolator = LinearInterpolator()
        mMusicAnim?.duration = mMusicAnimPeriod
        mMusicAnim?.addUpdateListener {
            val fraction = it.animatedFraction
            val fractionLeft = 1 - fraction
            for (bean in mMusicDataList) {
                val location = updateMusicAnim(fraction, fractionLeft, bean.startPonit!!, bean.firstPath!!, bean.secondPath!!, bean.endPonit!!)
                bean.img?.x = location.x
                bean.img?.y = location.y
            }

        }

        mMusicAnim?.addListener(object :Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
//                LogUtils.d("test_life","anim end:${animation.hashCode()}")

                for (bean in mMusicDataList) {
                    bean.img?.visibility = View.INVISIBLE
                }
                if (!mMusicAnimCancel) {
                    createNewMusicData()
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
                mMusicAnimCancel = true
//                LogUtils.d("life","anim cancel")
            }

            override fun onAnimationStart(animation: Animator?) {
                for (bean in mMusicDataList) {
                    bean.img?.visibility = View.VISIBLE
                }
            }
        })
    }


    fun setData(bean: ResponseSpeakBean, isMe: Boolean) {
        GlideUtils.commonLoad(context, bean.imgUrl, img_dish)
        GlideUtils.loadFuzzy(context, bean.imgUrl, img_bg)
        GlideUtils.commonLoad(context, bean.iconUrl, img_head)
        tv_name.text = bean.name
        tv_word.text = bean.content

        if (SPUtils.getInstance().getString(SpConstant.MY_USER_ID) == bean.userId) {
            button_attention.visibility = View.INVISIBLE
        } else {
            button_attention.visibility = View.VISIBLE
            AccompanyApplication.getAttentionList {
                if (it.contains(bean.userId)) {
                    button_attention.text = mAttentionText
                } else {
                    button_attention.text = mDisAttentionText
                }
                button_attention.setOnClickListener {
                    doAttention(bean.userId)
                }
            }
        }
        if (bean.isLike == 1) {
            img_heart.setImageResource(R.mipmap.heart_full)
            mLike = true
        } else {
            img_heart.setImageResource(R.mipmap.heart_empty)
            mLike = false
        }

        tv_count.text = bean.likeNum.toString()
        img_chat.setOnClickListener {
            if (SPUtils.getInstance().getString(SpConstant.MY_USER_ID) == bean.userId) {
                ToastUtils.showCommonToast("这是你自己哦")
            } else {
                RongIM.getInstance().startPrivateChat(context, bean.userId, bean.name)
            }
        }

        img_heart.setOnClickListener {
            setLike(bean)
        }

        if (isMe) {
            img_delete.visibility = View.VISIBLE
        } else {
            img_delete.visibility = View.GONE
        }
    }


    private fun doAttention(id: String) {
        val event: Int = if (button_attention.text == mAttentionText) {
            0
        } else {
            1
        }
        RequestUtils.dealAttention(id,event){
            if (it) {
                button_attention.text = mAttentionText
            } else {
                button_attention.text = mDisAttentionText
            }
        }

    }

    private fun setLike(bean:ResponseSpeakBean) {
        var event: Int
        if (!mLike) {
            img_heart.setImageResource(R.mipmap.heart_full)
            val scaleX = PropertyValuesHolder.ofFloat("scaleX", 0f, 1f)
            val scaleY = PropertyValuesHolder.ofFloat("scaleY", 0f, 1f)
            val animator = ObjectAnimator.ofPropertyValuesHolder(img_heart, scaleX, scaleY)
            animator.duration = 300
            animator.start()
            event = 0
            bean.likeNum += 1
            tv_count.text = bean.likeNum.toString()
        } else {
            img_heart.setImageResource(R.mipmap.heart_empty)
            event = 1
            bean.likeNum -= 1
            tv_count.text = bean.likeNum.toString()
        }
        if (mNet) {
            return
        }
        setLikeNet(event, bean.id)
    }

    fun setProgress(progress: Int, total: Int) {
        if (total <= 0) {
            tv_progress.text = AppUtils.timeParse(0L)
            tv_total.text = AppUtils.timeParse(0L)
            progress_bar.progress = 0
            return
        }
        tv_progress.text = AppUtils.timeParse(progress.toLong())
        tv_total.text = AppUtils.timeParse(total.toLong())
        progress_bar.progress = progress * 100 / total
    }

    private fun setLikeNet(event: Int, id: String) {
        mNet = true
        val bean = RequestSpeakLike(SPUtils.getInstance().getString(SpConstant.APP_TOKEN), id, event)
        val request = AccompanyRequest()
        val success: String = if (event == 0) {
            "点赞成功"
        } else {
            "取消点赞"
        }
        request.beginRequest(NetFactory.getNetRequest().netService.setVoiceLike(EncodeUtils.encodeInBody(GsonUtils.toJson(bean)))
        ,object :TypeToken<BaseDecodeBean<OnlyCodeBean>>(){}.type,object :NetListener<OnlyCodeBean>{
            override fun onSuccess(t: OnlyCodeBean?) {
                ToastUtils.showCommonToast(success)
                mNet = false
                mLike = !mLike
            }

            override fun onFailed(errCode: Int) {

            }

            override fun onError() {

            }

            override fun onComplete() {
                mNet = false
            }
        })


    }

    //kotlin算术运算 不能换行（+ 必须在保留在上一行），这个坑爹货
    private fun updateMusicAnim(fraction: Float, fractionLeft: Float, startValue: PointF, firstPath: PointF, secondPath: PointF, endValue: PointF) :PointF{
//        LogUtils.d("path", "update start:$startValue---first:$firstPath---second:$secondPath---end:$endValue---fraction:$fraction---fraction left:$fractionLeft")

        val currentPoint = PointF()
        currentPoint.x = (fractionLeft * fractionLeft * fractionLeft * startValue.x + 3 * fractionLeft * fractionLeft * fraction * firstPath.x
        +3 * fractionLeft * fraction * fraction * secondPath.x + fraction * fraction * fraction * endValue.x)

//        if (fraction == 1f) {
//            LogUtils.d("result","one:${fractionLeft * fractionLeft * fractionLeft * startValue.x}--two:${3 * fractionLeft * fractionLeft * fraction * firstPath.x}---" +
//                    "three:${3 * fractionLeft * fraction * fraction * secondPath.x}---four:${fraction * fraction * fraction * endValue.x}")
//
//            val test =   (3 * fractionLeft * fractionLeft * fraction * firstPath.x)
//            + (3 * fractionLeft * fraction * fraction * secondPath.x) + (fraction * fraction * fraction * endValue.x)
//
//            LogUtils.d("result","test:$test")
//        }

        currentPoint.y = (fractionLeft * fractionLeft * fractionLeft * startValue.y + 3 * fractionLeft * fractionLeft * fraction * firstPath.y
        +3 * fractionLeft * fraction * fraction * secondPath.y + fraction * fraction * fraction * endValue.y)

//        LogUtils.d("path", "current:$currentPoint")

        return currentPoint
    }

    //顺序 起点，第一点，第二点，终点
    private fun createMusicAnimPath() :ArrayList<PointF>{
        val angle = mRandom.nextInt(360)
        val angleRadians = Math.toRadians(angle.toDouble())
        val startPoint = PointF(mCenter.x + mDiameter / 2 * sin(angleRadians).toFloat(), mCenter.y + mDiameter / 2 * cos(angleRadians).toFloat())
        var endPoint = PointF()
        val sideRandom = mRandom.nextInt(2)
        val locationRandom = mRandom.nextInt(mDiameter)

        //parent四条边,top.right,bottom.left优先级， top:0,right:1  tight:0 bottom:1以此类推
        when (angle) {
            //top,right
            in 0..90 -> {
                if (sideRandom == 0) {
                    endPoint.y = 0f
                    endPoint.x = mDiameter + locationRandom.toFloat()
                } else {
                    endPoint.x = mDiameter * 2.toFloat()
                    endPoint.y = locationRandom.toFloat()
                }
            }
            //top,left
            in 91..180 -> {
                if (sideRandom == 0) {
                    endPoint.y = 0f
                    endPoint.x = locationRandom.toFloat()
                } else {
                    endPoint.x = 0f
                    endPoint.y = locationRandom.toFloat()
                }

            }
            //bottom,left
            in 181..270 -> {
                if (sideRandom == 0) {
                    endPoint.y = 2 * mDiameter.toFloat()
                    endPoint.x = locationRandom.toFloat()
                } else {
                    endPoint.x = 0f
                    endPoint.y = locationRandom.toFloat() + mDiameter
                }
            }
            //right,bottom
            in 271..360 -> {
                if (sideRandom == 0) {
                    endPoint.x = 2 * mDiameter.toFloat()
                    endPoint.y = mDiameter + locationRandom.toFloat()
                } else {
                    endPoint.y = 2 * mDiameter.toFloat()
                    endPoint.x = mDiameter + locationRandom.toFloat()
                }
            }
        }


        val center = PointF((endPoint.x + startPoint.x) / 2, (endPoint.y - startPoint.y) / 2)
        val diameter = sqrt((endPoint.x - startPoint.x).pow(2) + (endPoint.y - startPoint.y).pow(2))
        val diff = diameter / 4 * sin(Math.toRadians(45.toDouble()))

        val firstPoint = PointF()
        val secondPoint = PointF()
        if (startPoint.x > endPoint.x) {
            firstPoint.x = (startPoint.x - diff).toFloat()
            secondPoint.x = (endPoint.x + diff).toFloat()
        } else {
            firstPoint.x = (startPoint.x + diff).toFloat()
            secondPoint.x = (endPoint.x - diff).toFloat()
        }

        if (startPoint.y > endPoint.y) {
            firstPoint.y = (startPoint.y - diff).toFloat()
            secondPoint.y = (endPoint.y + diff).toFloat()
        } else {
            firstPoint.y = (startPoint.y + diff).toFloat()
            secondPoint.y = (endPoint.y - diff).toFloat()
        }

//        LogUtils.d("path", "create start:$startPoint---firstPath:$firstPoint----secondPath:$secondPoint---end:$endPoint")
//        LogUtils.d("running", "i am running id:${hashCode()}")

        return arrayListOf(startPoint, firstPoint, secondPoint, endPoint)

    }


     fun pause() {
        img_play.visibility = View.VISIBLE
        mRotateAnim?.pause()
        mSpreadAnimOne?.pause()
        mSpreadAnimTwo?.pause()
        mMusicAnim?.pause()
    }

     fun startAllAnim() {
         img_play.visibility = View.GONE

         if (mRotateAnim?.isPaused!!) {
            mRotateAnim?.resume()
        } else {
            mRotateAnim?.start()
        }

        if (mSpreadAnimOne?.isPaused!!) {
            mSpreadAnimOne?.resume()
        } else {
            mSpreadAnimOne?.start()
        }

        if (mSpreadAnimTwo?.isPaused!!) {
            mSpreadAnimTwo?.resume()
        } else {
            mSpreadAnimTwo?.start()
        }

        if (mMusicAnim?.isPaused!!) {
            mMusicAnim?.resume()
        } else {
            mMusicAnim?.start()
        }
    }

     fun destroy() {
        mRotateAnim?.cancel()
        mSpreadAnimOne?.cancel()
        mSpreadAnimTwo?.cancel()
        mMusicAnim?.cancel()
    }
}
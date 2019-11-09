package com.play.accompany.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.play.accompany.R
import com.play.accompany.base.BaseHolder
import com.play.accompany.bean.RecommandSpeakBean
import com.play.accompany.bean.ResponseSpeakBean
import com.play.accompany.design.*
import com.play.accompany.utils.AppUtils
import kotlinx.android.synthetic.main.fragment_living_sound.view.*
import kotlinx.android.synthetic.main.view_living_sound.view.*

class SoundAdapter(private val mList: List<ResponseSpeakBean>, private val mIsMe: Boolean, private val mCallback: (SpeakCallbackInfo) -> Unit): RecyclerView.Adapter<BaseHolder>(){
    private val mViewMap = hashMapOf<Int, LivingSoundView>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        return BaseHolder(LayoutInflater.from(parent.context).inflate(R.layout.fragment_living_sound, parent, false))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: BaseHolder, position: Int) {
        mViewMap[position] = holder.itemView.living_sound
        holder.itemView.living_sound.setData(mList[position],mIsMe)

        holder.itemView.living_sound.setOnClickListener {
            mCallback(SpeakCallbackInfo(SPEAK_TYPE_SHOW_HIDE,null))
        }

        holder.itemView.living_sound.img_dish.setOnClickListener {
            onPause(position)
        }

        holder.itemView.living_sound.img_play.setOnClickListener {
            onPause(position)
        }

        holder.itemView.img_delete.setOnClickListener {
            mCallback(SpeakCallbackInfo(SPEAK_TYPE_DELETE, mList[position]))
        }
    }

    private fun onPause(position: Int) {
        if (!AppUtils.isQuickCLick()) {
            mCallback(SpeakCallbackInfo(SPEAK_TYPE_CHANGE_PAUSE, mList[position]))
        }
    }

    fun getLivingView(position: Int): LivingSoundView? {
        return mViewMap[position]
    }

}
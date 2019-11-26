package com.play.accompany.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.play.accompany.R
import com.play.accompany.bean.ResponseSpeakBean
import com.play.accompany.utils.GlideUtils
import kotlinx.android.synthetic.main.item_speak.view.*

class SpeakAdapter(private val mIsMe: Boolean, private val mListener: (Int) -> Unit)
    :RecyclerView.Adapter<SpeakAdapter.SpeakHolder>() {

    private var mList = mutableListOf<ResponseSpeakBean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpeakHolder {
        return SpeakHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_speak, parent, false))
    }

    override fun getItemCount(): Int {
        if (mIsMe) {
            return mList.size + 1
        }
        return mList.size
    }

    override fun onBindViewHolder(holder: SpeakHolder, position: Int) {
         holder.itemView.run {

             if (mIsMe) {
                 if (position == 0) {
                     img_play.visibility = View.INVISIBLE
                     img_head.setImageResource(R.mipmap.item_add)
                     rl_bottom.visibility = View.GONE
                 } else {
                     GlideUtils.commonLoad(context, mList[position - 1].imgUrl, img_head)
                     tv_title.text = mList[position - 1].title
                     tv_count.text = mList[position -1].likeNum.toString()
                 }
             } else {
                 GlideUtils.commonLoad(context, mList[position].imgUrl, img_head)
                 tv_title.text = mList[position].title
                 tv_count.text = mList[position].likeNum.toString()
             }
             setOnClickListener {
                 mListener(position)
             }
        }

    }

    fun setData(list: List<ResponseSpeakBean>) {
        mList = list.toMutableList()
        notifyDataSetChanged()
    }

    fun addData(list: List<ResponseSpeakBean>) {
        mList.addAll(list)
        notifyDataSetChanged()
    }

    class SpeakHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
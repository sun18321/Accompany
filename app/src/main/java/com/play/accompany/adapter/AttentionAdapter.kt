package com.play.accompany.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.play.accompany.R
import com.play.accompany.bean.FansBean
import com.play.accompany.constant.OtherConstant
import com.play.accompany.utils.DateUtils
import com.play.accompany.utils.GlideUtils
import kotlinx.android.synthetic.main.item_attention.view.*
import kotlinx.android.synthetic.main.item_attention.view.tv_name
import kotlinx.android.synthetic.main.item_order.view.*

class AttentionAdapter(context:Context,dataList:ArrayList<FansBean>):RecyclerView.Adapter<AttentionAdapter.AttentionHolder>() {
    private var mContext = context
    private var mDataList = dataList
    private lateinit var mListener: AttentionListener

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): AttentionHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_attention, parent, false)
        return AttentionHolder(view)
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    override fun onBindViewHolder(holder: AttentionHolder, position: Int) {
        GlideUtils.commonLoad(mContext, mDataList[position].url, holder.itemView.head_img)
        holder.itemView.tv_name.text = mDataList[position].name
        holder.itemView.tv_sign.text = mDataList[position].sign
        if (mDataList[position].gender == OtherConstant.GENDER_MALE) {
            holder.itemView.lin_gender.setBackgroundResource(R.drawable.male_bg)
            holder.itemView.img_gender.setImageResource(R.drawable.male)
        } else {
            holder.itemView.lin_gender.setBackgroundResource(R.drawable.female_bg)
            holder.itemView.img_gender.setImageResource(R.drawable.female)
        }
        holder.itemView.tv_age.text = DateUtils.getAge(mDataList[position].date).toString()
        holder.itemView.tv_online.text = mDataList[position].onlineTime
        if (mDataList[position].attention) {
            holder.itemView.tv_attention.text = "已关注"
            holder.itemView.tv_attention.isSelected = true
        } else {
            holder.itemView.tv_attention.text = "关注"
            holder.itemView.tv_attention.isSelected = false
        }

        holder.itemView.head_img.setOnClickListener {
            if (mListener != null) {
                mListener.goUser(mDataList[position].userId, position)
            }
        }

        holder.itemView.tv_attention.setOnClickListener {
            if (mListener != null) {
                mListener.goAttention(mDataList[position].attention, position, mDataList[position].userId)
            }
        }

    }

    fun setData(newList: ArrayList<FansBean>) {
        mDataList = newList
        notifyDataSetChanged()
    }

    fun setListener(listener:AttentionListener) {
        mListener = listener
    }


    inner class AttentionHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    interface AttentionListener{
        fun goUser(id: String, position: Int)

        fun goAttention(attention: Boolean, position: Int, id: String)
    }

}
package com.play.accompany.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.play.accompany.R
import com.play.accompany.bean.UserInfo

class AttentionAdapter(context:Context,dataList:ArrayList<UserInfo>):RecyclerView.Adapter<AttentionAdapter.AttentionHolder>() {
    private var mContext = context
    private var mDataList = dataList

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): AttentionHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_attention, parent, false)
        return AttentionHolder(view)
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    override fun onBindViewHolder(holder: AttentionHolder, position: Int) {

    }


    inner class AttentionHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

}
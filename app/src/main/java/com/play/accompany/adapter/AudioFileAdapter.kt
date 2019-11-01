package com.play.accompany.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.play.accompany.R
import com.play.accompany.base.BaseHolder
import com.play.accompany.bean.FilterAudioBean
import kotlinx.android.synthetic.main.item_audio.view.*

class AudioFileAdapter(private val mList: List<FilterAudioBean>, private val mCallback: (FilterAudioBean?) -> Unit):RecyclerView.Adapter<BaseHolder>(){
    private var mSelectPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        return BaseHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_audio, parent, false))
    }

    override fun getItemCount(): Int {
       return mList.size
    }

    override fun onBindViewHolder(holder: BaseHolder, position: Int) {
        holder.itemView.run {
            tv_name.text = mList[position].name
            tv_size.text = mList[position].sizeString
            tv_duration.text = mList[position].audioDurationString
            if (mList[position].selected) {
                img_select.visibility = View.VISIBLE
            } else {
                img_select.visibility = View.INVISIBLE
            }

            setOnClickListener {
                if (mSelectPosition == position) {
                    mList[mSelectPosition].selected = false
                    notifyItemChanged(mSelectPosition)
                    mSelectPosition = -1
                    mCallback(null)
                } else {
                    if (mSelectPosition != -1) {
                        mList[mSelectPosition].selected = false
                        notifyItemChanged(mSelectPosition)
                    }
                    mSelectPosition = position
                    mList[mSelectPosition].selected = true
                    notifyItemChanged(mSelectPosition)
                    mCallback(mList[mSelectPosition])
                }
            }
        }
    }

}
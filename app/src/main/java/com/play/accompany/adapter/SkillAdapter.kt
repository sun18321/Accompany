package com.play.accompany.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.play.accompany.R
import com.play.accompany.bean.GameProperty
import com.play.accompany.utils.GlideUtils
import kotlinx.android.synthetic.main.item_skill.view.*

class SkillAdapter(private val mContext: Context, private val mGameList: List<GameProperty>, private val mListener: (Int) -> Unit)
    :RecyclerView.Adapter<SkillAdapter.SkillHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillHolder {
        return SkillHolder(LayoutInflater.from(mContext).inflate(R.layout.item_skill, parent, false))
    }

    override fun getItemCount(): Int {
        return mGameList.size
    }

    override fun onBindViewHolder(holder: SkillHolder, position: Int) {
        holder.itemView.run {
            GlideUtils.commonLoad(mContext, mGameList[position].url,img_head)
            skill_name.text = mGameList[position].name
            skill_price.text = mGameList[position].toString()
            button_order.setOnClickListener {
                mListener(position)
            }
        }
    }


    class SkillHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
package com.fcfm.yuni_corn.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.yuni_corn.R
import com.fcfm.yuni_corn.models.Rewards
import kotlinx.android.synthetic.main.item_reward_layout.view.*

class RewardsAdapter(private val listRewards: MutableList<Rewards>):
    RecyclerView.Adapter<RewardsAdapter.RewardsViewHolder>(){

    class RewardsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun asignarInformacion(reward: Rewards){
            itemView.tv_title_irl.text = reward.title
            itemView.tv_description_irl.text = reward.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardsAdapter.RewardsViewHolder {
        return RewardsAdapter.RewardsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_reward_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RewardsAdapter.RewardsViewHolder, position: Int) {
        holder.asignarInformacion(listRewards[position])
    }

    override fun getItemCount(): Int = listRewards.size
}
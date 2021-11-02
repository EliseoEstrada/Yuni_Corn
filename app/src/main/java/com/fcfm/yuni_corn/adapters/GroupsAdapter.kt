package com.fcfm.yuni_corn.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.yuni_corn.GroupActivity
import com.fcfm.yuni_corn.R
import com.fcfm.yuni_corn.models.Groups
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_group_layout.view.*
import kotlinx.android.synthetic.main.item_group_layout.view.ivImageGroupG
import kotlinx.android.synthetic.main.item_group_layout.view.tvGroupName

class GroupsAdapter(private val listGroups: MutableList<Groups>, val contexto: Context):
    RecyclerView.Adapter<GroupsAdapter.GroupsViewHolder>(){

    class  GroupsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun asignarInformacion(group: Groups, contexto: Context){
            itemView.tvGroupName.text = group.title

            if(group.image != ""){
                Picasso.get().load(group.image)
                    .placeholder(R.drawable.ic_baseline_group_24)
                    .error(R.drawable.ic_baseline_group_24)
                    .into(itemView.ivImageGroupG)
            }

            itemView.itemGroupContainer.setOnClickListener {
                val activity = Intent(contexto, GroupActivity::class.java)

                activity.putExtra("TITLE_GROUP", group.title)
                activity.putExtra("UID_GROUP", group.uid)
                activity.putExtra("IMAGE_GROUP", group.image)
                activity.putExtra("DESCRIPTION_GROUP", group.description)
                activity.putExtra("PROPETARY_GROUP", group.propetary)
                activity.putExtra("CAREER_GROUP", group.career)
                (contexto as Activity).startActivity(activity)
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsAdapter.GroupsViewHolder {
        return GroupsAdapter.GroupsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_group_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: GroupsViewHolder, position: Int) {
        holder.asignarInformacion(listGroups[position], contexto)
    }

    override fun getItemCount(): Int = listGroups.size

}


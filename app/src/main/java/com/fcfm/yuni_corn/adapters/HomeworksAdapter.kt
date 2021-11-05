package com.fcfm.yuni_corn.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.yuni_corn.HomeworkActivity
import com.fcfm.yuni_corn.R
import com.fcfm.yuni_corn.models.UserHomework
import kotlinx.android.synthetic.main.item_homework_layout.view.*

class HomeworksAdapter(private val listHomeworks: MutableList<UserHomework>, val contexto: Context) :
    RecyclerView.Adapter<HomeworksAdapter.HomeworksViewHolder>(){

    class HomeworksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun asignarInfomacion(homework: UserHomework, contexto: Context){
            itemView.tv_title_ih.text = homework.title
            itemView.tv_finishDate_ih.text = "Vence el " + homework.finishDate

            var state = "Pendiente"
            if(homework.sent){
                state = "Entregada"
                itemView.ll_homeworkContainer_ih.setBackgroundResource(R.drawable.rounded2)
            }

            itemView.tv_state_ih.text = state

            itemView.ll_homeworkContainer_ih.setOnClickListener {
                val activity = Intent(contexto, HomeworkActivity::class.java)

                //Paquete
                val args = Bundle()
                args.putString("uidHomework",homework.uid);
                args.putString("finishDate",homework.finishDate);
                args.putBoolean("sent",homework.sent);
                args.putString("title",homework.title);
                args.putString("uidGroup",homework.uidGroup);
                args.putInt("points",homework.points);
                args.putString("nameDocument",homework.nameDocument);

                activity.putExtra("HOMEWORK",args)

                (contexto as Activity).startActivity(activity)
            }
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : HomeworksViewHolder {
        return HomeworksViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_homework_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: HomeworksViewHolder, position: Int) {
        holder.asignarInfomacion(listHomeworks[position], contexto)
    }

    override fun getItemCount(): Int = listHomeworks.size
}
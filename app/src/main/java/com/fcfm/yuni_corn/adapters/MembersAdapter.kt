package com.fcfm.yuni_corn.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.yuni_corn.R
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_user_layout.view.*

class MembersAdapter(private val listMembers: MutableList<String>, val context: Context) :
    RecyclerView.Adapter<MembersAdapter.MembersViewHolder>(){

    class MembersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private var db = FirebaseDatabase.getInstance()
        private val usuariosRef = db.getReference("USERS")
        fun asignInformation(uid: String, contexto: Context){

            usuariosRef.child(uid).get().addOnSuccessListener {

                val user = it.child("user").value.toString()
                val career = it.child("career").value.toString()
                val state = it.child("state").value.toString()
                val image = it.child("image").value.toString()

                itemView.tv_user_iu.text = user
                itemView.tv_career_iu.text = career
                itemView.tv_state_iu.text = state

                //Cambiar color de estado
                if(state == "Disponible"){
                    itemView.tv_state_iu.setTextColor(contexto.getResources().getColor(R.color.Disponible))
                }
                if(state == "Ausente"){
                    itemView.tv_state_iu.setTextColor(contexto.getResources().getColor(R.color.Ausente))
                }
                if(state == "Ocupado"){
                    itemView.tv_state_iu.setTextColor(contexto.getResources().getColor(R.color.Ocupado))
                }
                if(image != ""){
                    Picasso.get().load(image)
                        .placeholder(R.drawable.ic_baseline_person_24)
                        .error(R.drawable.ic_baseline_person_24)
                        .into(itemView.iv_imageUser_iu)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : MembersViewHolder{
        return MembersViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_user_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MembersViewHolder, position: Int){
        holder.asignInformation(listMembers[position], context)
    }

    override fun getItemCount(): Int = listMembers.size
}


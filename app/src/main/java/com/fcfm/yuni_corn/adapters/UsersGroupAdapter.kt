package com.fcfm.yuni_corn.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.fcfm.yuni_corn.R
import com.fcfm.yuni_corn.utils.Globals
import com.fcfm.yuni_corn.utils.Members
import com.fcfm.yuni_corn.models.Users
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_user_group_layout.view.*
import kotlinx.android.synthetic.main.item_user_layout.view.*


class UsersGroupAdapter(private val listaUsuarios: MutableList<Users>, val contexto: Context) :
    RecyclerView.Adapter<UsersGroupAdapter.UsersGroupViewHolder>(){

    class UsersGroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun asignarInformacion(usuario: Users, contexto: Context){
            itemView.tv_user_iug.text = usuario.user
            itemView.tv_carer_iug.text = usuario.career
            itemView.tv_state_iug.text = usuario.state

            //Cambiar color de estado
            if(usuario.state == "Disponible"){
                itemView.tv_state_iug.setTextColor(contexto.getResources().getColor(R.color.Disponible))
            }
            if(usuario.state == "Ausente"){
                itemView.tv_state_iug.setTextColor(contexto.getResources().getColor(R.color.Ausente))
            }
            if(usuario.state == "Ocupado"){
                itemView.tv_state_iug.setTextColor(contexto.getResources().getColor(R.color.Ocupado))
            }

            Picasso.get().load(usuario.image)
                .placeholder(R.drawable.ic_baseline_person_24)
                .error(R.drawable.ic_baseline_person_24)
                .into(itemView.iv_imageUser_iug)


            itemView.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (buttonView.isShown()) {
                    if (buttonView.isChecked()) {
                        val member = Members(
                            usuario.uid,
                            usuario.user,
                            usuario.image
                        )
                        Globals.listUsersGroup.add(member)
                    }else{
                        var member = Members("","","")
                        for(obj in Globals.listUsersGroup){
                            if(obj.uid == usuario.uid){
                                member = obj
                                break
                            }
                        }
                        Globals.listUsersGroup.remove(member)
                    }
                }
               // Toast.makeText(contexto,Globals.listUsersGroup.toString(), Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersGroupViewHolder {
        return UsersGroupViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_user_group_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: UsersGroupViewHolder, position: Int) {
        holder.asignarInformacion(listaUsuarios[position], contexto)
    }

    override fun getItemCount() : Int = listaUsuarios.size
}
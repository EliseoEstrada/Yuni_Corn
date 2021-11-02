package com.fcfm.yuni_corn.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.fcfm.yuni_corn.models.Users
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.yuni_corn.ChatActivity
import com.fcfm.yuni_corn.R
import com.fcfm.yuni_corn.models.Chats
import com.fcfm.yuni_corn.utils.Globals
import com.fcfm.yuni_corn.utils.Members
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.item_user_layout.view.*

class UsersAdapter(private val listaUsuarios: MutableList<Users>, val contexto: Context) :
    RecyclerView.Adapter<UsersAdapter.UsersViewHolder>(){


    class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var db = FirebaseDatabase.getInstance()     //Variable que apunta al nombre de la base de datos
        private val chatsRef = db.getReference("CHATS")   //Variable para crear Tabla en bd

        fun asignarInformacion(usuario: Users, contexto: Context){
            //Colocar username
            itemView.tv_user_iu.text = usuario.user
            itemView.tv_career_iu.text = usuario.career
            itemView.tv_state_iu.text = usuario.state

            //Cambiar color de estado
            if(usuario.state == "Disponible"){
                itemView.tv_state_iu.setTextColor(contexto.getResources().getColor(R.color.Disponible))
            }
            if(usuario.state == "Ausente"){
                itemView.tv_state_iu.setTextColor(contexto.getResources().getColor(R.color.Ausente))
            }
            if(usuario.state == "Ocupado"){
                itemView.tv_state_iu.setTextColor(contexto.getResources().getColor(R.color.Ocupado))
            }

            //Colocar imagen
            Picasso.get().load(usuario.image)
                .placeholder(R.drawable.ic_baseline_person_24)
                .error(R.drawable.ic_baseline_person_24)
                .into(itemView.iv_imageUser_iu)

            //Si selecciona uno de los usuarios
            itemView.UserContainer.setOnClickListener {

                db.getReference("USER_CHAT").child(Globals.UserLogged.uid).get()
                    .addOnSuccessListener {
                        
                        //Comprobar si ya existe chat
                        var result = false
                        for(item in it.children){
                            if(usuario.uid == item.value.toString()){
                                result = true
                                break
                            }
                        }
                        if(!result) {

                            db.getReference("USER_CHAT").child(Globals.UserLogged.uid)
                                .child(usuario.uid).setValue(usuario.uid)
                            db.getReference("USER_CHAT").child(usuario.uid)
                                .child(Globals.UserLogged.uid).setValue(Globals.UserLogged.uid)

                            //Crear chat
                            val chat = chatsRef.push()
                            val uid = chat.key ?: ""

                            val obj = Chats(uid, 0, "","", ServerValue.TIMESTAMP, "")

                            chat.setValue(obj).addOnCompleteListener {
                                //Crear integrante (usuario logueado)
                                val me = Members(
                                    Globals.UserLogged.uid,
                                    Globals.UserLogged.user,
                                    Globals.UserLogged.image
                                )

                                //Crear integrante (usuario seleccionado)
                                val member = Members(usuario.uid, usuario.user, usuario.image)

                                //Agregar id del usuario logueado
                                chatsRef.child(uid).child("members").child(me.uid).setValue(me)
                                //agregar id del usuario seleccionado
                                chatsRef.child(uid).child("members").child(member.uid).setValue(member).addOnCompleteListener {

                                    val activity = Intent(contexto, ChatActivity::class.java)
                                    activity.putExtra("TITLE_CHAT", usuario.user)
                                    activity.putExtra("UID_CHAT", obj.uid)
                                    activity.putExtra("IMAGE_CHAT", usuario.image)

                                    (contexto as Activity).startActivity(activity)
                                    contexto.finish()
                                }
                            }
                        }else{
                            Toast.makeText(contexto, "Existing chat", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(contexto, "Error", Toast.LENGTH_SHORT).show()
                    }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        return UsersViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_user_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.asignarInformacion(listaUsuarios[position], contexto)
    }

    override fun getItemCount() : Int = listaUsuarios.size

}




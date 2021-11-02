package com.fcfm.yuni_corn.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View

import androidx.fragment.app.Fragment
import com.fcfm.yuni_corn.R
import com.fcfm.yuni_corn.NewChatActivity
import com.fcfm.yuni_corn.adapters.ChatsAdapter
import com.fcfm.yuni_corn.models.Chats
import com.fcfm.yuni_corn.utils.Globals
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_chats.*

class FragmentChats: Fragment(R.layout.fragment_chats)  {

    //Variable que almacena los chats de la BD
    private val listaChats = mutableListOf<Chats>()
    //Variable que guarda el adaptador de los chats cargados
    lateinit var  adaptador: ChatsAdapter

    private var db = FirebaseDatabase.getInstance()     //Variable que apunta al nombre de la base de datos
    private var dbRef = db.getReference()

    private val chatsRef = db.getReference("CHATS")   //Variable para crear Tabla en bd

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Obtiene variable del activity padre
        /*
        val UID = getArguments()?.getString("UID") ?: ""
        val USER = getArguments()?.getString("USER") ?: ""
        */

        val UID = Globals.UserLogged.uid
        val USER = Globals.UserLogged.user

        //Obtener acitity padre y convertirlo en contexto
        val contexto: Context = (activity as Context)

        //Cargar adaptador
        adaptador = ChatsAdapter(listaChats, contexto)
        rv_members_fc.adapter = adaptador

        cargarChats(UID)


        btn_addChat_fc.setOnClickListener {
            val registerActivity = Intent(activity, NewChatActivity::class.java)
            /*
            registerActivity.putExtra("UID", UID)
            registerActivity.putExtra("USER", USER)
             */
            startActivity(registerActivity)
        }

    }

    private fun cargarChats(userUid: String){
        chatsRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                //Limpiar lista
                listaChats.clear()

                //Recorrer todos los hijos de la referencia
                for(snap in snapshot.children){

                    val type: Int =  snap.child("type").value.toString().toInt()

                    //Ignorar chats tipo muro
                    if(type != 2){
                        var miChat: Boolean = false
                        var uidMember: String


                        //Recorrer todos los integrantes en busca del usuario
                        for(snapi in snap.child("members").children){
                            uidMember = snapi.child("uid").getValue().toString()
                            //Comprobar si usuario pertenece al chat
                            if(uidMember == userUid){
                                miChat = true
                                break
                            }
                        }

                        //Si el usuario pertenece al chat, mostrarlo
                        if(miChat){

                            val uid = snap.child("uid").value.toString()
                            val lastMessage = snap.child("lastMessage").value.toString()
                            val date = snap.child("date").value as Long
                            var image = snap.child("image").value.toString()
                            var title = ""


                            //Chat individual
                            if(type == 0){
                                //Recorrer todos los integrantes
                                for(snapi in snap.child("members").children){
                                    uidMember = snapi.child("uid").getValue().toString()
                                    //Comprobar si el integrante no es el usuario logueado
                                    if(uidMember != userUid){
                                        //Guardar el uid del receptor para buscar su usuario posteriormente
                                        title = snapi.child("user").getValue().toString()
                                        image = snapi.child("image").getValue().toString()
                                        break
                                    }
                                }
                            }

                            //Chat grupal
                            if(type == 1){
                                title = snap.child("title").value.toString()
                                image = snap.child("image").value.toString()
                            }

                            //Crear objeto chat
                            val chat = Chats(uid, type, title, lastMessage, date, image)

                            //Sumar chat a la lista
                            listaChats.add(chat)
                        }
                    }
                }

                if(listaChats.size > 0){
                    adaptador.notifyDataSetChanged()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


}
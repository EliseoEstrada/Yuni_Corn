package com.fcfm.yuni_corn.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.fcfm.yuni_corn.CreateGroupActivity
import com.fcfm.yuni_corn.R
import com.fcfm.yuni_corn.adapters.GroupsAdapter
import com.fcfm.yuni_corn.models.Groups
import com.fcfm.yuni_corn.utils.Globals
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_groups.*

class FragmentGroups: Fragment(R.layout.fragment_groups)  {

    private val listMyGroups = mutableListOf<String>()  //Guarda ids de los grupos a los que pertenece
    private val listGroups = mutableListOf<Groups>()    //Obtiene los nodos de los grupos
    //Variable que guarda el adaptador de los chats cargados
    lateinit var  adapter: GroupsAdapter

    private var db = FirebaseDatabase.getInstance()     //Variable que apunta al nombre de la base de datos
    private val groupsRef = db.getReference("GROUPS")
    private val user_groupsRef = db.getReference("USER_GROUPS")

    var UID = Globals.UserLogged.uid
    var CAREER = Globals.UserLogged.career

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Obtener acitity padre y convertirlo en contexto
        val contexto: Context = (activity as Context)
        listMyGroups.clear()

        //Cargar adaptador
        adapter = GroupsAdapter(listGroups, contexto)
        rv_groups_fg.adapter = adapter
        loadGroups()

        btn_addGroup_fg.setOnClickListener {
            val activity = Intent(contexto, CreateGroupActivity::class.java)
            startActivity(activity)
        }

    }

    private fun loadGroups(){

        //Traer lista de grupos al que el usuario pertenece
        user_groupsRef.child(UID).get().addOnSuccessListener{ myGroups ->

            for(snap in myGroups.children){
                listMyGroups.add(snap.value.toString())
            }

            if(listMyGroups.isNotEmpty()){
                //Obtener grupos pertenecientes a la carrera del usuario
                groupsRef.orderByChild("career").equalTo(CAREER).get().addOnSuccessListener{

                    for(group in it.children){
                        //Comprobar si es miembro del grupo
                        val uid = group.child("uid").value.toString()
                        if(isMember(uid)){
                            val career = group.child("career").value.toString()
                            val title = group.child("title").value.toString()
                            val description = group.child("description").value.toString()
                            val image = group.child("image").value.toString()
                            val propetary = group.child("propetary").value.toString()

                            val obj = Groups(uid, title, description, career, image, propetary)

                            listGroups.add(obj)
                            adapter.notifyDataSetChanged()
                        }

                    }
                }.addOnFailureListener {
                    Toast.makeText((activity as Context), it.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    //Fncion para saber si el usuario es miembro del grupo
    private fun isMember(uid: String): Boolean{
        for(item in listMyGroups){
            if(uid == item)
                return true
        }
        return false
    }
}
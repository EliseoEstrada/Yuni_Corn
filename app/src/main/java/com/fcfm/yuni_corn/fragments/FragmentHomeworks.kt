package com.fcfm.yuni_corn.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.fcfm.yuni_corn.R
import com.fcfm.yuni_corn.adapters.HomeworksAdapter
import com.fcfm.yuni_corn.utils.Globals
import com.fcfm.yuni_corn.models.UserHomework
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_homeworks.*

class FragmentHomeworks: Fragment(R.layout.fragment_homeworks)  {
    private var db = FirebaseDatabase.getInstance()
    private val membersRef = db.getReference("USER_HOMEWORKS")
    private val listHomeworks = mutableListOf<UserHomework>()
    lateinit var  adapter: HomeworksAdapter
    val UID = Globals.UserLogged.uid

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val openFrom = getArguments()?.getString("open_from") ?: ""


        val contexto: Context = (activity as Context)
        adapter = HomeworksAdapter(listHomeworks, contexto)
        rv_homeworks_fh.adapter = adapter

        if(openFrom == "MAIN"){
            loadHomeworks()
        }

        if(openFrom == "GROUP"){
            val UID_GROUP = getArguments()?.getString("UID_GROUP") ?: ""
            loadHomeworksFromGroup(UID_GROUP)
        }
    }

    //Obtiene todas las tareas del usuario
    private fun loadHomeworks(){
        membersRef.child(UID).get().addOnSuccessListener {
            listHomeworks.clear()
            for(snap in it.children){

                val obj = UserHomework(
                        snap.child("uid").value.toString(),
                        snap.child("title").value.toString(),
                        snap.child("uidGroup").value.toString(),
                        snap.child("finishDate").value.toString(),
                        snap.child("sendDate").value.toString(),
                        snap.child("document").value.toString(),
                        snap.child("nameDocument").value.toString(),
                        snap.child("points").value.toString().toInt(),
                        snap.child("sent").value.toString().toBoolean()
                    )

                listHomeworks.add(obj)
            }

            if(listHomeworks.size > 0){
                adapter.notifyDataSetChanged()
            }

        }
    }

    //Obtiene todas las tareas del usuario por grupo
    private fun loadHomeworksFromGroup(uidGroup: String){
        membersRef.child(UID).get().addOnSuccessListener {
            listHomeworks.clear()
            for(snap in it.children){

                if(snap.child("uidGroup").value.toString() == uidGroup){
                    val obj = UserHomework(
                        snap.child("uid").value.toString(),
                        snap.child("title").value.toString(),
                        snap.child("uidGroup").value.toString(),
                        snap.child("finishDate").value.toString(),
                        snap.child("sendDate").value.toString(),
                        snap.child("document").value.toString(),
                        snap.child("nameDocument").value.toString(),
                        snap.child("points").value.toString().toInt(),
                        snap.child("sent").value.toString().toBoolean()
                    )

                    listHomeworks.add(obj)
                }

            }

            if(listHomeworks.size > 0){
                adapter.notifyDataSetChanged()
            }

        }
    }
}
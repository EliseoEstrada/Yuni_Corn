package com.fcfm.yuni_corn.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.fcfm.yuni_corn.AddMembersGroupActivity
import com.fcfm.yuni_corn.R
import com.fcfm.yuni_corn.adapters.MembersAdapter
import com.fcfm.yuni_corn.utils.Globals
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_members.*

class FragmentMembers: Fragment(R.layout.fragment_members) {

    lateinit var adaptador: MembersAdapter

    private var db = FirebaseDatabase.getInstance()     //Variable que apunta al nombre de la base de datos
    private val membersRef = db.getReference("GROUP_MEMBERS")

    var UID_GROUP = ""
    var CAREER_GROUP = ""
    var PROPETARY_GROUP = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        UID_GROUP = getArguments()?.getString("UID_GROUP") ?: ""
        CAREER_GROUP = getArguments()?.getString("CAREER_GROUP") ?: ""
        PROPETARY_GROUP = getArguments()?.getString("PROPETARY_GROUP") ?: ""

        val contexto: Context = (activity as Context)

        adaptador = MembersAdapter(Globals.listMembersGroup, contexto)
        rv_members_fm.adapter = adaptador

        loadMembers()

        /*
        if(PROPETARY_GROUP == Globals.UserLogged.uid){
            btnAddMemberGroup.visibility = View.VISIBLE
        }else{
            btnAddMemberGroup.visibility = View.GONE
        }

         */

        btn_addMember_fm.setOnClickListener {
            val intent = Intent(activity, AddMembersGroupActivity::class.java)
            intent.putExtra("UID_GROUP", UID_GROUP)
            intent.putExtra("CAREER_GROUP", CAREER_GROUP)
            startActivity(intent)
        }

    }

    private fun loadMembers(){
        membersRef.child(UID_GROUP).addValueEventListener(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                Globals.listMembersGroup.clear()

                for(snap in snapshot.children){
                    val member = snap.value.toString()
                    Globals.listMembersGroup.add(member)
                }

                if(Globals.listMembersGroup.size > 0){
                    adaptador.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}
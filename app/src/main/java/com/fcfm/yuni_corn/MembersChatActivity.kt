package com.fcfm.yuni_corn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fcfm.yuni_corn.adapters.MembersAdapter
import com.fcfm.yuni_corn.utils.Globals
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_members_chat.*
import kotlinx.android.synthetic.main.fragment_members.*

class MembersChatActivity : AppCompatActivity() {

    lateinit var adaptador: MembersAdapter

    private var db = FirebaseDatabase.getInstance()     //Variable que apunta al nombre de la base de datos
    private val membersRef = db.getReference("CHATS")

    private var UID_CHAT = ""
    private var TYPE_CHAT = 0
    private var listMembers = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members_chat)

        UID_CHAT = intent.getStringExtra("UID_CHAT") ?: ""
        TYPE_CHAT = intent.getIntExtra("TYPE_CHAT",0)

        //Si el chat es de tipo muro, cargar los miembros de ese grupo y no del chat
        if(TYPE_CHAT == 2){
            listMembers = Globals.listMembersGroup
        }

        adaptador = MembersAdapter(listMembers, this)
        rv_members_mc.adapter = adaptador


        loadMembers()

        iv_return_mc.setOnClickListener {
            finish()
        }
    }

    private fun loadMembers(){
        membersRef.child(UID_CHAT).child("members").get().addOnSuccessListener {
            for(snap in it.children){
                listMembers.add(snap.child("uid").value.toString())
            }

            adaptador.notifyDataSetChanged()
        }
    }
}
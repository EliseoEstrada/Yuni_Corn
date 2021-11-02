package com.fcfm.yuni_corn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.fcfm.yuni_corn.adapters.UsersGroupAdapter
import com.fcfm.yuni_corn.models.Users
import com.fcfm.yuni_corn.utils.Globals
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_members_group.*

class AddMembersGroupActivity : AppCompatActivity() {

    private val listUsers = mutableListOf<Users>()
    lateinit var adaptador: UsersGroupAdapter

    private var db = FirebaseDatabase.getInstance()
    private val usersRef = db.getReference("USERS")
    private val user_groupsRef = db.getReference("USER_GROUPS")
    private val group_membersRef = db.getReference("GROUP_MEMBERS")
    var UID_GROUP = ""
    var CAREER_GROUP = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_members_group)

        Globals.listUsersGroup.clear()
        UID_GROUP = intent.getStringExtra("UID_GROUP") ?: ""
        CAREER_GROUP = intent.getStringExtra("CAREER_GROUP") ?: ""

        adaptador = UsersGroupAdapter(listUsers, this)
        rv_users_amg.adapter = adaptador
        loadUsers()

        btn_addMembersGroup_amg.setOnClickListener {
            if(!Globals.listUsersGroup.isEmpty()){
                addUserToGroup()
            }else{
                Toast.makeText(this, "Select an user", Toast.LENGTH_SHORT).show()
            }
        }

        iv_return_amg.setOnClickListener {
            finish()
        }
    }

    private fun loadUsers(){
        usersRef.orderByChild("career").equalTo(CAREER_GROUP).get().addOnSuccessListener {
            for(snap in it.children){
                val uidUser = snap.child("uid").value.toString()
                //Comprobar si el usuario se encuentra en el chat
                var result = userIsInGroup(uidUser)
                if(!result){
                    val user = snap.child("user").value.toString()
                    val image = snap.child("image").value.toString()
                    val career = snap.child("career").value.toString()
                    val state = snap.child("state").value.toString()

                    val obj = Users(uidUser, user, "","", career, image, state)

                    listUsers.add(obj)
                }
            }
            if(listUsers.count() > 0){
                adaptador.notifyDataSetChanged()
            }
        }
    }

    private fun userIsInGroup(uid: String): Boolean{
        for(userUid in Globals.listMembersGroup){
            if(uid == userUid){
                return true
            }
        }
        return false
    }

    private fun addUserToGroup(){
        for (item in Globals.listUsersGroup) {
            user_groupsRef.child(item.uid).child(UID_GROUP).setValue(UID_GROUP)
            group_membersRef.child(UID_GROUP).child(item.uid).setValue(item.uid)
        }
        finish()
    }
}
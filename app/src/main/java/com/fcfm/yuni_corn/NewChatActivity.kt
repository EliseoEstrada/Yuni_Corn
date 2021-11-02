package com.fcfm.yuni_corn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fcfm.yuni_corn.adapters.UsersAdapter
import com.fcfm.yuni_corn.utils.Globals
import com.google.firebase.database.FirebaseDatabase
import com.fcfm.yuni_corn.models.Users
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_new_chat.*

class NewChatActivity : AppCompatActivity() {

    private val listUsuarios = mutableListOf<Users>()
    lateinit var adaptador: UsersAdapter

    private var db = FirebaseDatabase.getInstance()     //Variable que apunta al nombre de la base de datos
    private var dbRef = db.getReference()
    private val usuariosRef = db.getReference("USERS")   //Variable para crear Tabla en bd

    private var UID: String = ""
    private var USER: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_chat)

//        UID = intent.getStringExtra("UID") ?: ""
//        USER = intent.getStringExtra("USER") ?: ""

        UID = Globals.UserLogged.uid
        USER = Globals.UserLogged.user


        adaptador = UsersAdapter(listUsuarios, this)
        rv_users_nc.adapter = adaptador

        cargarUsuarios()


        btn_newGroupChat_nc.setOnClickListener {
            val activity = Intent(this, NewGroupChatActivity::class.java)
            startActivity(activity)
            finish()
        }

        iv_return_nc.setOnClickListener {
            finish()
        }

    }

    private fun cargarUsuarios(){
        //Escuchar constantemente
        usuariosRef.addValueEventListener(object: ValueEventListener{
            //Success
            override fun onDataChange(snapshot: DataSnapshot) {
                //Limpiar lista de mensajes
                listUsuarios.clear()

                for (snap in snapshot.children){
                    //Casteo

                    //val usuario: Users = snap.getValue(Users::class.java) as Users

                    val uid = snap.child("uid").value.toString()

                    if(uid != UID){

                        val user = snap.child("user").value.toString()
                        val email = snap.child("email").value.toString()
                        val career = snap.child("career").value.toString()
                        val image = snap.child("image").value.toString()
                        val state = snap.child("state").value.toString()

                        val obj = Users(uid, user, email, "", career, image, state)

                        listUsuarios.add(obj)
                    }
                }

                if(listUsuarios.size > 0){
                    adaptador.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}
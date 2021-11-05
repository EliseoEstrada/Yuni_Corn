package com.fcfm.yuni_corn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.fcfm.yuni_corn.fragments.*
import com.fcfm.yuni_corn.utils.Globals
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase

class GroupActivity : AppCompatActivity() {

    var TITLE_GROUP = ""
    var UID_GROUP =  ""
    var IMAGE_GROUP =  ""
    var DESCRIPTION_GROUP =  ""
    var PROPETARY_GROUP = ""
    var CAREER_GROUP = ""

    private var db = FirebaseDatabase.getInstance()
    private val membersRef = db.getReference("GROUP_MEMBERS")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        TITLE_GROUP = intent.getStringExtra("TITLE_GROUP") ?: ""
        UID_GROUP = intent.getStringExtra("UID_GROUP") ?: ""
        IMAGE_GROUP = intent.getStringExtra("IMAGE_GROUP") ?: ""
        DESCRIPTION_GROUP = intent.getStringExtra("DESCRIPTION_GROUP") ?: ""
        PROPETARY_GROUP = intent.getStringExtra("PROPETARY_GROUP") ?: ""
        CAREER_GROUP = intent.getStringExtra("CAREER_GROUP") ?: ""


        loadMembers()
        cambiarFragmentos(FragmentGroupData(), "fragmentoDatosGrupo")
        val navigation: BottomNavigationView = findViewById(R.id.bnv_menu_g)

        navigation.setOnItemSelectedListener{ item ->
            when (item.itemId) {
                R.id.opcInfo_group -> {
                    cambiarFragmentos(FragmentGroupData(), "fragmentoDatosGrupo")
                    true
                }
                R.id.opcHomeworks_group -> {
                    cambiarFragmentos(FragmentHomeworks(), "fragmentoTareas")
                    true
                }
                R.id.opcChat_group -> {
                    val activity = Intent(this, ChatActivity::class.java)
                    activity.putExtra("TITLE_CHAT", TITLE_GROUP)
                    activity.putExtra("UID_CHAT", UID_GROUP)
                    activity.putExtra("IMAGE_CHAT", IMAGE_GROUP)
                    activity.putExtra("TYPE_CHAT",2)    //Decirle al chat que es tipo muro
                    startActivity(activity)
                    true
                }
                R.id.opcMembers_group -> {
                    cambiarFragmentos(FragmentMembers(), "fragmentoMiembros")
                    true
                }
                else -> false
            }
        }
    }


    private fun cambiarFragmentos(fragmentoNuevo: Fragment, etiqueta: String){
        val args = Bundle()
        args.putString("TITLE_GROUP",TITLE_GROUP)
        args.putString("UID_GROUP",UID_GROUP)
        args.putString("IMAGE_GROUP",IMAGE_GROUP)
        args.putString("DESCRIPTION_GROUP",DESCRIPTION_GROUP)
        args.putString("PROPETARY_GROUP",PROPETARY_GROUP)
        args.putString("CAREER_GROUP",CAREER_GROUP)

        if(etiqueta == "fragmentoTareas"){
            args.putString("open_from","GROUP")
        }



        fragmentoNuevo.setArguments(args);
        //Comenzar transaccion
        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_container_g, fragmentoNuevo, etiqueta)
            .commit()
    }

    private fun loadMembers(){
        Globals.listMembersGroup.clear()
        membersRef.child(UID_GROUP).get().addOnSuccessListener {
            for(snap in it.children){
                Globals.listMembersGroup.add(snap.value.toString())
            }
        }
    }
}
package com.fcfm.yuni_corn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.fcfm.yuni_corn.fragments.FragmentChats
import com.fcfm.yuni_corn.fragments.FragmentGroups
import com.fcfm.yuni_corn.fragments.FragmentHomeworks
import com.fcfm.yuni_corn.fragments.FragmentProfile
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity() {

    private var db = FirebaseDatabase.getInstance()     //Variable que apunta al nombre de la base de datos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navigation: BottomNavigationView = findViewById(R.id.bvn_menu_m)

        /*
        UID = intent.getStringExtra("UID") ?: ""
        EMAIL = intent.getStringExtra("EMAIL") ?: ""
        USER = intent.getStringExtra("USER") ?: ""
        CAREER = intent.getStringExtra("CAREER") ?: ""
        PASS = intent.getStringExtra("PASSWORD") ?: ""
*/

        //navigation.selectedItemId = R.id.opcGroups
        cambiarFragmentos(FragmentGroups(), "fragmentoGrupos")

        navigation.setOnItemSelectedListener { item ->
            when (item.itemId){
                R.id.opcGroups ->{
                    cambiarFragmentos(FragmentGroups(), "fragmentoGrupos")
                    true
                }
                R.id.opcChats -> {
                    cambiarFragmentos(FragmentChats(), "fragmentoChats")
                    true
                }
                R.id.opcHomeworks -> {
                    cambiarFragmentos(FragmentHomeworks(), "fragmentoTareas")
                    true
                }
                R.id.opcProfile -> {
                    cambiarFragmentos(FragmentProfile(), "fragmentoPerfil")

                    true
                }
                else -> false
            }
        }


    }



    private fun cambiarFragmentos(fragmentoNuevo: Fragment, etiqueta: String){

        //Pasar valores de usuario al fragmento
        /*
        val args = Bundle()
        args.putString("UID",UID)
        args.putString("EMAIL",EMAIL)
        args.putString("USER",USER)
        args.putString("CAREER",CAREER)
        args.putString("PASSWORD",PASS)
        fragmentoNuevo.setArguments(args);
        */
        //Comenzar transaccion
        if(etiqueta == "fragmentoTareas"){
            val args = Bundle()
            args.putString("open_from","MAIN")
            fragmentoNuevo.setArguments(args)
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_container_m, fragmentoNuevo, etiqueta)
            .commit()
    }
}
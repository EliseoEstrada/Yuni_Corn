package com.fcfm.yuni_corn

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.fcfm.yuni_corn.fragments.*
import com.fcfm.yuni_corn.models.Rewards
import com.fcfm.yuni_corn.utils.Globals
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_toast_01.*
import kotlinx.android.synthetic.main.custom_toast_01.view.*


class MainActivity : AppCompatActivity() {

    private var db = FirebaseDatabase.getInstance()     //Variable que apunta al nombre de la base de datos
    private val rewardsRef = db.getReference("USER_REWARDS")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var contadorLogo = 0

        val navigation: BottomNavigationView = findViewById(R.id.bvn_menu_m)

        var logroSecreto = false
        for(item in Globals.listRewardsUser){
            if(item.uid == "logro_05"){
                logroSecreto = true
                break
            }
        }


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
                R.id.opcRewards -> {
                    cambiarFragmentos(FragmentRewards(), "fragmentoRecompensas")
                    true
                }
                R.id.opcProfile -> {
                    cambiarFragmentos(FragmentProfile(), "fragmentoPerfil")

                    true
                }
                else -> false
            }
        }

        cl_logo_m.setOnClickListener{

            if(!logroSecreto){
                contadorLogo++
                if(contadorLogo > 4){
                    logroSecreto = true
                    showToastReward()
                }
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


    fun showToastReward(){

        val reward = Rewards(
            "logro_05",
            "Se me acabaron las ideas",
            "Logro que solo los mas cracks descubren"
        )
        rewardsRef.child(Globals.UserLogged.uid).child(reward.uid).setValue(reward).addOnSuccessListener {
            //Agregar a la lista
            Globals.listRewardsUser.add(reward)


            val mp: MediaPlayer
            mp = MediaPlayer.create(this, R.raw.notification)
            mp.start()

            val inflater = LayoutInflater.from(this)
            val layout: View = inflater.inflate(R.layout.custom_toast_01, ll_toastLayout_ct)
            layout.tv_title_ct.setText("Se me acabaron las ideas")
            val toast = Toast(applicationContext)
            toast.setGravity(Gravity.CENTER_VERTICAL / Gravity.CENTER_HORIZONTAL, 0, 500)
            toast.duration = Toast.LENGTH_LONG
            toast.view = layout
            toast.show()
        }


    }
}
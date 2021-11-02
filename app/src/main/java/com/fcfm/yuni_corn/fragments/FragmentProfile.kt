package com.fcfm.yuni_corn.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.fcfm.yuni_corn.LoginActivity
import com.fcfm.yuni_corn.R
import com.fcfm.yuni_corn.utils.Globals
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import android.widget.AdapterView
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.item_user_layout.view.*


class FragmentProfile: Fragment(R.layout.fragment_profile) {

    private var db = FirebaseDatabase.getInstance()     //Variable que apunta al nombre de la base de datos
    private val usuariosRef = db.getReference("USERS")   //Variable para crear Tabla en bd

    val UID = Globals.UserLogged.uid
    val CORREO = Globals.UserLogged.email
    val USUARIO = Globals.UserLogged.user
    val CARRERA = Globals.UserLogged.career
    val ESTADO = Globals.UserLogged.state
    val CONTRA = Globals.UserLogged.password
    val IMAGEN = Globals.UserLogged.image



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val UID = getArguments()?.getString("UID")

        val contexto: Context = (activity as Context)

        tv_password_p.setText(CONTRA)
        tv_email_p.setText(CORREO)
        tv_user_p.setText(USUARIO)
        tv_career_p.setText(CARRERA)
        tv_state_p.setText(ESTADO)

        Picasso.get().load(IMAGEN)
            .placeholder(R.drawable.ic_baseline_person_24)
            .error(R.drawable.ic_baseline_person_24)
            .into(iv_imageUser_p)


        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                itemSeleccionado: Int,
                l: Long
            ) {

                if (itemSeleccionado == 1) {
                    changeState(spinner.selectedItem.toString(), contexto)
                }
                if (itemSeleccionado == 2) {
                    changeState(spinner.selectedItem.toString(), contexto)
                }
                if (itemSeleccionado == 3) {
                    changeState(spinner.selectedItem.toString(), contexto)
                }
                if (itemSeleccionado == 4) {
                    changeState(spinner.selectedItem.toString(), contexto)
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })

        btn_closeSesion_p.setOnClickListener {
            val loginActivity = Intent(getActivity(), LoginActivity::class.java)
            startActivity(loginActivity)
            activity?.finish()
        }
    }

    private fun changeState(state: String, contexto:Context){
        usuariosRef.child(UID).child("state").setValue(state).addOnSuccessListener {
            tv_state_p.text = state

            if(state == "Disponible"){
                tv_state_p.setTextColor(contexto.getResources().getColor(R.color.Disponible))
            }
            if(state == "Ausente"){
                tv_state_p.setTextColor(contexto.getResources().getColor(R.color.Ausente))
            }
            if(state == "Ocupado"){
                tv_state_p.setTextColor(contexto.getResources().getColor(R.color.Ocupado))
            }
        }.addOnFailureListener {
            Toast.makeText(contexto, "Error", Toast.LENGTH_SHORT).show()
        }
    }
} 
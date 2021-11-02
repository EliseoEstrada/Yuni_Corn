package com.fcfm.yuni_corn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.fcfm.yuni_corn.utils.Globals
import com.fcfm.yuni_corn.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*

import android.view.WindowManager


class LoginActivity : AppCompatActivity() {

    private var db = FirebaseDatabase.getInstance()     //Variable que apunta al nombre de la base de datos
    private var dbRef = db.getReference()
    private val usuariosRef = db.getReference("USERS")   //Variable para crear Tabla en bd
    private val mAuth = FirebaseAuth.getInstance()      //Instancia de autentification

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnLogin = findViewById<Button>(R.id.btn_login_l)
        val btnRegister = findViewById<Button>(R.id.btn_register_l)

        val txtEmail = findViewById<EditText>(R.id.et_email_l)
        val txtPassword = findViewById<EditText>(R.id.et_password_l)


        btnLogin.setOnClickListener {
            val EMAIL = txtEmail.text.toString()
            val PASSWORD = txtPassword.text.toString()
            login(EMAIL,PASSWORD)
        }

        btnRegister.setOnClickListener {
            val registerActivity = Intent(this, RegisterActivity::class.java)
            startActivity(registerActivity)
            finish()
        }
    }

    private fun login(email: String, password: String){
        val result = comprobarCampos(email, password)
        if(result) {
            mostrarProgress(true)
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        val uid = mAuth.currentUser?.uid.toString()

                        //Si inicio sesion, obtener datos del usuario
                        usuariosRef.child(uid).get().addOnSuccessListener { It ->
                            mostrarProgress(false)

                            val obj = Users(
                                uid,
                                It.child("user").value.toString(),
                                email,
                                password,
                                It.child("career").value.toString(),
                                It.child("image").value.toString(),
                                It.child("state").value.toString()
                            )
                            Globals.UserLogged = obj

                            val activity = Intent(this, MainActivity::class.java)
                            //activity.putExtra("UID", uid)
                            startActivity(activity)
                            finish()
                        }.addOnFailureListener {
                            mostrarProgress(false)
                            Toast.makeText(this, "Error login", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        mostrarProgress(false)
                        Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }

    private fun comprobarCampos(email: String, pass: String) : Boolean {

        if(!email.isNotEmpty()){
            Toast.makeText(this, "Email required", Toast.LENGTH_SHORT).show()
            return false
        }

        if(pass.length > 5){
            return true
        }else{
            Toast.makeText(this, "The password must have a minimum of 6 characters", Toast.LENGTH_SHORT).show()
            return false
        }
    }

    fun mostrarProgress(mostrar: Boolean){
        if(mostrar){
            progressBar3.visibility = View.VISIBLE
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }else{
            progressBar3.visibility = View.GONE
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }
}
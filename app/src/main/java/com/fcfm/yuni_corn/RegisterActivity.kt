package com.fcfm.yuni_corn

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.fcfm.yuni_corn.utils.Globals
import com.fcfm.yuni_corn.models.Users
//import com.github.drjacky.imagepicker.ImagePicker
//import com.github.drjacky.imagepicker.constant.ImageProvider

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.dialog_upload_image.view.*
import java.io.ByteArrayOutputStream
import java.util.*


class RegisterActivity : AppCompatActivity() {
    //Variable que apunta al nombre de la base de datos
    private var db = FirebaseDatabase.getInstance()
    private var dbRef = db.getReference()
    //Variable para crear Tabla en bd
    private val usuariosRef = db.getReference("USERS")

    private val mStorageRef: StorageReference = FirebaseStorage.getInstance().getReference()
    //Instancia de autentification
    private val mAuth = FirebaseAuth.getInstance()

    var USER = ""
    var EMAIL = ""
    var PASS = ""
    var CAREER = ""
    var selectedImage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnLogin = findViewById<Button>(R.id.btn_login_r)
        val btnRegister = findViewById<Button>(R.id.btn_register_r)

        val txtUser = findViewById<EditText>(R.id.et_username_r)
        val txtPassword = findViewById<EditText>(R.id.et_password_r)
        val txtEmail = findViewById<EditText>(R.id.et_email_r)

        val spnCareers = findViewById<Spinner>(R.id.spn_careers_r)

        //Seleccionar imagen de usuario
        iv_imageUser_r.setOnClickListener{
            //inflate el dialogo con el diseño
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_upload_image, null)
            //Construir la alerta del dialogo
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("Select")
            //mostrar dialogo
            val mAlertDialog = mBuilder.show()

            mDialogView.btnGallery.setOnClickListener {
                mAlertDialog.dismiss()
                uploadImagen(1)
            }
            mDialogView.btnCamera.setOnClickListener {
                mAlertDialog.dismiss()
                uploadImagen(2)
            }
        }

        //Registrarse
        btnRegister.setOnClickListener {
            USER = txtUser.text.toString()
            EMAIL = txtEmail.text.toString()
            PASS = txtPassword.text.toString()
            CAREER = spnCareers.selectedItem.toString()

            val result = comprobarCampos(USER, EMAIL, PASS, selectedImage)
            if(result){
                performRegister()
            }
        }
        btnLogin.setOnClickListener {
            val activity = Intent(this, LoginActivity::class.java)
            startActivity(activity)
            finish()
        }
    }

    private fun uploadImagen(type: Int){
        //Galeria
        if(type == 1){
            val intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(intent, 1)
        }
        //Camara
        if(type==2){
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if(intent.resolveActivity(packageManager) != null){
                startActivityForResult(intent, 2)
            }
        }
    }

    //Resultado de cargar imagen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Galeria
        if(requestCode == 1 && resultCode == RESULT_OK){
            val uri: Uri? = data?.data
            iv_imageUser_r.setImageURI(uri)
            selectedImage = true
        }
        //Camara
        if(requestCode == 2 && resultCode == RESULT_OK){
            val uri = data?.extras
            val bitmap = uri?.get("data") as Bitmap
            iv_imageUser_r.setImageBitmap(bitmap)
            selectedImage = true
        }
    }

    private fun performRegister(){
        showProgress(true)
        //Guardar imagen, despues usuario
        saveImage()
    }

    private fun saveImage(){
        val filename = UUID.randomUUID().toString()
        //Crear referencia a imagen
        val imagenesRef = mStorageRef.child("Users/"+filename+".png")

        //SUBIR IMAGEN DE PERFIL
        iv_imageUser_r.isDrawingCacheEnabled = true
        iv_imageUser_r.buildDrawingCache()
        val bitmap = (iv_imageUser_r.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()

        //Guardar imagen
        imagenesRef.putBytes(data).addOnSuccessListener{
            //Leer url de imagen cargada
            imagenesRef.downloadUrl.addOnSuccessListener{
                //Guardar usuario
                saveUser(it.toString())
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error upload image", Toast.LENGTH_SHORT).show()
            showProgress(false)
        }
    }

    private fun saveUser(IMAGE: String){
        mAuth.createUserWithEmailAndPassword(EMAIL, PASS).addOnSuccessListener {
            //Obtener id de usuario agregado
            var uid = mAuth.currentUser?.uid.toString()
            //Crear objeto de usuario
            val obj = Users(uid, USER, EMAIL, PASS, CAREER, IMAGE, "Disponible")

            usuariosRef.child(obj.uid).setValue(obj).addOnSuccessListener{
                showProgress(false)
                //Actualizar usuario logueado
                Globals.UserLogged = obj
                //ABRIR ACTIVITY MAIN
                val activity = Intent(this, MainActivity::class.java)
                //activity.putExtra("UID", uid)
                startActivity(activity)
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                showProgress(false)
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            showProgress(false)
        }
    }

    private fun comprobarCampos(user: String, email: String, pass: String, selectedImage:Boolean) : Boolean {
        if(!user.isNotEmpty()){
            Toast.makeText(this, "User required", Toast.LENGTH_SHORT).show()
            return false
        }
        if(!email.isNotEmpty()){
            Toast.makeText(this, "Email required", Toast.LENGTH_SHORT).show()
            return false
        }
        if(pass.length < 6){
            Toast.makeText(this, "The password must have a minimum of 6 characters", Toast.LENGTH_SHORT).show()
            return false
        }
        if(!selectedImage){
            Toast.makeText(this, "select an image", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    fun showProgress(show: Boolean){
        if(show){
            progressBar2.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }else{
            progressBar2.visibility = View.GONE
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }
}
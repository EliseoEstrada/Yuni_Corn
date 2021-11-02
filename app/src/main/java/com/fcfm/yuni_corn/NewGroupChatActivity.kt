package com.fcfm.yuni_corn

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast


import com.fcfm.yuni_corn.adapters.UsersGroupAdapter
import com.fcfm.yuni_corn.models.Chats
import com.fcfm.yuni_corn.models.Users
import com.fcfm.yuni_corn.utils.Globals
import com.fcfm.yuni_corn.utils.Members
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_new_group_chat.*
import java.io.ByteArrayOutputStream
import java.util.*


class NewGroupChatActivity : AppCompatActivity() {

    private val listUsuarios = mutableListOf<Users>()
    lateinit var adaptador: UsersGroupAdapter

    private var db = FirebaseDatabase.getInstance()     //Variable que apunta al nombre de la base de datos
    private val usuariosRef = db.getReference("USERS")   //Variable para crear Tabla en bd
    private val chatsRef = db.getReference("CHATS")   //Variable para crear Tabla en bd
    private val mStorageRef: StorageReference = FirebaseStorage.getInstance().getReference()
    private var UID: String = ""
    private var USER: String = ""
    private var IMAGE: String = ""
    private var imageSelected = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_group_chat)

        UID = Globals.UserLogged.uid
        USER = Globals.UserLogged.user
        IMAGE = Globals.UserLogged.image

        //Agregarte al grupo
        val me = Members(UID,USER,IMAGE)
        Globals.listUsersGroup.clear()
        Globals.listUsersGroup.add(me)

        adaptador = UsersGroupAdapter(listUsuarios, this)
        rv_users_ngc.adapter = adaptador

       loadUsers()

        iv_imageGroup_ngc.setOnClickListener {
            val intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(intent, 1)
        }

        //Crear chats
        btn_createGroup_ngc.setOnClickListener {
            if(Globals.listUsersGroup.count() > 1) {
                if(imageSelected){
                    showProgress(true)
                    saveImage()
                }else{
                    Toast.makeText(this, "Select an image", Toast.LENGTH_SHORT).show()
                }

            }else{
                Toast.makeText(this, "Select a user", Toast.LENGTH_SHORT).show()
            }
        }

        iv_return_ngc.setOnClickListener {
            finish()
        }

    }


    private fun loadUsers(){

        usuariosRef.get().addOnSuccessListener {
            //Limpiar lista de mensajes
            listUsuarios.clear()
            for (snap in it.children){

                val uidUser = snap.child("uid").value.toString()
                //Omitir usuario logueado
                if(uidUser != UID){
                    val obj = Users(
                        uidUser,
                        snap.child("user").value.toString(),
                        snap.child("email").value.toString(),
                        "",
                        snap.child("career").value.toString(),
                        snap.child("image").value.toString(),
                        snap.child("state").value.toString()
                    )

                    listUsuarios.add(obj)
                }
            }
            if(listUsuarios.size > 0){
                adaptador.notifyDataSetChanged()
            }
        }
    }


    //Resultado de cargar imagen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Galeria
        if(requestCode == 1 && resultCode == RESULT_OK){
            val uri: Uri? = data?.data
            iv_imageGroup_ngc.setImageURI(uri)
            imageSelected = true
        }
    }

    private fun saveImage(){
        val filename = UUID.randomUUID().toString()

        //Imagen
        iv_imageGroup_ngc.isDrawingCacheEnabled = true
        iv_imageGroup_ngc.buildDrawingCache()
        val bitmap = (iv_imageGroup_ngc.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()

        //Crear referencia a imagen
        val imagenesRef = mStorageRef.child("Chats/"+filename+".png")
        imagenesRef.putBytes(data).addOnCompleteListener{
            imagenesRef.downloadUrl.addOnSuccessListener{ image ->
                //Crear grupo
                createGroup(image.toString())
            }
        }.removeOnFailureListener{
            Toast.makeText(this, "Error upload image", Toast.LENGTH_SHORT).show()
            showProgress(false)
        }
    }

    private fun createGroup(image: String){
        val chat = chatsRef.push()
        val uid = chat.key ?: ""
        val title = et_title_ngc.text.toString()

        val obj = Chats(uid, 1, title, "", ServerValue.TIMESTAMP, image)

        //Agregar chat
        chat.setValue(obj).addOnSuccessListener{
            //Agregar integrantes
            for (item in Globals.listUsersGroup) {
                chatsRef.child(uid).child("members").child(item.uid).setValue(item)
            }

            showProgress(false)
            val activity = Intent(this, ChatActivity::class.java)
            activity.putExtra("TITLE_CHAT", obj.title)
            activity.putExtra("UID_CHAT", obj.uid)
            activity.putExtra("IMAGE_CHAT", obj.image)
            startActivity(activity)
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Could not create chat", Toast.LENGTH_SHORT).show()
            showProgress(false)
        }
    }

    fun showProgress(show: Boolean){
        if(show){
            progressBar4.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }else{
            progressBar4.visibility = View.GONE
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }


}
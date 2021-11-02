package com.fcfm.yuni_corn

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.fcfm.yuni_corn.adapters.MessagesAdapter
import com.fcfm.yuni_corn.utils.Globals
import com.fcfm.yuni_corn.models.Messages
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*

class ChatActivity : AppCompatActivity() {

    private val listaMensajes = mutableListOf<Messages>()
    lateinit var  adaptador: MessagesAdapter

    private var db = FirebaseDatabase.getInstance()     //Variable que apunta al nombre de la base de datos
    private val mensajesRef = db.getReference("MESSAGES")   //Variable para crear Tabla en bd
    private val chatsRef = db.getReference("CHATS")
    private val mStorageRef: StorageReference = FirebaseStorage.getInstance().getReference("Messages")

    var UID = ""
    var USER = ""
    var UID_CHAT = ""
    var TITLE_CHAT = ""
    var IMAGE_CHAT = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        TITLE_CHAT = intent.getStringExtra("TITLE_CHAT") ?: "Usuario"
        UID_CHAT = intent.getStringExtra("UID_CHAT") ?: ""
        IMAGE_CHAT = intent.getStringExtra("IMAGE_CHAT") ?: ""

        if(IMAGE_CHAT != "") {
            Picasso.get().load(IMAGE_CHAT)
                .placeholder(R.drawable.ic_baseline_person_24)
                .error(R.drawable.ic_baseline_person_24)
                .into(iv_imageChat_c)
        }

        UID = Globals.UserLogged.uid
        USER = Globals.UserLogged.user

        tv_title_c.text = TITLE_CHAT

        //Cargar adaptador
        adaptador = MessagesAdapter(listaMensajes, UID)
        rv_messages_c.adapter = adaptador

        //Cerrar activity
        iv_return_c.setOnClickListener {
            finish()
        }

        //Enviar mensaje
        iv_sendMessage_c.setOnClickListener {
            val mensaje = et_message_c.text.toString()
            if(mensaje.isNotEmpty()){
                et_message_c.text.clear()
                sendMessage(mensaje, "text")
            }
        }

        iv_sendImage_c.setOnClickListener {
            val intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(intent, 1)
        }

        getMessages()
    }

    private fun sendMessage(mensaje: String, type: String){

        val message = mensajesRef.child(UID_CHAT).push()
        val uidMessage = message.key ?: ""
        val date = ServerValue.TIMESTAMP
        var lastMessage = mensaje
        if(type == "image"){
            lastMessage = "image"
        }
        val obj = Messages(uidMessage, mensaje, type, date, UID, USER)

        mensajesRef.child(UID_CHAT).child(uidMessage).setValue(obj).addOnCompleteListener {
            chatsRef.child(UID_CHAT).child("lastMessage").setValue(lastMessage)
            chatsRef.child(UID_CHAT).child("date").setValue(date)

            rv_messages_c.smoothScrollToPosition(listaMensajes.size - 1)
        }

        //Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    //Resultado de cargar imagen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Galeria
        if(requestCode == 1 && resultCode == RESULT_OK){
           val uriImage = data?.data!!

            if(!Uri.EMPTY.equals(uriImage)){
                sendImage(uriImage)
            }else{
                Toast.makeText(this, "picture could not be sent", Toast.LENGTH_SHORT).show()
            }

        }
    }
    private fun sendImage(image: Uri){
        val filename = UUID.randomUUID().toString()
        //Crear referencia a imagen
        val imagenesRef = mStorageRef.child("$UID_CHAT/"+filename+".png")

        //SUBIR IMAGEN
        imagenesRef.putFile(image).addOnSuccessListener{
            //Leer url de imagen cargada
            imagenesRef.downloadUrl.addOnSuccessListener{
                //Guardar mensaje
                sendMessage(it.toString(), "image")
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error upload image", Toast.LENGTH_SHORT).show()
        }

    }


    private fun getMessages(){
        mensajesRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaMensajes.clear()

                for (snap in snapshot.child(UID_CHAT).children){

                    val uid = snap.child("uid").value.toString()
                    val content = snap.child("content").value.toString()
                    val type = snap.child("type").value.toString()
                    val date = snap.child("date").value as Long
                    val uidUser = snap.child("uidUser").value.toString()
                    val user = snap.child("user").value.toString()

                    val msg = Messages(uid, content,type,  date, uidUser, user)

                    listaMensajes.add(msg)
                }

               if(listaMensajes.size > 0){

                   adaptador.notifyDataSetChanged()
                   rv_messages_c.smoothScrollToPosition(listaMensajes.size - 1)
               }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}
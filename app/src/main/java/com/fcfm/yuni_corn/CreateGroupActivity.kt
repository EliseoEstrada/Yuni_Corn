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
import com.fcfm.yuni_corn.models.Chats
import com.fcfm.yuni_corn.models.Groups
import com.fcfm.yuni_corn.utils.Globals
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_create_group.*
import java.io.ByteArrayOutputStream
import java.util.*

class CreateGroupActivity : AppCompatActivity() {

    private var db = FirebaseDatabase.getInstance()     //Variable que apunta al nombre de la base de datos
    private val groupRef = db.getReference("GROUPS")
    private val group_membersRef = db.getReference("GROUP_MEMBERS")
    private val user_groupsRef = db.getReference("USER_GROUPS")
    private val chatRef = db.getReference("CHATS")
    private val mStorageRef: StorageReference = FirebaseStorage.getInstance().getReference("Groups")

    private lateinit var UID: String
    private lateinit var CAREER: String
    private var selectedImage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        UID = Globals.UserLogged.uid
        CAREER = Globals.UserLogged.career
        tv_career_cg.text = CAREER

        iv_imageGroup_cg.setOnClickListener {
            val intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(intent, 1)
        }
        btn_createGroup_cg.setOnClickListener {
            performGroupCreate()
        }

        iv_return_cg.setOnClickListener {
            finish()
        }
    }

    //Resultado de cargar imagen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Galeria
        if(requestCode == 1 && resultCode == RESULT_OK){
            val uri: Uri? = data?.data
            iv_imageGroup_cg.setImageURI(uri)
            selectedImage = true
        }
    }

    private fun performGroupCreate(){
        if(checkFields()){
            showProgress(true)
            saveImage()
        }
    }

    private fun saveImage(){
        val filename = UUID.randomUUID().toString()

        //Imagen
        iv_imageGroup_cg.isDrawingCacheEnabled = true
        iv_imageGroup_cg.buildDrawingCache()
        val bitmap = (iv_imageGroup_cg.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()

        //Crear referencia a imagen
        val imagenesRef = mStorageRef.child(filename+".png")
        imagenesRef.putBytes(data).addOnCompleteListener{
            imagenesRef.downloadUrl.addOnSuccessListener{ image ->
                //Crear grupo
                saveGroup(image.toString())
            }.addOnFailureListener {
                Toast.makeText(this, "Could not create group", Toast.LENGTH_SHORT).show()
                showProgress(false)
            }
        }.addOnFailureListener{
            Toast.makeText(this, "Error upload image", Toast.LENGTH_SHORT).show()
            showProgress(false)
        }
    }

    private fun saveGroup(image: String){

        val group = groupRef.push()
        val uidGroup = group.key ?: ""
        val title = et_title_cg.text.toString()
        val description = et_description_cg.text.toString()

        val obj = Groups(uidGroup, title, description, CAREER, image, UID)

        //Agregar nodo de grupo
        groupRef.child(uidGroup).setValue(obj).addOnSuccessListener {

            //Crear chat del grupo
            val chat = Chats(obj.uid,2,obj.title, "", ServerValue.TIMESTAMP, obj.image)
            chatRef.child(obj.uid).setValue(chat).addOnCompleteListener {

                //Agregar miembro al grupo
                group_membersRef.child(obj.uid).child(UID).setValue(UID).addOnSuccessListener {
                    //colocar uid e grupo en la lista del usuario
                    user_groupsRef.child(UID).child(obj.uid).setValue(obj.uid).addOnSuccessListener {
                        showProgress(false)
                        val activity = Intent(this, GroupActivity::class.java)
                        activity.putExtra("TITLE_GROUP", obj.title)
                        activity.putExtra("UID_GROUP", obj.uid)
                        activity.putExtra("IMAGE_GROUP", obj.image)
                        activity.putExtra("DESCRIPTION_GROUP", obj.description)
                        activity.putExtra("PROPETARY_GROUP", obj.propetary)
                        activity.putExtra("CAREER_GROUP", obj.career)
                        startActivity(activity)
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Could not create group", Toast.LENGTH_SHORT).show()
                        showProgress(false)
                    }

                }.addOnFailureListener {
                    Toast.makeText(this, "Could not create group", Toast.LENGTH_SHORT).show()
                    showProgress(false)
                }

            }.addOnFailureListener {
                Toast.makeText(this, "Could not create group", Toast.LENGTH_SHORT).show()
                showProgress(false)
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Could not create group", Toast.LENGTH_SHORT).show()
            showProgress(false)
        }

    }

    private fun checkFields() : Boolean{
        if(et_title_cg.text.isEmpty()){
            Toast.makeText(this, "Title required", Toast.LENGTH_SHORT).show()
            return false
        }
        if(et_description_cg.text.isEmpty()){
            Toast.makeText(this, "Description required", Toast.LENGTH_SHORT).show()
            return false
        }
        if(!selectedImage){
            Toast.makeText(this, "Image required", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }



    private fun showProgress(mostrar: Boolean){
        if(mostrar){
            progressBar5.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }else{
            progressBar5.visibility = View.GONE
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }
}
package com.fcfm.yuni_corn

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.fcfm.yuni_corn.models.Rewards
import com.fcfm.yuni_corn.utils.Globals
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_homework.*
import kotlinx.android.synthetic.main.custom_toast_01.*
import kotlinx.android.synthetic.main.custom_toast_01.view.*
import kotlinx.android.synthetic.main.dialog_upload_homework.view.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class HomeworkActivity : AppCompatActivity() {

    private var db = FirebaseDatabase.getInstance()
    private val homeworkRef = db.getReference("HOMEWORKS")
    private val user_HomeworkRef = db.getReference("USER_HOMEWORKS")
    private val mStorageRef: StorageReference = FirebaseStorage.getInstance().getReference()
    private val homeworkStorageRef = mStorageRef.child("Homeworks")
    private val rewardsRef = db.getReference("USER_REWARDS")

    private var UID_HOMEWORK = ""
    private var SENT = false
    private var POINTS = 0
    private var FINISH_DATE = ""
    private var TITLE = ""
    private var UID_GROUP = ""

    lateinit private var document: Uri
    private var nameDocument: String = ""
    private var uploadDocument = false

    private var logro = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homework)


        //Obtener paquete
        val UIDS = intent.getBundleExtra("HOMEWORK")
        if(UIDS != null){
            UID_HOMEWORK = UIDS.get("uidHomework") as String
            SENT = UIDS.get("sent") as Boolean
            POINTS = UIDS.get("points") as Int
            FINISH_DATE = UIDS.get("finishDate") as String
            TITLE = UIDS.get("title") as String
            UID_GROUP = UIDS.get("uidGroup") as String
            nameDocument = UIDS.get("nameDocument") as String
        }

        tv_points_h.text = POINTS.toString()
        tv_document_h.text = nameDocument

        loadHomework()

        for(item in Globals.listRewardsUser){
            if(item.uid == "logro_04"){
                logro = true
                break
            }
        }

        btn_selectDocument_h.setOnClickListener {
            //inflate el dialogo con el diseño
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_upload_homework, null)
            //Construir la alerta del dialogo
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("Select")
            //mostrar dialogo
            val mAlertDialog = mBuilder.show()

            mDialogView.btn_image_duh.setOnClickListener {
                mAlertDialog.dismiss()
                uploadDocument(1)
            }
            mDialogView.btn_pdf_duh.setOnClickListener {
                mAlertDialog.dismiss()
                uploadDocument(2)
            }
        }

        if(SENT) {
            btn_sendHomework_h.visibility = View.GONE
            btn_selectDocument_h.visibility = View.GONE
        }

        btn_sendHomework_h.setOnClickListener {
            if(uploadDocument){
                uploadHomework()
            }else{
                Toast.makeText(this, "Select a document", Toast.LENGTH_SHORT).show()
            }
            
        }

        iv_return_h.setOnClickListener {
            finish()
        }
    }

    private fun loadHomework(){
        homeworkRef.child(UID_HOMEWORK).get().addOnSuccessListener {
            tv_title_h.text= it.child("title").value.toString()
            tv_group_h.text = it.child("group").value.toString()
            tv_startDate_h.text = it.child("startDate").value.toString()
            tv_finishDate_h.text = it.child("finishDate").value.toString()
            tv_description_h.text = it.child("description").value.toString()
        }.addOnFailureListener {
            Toast.makeText(this, "Error load homework", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadDocument(type: Int){
        //Galeria
        if(type == 1){
            val intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(intent, 1)
        }
        //Camara
        if(type==2){
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "application/pdf"
            startActivityForResult(intent, 2)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Galeria
        if(requestCode == 1 && resultCode == RESULT_OK){
            val uri: Uri? = data?.data
            if (uri != null) {
                document = uri
                uploadDocument = true
                
                val f = File(uri.path.toString())
                nameDocument = f.name.toString()
                tv_document_h.text = nameDocument
            }


        }
        //Camara
        if(requestCode == 2 && resultCode == RESULT_OK){
            val uri: Uri? = data?.data
            if (uri != null) {
                document = uri

                uploadDocument = true
                val f = File(uri.path.toString())
                nameDocument = f.name.toString()
                tv_document_h.text = nameDocument
            }


        }
    }

    fun uploadHomework(){
        showProgress(true)

        val uidUser = Globals.UserLogged.uid
        homeworkStorageRef.child(uidUser).child(UID_HOMEWORK).child(nameDocument).putFile(document).addOnSuccessListener {
            homeworkStorageRef.child(uidUser).child(UID_HOMEWORK).child(nameDocument).downloadUrl.addOnSuccessListener {

                val sendDate = SimpleDateFormat("dd/MM/yyyy", Locale("es", "MX"))

                val childUpdates = mapOf<String, Any>(
                    "document" to it.toString(),
                    "nameDocument" to nameDocument,
                    "finishDate" to FINISH_DATE,
                    "points" to 100,
                    "sendDate" to sendDate.toString(),
                    "sent" to true,
                    "title" to TITLE,
                    "uid" to UID_HOMEWORK,
                    "uidGroup" to UID_GROUP,
                )

                //Actualizar datos
                user_HomeworkRef.child(uidUser).child(UID_HOMEWORK).updateChildren(childUpdates).addOnSuccessListener {
                    showProgress(false)
                    if(!logro){
                        showToastReward()
                    }

                    Toast.makeText(this, "homework sent", Toast.LENGTH_SHORT).show()
                    finish()
                }.addOnFailureListener {
                    showProgress(false)
                    Toast.makeText(this, "Homework could not be submitted", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                showProgress(false)
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener{
            showProgress(false)
            Toast.makeText(this, "Could not upload  document", Toast.LENGTH_SHORT).show()
        }
    }

    fun showProgress(show: Boolean){
        if(show){
            progressBar7.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }else{
            progressBar7.visibility = View.GONE
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    fun showToastReward(){

        val reward = Rewards(
            "logro_04",
            "Lo hice con mis lagrimas",
            "Se obtiene al enviar una tarea por primera vez"
        )
        rewardsRef.child(Globals.UserLogged.uid).child(reward.uid).setValue(reward).addOnSuccessListener {
            //Agregar a la lista
            Globals.listRewardsUser.add(reward)

            val mp: MediaPlayer
            mp = MediaPlayer.create(this, R.raw.notification)
            mp.start()

            val inflater = LayoutInflater.from(this)
            val layout: View = inflater.inflate(R.layout.custom_toast_01, ll_toastLayout_ct)
            layout.tv_title_ct.setText(reward.title)
            val toast = Toast(applicationContext)
            toast.setGravity(Gravity.CENTER_VERTICAL / Gravity.CENTER_HORIZONTAL, 0, 500)
            toast.duration = Toast.LENGTH_LONG
            toast.view = layout
            toast.show()
        }

    }
}
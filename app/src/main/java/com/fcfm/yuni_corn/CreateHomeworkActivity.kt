package com.fcfm.yuni_corn

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.fcfm.yuni_corn.models.Homeworks
import com.fcfm.yuni_corn.utils.Globals
import com.fcfm.yuni_corn.models.UserHomework
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_create_homework.*
import java.text.SimpleDateFormat
import java.util.*

class CreateHomeworkActivity : AppCompatActivity() {

    private var db = FirebaseDatabase.getInstance()
    private val homeworksRef = db.getReference("HOMEWORKS")
    private val user_homeworksRef = db.getReference("USER_HOMEWORKS")

    var UID_GROUP = ""
    var TITLE_GROUP = ""
    var CAREER_GROUP = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_homework)

        UID_GROUP = intent.getStringExtra("UID_GROUP") ?: ""
        TITLE_GROUP = intent.getStringExtra("TITLE_GROUP") ?: ""
        CAREER_GROUP = intent.getStringExtra("CAREER_GROUP") ?: ""

        tv_group_ch.text = TITLE_GROUP
        tv_career_ch.text = CAREER_GROUP

        btn_select_ch.setOnClickListener {
            selectDate()
        }

        btn_addHomework_ch.setOnClickListener {
            performHomeworkCreation()
        }
    }

    private fun checkFields() : Boolean{
        if(et_title_ch.text.isEmpty()){
            Toast.makeText(this, "Title required", Toast.LENGTH_SHORT).show()
            return false
        }

        if(et_description_ch.text.isEmpty()){
            Toast.makeText(this, "Description required", Toast.LENGTH_SHORT).show()
            return false
        }

        if(tv_finishDate_ch.text == "Finish date"){
            Toast.makeText(this, "Date of delivery required", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun selectDate(){
        var cal = Calendar.getInstance()

        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            tv_finishDate_ch.text = sdf.format(cal.time)

        }, Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH)

        dpd.getDatePicker().setMinDate(Date().time)
        dpd.show()
    }

    private fun performHomeworkCreation(){
        if(checkFields()){
            if(Globals.listMembersGroup.toString().isNotEmpty()){
                showProgress(true)
                saveHomework()
            }else{
                Toast.makeText(this, "There are not members in group", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveHomework(){
        showProgress(true)
        val homework = homeworksRef.push()
        val uidHomework = homework.key ?: ""
        val group = TITLE_GROUP
        val uidGroup = UID_GROUP
        val title = et_title_ch.text.toString()

        val startDate = SimpleDateFormat("dd/MM/yyyy", Locale("es", "MX"))
            .format(Date().time)
        val description = et_description_ch.text.toString()
        val finishDate = tv_finishDate_ch.text.toString()

        val obj = Homeworks(uidHomework,title,description, startDate, finishDate, uidGroup, group)

        homeworksRef.child(uidHomework).setValue(obj).addOnSuccessListener {
            showProgress(false)
            Toast.makeText(this, "Homework create with exit", Toast.LENGTH_SHORT).show()

            for(item in Globals.listMembersGroup){
                val userHomework = UserHomework(uidHomework, title,uidGroup, finishDate, "", "", "",0, false)
                user_homeworksRef.child(item).child(uidHomework).setValue(userHomework)
            }

            finish()
        }.addOnFailureListener {
            showProgress(false)
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        }
    }


    private fun showProgress(mostrar: Boolean){
        if(mostrar){
            progressBar6.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }else{
            progressBar6.visibility = View.GONE
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }
}
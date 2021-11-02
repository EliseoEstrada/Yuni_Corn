package com.fcfm.yuni_corn.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.fcfm.yuni_corn.AddMembersGroupActivity
import com.fcfm.yuni_corn.CreateHomeworkActivity
import com.fcfm.yuni_corn.R
import com.fcfm.yuni_corn.utils.Globals
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_group_data.*

class FragmentGroupData: Fragment(R.layout.fragment_group_data) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val TITLE_GROUP = getArguments()?.getString("TITLE_GROUP") ?: ""
        val UID_GROUP = getArguments()?.getString("UID_GROUP") ?: ""
        val IMAGE_GROUP = getArguments()?.getString("IMAGE_GROUP") ?: ""
        val DESCRIPTION_GROUP = getArguments()?.getString("DESCRIPTION_GROUP") ?: ""
        val PROPETARY_GROUP = getArguments()?.getString("PROPETARY_GROUP") ?: ""
        val CAREER_GROUP = getArguments()?.getString("CAREER_GROUP") ?: ""

        tv_name_fgd.text = TITLE_GROUP
        tv_description_fgd.text = DESCRIPTION_GROUP
        tv_career_fgd.text = CAREER_GROUP

        if(PROPETARY_GROUP == Globals.UserLogged.uid){
            btn_createHomework_fgd.visibility = View.VISIBLE
        }else{
            btn_createHomework_fgd.visibility = View.GONE
        }

        Picasso.get().load(IMAGE_GROUP)
            .placeholder(R.drawable.ic_baseline_image_24)
            .error(R.drawable.ic_baseline_image_24)
            .into(iv_group_fgd)


        btn_createHomework_fgd.setOnClickListener {
            val intent = Intent(activity, CreateHomeworkActivity::class.java)
            intent.putExtra("UID_GROUP", UID_GROUP)
            intent.putExtra("TITLE_GROUP", TITLE_GROUP)
            intent.putExtra("CAREER_GROUP", CAREER_GROUP)
            startActivity(intent)
        }
    }

}
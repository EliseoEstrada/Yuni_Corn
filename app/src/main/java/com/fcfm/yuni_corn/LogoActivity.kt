package com.fcfm.yuni_corn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler


class LogoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //Colocar el tema principal

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logo)

        Handler().postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }, 1500)


        //Thread.sleep(8000)
        //val registerActivity = Intent(this, LoginActivity::class.java)
        //startActivity(registerActivity)
        //finish()
    }

}
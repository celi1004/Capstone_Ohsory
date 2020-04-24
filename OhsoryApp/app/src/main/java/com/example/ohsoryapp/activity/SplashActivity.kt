package com.example.ohsoryapp.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.ohsoryapp.db.SharedPreferenceController
import org.jetbrains.anko.startActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.ohsoryapp.R.layout.activity_splash)

        val token = SharedPreferenceController.getAuthorization(this)

        Handler().apply {
            postDelayed({
                if(token.length==0){
                    startActivity<IntroActivity>()
                    finish()
                }else{
                    startActivity<MainActivity>("token" to token)
                    finish()
                }
            }, 2000)
        }
    }
}

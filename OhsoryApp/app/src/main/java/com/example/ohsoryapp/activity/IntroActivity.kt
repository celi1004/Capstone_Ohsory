package com.example.ohsoryapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ohsoryapp.R
import com.example.ohsoryapp.adapter.IntroPagerAdapter
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        configureIntroNavigation()
    }

    private fun configureIntroNavigation() {
        vp_intro.adapter = IntroPagerAdapter(supportFragmentManager, 4)
        vp_intro.offscreenPageLimit = 4

        lo_tab_intro.setupWithViewPager(vp_intro)
    }
}

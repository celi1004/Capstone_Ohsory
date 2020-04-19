package com.example.ohsoryapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.example.ohsoryapp.R
import com.example.ohsoryapp.adapter.MainPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var time : Long = 0

    companion object {
        lateinit var instance: MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureBottomNavigation()

        instance = this

        //TODO Seekbar 안 움직이게 터치리스너 달고 막아버려
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis()-time>=2000){
            time=System.currentTimeMillis()
            Toast.makeText(getApplicationContext(),"뒤로 버튼을 한번 더 누르면 종료합니다.", Toast.LENGTH_SHORT).show();
        }else if(System.currentTimeMillis()-time<2000){
            finish()
        }
    }

    private fun configureBottomNavigation() {
        vp_bottom_navi_act_frag_pager.adapter = MainPagerAdapter(supportFragmentManager, 4)
        vp_bottom_navi_act_frag_pager.offscreenPageLimit = 4

        tl_bottom_navi_act_bottom_menu.setupWithViewPager(vp_bottom_navi_act_frag_pager)
        //TabLayout에 붙일 layout을 찾아준 다음
        val bottomNaviLayout: View = this.layoutInflater.inflate(R.layout.tab_bar, null, false)
        //탭 하나하나 TabLayout에 연결시켜줍니다.
        tl_bottom_navi_act_bottom_menu.getTabAt(0)!!.customView = bottomNaviLayout.findViewById(R.id.ibtn_main_act_chart) as ImageView
        tl_bottom_navi_act_bottom_menu.getTabAt(1)!!.customView = bottomNaviLayout.findViewById(R.id.ibtn_main_act_file) as ImageView
        tl_bottom_navi_act_bottom_menu.getTabAt(2)!!.customView = bottomNaviLayout.findViewById(R.id.ibtn_main_act_bell) as ImageView
        tl_bottom_navi_act_bottom_menu.getTabAt(3)!!.customView = bottomNaviLayout.findViewById(R.id.ibtn_main_act_user) as ImageView

        tl_bottom_navi_act_bottom_menu.getTabAt(0)!!.select()
        //기본으로 0 셀렉됌
    }
}

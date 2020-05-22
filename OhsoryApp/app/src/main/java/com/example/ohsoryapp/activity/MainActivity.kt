package com.example.ohsoryapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ohsoryapp.adapter.MainPagerAdapter
import com.example.ohsoryapp.myclass.NetworkManager
import kotlinx.android.synthetic.main.activity_main.*
import com.example.ohsoryapp.myclass.PermissionHelper
import android.content.Intent
import android.content.DialogInterface
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import androidx.core.view.accessibility.AccessibilityManagerCompat.getEnabledAccessibilityServiceList
import android.content.Context.ACCESSIBILITY_SERVICE
import android.view.accessibility.AccessibilityManager
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.appcompat.app.AlertDialog
import android.provider.Settings


class MainActivity : AppCompatActivity() {

    internal var mPermissionsHelper: PermissionHelper? = null
    internal var mNetworkManager : NetworkManager? = null

    var time : Long = 0

    companion object {
        lateinit var instance: MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.ohsoryapp.R.layout.activity_main)

        configureBottomNavigation()

        instance = this

        mPermissionsHelper = PermissionHelper(this)
        mPermissionsHelper!!.requestAllPermissions(this)

        fileTransfer()
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
        //기본으로 0 셀렉됌

        tl_bottom_navi_act_bottom_menu.getTabAt(0)?.setIcon(com.example.ohsoryapp.R.drawable.ic_tab_bar_chart)
        tl_bottom_navi_act_bottom_menu.getTabAt(1)?.setIcon(com.example.ohsoryapp.R.drawable.ic_tab_file)
        tl_bottom_navi_act_bottom_menu.getTabAt(2)?.setIcon(com.example.ohsoryapp.R.drawable.ic_tab_bell)
        tl_bottom_navi_act_bottom_menu.getTabAt(3)?.setIcon(com.example.ohsoryapp.R.drawable.ic_tab_user)
    }

    private fun fileTransfer(){
        mNetworkManager = NetworkManager(this)
        if(mNetworkManager!!.checkNetworkState()){
            //데이터가 연결되어있으면
            Toast.makeText(this@MainActivity, "인터넷 연결", Toast.LENGTH_SHORT).show()

            //파일 서버로 보내고

            //그 파일 지워
        }else{
            Toast.makeText(this@MainActivity, "연결없음", Toast.LENGTH_SHORT).show()
        }

    }
}

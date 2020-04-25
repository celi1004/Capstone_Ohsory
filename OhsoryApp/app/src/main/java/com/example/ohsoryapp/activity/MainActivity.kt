package com.example.ohsoryapp.activity

import android.Manifest
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.aykuttasil.callrecord.CallRecord
import com.example.ohsoryapp.adapter.MainPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.aykuttasil.callrecord.helper.LogUtils
import com.example.ohsoryapp.R
import com.example.ohsoryapp.listener.PhoneListener
import com.example.ohsoryapp.myclass.PermissionHelper


class MainActivity : AppCompatActivity() {

    internal var mPermissionsHelper: PermissionHelper? = null

    var time : Long = 0
    lateinit var phoneListener : PhoneListener

    private lateinit var callRecord: CallRecord

    companion object {
        lateinit var instance: MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.ohsoryapp.R.layout.activity_main)

        //권한 허락 받아

        configureBottomNavigation()

        phoneListener = PhoneListener.getInstance(this) //앱이 시작될때 한번 필수로 만들어줘야해!

        callrecord()
        StartCallRecordClick()

        instance = this


        mPermissionsHelper = PermissionHelper(this)
        mPermissionsHelper!!.requestAllPermissions(this)
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
        //기본으로 0 셀렉됌

        tl_bottom_navi_act_bottom_menu.getTabAt(0)?.setIcon(com.example.ohsoryapp.R.drawable.ic_tab_bar_chart)
        tl_bottom_navi_act_bottom_menu.getTabAt(1)?.setIcon(com.example.ohsoryapp.R.drawable.ic_tab_file)
        tl_bottom_navi_act_bottom_menu.getTabAt(2)?.setIcon(com.example.ohsoryapp.R.drawable.ic_tab_bell)
        tl_bottom_navi_act_bottom_menu.getTabAt(3)?.setIcon(com.example.ohsoryapp.R.drawable.ic_tab_user)
    }

    private fun callrecord(){
        callRecord = CallRecord.Builder(this)
            .setRecordFileName(
                "Record_" + SimpleDateFormat(
                    "ddMMyyyyHHmmss",
                    Locale.US
                ).format(Date())
            )
            .setRecordDirName("CallRecord")
            .setRecordDirPath(Environment.getExternalStorageDirectory().path)
            .setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            .setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
            .setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
            .setShowSeed(true).build()
    }

    fun StartCallRecordClick() {
        LogUtils.i("시발", "StartCallRecordClick")
        callRecord.startCallReceiver()

        //callRecord.enableSaveFile();
        //callRecord.changeRecordDirName("NewDirName");
    }

    fun StopCallRecordClick() {
        LogUtils.i("시발", "StopCallRecordClick")
        callRecord.stopCallReceiver()

        //callRecord.disableSaveFile();
        //callRecord.changeRecordFileName("NewFileName");
    }
}

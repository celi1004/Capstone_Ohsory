package com.example.ohsoryapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ohsoryapp.adapter.MainPagerAdapter
import com.example.ohsoryapp.myclass.NetworkManager
import kotlinx.android.synthetic.main.activity_main.*
import com.example.ohsoryapp.myclass.PermissionHelper
import okhttp3.RequestBody
import okhttp3.MediaType
import java.io.File
import android.util.Log
import com.example.ohsoryapp.data.FileUploadData
import com.example.ohsoryapp.db.SharedPreferenceController
import com.example.ohsoryapp.network.ApplicationController
import com.example.ohsoryapp.network.NetworkService
import com.example.ohsoryapp.post.PostFileUpload
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    internal var mPermissionsHelper: PermissionHelper? = null
    internal var mNetworkManager : NetworkManager? = null

    var time : Long = 0

    var mDirPath = "/sdcard/AudioRecorder/"

    var user_id = 0

    companion object {
        lateinit var instance: MainActivity
    }

    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.ohsoryapp.R.layout.activity_main)

        configureBottomNavigation()

        user_id = SharedPreferenceController.getUserID(this)

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

            val directory = File(mDirPath);
            val dir_name_list : Array<File> = directory.listFiles();

            var fpath : String

            if(dir_name_list==null){

            }else{
                if(dir_name_list.size==0){

                }else{
                    for(i in 0 until dir_name_list.size){
                        //파일들을 하나하나 돌면서 이름, 생성날짜 가져오기
                        fpath = dir_name_list[i].path

                        uploadFile(fpath)
                    }
                }
            }


            //그 파일 지워
        }else{
            Toast.makeText(this@MainActivity, "연결없음", Toast.LENGTH_SHORT).show()
        }

    }

    private fun uploadFile(fpath : String){

        val file = File(fpath)

//        val tfile = RequestBody.create(MediaType.parse("audio/*"), file)
//        val fileUploadData = FileUploadData(user_id, tfile)

//        val postFileUpload = networkService.postFileUpload(fileUploadData)

        val tfile = RequestBody.create(MediaType.parse("multipart/form-data"), file)

        val postFileUpload = networkService.postFileUpload(user_id, tfile)

        postFileUpload!!.enqueue(object : Callback<PostFileUpload> {
            //통신을 못 했을 때
            override fun onFailure(call: Call<PostFileUpload>, t: Throwable) {
                Log.e("file upload fail", t.toString())
            }

            override fun onResponse(call: Call<PostFileUpload>, response: Response<PostFileUpload>) {
                //통신을 성공적으로 했을 때
                if (response.isSuccessful) {
                    //서버로 보내는 거 성공하면 삭제
                    Toast.makeText(this@MainActivity, fpath+"삭제", Toast.LENGTH_LONG).show()
                    val f : File = File(fpath)
                    f.delete()
                }
                else{
                    Toast.makeText(this@MainActivity, fpath+" "+response.code().toString(), Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}

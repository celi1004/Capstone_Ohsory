package com.example.ohsoryapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.ohsoryapp.R
import com.example.ohsoryapp.myclass.DBHelper
import kotlinx.android.synthetic.main.activity_ttstest.*
import java.io.File
import com.example.ohsoryapp.data.ShareeData
import com.example.ohsoryapp.db.SharedPreferenceController
import com.example.ohsoryapp.get.GetProgressResponse
import com.example.ohsoryapp.get.GetShareModelListResponse
import com.example.ohsoryapp.myclass.NetworkManager
import com.example.ohsoryapp.network.ApplicationController
import com.example.ohsoryapp.network.NetworkService
import org.jetbrains.anko.share
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class TTSTestActivity : AppCompatActivity() {

    lateinit var shareeData: ShareeData

    internal var mNetworkManager : NetworkManager? = null
    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }

    var model_name_list = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ttstest)

        setSpinnser()

        setButtonClickListener()
    }

    fun setButtonClickListener(){

        bt_hear.setOnClickListener(){
            //서버에 듣고 싶은 문장 보내고 받아오고 들려주기
        }

        bt_dowload.setOnClickListener(){
            //서버에서 받아온 파일 내장 디비에

            val dirPath = Environment.getExternalStorageDirectory().toString() + "/OhSory"
            val subPath = "/내 모델/"
            val filename = et_text.text.toString() +".wav"

            //디렉토리 없으면 생성
            val dir = File(dirPath)
            if (!dir.exists()) {
                dir.mkdir()
            }

            val subdir = File(dir, subPath)
            if (!subdir.exists()) {
                subdir.mkdir()
            }

            //파일객체
            val file = File(subdir, filename)
            try {
                //쓰기객체
                val fos = FileOutputStream(file)
                //버퍼 - 1MB씩쓰기
                val buffer = ByteArray(1 * 1024 * 1024)
                for (i in 0..9) {    //10MB
                    fos.write(buffer, 0, buffer.size)    //1MB씩 10번쓰기
                    fos.flush()
                }
                val len = 0

                fos.close()

                Toast.makeText(this, "다운로드 완료", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        bt_share.setOnClickListener(){
            //서버에서 받아온 파일 공유
        }
    }

    private fun setSpinnser(){
        model_name_list.clear()

        mNetworkManager = NetworkManager(this)
        if(mNetworkManager!!.checkNetworkState()){
            //데이터가 연결되어있으면

            //만약 내가 전 화면에서 progress가 100이라면 내 모델 사용 가능
            //model_name_list.add("내 모델")

            //서버랑 통신해서 내가 가진 모델 유저 네임들 나타내기
            val sharee_name = SharedPreferenceController.getUserName(this)
            shareeData = ShareeData(sharee_name)
            val getProgressResponse = networkService.getSharedModelListResponse(shareeData)

            getProgressResponse!!.enqueue(object : Callback<ArrayList<GetShareModelListResponse>> {
                //통신을 못 했을 때
                override fun onFailure(call: Call<ArrayList<GetShareModelListResponse>>, t: Throwable) {
                    Log.e("load fail", t.toString())
                }

                override fun onResponse(call: Call<ArrayList<GetShareModelListResponse>>, response: Response<ArrayList<GetShareModelListResponse>>) {
                    //통신을 성공적으로 했을 때
                    if (response.isSuccessful) {
                        val listlen = response.body()!!.size
                        for (element in 0 .. listlen - 1) {
                            model_name_list.add(response.body()!![listlen - element - 1].sharer_name)
                        }
                        response
                    }
                    else{
                        Log.e("서버 에러", "서버 에러")
                    }
                }
            })

            //model_name_list.add("minhee0325")
        }else{
            model_name_list.add("네트워크 연결이 필요합니다")
        }

        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, model_name_list)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        sp_model_name.adapter = arrayAdapter

        //볼 파일이 있을 때만 클릭리스너 작동
        sp_model_name.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                //선택된 모델로 할일 하세요
            }
        }
    }
}

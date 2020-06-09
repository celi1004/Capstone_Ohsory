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
import java.util.*
import kotlin.collections.ArrayList
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.example.ohsoryapp.data.FileDownloadData
import com.example.ohsoryapp.data.NotificationData
import com.example.ohsoryapp.get.GetFileDownloadResponse
import com.example.ohsoryapp.post.PostNotificationResponse
import okhttp3.ResponseBody
import java.io.*




class TTSTestActivity : AppCompatActivity() {

    lateinit var shareeData: ShareeData
    lateinit var fileDownloadData : FileDownloadData
    lateinit var notificationData : NotificationData

    internal var mNetworkManager : NetworkManager? = null
    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }

    var model_name_list = ArrayList<String>()
    var selected_model_name = ""
    var sharee_name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ttstest)

        sharee_name = SharedPreferenceController.getUserName(this)

        setSpinnser()

        setButtonClickListener()
    }

    fun setButtonClickListener(){

        bt_hear.setOnClickListener(){
            downloadFile(et_text.text.toString())
            //서버에 듣고 싶은 문장 보내고 받아오고 들려주기
            Toast.makeText(this@TTSTestActivity, et_text.text.toString(), Toast.LENGTH_SHORT).show()
            sendAlarm(0, et_text.text.toString())
        }

        bt_dowload.setOnClickListener(){
            //서버에서 받아온 파일 내장 디비에
            sendAlarm(1, et_text.text.toString())

            val dirPath = Environment.getExternalStorageDirectory().toString() + "/OhSory"
            val subPath = "/"+selected_model_name+"/"
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
            //파일이 있으면 공유 없으면 청취 버튼부터 누르라고!
        }
    }

    private fun downloadFile(req_text : String){
        fileDownloadData = FileDownloadData(sharee_name, req_text) //FileDownloadData(selected_model_name, req_text)
        val getFileDownload = networkService.getFileDownload(fileDownloadData)

        getFileDownload!!.enqueue(object : Callback<GetFileDownloadResponse> {
            //통신을 못 했을 때
            override fun onFailure(call: Call<GetFileDownloadResponse>, t: Throwable) {
                Log.e("load fail", t.toString())
            }

            override fun onResponse(call: Call<GetFileDownloadResponse>, response: Response<GetFileDownloadResponse>) {
                //통신을 성공적으로 했을 때
                if (response.isSuccessful) {

                    val getFileDownloadUseUrl = networkService.getFileDownloadUseUrl(response.body()!!.tts_file)

                    getFileDownloadUseUrl!!.enqueue(object : Callback<ResponseBody> {
                        //통신을 못 했을 때
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Log.e("load fail", t.toString())
                        }

                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            //통신을 성공적으로 했을 때
                            if (response.isSuccessful) {
                                val writtenToDisk = writeResponseBodyToDisk(response.body()!!)
                                Log.i("success?", writtenToDisk.toString())
                            } else {
                                Log.e("서버에러" + response.code().toString(), "파일 다운로드 실패")
                            }
                        }
                    })
                } else {
                    Log.e("서버에러" + response.code().toString(), "파일 다운로드 링크 불러오기 실패")
                }
            }
        })
    }

    private fun writeResponseBodyToDisk(body: ResponseBody): Boolean {
        try {
            val dirPath = Environment.getExternalStorageDirectory().toString() + "/OhSory"
            val subPath = "/"+sharee_name+"/"
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

            // todo change the file location/name according to your needs
            val futureStudioIconFile = File(subdir, filename)

            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null

            try {
                val fileReader = ByteArray(4096)

                val fileSize = body.contentLength()
                var fileSizeDownloaded: Long = 0

                inputStream = body.byteStream()
                outputStream = FileOutputStream(futureStudioIconFile)

                while (true) {
                    val read = inputStream!!.read(fileReader)

                    if (read == -1) {
                        break
                    }

                    outputStream!!.write(fileReader, 0, read)

                    fileSizeDownloaded += read.toLong()

                    Log.d("File Download: ", "$fileSizeDownloaded of $fileSize")
                }

                outputStream!!.flush()

                return true
            } catch (e: IOException) {
                return false
            } finally {
                if (inputStream != null) {
                    inputStream!!.close()
                }

                if (outputStream != null) {
                    outputStream!!.close()
                }
            }
        } catch (e: IOException) {
            return false
        }

    }

    private fun sendAlarm(req_type : Int, req_text : String){
        notificationData = NotificationData(selected_model_name, sharee_name, req_type, req_text)
        val postNotificationResponse = networkService.postNotificationResponse(notificationData)

        postNotificationResponse!!.enqueue(object : Callback<PostNotificationResponse> {
            //통신을 못 했을 때
            override fun onFailure(call: Call<PostNotificationResponse>, t: Throwable) {
                Log.e("load fail", t.toString())
            }

            override fun onResponse(call: Call<PostNotificationResponse>, response: Response<PostNotificationResponse>) {
                //통신을 성공적으로 했을 때
                if (response.isSuccessful) {
                    Toast.makeText(this@TTSTestActivity, notificationData.toString(), Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("알람" + response.code().toString(), "서버 에러")
                }
            }
        })
    }

    private fun setSpinnser(){
        model_name_list.clear()

        mNetworkManager = NetworkManager(this)
        if(mNetworkManager!!.checkNetworkState()){
            //데이터가 연결되어있으면

            //만약 내가 전 화면에서 progress가 100이라면 내 모델 사용 가능
            //model_name_list.add("내 모델")

            //서버랑 통신해서 내가 가진 모델 유저 네임들 나타내기
            shareeData = ShareeData(sharee_name)
            val getSharedModelListResponse = networkService.getSharedModelListResponse(shareeData)

            getSharedModelListResponse!!.enqueue(object : Callback<ArrayList<GetShareModelListResponse>> {
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
                    }
                    else{
                        if(response.code() == 400){
                            if(model_name_list.size == 0){
                                model_name_list.add("사용할 수 있는 모델이 없습니다")
                            }
                        }else{
                            Log.e("스피너" + response.code().toString(), "서버 에러")
                        }
                    }

                    val arrayAdapter = ArrayAdapter(this@TTSTestActivity, android.R.layout.simple_spinner_item, model_name_list)
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    sp_model_name.adapter = arrayAdapter

                    //볼 파일이 있을 때만 클릭리스너 작동
                    sp_model_name.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                        override fun onNothingSelected(p0: AdapterView<*>?) {

                        }

                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                            //선택된 모델로 할일 하세요
                            selected_model_name = model_name_list[position]
                        }
                    }
                }
            })
        }else{
            model_name_list.add("네트워크 연결이 필요합니다")

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
}

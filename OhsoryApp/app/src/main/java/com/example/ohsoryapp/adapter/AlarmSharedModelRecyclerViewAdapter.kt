package com.example.ohsoryapp.adapter

import android.content.Context
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.ohsoryapp.R
import com.example.ohsoryapp.data.AlarmModelData
import com.example.ohsoryapp.data.FileDownloadData
import com.example.ohsoryapp.get.GetFileDownloadResponse
import com.example.ohsoryapp.network.ApplicationController
import com.example.ohsoryapp.network.NetworkService
import kotlinx.android.synthetic.main.activity_ttstest.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class AlarmSharedModelRecyclerViewAdapter(val ctx: Context, val dataList: ArrayList<AlarmModelData>, val token : String) : RecyclerView.Adapter<AlarmSharedModelRecyclerViewAdapter.Holder>() {

    lateinit var mContext : Context

    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        mContext = ctx
        val view: View = LayoutInflater.from(ctx).inflate(R.layout.rv_item_alarm_shared_model , parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = dataList.size

    //0 다운로드 승인 대기 중 1 다운 승인 2 다운 거절 3 청취
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.date.text = dataList[position].date
        holder.username.text = dataList[position].username
        holder.sentence.text = dataList[position].sentence
        var tempInt : Int = dataList[position].state
        when(tempInt){
            0 -> holder.state.text="승인 대기"
            1 -> {holder.state.visibility = View.GONE
                holder.download.visibility = View.VISIBLE}
            2 -> holder.state.text="요청 거절"
            3 -> holder.state.text="청취 기록"
            else -> holder.state.text="오류 발생"
        }

        holder.download.setOnClickListener{
            downloadFile(dataList[position])
        }

        //셋 온클릭같이 해야하는 일도 여기서!
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.tv_rv_item_alarm_shared_model_date) as TextView
        val username: TextView = itemView.findViewById(R.id.tv_rv_item_alarm_shared_model_username) as TextView
        val sentence: TextView = itemView.findViewById(R.id.tv_rv_item_alarm_shared_model_sentence) as TextView
        val state: TextView = itemView.findViewById(R.id.tv_rv_item_alarm_shared_model_state) as TextView
        val download : Button = itemView.findViewById(R.id.bt_rv_item_alarm_shared_download) as Button
    }

    private fun downloadFile(alarmModelData : AlarmModelData){
        Toast.makeText(mContext, "다운로드 시작", Toast.LENGTH_SHORT).show()
        val getFileDownload = networkService.getFileDownload(FileDownloadData(alarmModelData.username, alarmModelData.sentence))

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
                                val writtenToDisk = writeResponseBodyToDisk(response.body()!!, alarmModelData.username, alarmModelData.sentence)
                                if(writtenToDisk){
                                    Toast.makeText(mContext, "다운로드 완료", Toast.LENGTH_SHORT).show()
                                }
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

    private fun writeResponseBodyToDisk(body: ResponseBody, selected_model_name : String, sentence : String): Boolean {
        try {
            val dirPath = Environment.getExternalStorageDirectory().toString() + "/OhSory"
            val subPath = "/"+selected_model_name+"/"
            val filename = sentence +".wav"

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
}
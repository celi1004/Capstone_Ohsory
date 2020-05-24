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
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AlarmSharedModelRecyclerViewAdapter(val ctx: Context, val dataList: ArrayList<AlarmModelData>, val token : String) : RecyclerView.Adapter<AlarmSharedModelRecyclerViewAdapter.Holder>() {

    lateinit var mContext : Context

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
            makeTempfile(dataList[position].username, dataList[position].sentence)
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

    fun makeTempfile(username : String, filename : String){
        val dirPath = Environment.getExternalStorageDirectory().toString() + "/OhSory"
        val subPath = "/"+username+"/"
        val filename = filename +".wav"

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

            Toast.makeText(mContext, "다운로드 완료", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
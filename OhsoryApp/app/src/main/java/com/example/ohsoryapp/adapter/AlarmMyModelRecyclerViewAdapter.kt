package com.example.ohsoryapp.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.example.ohsoryapp.R
import com.example.ohsoryapp.data.AlarmModelData
import com.example.ohsoryapp.network.ApplicationController
import com.example.ohsoryapp.network.NetworkService
import com.example.ohsoryapp.put.PutAfterRequestNotificationResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.os.HandlerCompat.postDelayed
import android.os.Handler


class AlarmMyModelRecyclerViewAdapter(val ctx: Context, val dataList: ArrayList<AlarmModelData>, val token : String) : RecyclerView.Adapter<AlarmMyModelRecyclerViewAdapter.Holder>() {

    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }

    lateinit var putAfterRequestNotificationResponse: PutAfterRequestNotificationResponse

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View = LayoutInflater.from(ctx).inflate(R.layout.rv_item_alarm_my_model , parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.date.text = dataList[position].date
        holder.username.text = dataList[position].username
        holder.sentence.text = dataList[position].sentence
        var tempInt : Int = dataList[position].state

        //0 다운로드 승인 대기 중 1 다운 승인 2 다운 거절 3 청취
        when(tempInt){
            0 -> {holder.state.visibility = View.GONE
                holder.ok.visibility = View.VISIBLE
                holder.rej.visibility = View.VISIBLE}
            1 -> holder.state.text="다운로드 승인"
            2 -> holder.state.text="다운로드 거절"
            3 -> holder.state.text="청취"
            else -> holder.state.text="오류 발생"
        }

        //셋 온클릭같이 해야하는 일도 여기서!
        holder.ok.setOnClickListener{
            downloadApprove(dataList[position].id, 1,position)
        }

        holder.rej.setOnClickListener{
            downloadApprove(dataList[position].id, 2, position)
        }
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.tv_rv_item_alarm_my_model_date) as TextView
        val username: TextView = itemView.findViewById(R.id.tv_rv_item_alarm_my_model_username) as TextView
        val sentence: TextView = itemView.findViewById(R.id.tv_rv_item_alarm_my_model_sentence) as TextView
        val state: TextView = itemView.findViewById(R.id.tv_rv_item_alarm_my_model_state) as TextView
        val ok : Button = itemView.findViewById(R.id.bt_rv_item_alarm_my_model_ok) as Button
        val rej : Button = itemView.findViewById(R.id.bt_rv_item_alarm_my_model_rej) as Button
    }

    private fun downloadApprove(id : Int, if_approve : Int, pos : Int){
        putAfterRequestNotificationResponse = PutAfterRequestNotificationResponse(id, if_approve)

        val putApproveResponse = networkService.putApproveResponse(id, putAfterRequestNotificationResponse)

        putApproveResponse.enqueue(object : Callback<PutAfterRequestNotificationResponse> {
            //통신을 못 했을 때
            override fun onFailure(call: Call<PutAfterRequestNotificationResponse>, t: Throwable) {
                Log.e("load fail", t.toString())
            }

            override fun onResponse(call: Call<PutAfterRequestNotificationResponse>, response: Response<PutAfterRequestNotificationResponse>) {
                //통신을 성공적으로 했을 때
                if (response.isSuccessful) {
                    Toast.makeText(ctx, "다운로드 요청을 처리하였습니다", Toast.LENGTH_SHORT).show()
                    dataList[pos].state = if_approve
                    notifyItemChanged(pos)
                }
                else{
                    Log.e("서버 에러", "서버 에러")
                }
            }
        })
    }
}
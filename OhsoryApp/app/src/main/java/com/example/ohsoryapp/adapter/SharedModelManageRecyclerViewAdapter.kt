package com.example.ohsoryapp.adapter

import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.ohsoryapp.R
import com.example.ohsoryapp.data.ListenDownloadAlarmData
import com.example.ohsoryapp.data.SharedModelManageData
import com.example.ohsoryapp.db.SharedPreferenceController
import com.example.ohsoryapp.network.ApplicationController
import com.example.ohsoryapp.network.NetworkService
import com.example.ohsoryapp.put.PutShareAuthResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SharedModelManageRecyclerViewAdapter(val ctx: Context, val dataList: ArrayList<SharedModelManageData>, val token : String) : RecyclerView.Adapter<SharedModelManageRecyclerViewAdapter.Holder>() {

    lateinit var listenDownloadAlarmData: ListenDownloadAlarmData

    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View = LayoutInflater.from(ctx).inflate(R.layout.rv_item_shared_model_manage, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.username.text = dataList[position].username
        var alarmsettingtext = "청취 시 "
        if(dataList[position].ln){
            alarmsettingtext += "on "
        }else{
            alarmsettingtext += "off "
        }
        alarmsettingtext += "\n다운로드 시 "
        if(dataList[position].dn){
            alarmsettingtext += "on"
        }else{
            alarmsettingtext += "off"
        }
        holder.alarmsetting.text=alarmsettingtext

        if(dataList[position].da){
            holder.auth.text="O"
        }else{
            holder.auth.text="X"
        }
        //셋 온클릭같이 해야하는 일도 여기서!

        holder.state.visibility = View.GONE
        holder.delete.visibility = View.VISIBLE

        holder.alarmsetting.setOnClickListener{
            showShareEditDialog(position)
        }

        holder.auth.setOnClickListener{
            showShareEditDialog(position)
        }

        holder.delete.setOnClickListener{
            Log.i("먀", dataList[position].share_id.toString())
            val deleteShareDeleteResponse = networkService.deleteShareDeleteResponse(dataList[position].share_id)

            deleteShareDeleteResponse.enqueue(object : Callback<Void> {
                //통신을 못 했을 때
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("load fail", t.toString())
                }

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    //통신을 성공적으로 했을 때
                    if (response.isSuccessful) {
                        Toast.makeText(ctx, "모델 공유를 취소하였습니다", Toast.LENGTH_SHORT).show()
                        dataList.removeAt(position)
                        notifyItemChanged(position)
                    }
                    else{
                        Log.e("서버 에러", "서버 에러")
                    }
                }
            })
        }
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.tv_rv_item_shared_model_manage_username) as TextView
        val alarmsetting: TextView = itemView.findViewById(R.id.tv_rv_item_shared_model_manage_alarmsetting) as TextView
        val auth: TextView = itemView.findViewById(R.id.tv_rv_item_shared_model_manage_auth) as TextView
        val state: TextView = itemView.findViewById(R.id.tv_rv_item_shared_model_manage_state) as TextView
        val delete: Button = itemView.findViewById(R.id.btn_rv_item_shared_model_manage_delete) as Button
    }

    fun showShareEditDialog(pos : Int){
        val layoutInflater : LayoutInflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val builder = AlertDialog.Builder(ctx)
        val dialogView = layoutInflater.inflate(R.layout.dialog_shareeditmymodel, null)
        val listen_noti = dialogView.findViewById<ToggleButton>(R.id.rb_listennoti)
        val down_noti = dialogView.findViewById<ToggleButton>(R.id.rb_downnoti)
        val down_auth = dialogView.findViewById<ToggleButton>(R.id.rb_downauth)

        listen_noti.isChecked = dataList[pos].ln
        down_noti.isChecked = dataList[pos].dn
        down_auth.isChecked = dataList[pos].da

        builder.setView(dialogView)
                .setPositiveButton("확인") { dialogInterface, i ->
                    //알림 설정 변경해주는 서버 작업해줘
                    val b1 = listen_noti.isChecked
                    val b2 = down_noti.isChecked
                    val b3 = down_auth.isChecked

                    dataList[pos].ln = listen_noti.isChecked
                    dataList[pos].dn = down_noti.isChecked
                    dataList[pos].da = down_auth.isChecked

                    val sharerName = SharedPreferenceController.getUserName(ctx)
                    val shareeName = dataList[pos].username

                    listenDownloadAlarmData = ListenDownloadAlarmData(b1, b2, b3)

                    shareEditMyModel(dataList[pos].share_id, listenDownloadAlarmData)

                    notifyItemChanged(pos)
                }
                .setNegativeButton("취소") { dialogInterface, i ->

                }
                .show()

    }

    fun shareEditMyModel(share_id : Int, listenDownloadAlarmData: ListenDownloadAlarmData){
        val putAuthUpdateResponse = networkService.putAuthUpdateResponse(share_id, listenDownloadAlarmData)

        putAuthUpdateResponse.enqueue(object : Callback<PutShareAuthResponse> {
            //통신을 못 했을 때

            override fun onFailure(call: Call<PutShareAuthResponse>, t: Throwable) {
                Log.i("서버에러", "실패")
            }

            override fun onResponse(call: Call<PutShareAuthResponse>, response: Response<PutShareAuthResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(ctx, "알림 변경 완료", Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(ctx, "알림 변경 실패", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}
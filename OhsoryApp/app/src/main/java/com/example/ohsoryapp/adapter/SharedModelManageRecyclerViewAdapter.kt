package com.example.ohsoryapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ohsoryapp.R
import com.example.ohsoryapp.data.SharedModelManageData

class SharedModelManageRecyclerViewAdapter(val ctx: Context, val dataList: ArrayList<SharedModelManageData>, val token : String) : RecyclerView.Adapter<SharedModelManageRecyclerViewAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View = LayoutInflater.from(ctx).inflate(R.layout.rv_item_shared_model_manage, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.username.text = dataList[position].username
        var tempString : String? = dataList[position].alarmsetting.toString()
        when(tempString){
            "1" -> holder.alarmsetting.text="청취 / 다운로드 알람"
            "2" -> holder.alarmsetting.text="청취 알림 없음 /\n다운로드 허락"
            "3" -> holder.alarmsetting.text="청취 알람 없음 /\n다운로드 알람"
            else -> holder.alarmsetting.text="전체 알림 없음"
        }
        holder.shareddate.text = dataList[position].shareddate.toString()
        tempString = dataList[position].state.toString()
        when(tempString){
            "1" -> holder.state.text="공유 중"
            "2" -> holder.state.text="공유 취소"
            else -> holder.state.text="상태"
        }
        //셋 온클릭같이 해야하는 일도 여기서!
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.tv_rv_item_shared_model_manage_username) as TextView
        val alarmsetting: TextView = itemView.findViewById(R.id.tv_rv_item_shared_model_manage_alarmsetting) as TextView
        val shareddate: TextView = itemView.findViewById(R.id.tv_rv_item_shared_model_manage_shareddate) as TextView
        val state: TextView = itemView.findViewById(R.id.tv_rv_item_shared_model_manage_state) as TextView
    }
}
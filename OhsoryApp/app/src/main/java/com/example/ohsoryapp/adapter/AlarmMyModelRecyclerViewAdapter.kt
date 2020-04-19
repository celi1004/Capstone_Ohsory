package com.example.ohsoryapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ohsoryapp.R
import com.example.ohsoryapp.data.AlarmModelData

class AlarmMyModelRecyclerViewAdapter(val ctx: Context, val dataList: ArrayList<AlarmModelData>, val token : String) : RecyclerView.Adapter<AlarmMyModelRecyclerViewAdapter.Holder>() {

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

        when(tempInt){
            1 -> {holder.state.visibility = View.GONE
                holder.ok.visibility = View.VISIBLE
                holder.rej.visibility = View.VISIBLE}
            2 -> holder.state.text="승인 완료"
            3 -> holder.state.text="거절 완료"
            else -> holder.state.text="오류 발생"
        }

        //셋 온클릭같이 해야하는 일도 여기서!
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.tv_rv_item_alarm_my_model_date) as TextView
        val username: TextView = itemView.findViewById(R.id.tv_rv_item_alarm_my_model_username) as TextView
        val sentence: TextView = itemView.findViewById(R.id.tv_rv_item_alarm_my_model_sentence) as TextView
        val state: TextView = itemView.findViewById(R.id.tv_rv_item_alarm_my_model_state) as TextView
        val ok : Button = itemView.findViewById(R.id.bt_rv_item_alarm_my_model_ok) as Button
        val rej : Button = itemView.findViewById(R.id.bt_rv_item_alarm_my_model_rej) as Button
    }
}
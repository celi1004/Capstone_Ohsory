package com.example.ohsoryapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ohsoryapp.R
import com.example.ohsoryapp.data.AudioFileData

class AudioFileRecyclerViewAdapter(val ctx: Context, val dataList: ArrayList<AudioFileData>, val token : String) : RecyclerView.Adapter<AudioFileRecyclerViewAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View = LayoutInflater.from(ctx).inflate(R.layout.rv_item_audio_file, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.date.text = dataList[position].date
        holder.title.text = dataList[position].title
        holder.explanation.text = dataList[position].explanation

        //TODO 롱 클릭시 여러가지 게종하는 거 만드삼 
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.tv_rv_item_audio_file_date) as TextView
        val title: TextView = itemView.findViewById(R.id.tv_rv_item_audio_file_title) as TextView
        val explanation: TextView = itemView.findViewById(R.id.tv_rv_item_audio_file_exp) as TextView
    }
}
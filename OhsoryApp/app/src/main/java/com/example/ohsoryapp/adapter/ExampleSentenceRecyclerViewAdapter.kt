package com.example.ohsoryapp.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.ohsoryapp.R
import com.example.ohsoryapp.data.FileListData
import com.example.ohsoryapp.data.RecordingData
import com.example.ohsoryapp.myclass.DBHelper
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ExampleSentenceRecyclerViewAdapter(val ctx: Context, val dataList: ArrayList<String>) : RecyclerView.Adapter<ExampleSentenceRecyclerViewAdapter.Holder>() {

    lateinit var mContext : Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        mContext = ctx
        //mDatabase.setOnDatabaseChangedListener(this) ??
        val view: View = LayoutInflater.from(ctx).inflate(R.layout.rv_item_example_sentence, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = Integer.MAX_VALUE

    override fun onBindViewHolder(holder: Holder, position: Int) {
        var positionInList = position % dataList.size;
        holder.text.text = dataList[positionInList]
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.tv_example_sentence) as TextView
        val body : RelativeLayout = itemView.findViewById(R.id.bt_rv_example_sentence) as RelativeLayout
    }
}
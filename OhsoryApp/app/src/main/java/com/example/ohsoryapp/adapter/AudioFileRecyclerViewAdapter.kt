package com.example.ohsoryapp.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.ohsoryapp.R
import com.example.ohsoryapp.data.AudioFileData
import com.example.ohsoryapp.data.RecordingData
import com.example.ohsoryapp.myclass.DBHelper
import java.io.File

class AudioFileRecyclerViewAdapter(val ctx: Context, val dataList: ArrayList<AudioFileData>, val token : String) : RecyclerView.Adapter<AudioFileRecyclerViewAdapter.Holder>() {

    lateinit var mContext : Context
    lateinit var mDatabase: DBHelper

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        mContext = ctx
        mDatabase = DBHelper(mContext)
        //mDatabase.setOnDatabaseChangedListener(this) ??
        val view: View = LayoutInflater.from(ctx).inflate(R.layout.rv_item_audio_file, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.date.text = dataList[position].date
        holder.title.text = dataList[position].title
        holder.explanation.text = dataList[position].explanation

        holder.body.setOnLongClickListener {
            val entrys = java.util.ArrayList<String>()
            entrys.add("공유하기")
            entrys.add("파일 이름 변경")
            entrys.add("파일 삭제")

            val items = entrys.toTypedArray<CharSequence>()

            // File delete confirm
            val builder = AlertDialog.Builder(mContext)
            builder.setTitle("Option")
            builder.setItems(items) { dialog, item ->
                if (item == 0) {
                    shareFileDialog(position)
                }
                if (item == 1) {
                    //renameFileDialog(position)
                } else if (item == 2) {
                    //deleteFileDialog(position)
                }
            }
            builder.setCancelable(true)
            builder.setNegativeButton(
                "Cancle"
            ) { dialog, id -> dialog.cancel() };

            val alert = builder.create()
            alert.show()

            false
        };
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.tv_rv_item_audio_file_date) as TextView
        val title: TextView = itemView.findViewById(R.id.tv_rv_item_audio_file_title) as TextView
        val explanation: TextView = itemView.findViewById(R.id.tv_rv_item_audio_file_exp) as TextView
        val body : RelativeLayout = itemView.findViewById(R.id.bt_rv_audio_file) as RelativeLayout
    }

    fun getItem(position: Int): RecordingData {
        return mDatabase.getItemAt(position)
    }

    fun shareFileDialog(position: Int) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(getItem(position).getFilePath())))
        shareIntent.type = "audio/mp4"
        mContext.startActivity(Intent.createChooser(shareIntent, "공유하기"))
    }
}
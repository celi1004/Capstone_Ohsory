package com.example.ohsoryapp.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaPlayer
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
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList



class AudioFileRecyclerViewAdapter(val ctx: Context, val dataList: ArrayList<FileListData>, val foldername : String) : RecyclerView.Adapter<AudioFileRecyclerViewAdapter.Holder>() {

    lateinit var mContext : Context
    lateinit var mDatabase: DBHelper
    lateinit var mediaPlayer : MediaPlayer

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        mContext = ctx
        mDatabase = DBHelper(mContext)
        //mDatabase.setOnDatabaseChangedListener(this) ??
        val view: View = LayoutInflater.from(ctx).inflate(R.layout.rv_item_audio_file, parent, false)

        mediaPlayer = MediaPlayer()

        return Holder(view)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.date.text = dataList[position].date
        holder.title.text = dataList[position].title

        holder.body.setOnClickListener{
            
            mediaPlayer.reset()

            val fis = FileInputStream(File(dataList[position].path))

            mediaPlayer.setDataSource(fis.getFD())

            mediaPlayer.prepare()

            mediaPlayer.start()
        }

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
                    shareFileDialog(dataList[position].path)
                }
                if (item == 1) {
                    renameFileDialog(dataList[position].path, position)
                } else if (item == 2) {
                    deleteFileDialog(dataList[position].path, position)
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
        val body : RelativeLayout = itemView.findViewById(R.id.bt_rv_audio_file) as RelativeLayout
    }

    fun shareFileDialog(fpath: String) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(fpath))
        shareIntent.type = "audio/wav"
        mContext.startActivity(Intent.createChooser(shareIntent, "공유하기"))
    }

    private fun renameFileDialog(fpath: String, position: Int){
        // File rename dialog
        val renameFileBuilder = AlertDialog.Builder(mContext)

        val inflater = LayoutInflater.from(mContext)
        val view = inflater.inflate(R.layout.dialog_rename_file, null)

        val input = view.findViewById<View>(R.id.new_name) as EditText

        renameFileBuilder.setTitle("파일 이름 변경")
        renameFileBuilder.setCancelable(true)
        renameFileBuilder.setPositiveButton("확인",
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, id: Int) {
                        try {
                            val value = input.getText().toString().trim({ it <= ' ' }) + ".wav"
                            rename(fpath, value, position)

                        } catch (e: Exception) {
                        }

                        dialog.cancel()
                    }
                })
        renameFileBuilder.setNegativeButton("취소", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, id: Int) {
                        dialog.cancel()
                    }
                })

        renameFileBuilder.setView(view)
        val alert = renameFileBuilder.create()
        alert.show()
    }

    fun rename(fpath: String, name: String, position: Int) {

        val filePre = File(fpath)
        val fileNow = File(Environment.getExternalStorageDirectory().absolutePath + "/OhSory/"+foldername, name)

        if (filePre.renameTo(fileNow)) {
            //notifyItemChanged(position)
        } else {
            Toast.makeText(mContext, "변경 실패", Toast.LENGTH_SHORT).show()
        }
    }

     fun deleteFileDialog(fpath : String, position:Int) {
        // File delete confirm
        val confirmDelete = AlertDialog.Builder(mContext)
        confirmDelete.setTitle("파일 삭제")
        confirmDelete.setMessage("정말 삭제하시겠습니까?")
        confirmDelete.setCancelable(true)
        confirmDelete.setPositiveButton("네") { dialog, id ->
        try {
            //remove item from database, recyclerview, and storage
            remove(fpath, position)

        } catch (e:Exception) {
        }

        dialog.cancel()
        }
         confirmDelete.setNegativeButton("아니요", object:DialogInterface.OnClickListener {
            override fun onClick(dialog:DialogInterface, id:Int) { dialog.cancel() } })

        val alert = confirmDelete.create()
        alert.show()
     }

    fun remove(fpath : String,position: Int) {
        //remove item from database, recyclerview and storage

        //delete file from storage
        val file = File(fpath)
        file.delete()

        Toast.makeText(
                mContext,
                "삭제 완료",
                Toast.LENGTH_SHORT
        ).show()

        Log.i("에", position.toString())
        //notifyItemRemoved(position)
    }
}
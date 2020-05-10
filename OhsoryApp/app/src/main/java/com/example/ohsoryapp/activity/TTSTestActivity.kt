package com.example.ohsoryapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import com.example.ohsoryapp.R
import com.example.ohsoryapp.myclass.DBHelper
import kotlinx.android.synthetic.main.activity_ttstest.*
import java.io.File

class TTSTestActivity : AppCompatActivity() {

    lateinit var mDatabase : DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ttstest)

        setButtonClickListener()
    }

    fun setButtonClickListener(){

        bt_hear.setOnClickListener(){
            //서버에 듣고 싶은 문장 보내고 받아오고 들려주기
        }

        bt_dowload.setOnClickListener(){
            //서버에서 받아온 파일 내장 디비에

            //우선 임시로 아무 템프파일 생성해서 뿌리는 거 까지 해보자
            mDatabase = DBHelper(this)

            val mFileName = et_text.text.toString()
            var mFilePath = Environment.getExternalStorageDirectory().absolutePath + "/OhSory/"
            val newfile = File(mFilePath+mFileName+".wav")
            mFilePath += "/Ohsory/" + mFileName
            newfile.createNewFile()

            mDatabase.addRecording(mFileName, mFilePath)
        }

        bt_share.setOnClickListener(){
            //서버에서 받아온 파일 공유
        }
    }
}

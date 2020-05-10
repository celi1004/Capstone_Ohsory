package com.example.ohsoryapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import com.example.ohsoryapp.R
import com.example.ohsoryapp.myclass.DBHelper
import kotlinx.android.synthetic.main.activity_ttstest.*
import java.io.File
import android.widget.Toast
import java.io.FileOutputStream
import java.io.IOException




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

            val dirPath = Environment.getExternalStorageDirectory().toString() + "/OhSory"
            val subPath = "/내 모델/"
            val filename = et_text.text.toString() +".wav"

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

                Toast.makeText(this, "다운로드 완", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        bt_share.setOnClickListener(){
            //서버에서 받아온 파일 공유
        }
    }
}

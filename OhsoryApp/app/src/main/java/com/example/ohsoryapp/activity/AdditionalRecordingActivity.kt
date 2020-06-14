package com.example.ohsoryapp.activity

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.example.ohsoryapp.R
import com.example.ohsoryapp.db.SharedPreferenceController
import com.example.ohsoryapp.myclass.WavRecorder
import com.example.ohsoryapp.network.ApplicationController
import com.example.ohsoryapp.network.NetworkService
import com.example.ohsoryapp.post.PostFileUploadResponse
import kotlinx.android.synthetic.main.activity_additional_recording.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.gms.common.util.IOUtils.toByteArray
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.example.ohsoryapp.adapter.ExampleSentenceRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_ttstest.*
import java.io.*


class AdditionalRecordingActivity : AppCompatActivity() {

    val dirPath = Environment.getExternalStorageDirectory().toString() + "/OhSory"
    val filename = "ohsory.txt"

    lateinit var exampleSentenceRecyclerViewAdapter : ExampleSentenceRecyclerViewAdapter
    lateinit var mLayoutManager : LinearLayoutManager

    val dataList: ArrayList<String> by lazy {
        ArrayList<String>()
    }

    private var isPlaying = false
    private var isRecording = false

    private var first = true

    private var mIntent: Intent? = null

    private var player: MediaPlayer? = null

    var user_id = 0

    var mFileName = ""
    var mFilePath = ""
    var do_flag = false

    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }

    internal var recorder = WavRecorder("/sdcard/AudioRecorder/", this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.ohsoryapp.R.layout.activity_additional_recording)

        //TODO
        //추가 녹음 와이파이 연결 상태에서만 하기

        user_id = SharedPreferenceController.getUserID(this)

        setButtonClickListener()

        setRecyclerView()
    }

    private fun setButtonClickListener(){
        icon_right.setOnClickListener{
            var lastVisibleItemIndex = mLayoutManager.findLastVisibleItemPosition();
            mLayoutManager.smoothScrollToPosition(rv_example_sentence,null,lastVisibleItemIndex+1)
        }

        icon_left.setOnClickListener {
            var firstVisibleItemIndex = mLayoutManager.findLastVisibleItemPosition();
            mLayoutManager.smoothScrollToPosition(rv_example_sentence, null, firstVisibleItemIndex - 1)
        }

        bt_record.setOnClickListener{
            onRecord()
        }

        bt_pause.setOnClickListener{
            onSave()
        }

        bt_play.setOnClickListener{
            onPlay()
        }

        bt_end.setOnClickListener(){
            onEnd()
        }
    }

    override fun onDestroy() {
        if (isPlaying) {
            if (player != null) {
                try {
                    player!!.stop()
                    player!!.reset()
                } catch (e: Exception) {
                }

            }
            isPlaying = false
        }

        if(isRecording){
            //저장 안하구 그냥 녹음 중이였을 때
            val file = File(mFilePath)
            file.delete()
        }

        if (do_flag){
            //녹음을 한 기록이 있으면 파일을 업데이트 해서 저장하기
            writeTxtfileAddDataList()
        }
        super.onDestroy()
    }

    private fun onRecord(){
        isRecording = true
        if (isPlaying) {
            if (player != null) {
                try {
                    player!!.stop()
                    player!!.reset()
                } catch (e: Exception) {
                }

            }
            isPlaying = false
        }
        if (first) {
            //녹음 처음 시작
            bt_play.visibility = View.INVISIBLE
            bt_end.visibility = View.INVISIBLE
            bt_record.visibility = View.GONE

            //mPauseButton.setVisibility(View.VISIBLE);
            val folder = File(Environment.getExternalStorageDirectory().toString() + "/SoundRecorder")
            if (!folder.exists()) {
                //folder /SoundRecorder doesn't exist, create the folder
                folder.mkdir()
            }

            var nowpos = mLayoutManager.findLastVisibleItemPosition() % dataList.size

            mFileName = dataList[nowpos] + ".wav"
            mFilePath = "/sdcard/AudioRecorder/" + mFileName

            recorder.setFilename(mFileName)
            //start RecordingService
            recorder.startRecording()
            //keep screen on while recording
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

            first = false

        } else {
            //녹음 파일 저장된거 지우고 녹음 재시작

            val file = File(mFilePath)
            file.delete()

            bt_play.visibility = View.INVISIBLE
            bt_end.visibility = View.INVISIBLE
            bt_record.visibility = View.GONE

            //mPauseButton.setVisibility(View.VISIBLE);
            val folder = File(Environment.getExternalStorageDirectory().toString() + "/SoundRecorder")
            if (!folder.exists()) {
                //folder /SoundRecorder doesn't exist, create the folder
                folder.mkdir()
            }

            //start RecordingService
            recorder.startRecording()
            //keep screen on while recording
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun onSave(){
        bt_record.visibility = View.VISIBLE
        bt_play.visibility = View.VISIBLE
        bt_end.visibility = View.VISIBLE

        recorder.stopRecording()
        //allow the screen to turn off again once recording is finished
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        do_flag = true
    }

    private fun onPlay() {
        if (isPlaying) {
            if (player != null) {
                try {
                    player!!.stop()
                    player!!.reset()
                } catch (e: Exception) {
                }

            }

        } else {
            if (player != null) {
                try {
                    player!!.stop()
                    player!!.reset()
                } catch (e: Exception) {
                }

            }
            try {
                player = MediaPlayer()
                player!!.setOnCompletionListener(object : MediaPlayer.OnCompletionListener {
                    override fun onCompletion(mp: MediaPlayer) {
                        isPlaying = false
                    }
                })
                player!!.setDataSource(mFilePath)
                player!!.prepare()
                player!!.start()

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        isPlaying = !isPlaying
    }

    private fun onEnd() {

        first = true
        isPlaying = false

        //stop recording
        bt_play.visibility = View.INVISIBLE
        bt_end.visibility = View.INVISIBLE
        bt_record.visibility = View.VISIBLE
        //예시 문장 변경

        uploadFile(mFilePath)
        isRecording = false

        val nowpos = mLayoutManager.findLastVisibleItemPosition() % dataList.size
        dataList.removeAt(nowpos)
        exampleSentenceRecyclerViewAdapter.notifyDataSetChanged()
        //예시문장 업데이트
    }

    private fun uploadFile(fpath : String){

        val file = File(fpath)

        val tfile = RequestBody.create(MediaType.parse("multipart/form-data"), file)

        val nowpos = mLayoutManager.findLastVisibleItemPosition() % dataList.size
        val text = dataList[nowpos]

        val postFileUpload = networkService.postAddFileUpload(user_id, text, tfile)

        postFileUpload!!.enqueue(object : Callback<Void> {
            //통신을 못 했을 때
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("file upload fail", t.toString())
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                //통신을 성공적으로 했을 때
                if (response.isSuccessful) {
                    //서버로 보내는 거 성공하면 삭제
                    Toast.makeText(this@AdditionalRecordingActivity, "녹음 파일 전송 완료", Toast.LENGTH_LONG).show()
                    val f : File = File(fpath)
                    f.delete()
                }
                else{
                    if(response.code() == 400){
                        Log.i("빈 파일", "빈 파일")
                        val f : File = File(fpath)
                        f.delete()
                    }else{
                        Toast.makeText(this@AdditionalRecordingActivity, response.code().toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun setRecyclerView() {

        readTxtfileAddDataList()

        exampleSentenceRecyclerViewAdapter = ExampleSentenceRecyclerViewAdapter(this, dataList)
        rv_example_sentence.adapter = exampleSentenceRecyclerViewAdapter

        var snapHelper : SnapHelper = PagerSnapHelper();
        snapHelper.attachToRecyclerView(rv_example_sentence);

        rv_example_sentence.scrollToPosition(dataList.size/2)
        mLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_example_sentence.layoutManager = mLayoutManager
        rv_example_sentence.layoutManager!!.scrollToPosition(((Int.MAX_VALUE/2)-((Int.MAX_VALUE/2)%dataList.size)))
    }

    fun readTxtfileAddDataList(){
        //디렉토리 없으면 생성
        val dir = File(dirPath)
        if (!dir.exists()) {
            dir.mkdir()
        }

        val txtFile = File(dir, filename)

        try {
            var line : String?
            val buf = BufferedReader(FileReader(txtFile))
            line = buf.readLine()
            while (line != null) {
                if (line != ""){
                    dataList.add(line)
                }
                line = buf.readLine()
            }

            buf.close();
        }catch (e:Exception){
            Log.d("호", e.toString())
        }

        if(dataList.size == 0){
            //내부저장소에 파일이 아직 안 생성 or 다 읽어벌이 문장을
            //raw에서 꺼내서 datalist에 넣어놔
            try {
                val inputStream: InputStream = this.getResources().openRawResource(R.raw.ohsory)
                val inputStreamReader = InputStreamReader(inputStream)
                var line: String?
                val br = BufferedReader(inputStreamReader)
                line = br.readLine()
                while (line != null) {
                    if (line != ""){
                        dataList.add(line)
                    }
                    line = br.readLine()
                }
                br.close()
            } catch (e:Exception){
                Log.d("호", e.toString())
            }
        }

    }

    fun writeTxtfileAddDataList(){
        val dir = File(dirPath)
        if (!dir.exists()) {
            dir.mkdir()
        }

        val txtFile = File(dir, filename)

        try {
            val buf = BufferedWriter(FileWriter(txtFile))
            for (str in dataList){
                buf.append(str)
                buf.newLine()
            }
            buf.close()
        }catch (e:Exception){
            Log.d("호", e.toString())
        }
    }
}

package com.example.ohsoryapp.fragment


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.example.ohsoryapp.R
import com.example.ohsoryapp.activity.AdditionalRecordingActivity
import com.example.ohsoryapp.activity.TTSTestActivity
import com.example.ohsoryapp.db.SharedPreferenceController
import com.example.ohsoryapp.get.GetProgressResponse
import com.example.ohsoryapp.myclass.NetworkManager
import com.example.ohsoryapp.network.ApplicationController
import com.example.ohsoryapp.network.NetworkService
import kotlinx.android.synthetic.main.fragment_progress.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.view.MotionEvent
import android.content.DialogInterface
import com.example.ohsoryapp.activity.MainActivity
import android.widget.EditText
import android.app.AlertDialog
import android.widget.RadioButton
import android.widget.ToggleButton
import com.example.ohsoryapp.data.ShareCreateData
import com.example.ohsoryapp.data.UserIdData
import com.example.ohsoryapp.post.PostShareCreateResponse
import kotlinx.android.synthetic.main.dialog_sharemymodel.*


/**
 * A simple [Fragment] subclass.
 */
class ProgressFragment : Fragment() {

    lateinit var shareCreateData: ShareCreateData

    internal var mNetworkManager : NetworkManager? = null
    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }

    var mprogress : Int = 0
    var user_id = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_progress, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setProgressSeekBar()
        setButtonClickListener()

        sb_progress.setOnTouchListener(View.OnTouchListener { v, event -> true })
    }

    fun setProgressSeekBar(){
        mNetworkManager = NetworkManager(activity!!)
        if(mNetworkManager!!.checkNetworkState()){
            //데이터가 연결되어있으면 서버에서 progress 가져와
            getProgressResponse()
        }else{
            //제일 최근에 저장되있는 progress정보 띄워줘
            val progress_degree = SharedPreferenceController.getUserPG(activity!!)
            sb_progress.progress = progress_degree

            tv_progress.setText(progress_degree.toString()+"%")
            setAudioTimeTextView(SharedPreferenceController.getUserAT(activity!!))
        }


    }

    private fun getProgressResponse() {
        //TODO 서버에서 진행률 가져와서 seekbar 그 값으로 바꾸기
        user_id = SharedPreferenceController.getUserID(activity!!)
        val getProgressResponse = networkService.getProgressResponse(user_id)

        getProgressResponse!!.enqueue(object : Callback<GetProgressResponse> {
            //통신을 못 했을 때
            override fun onFailure(call: Call<GetProgressResponse>, t: Throwable) {
                Log.e("progress load fail", t.toString())
            }

            override fun onResponse(call: Call<GetProgressResponse>, response: Response<GetProgressResponse>) {
                //통신을 성공적으로 했을 때
                if (response.isSuccessful) {
                    mprogress = response.body()!!.progress
                    val maudios = response.body()!!.audio_time

                    sb_progress.progress = mprogress

                    SharedPreferenceController.setUserPG(activity!!, mprogress)
                    SharedPreferenceController.setUserAT(activity!!, maudios)

                    tv_progress.setText(mprogress.toString()+"%")
                    setAudioTimeTextView(maudios)

                    if (mprogress == 100){
                        //모델 생성할 수 있으면
                        if(SharedPreferenceController.getUserME(activity!!)==0){
                            //모델이 생성된적 없으면
                            SharedPreferenceController.setUserME(activity!!, 1)
                            //생성하도록 요청했다는 말하고
                            trainModel()
                            //요청 보내자
                        }
                    }
                }
                else{
                    Toast.makeText(activity,
                            "서버 에러", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    fun setAudioTimeTextView(sec : Float){
        var hour: Int = 0
        var min: Int = 0
        var isec : Int = 0

        min = (sec/60).toInt();
        hour = min / 60;
        isec = (sec % 60).toInt();
        min = min % 60;

        var textString = ""
        if(hour !=0){
            textString += hour.toString() +"시간 "
        }
        if(min != 0){
            textString += min.toString() +"분 "
        }
        tv_audio_time.setText(isec.toString()+"초 데이터 획득")
    }

    fun setButtonClickListener(){

        bt_ttstest.setOnClickListener(){
            activity?.let{
                val iT = Intent(context, TTSTestActivity::class.java)
                startActivity(iT)
            }
        }

        bt_add_rec.setOnClickListener(){
            activity?.let{
                val iT = Intent(context, AdditionalRecordingActivity::class.java)
                startActivity(iT)
            }
        }

        bt_share.setOnClickListener(){
            showShareDialog()
        }
    }

    fun showShareDialog(){
        val builder = AlertDialog.Builder(activity!!)
        val dialogView = layoutInflater.inflate(R.layout.dialog_sharemymodel, null)
        val dialogText = dialogView.findViewById<EditText>(R.id.et_username)
        val listen_noti = dialogView.findViewById<ToggleButton>(R.id.rb_listennoti)
        val down_noti = dialogView.findViewById<ToggleButton>(R.id.rb_downnoti)
        val down_auth = dialogView.findViewById<ToggleButton>(R.id.rb_downauth)

        builder.setView(dialogView)
                .setPositiveButton("확인") { dialogInterface, i ->
                    //공유 해주는 서버 작업해줘
                    val b1 = listen_noti.isChecked
                    val b2 = down_noti.isChecked
                    val b3 = down_auth.isChecked
                    val username = SharedPreferenceController.getUserName(activity!!)
                    shareCreateData = ShareCreateData(username, dialogText.text.toString(), b1, b2, b3)

                    Log.i("에", shareCreateData.toString())
                    shareMyModel()
                }
                .setNegativeButton("취소") { dialogInterface, i ->

                }
                .show()

    }

    fun shareMyModel(){
        val postShareCreateResponse = networkService.postShareCreateResponse(shareCreateData)

        postShareCreateResponse!!.enqueue(object : Callback<PostShareCreateResponse> {
            //통신을 못 했을 때

            override fun onFailure(call: Call<PostShareCreateResponse>, t: Throwable) {
                Log.i("서버에러", "실패")
            }

            override fun onResponse(call: Call<PostShareCreateResponse>, response: Response<PostShareCreateResponse>) {
                when (response.code()) {
                    201 -> {
                        Toast.makeText(activity!!,
                                "공유 완료", Toast.LENGTH_LONG).show()
                    }
                    404 ->{
                        Toast.makeText(activity!!,
                                "공유 받을 사람의 이름을 정확히 입력해주세요", Toast.LENGTH_LONG).show()
                    }
                    406 ->{
                        Toast.makeText(activity!!,
                                "본인 username으로 요청을 하였습니다 공유 받을 사람의 이름으로 입력해주세요", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        Toast.makeText(activity!!,
                                "저장 실패", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun trainModel(){

        val postTrainModel = networkService.postTrainModel(UserIdData(user_id))

        postTrainModel!!.enqueue(object : Callback<Void> {
            //통신을 못 했을 때
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("file upload fail", t.toString())
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                //통신을 성공적으로 했을 때
                if (response.isSuccessful) {
                    //서버로 보내는 거 성공하면 삭제
                    Toast.makeText(activity!!, "모델 생성 요청", Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(activity!!, "모델 생성 요청 실패" + response.code().toString(), Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}

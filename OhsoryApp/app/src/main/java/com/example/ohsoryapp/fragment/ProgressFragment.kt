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





/**
 * A simple [Fragment] subclass.
 */
class ProgressFragment : Fragment() {

    internal var mNetworkManager : NetworkManager? = null
    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }

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
        val muser_pk = SharedPreferenceController.getUserID(activity!!)
        val getProgressResponse = networkService.getProgressResponse(muser_pk)

        getProgressResponse!!.enqueue(object : Callback<GetProgressResponse> {
            //통신을 못 했을 때
            override fun onFailure(call: Call<GetProgressResponse>, t: Throwable) {
                Log.e("progress load fail", t.toString())
            }

            override fun onResponse(call: Call<GetProgressResponse>, response: Response<GetProgressResponse>) {
                //통신을 성공적으로 했을 때
                if (response.isSuccessful) {
                    val mprogress = response.body()!!.progress
                    val maudios = response.body()!!.audio_time

                    sb_progress.progress = mprogress

                    SharedPreferenceController.setUserPG(activity!!, mprogress)
                    SharedPreferenceController.setUserAT(activity!!, maudios)

                    tv_progress.setText(mprogress.toString()+"%")
                    setAudioTimeTextView(maudios)
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
    }
}

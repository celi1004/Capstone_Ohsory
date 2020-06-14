package com.example.ohsoryapp.fragment


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.ohsoryapp.R
import com.example.ohsoryapp.adapter.AlarmMyModelRecyclerViewAdapter
import com.example.ohsoryapp.data.AlarmModelData
import com.example.ohsoryapp.data.SharerData
import com.example.ohsoryapp.db.SharedPreferenceController
import com.example.ohsoryapp.get.GetMyShareAlarmListResponse
import com.example.ohsoryapp.network.ApplicationController
import com.example.ohsoryapp.network.NetworkService
import com.example.ohsoryapp.post.PostNotificationResponse
import kotlinx.android.synthetic.main.fragment_alarm_my.*
import kotlinx.android.synthetic.main.fragment_alarm_share.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat


/**
 * A simple [Fragment] subclass.
 */
class AlarmMyFragment : Fragment() {

    lateinit var alarmMyModelRecyclerViewAdapter : AlarmMyModelRecyclerViewAdapter

    val dataList: ArrayList<AlarmModelData> by lazy {
        ArrayList<AlarmModelData>()
    }

    var shareInfoList = ArrayList<PostNotificationResponse>()

    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }

    lateinit var sharerData: SharerData

    var sharer_name = ""
    var token : String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alarm_my, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sharer_name = SharedPreferenceController.getUserName(activity!!)

        setSwipeLayout()

        setRecyclerView()
    }

    private fun setSwipeLayout(){
        srl_alarm_my_list.setOnRefreshListener {
            setRecyclerView()
            srl_alarm_my_list.isRefreshing = false
        }
    }

    private fun setRecyclerView() {

        dataList.clear()

        sharerData = SharerData(sharer_name)

        val getSharedModelListResponse = networkService.getMyShareModelRecordResponse(sharerData)

        getSharedModelListResponse.enqueue(object : Callback<ArrayList<GetMyShareAlarmListResponse>> {
            //통신을 못 했을 때
            override fun onFailure(call: Call<ArrayList<GetMyShareAlarmListResponse>>, t: Throwable) {
                Log.e("load fail", t.toString())
            }

            override fun onResponse(call: Call<ArrayList<GetMyShareAlarmListResponse>>, response: Response<ArrayList<GetMyShareAlarmListResponse>>) {
                //통신을 성공적으로 했을 때
                if (response.isSuccessful) {
                    val listlen = response.body()!!.size
                    for (element in 0 .. listlen - 1) {
                        var temp_sharee_name = response.body()!![listlen - element - 1].sharee_name
                        shareInfoList = response.body()!![listlen - element - 1].share_infos

                        val infolistlen = shareInfoList.size

                        for (element2 in 0 .. infolistlen - 1) {
                            val sdf = SimpleDateFormat("yyyy-MM-dd\nhh:mm")

                            var temp_timestamp = sdf.format(shareInfoList[infolistlen - element2 - 1].timestamp)
                            var temp_state : Int
                            if(shareInfoList[infolistlen - element2 - 1].req_type==0){
                                //듣기일 때
                                temp_state = 3
                            }else{
                                //다운로드 일 때
                                temp_state = shareInfoList[infolistlen - element2 - 1].if_approve
                            }

                            var temp_req_text = shareInfoList[infolistlen - element2 - 1].req_text
                            var temp_id = shareInfoList[infolistlen - element2 - 1].id

                            dataList.add(AlarmModelData(temp_timestamp.toString(), temp_sharee_name, temp_req_text, temp_state, temp_id))
                        }
                    }

                    alarmMyModelRecyclerViewAdapter = AlarmMyModelRecyclerViewAdapter(activity!!, dataList, token)

                    rv_alarm_my.adapter = alarmMyModelRecyclerViewAdapter
                    rv_alarm_my.layoutManager = LinearLayoutManager(activity)
                }
                else{
                    Log.e("서버 에러", "서버 에러")
                }
            }
        })


    }
}

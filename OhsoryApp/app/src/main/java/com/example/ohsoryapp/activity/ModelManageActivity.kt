package com.example.ohsoryapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ohsoryapp.R
import com.example.ohsoryapp.adapter.SharedModelManageRecyclerViewAdapter
import com.example.ohsoryapp.data.SharedModelManageData
import com.example.ohsoryapp.data.SharerData
import com.example.ohsoryapp.db.SharedPreferenceController
import com.example.ohsoryapp.get.GetMyShareModelListResponse
import com.example.ohsoryapp.network.ApplicationController
import com.example.ohsoryapp.network.NetworkService
import kotlinx.android.synthetic.main.activity_model_manage.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

class ModelManageActivity : AppCompatActivity() {

    lateinit var sharedModelManageRecyclerViewAdapter : SharedModelManageRecyclerViewAdapter

    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }

    val dataList: ArrayList<SharedModelManageData> by lazy {
        ArrayList<SharedModelManageData>()
    }

    lateinit var sharerData: SharerData

    var sharer_name = ""
    var token : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_model_manage)

        sharer_name = SharedPreferenceController.getUserName(this)

        setRecyclerView()
    }

    private fun setRecyclerView() {

        dataList.clear()

        sharerData = SharerData(sharer_name)

        val getSharedModelListResponse = networkService.getMyShareModelListResponse(sharerData)

        getSharedModelListResponse.enqueue(object : Callback<ArrayList<GetMyShareModelListResponse>> {
            //통신을 못 했을 때
            override fun onFailure(call: Call<ArrayList<GetMyShareModelListResponse>>, t: Throwable) {
                Log.e("load fail", t.toString())
            }

            override fun onResponse(call: Call<ArrayList<GetMyShareModelListResponse>>, response: Response<ArrayList<GetMyShareModelListResponse>>) {
                //통신을 성공적으로 했을 때
                if (response.isSuccessful) {
                    val listlen = response.body()!!.size
                    Log.i("에", listlen.toString())
                    for (element in 0 .. listlen - 1) {
                        var temp_user_name = response.body()!![listlen - element - 1].sharee_name
                        var temp_share_id = response.body()!![listlen - element - 1].id
                        var temp_ln = response.body()!![listlen - element - 1].listening_noti
                        var temp_da = response.body()!![listlen - element - 1].download_auth
                        var temp_dn = response.body()!![listlen - element - 1].download_noti

                        dataList.add(SharedModelManageData(temp_user_name,temp_share_id, temp_ln,temp_dn, temp_da))
                    }

                    sharedModelManageRecyclerViewAdapter = SharedModelManageRecyclerViewAdapter(this@ModelManageActivity, dataList, token)

                    rv_model_manage_list.adapter = sharedModelManageRecyclerViewAdapter
                    rv_model_manage_list.layoutManager = LinearLayoutManager(this@ModelManageActivity)
                }
                else{
                    Log.e("서버 에러", "서버 에러")
                }
            }
        })



    }
}

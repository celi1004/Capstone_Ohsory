package com.example.ohsoryapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ohsoryapp.R
import com.example.ohsoryapp.adapter.SharedModelManageRecyclerViewAdapter
import com.example.ohsoryapp.data.SharedModelManageData
import com.example.ohsoryapp.network.ApplicationController
import com.example.ohsoryapp.network.NetworkService
import kotlinx.android.synthetic.main.activity_model_manage.*
import java.time.LocalDate

class ModelManageActivity : AppCompatActivity() {

    lateinit var sharedModelManageRecyclerViewAdapter : SharedModelManageRecyclerViewAdapter

    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }

    val dataList: ArrayList<SharedModelManageData> by lazy {
        ArrayList<SharedModelManageData>()
    }

    var token : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_model_manage)

        setRecyclerView()
    }

    private fun setRecyclerView() {

        //getMyShareModelListResponse


       sharedModelManageRecyclerViewAdapter = SharedModelManageRecyclerViewAdapter(this, dataList, token)

        rv_model_manage_list.adapter = sharedModelManageRecyclerViewAdapter
        rv_model_manage_list.layoutManager = LinearLayoutManager(this)
    }
}

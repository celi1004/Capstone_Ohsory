package com.example.ohsoryapp.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.ohsoryapp.R
import com.example.ohsoryapp.adapter.AlarmMyModelRecyclerViewAdapter
import com.example.ohsoryapp.data.AlarmModelData
import kotlinx.android.synthetic.main.fragment_alarm_my.*

/**
 * A simple [Fragment] subclass.
 */
class AlarmMyFragment : Fragment() {

    lateinit var alarmMyModelRecyclerViewAdapter : AlarmMyModelRecyclerViewAdapter

    val dataList: ArrayList<AlarmModelData> by lazy {
        ArrayList<AlarmModelData>()
    }

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

        setRecyclerView()
    }

    private fun setRecyclerView() {

        dataList.add(AlarmModelData("2020년\n4월1일", "이민희", "배고파아",1))
        dataList.add(AlarmModelData("2020년\n3월28일","박진영", "먀아", 2))

        alarmMyModelRecyclerViewAdapter = AlarmMyModelRecyclerViewAdapter(activity!!, dataList, token)

        rv_alarm_my.adapter = alarmMyModelRecyclerViewAdapter
        rv_alarm_my.layoutManager = LinearLayoutManager(activity)
    }
}

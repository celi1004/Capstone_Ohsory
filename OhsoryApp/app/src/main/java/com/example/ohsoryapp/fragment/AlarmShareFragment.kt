package com.example.ohsoryapp.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.ohsoryapp.R
import com.example.ohsoryapp.adapter.AlarmSharedModelRecyclerViewAdapter
import com.example.ohsoryapp.data.AlarmModelData
import kotlinx.android.synthetic.main.fragment_alarm_share.*

/**
 * A simple [Fragment] subclass.
 */
class AlarmShareFragment : Fragment() {

    lateinit var alarmSharedModelRecyclerViewAdapter : AlarmSharedModelRecyclerViewAdapter

    val dataList: ArrayList<AlarmModelData> by lazy {
        ArrayList<AlarmModelData>()
    }

    var token : String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alarm_share, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setRecyclerView()
    }

    private fun setRecyclerView() {

        dataList.add(AlarmModelData("2020년\n4월12일", "이준협", "네?",2))
        dataList.add(AlarmModelData("2020년\n3월21일","양수영", "술마시자", 1))

        alarmSharedModelRecyclerViewAdapter = AlarmSharedModelRecyclerViewAdapter(activity!!, dataList, token)

        rv_alarm_shared.adapter = alarmSharedModelRecyclerViewAdapter
        rv_alarm_shared.layoutManager = LinearLayoutManager(activity)
    }
}

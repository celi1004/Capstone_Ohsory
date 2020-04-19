package com.example.ohsoryapp.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.ohsoryapp.R
import com.example.ohsoryapp.adapter.AudioFileRecyclerViewAdapter
import com.example.ohsoryapp.data.AudioFileData
import kotlinx.android.synthetic.main.fragment_filelist.*

/**
 * A simple [Fragment] subclass.
 */
class FilelistFragment : Fragment() {

    lateinit var audioFileRecyclerViewAdapter : AudioFileRecyclerViewAdapter

    val dataList: ArrayList<AudioFileData> by lazy {
        ArrayList<AudioFileData>()
    }

    var token : String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filelist, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setRecyclerView()
    }

    private fun setRecyclerView() {

        dataList.add(AudioFileData("2020.4.12 11:24AM", "알람음", "이러나라이러나라"))
        dataList.add(AudioFileData("2020.3.21 2:59PM","웃음소리", "헤ㅔ헤"))

        audioFileRecyclerViewAdapter = AudioFileRecyclerViewAdapter(activity!!, dataList, token)

        rv_audio_file.adapter = audioFileRecyclerViewAdapter
        rv_audio_file.layoutManager = LinearLayoutManager(activity)
    }

}

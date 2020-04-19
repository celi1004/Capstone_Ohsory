package com.example.ohsoryapp.fragment


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.ohsoryapp.R
import com.example.ohsoryapp.activity.AdditionalRecordingActivity
import com.example.ohsoryapp.activity.TTSTestActivity
import kotlinx.android.synthetic.main.fragment_progress.*

/**
 * A simple [Fragment] subclass.
 */
class ProgressFragment : Fragment() {

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
    }

    fun setProgressSeekBar(){
        //TODO 서버에서 진행률 가져와서 seekbar 그 값으로 바꾸기
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

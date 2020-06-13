package com.example.ohsoryapp.fragment


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.ohsoryapp.R
import com.example.ohsoryapp.activity.LoginActivity
import com.example.ohsoryapp.activity.ModelManageActivity
import com.example.ohsoryapp.db.SharedPreferenceController
import com.example.ohsoryapp.myclass.AppPreferences
import kotlinx.android.synthetic.main.fragment_mypage.*
import org.jetbrains.anko.support.v4.act
import org.jetbrains.anko.support.v4.startActivity


/**
 * A simple [Fragment] subclass.
 */
class MypageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mypage, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getInfo()

        setButtonClickListener()
    }

    override fun onStop() {

        setState()

        super.onStop()
    }

    fun getInfo(){
        //디비에서 꺼내서 정보 띄워줘
        tv_name.text = SharedPreferenceController.getUserName(activity!!)
        sw_call_rec.isChecked = AppPreferences.getInstance(context).isRecordingEnabled()
        sw_alarm.isChecked = SharedPreferenceController.getActivateAlarm(activity!!)
    }

    fun setState(){
        //디비 업데이트
        AppPreferences.getInstance(context).setRecordingEnabled(sw_call_rec.isChecked)
        SharedPreferenceController.setActivateAlarm(activity!!, sw_alarm.isChecked)
    }

    fun setButtonClickListener(){

        tv_change_info.setOnClickListener(){

        }

        tv_model_setting.setOnClickListener(){
            activity?.let{
                val iT = Intent(context, ModelManageActivity::class.java)
                startActivity(iT)
            }
        }

        tv_log_out.setOnClickListener(){
            SharedPreferenceController.clearUserSharedPreferences(activity!!)
            startActivity<LoginActivity>()
            activity!!.finish()
        }

    }
}

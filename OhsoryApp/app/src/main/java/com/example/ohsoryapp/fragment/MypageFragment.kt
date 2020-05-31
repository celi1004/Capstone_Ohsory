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
import kotlinx.android.synthetic.main.fragment_mypage.*
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

        setInfo()
        //setState() 종료 되었을 때 state 변한거 있늕는지 봐바
        setButtonClickListener()
    }

    fun setInfo(){
        //TODO 서버에서 정보 가져와서 띄워줘
    }

    fun getState(){
        //TODO 프리퍼런스에서 스위치 정보가져와..? 저장해놔?
    }

    fun setState(){
        //TODO 화면 끌 때 스위치 상태 저장해 그리고 그렇게 행동
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

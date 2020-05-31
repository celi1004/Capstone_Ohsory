package com.example.ohsoryapp.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.ohsoryapp.R
import com.example.ohsoryapp.activity.LoginActivity
import com.example.ohsoryapp.activity.MainActivity
import com.example.ohsoryapp.db.SharedPreferenceController
import kotlinx.android.synthetic.main.fragment_intro4.*
import org.jetbrains.anko.support.v4.startActivity

/**
 * A simple [Fragment] subclass.
 */
class Intro4Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_intro4, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val token = SharedPreferenceController.getAuthorization(activity!!)

        bt_record.setOnClickListener(){
            if(token.length!=0)
            {
                startActivity<MainActivity>("token" to token)
            }

            else
            {
                startActivity<LoginActivity>()
            }
            activity!!.finish()
        }

    }

}

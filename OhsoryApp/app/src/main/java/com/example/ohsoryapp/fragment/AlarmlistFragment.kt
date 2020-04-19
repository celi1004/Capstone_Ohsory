package com.example.ohsoryapp.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.ohsoryapp.R
import com.example.ohsoryapp.adapter.AlarmPagerAdapter
import kotlinx.android.synthetic.main.fragment_alarmlist.*

/**
 * A simple [Fragment] subclass.
 */
class AlarmlistFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alarmlist, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        configureBottomNavigation()
    }

    private fun configureBottomNavigation() {

        vp_my_share_navi_act_frag_pager.adapter = AlarmPagerAdapter(childFragmentManager, 2)
        vp_my_share_navi_act_frag_pager.offscreenPageLimit = 2

        tl_my_share_navi_act_menu.setupWithViewPager(vp_my_share_navi_act_frag_pager)
    }

}

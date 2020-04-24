package com.example.ohsoryapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.ohsoryapp.fragment.Intro1Fragment
import com.example.ohsoryapp.fragment.Intro2Fragment
import com.example.ohsoryapp.fragment.Intro3Fragment
import com.example.ohsoryapp.fragment.Intro4Fragment

class IntroPagerAdapter(fm : FragmentManager, val FragmentCount : Int) : FragmentStatePagerAdapter(fm){

    override fun getItem(position: Int): Fragment? {
        when(position)
        {
            0->return Intro1Fragment()
            1->return Intro2Fragment()//SearchFragment()
            2->return Intro3Fragment()
            3->return Intro4Fragment()

            else->return null
        }
    }

    override fun getCount(): Int = FragmentCount
}
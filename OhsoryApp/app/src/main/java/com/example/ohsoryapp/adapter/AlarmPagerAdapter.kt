package com.example.ohsoryapp.adapter

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.example.ohsoryapp.fragment.*

class AlarmPagerAdapter(fm: FragmentManager, val FragmentCount: Int) : FragmentStatePagerAdapter(fm) {

    private val fragmentTitleList = mutableListOf("내 모델","공유 모델")

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return AlarmMyFragment()
            1 -> return AlarmShareFragment()

            else -> return null
        }
    }

    override fun getCount(): Int = FragmentCount

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        //Log.e("FragmentPagerAdapter", "destroyItem position : $position")
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitleList[position]
    }
}

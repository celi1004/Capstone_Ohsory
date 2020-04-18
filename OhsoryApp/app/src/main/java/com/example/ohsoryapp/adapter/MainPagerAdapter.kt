package com.example.ohsoryapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.example.ohsoryapp.fragment.AlarmlistFragment
import com.example.ohsoryapp.fragment.FilelistFragment
import com.example.ohsoryapp.fragment.MypageFragment
import com.example.ohsoryapp.fragment.ProgressFragment

class MainPagerAdapter(fm: FragmentManager, val FragmentCount: Int) : FragmentStatePagerAdapter(fm) {

    private val fragmentTitleList = mutableListOf("진척도","파일관리","알림목록","마이페이지")

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return ProgressFragment()
            1 -> return FilelistFragment()
            2 -> return AlarmlistFragment()
            3 -> return MypageFragment()

            else -> return null
        }
    }

    override fun getCount(): Int = FragmentCount

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitleList[position]
    }
}

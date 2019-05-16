package com.zzx.headerlayout.wangyiyun

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class FragmentAdapter(private val fragmentManager: FragmentManager,
                      private val fragmentList: List<Fragment>): FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment = fragmentList[position]

    override fun getCount(): Int = fragmentList.size

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {}
}
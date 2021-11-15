package com.ssafy.cafe.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ssafy.cafe.fragment.AllMenuFragment
import com.ssafy.cafe.fragment.UserCustomMenuFragment

class OrderMenuPagerAdapter : FragmentPagerAdapter{

   var page1:Fragment = AllMenuFragment()
    var page2:Fragment = UserCustomMenuFragment()
    var data:ArrayList<Fragment> = arrayListOf(page1,page2)

    constructor(fm: FragmentManager) : super(fm){

    }

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Fragment {
        return data.get(position)
    }


}
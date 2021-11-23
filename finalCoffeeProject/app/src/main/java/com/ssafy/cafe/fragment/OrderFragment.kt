package com.ssafy.cafe.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import com.google.android.material.tabs.TabLayout
import com.ssafy.cafe.R
import com.ssafy.cafe.activity.MainActivity
import com.ssafy.cafe.config.BaseFragment
import com.ssafy.cafe.databinding.FragmentOrderBinding

class OrderFragment : BaseFragment<FragmentOrderBinding>(FragmentOrderBinding::bind, R.layout.fragment_order) {

    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ibtnShoplist.setOnClickListener {
            mainActivity.openFragment(1)
        }

        initTab()
        mainActivity.openFragment(7)    // default : 전체 메뉴
    }

    private fun initTab(){
        binding.tabOrderMenu.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab!!.position){
                    0 -> {
                        mainActivity.openFragment(7)
                    }
                    1 -> mainActivity.openFragment(8)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
    }
}


package com.ssafy.cafe.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.ssafy.cafe.R
import com.ssafy.cafe.activity.MainActivity
import com.ssafy.cafe.adapter.OrderMenuPagerAdapter
import com.ssafy.cafe.databinding.FragmentOrderBinding

class OrderFragment : Fragment() {

    private lateinit var binding:FragmentOrderBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var pagerAdapter:OrderMenuPagerAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOrderBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ibtnShoplist.setOnClickListener {
            mainActivity.openFragment(1)
        }

        initTab()

        mainActivity.openFragment(7)    // default : 전체 메뉴
    }

    fun initTab(){

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


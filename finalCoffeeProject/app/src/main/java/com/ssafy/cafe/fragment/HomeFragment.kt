package com.ssafy.cafe.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.cafe.R
import com.ssafy.cafe.activity.MainActivity
import com.ssafy.cafe.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private lateinit var mainActivity: MainActivity
    private lateinit var binding:FragmentHomeBinding
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ibtnNotificaton.setOnClickListener{
            mainActivity.openFragment(6)
        }
    }
    fun initAdapter(){
        binding.rvBestMenuList.apply{
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        }
    }

}
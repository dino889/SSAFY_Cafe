package com.ssafy.cafe.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.cafe.R
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.databinding.FragmentPayBinding

class PayFragment : Fragment() {
    private lateinit var binding: FragmentPayBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPayBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPay()
    }
    fun initPay(){
        val pay = ApplicationClass.sharedPreferencesUtil.getUserPay()
        binding.tvUserPay.text = "$pay 원"
    }
}
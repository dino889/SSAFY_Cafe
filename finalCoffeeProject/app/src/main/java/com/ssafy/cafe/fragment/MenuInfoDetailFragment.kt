package com.ssafy.cafe.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.cafe.R
import com.ssafy.cafe.databinding.FragmentMenuInfoDetailBinding

class MenuInfoDetailFragment : Fragment() {
    private lateinit var binding:FragmentMenuInfoDetailBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentMenuInfoDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(key:String, value:Int) =
            AllMenuFragment().apply {
                arguments = Bundle().apply {
                    putInt(key, value)
                }
            }
    }
}
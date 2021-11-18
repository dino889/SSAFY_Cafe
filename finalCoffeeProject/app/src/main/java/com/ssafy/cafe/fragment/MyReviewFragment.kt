package com.ssafy.cafe.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.cafe.R
import com.ssafy.cafe.databinding.FragmentMyReviewBinding

class MyReviewFragment : Fragment() {
    private lateinit var binding: FragmentMyReviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMyReviewBinding.inflate(inflater,container,false)
        return binding.root
    }

}
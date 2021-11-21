package com.ssafy.cafe.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.cafe.R
import com.ssafy.cafe.activity.MainActivity
import com.ssafy.cafe.config.BaseFragment
import com.ssafy.cafe.databinding.FragmentNotificationBinding

class NotificationFragment : BaseFragment<FragmentNotificationBinding>(FragmentNotificationBinding::bind, R.layout.fragment_notification) {

    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_notification, container, false)
//    }

}
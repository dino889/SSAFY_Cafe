package com.ssafy.cafe.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.cafe.R
import com.ssafy.cafe.activity.MainActivity
import com.ssafy.cafe.adapter.NotificationAdapter
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.config.BaseFragment
import com.ssafy.cafe.databinding.FragmentNotificationBinding
import com.ssafy.cafe.dto.Notification
import com.ssafy.cafe.service.NotificationService

class NotificationFragment : BaseFragment<FragmentNotificationBinding>(FragmentNotificationBinding::bind, R.layout.fragment_notification) {

    private lateinit var mainActivity: MainActivity
    private lateinit var nAdapter:NotificationAdapter
    private lateinit var list:List<Notification>
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = ApplicationClass.sharedPreferencesUtil.getUser();
        val userid = user.id
        initData(userid)
    }
    fun initData(userId:String){
        val notificationLiveData = NotificationService().getUserWithNotification(userId)
        notificationLiveData.observe(
            viewLifecycleOwner,
            {
                list = it
                nAdapter = NotificationAdapter(list)
                binding.rvNoti.apply {
                    val linearLayoutManager = LinearLayoutManager(context)
                    linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
                    layoutManager = linearLayoutManager
                    adapter = nAdapter
                    adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

                }
            }
        )
    }
}
package com.ssafy.cafe.src.main.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.cafe.R
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.config.BaseFragment
import com.ssafy.cafe.databinding.FragmentNotificationBinding
import com.ssafy.cafe.src.main.MainActivity
import com.ssafy.cafe.src.main.dto.Notification
import com.ssafy.cafe.src.main.network.service.NotificationService

private const val TAG = "NotificationFragment_싸피"
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
        setSpinner()
        initData()

        // notification 수신
        val intentFilter = IntentFilter("com.ssafy.cafe")
        val receiver = object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val action = intent!!.action
                Log.d(TAG, "receive : $action")
                initData()
            }
        }
        mainActivity.registerReceiver(receiver, intentFilter)

    }

    private fun initData(){
        val user = ApplicationClass.sharedPreferencesUtil.getUser();
        val userid = user.id

        val notificationLiveData = NotificationService().getUserWithNotification(userid)
        notificationLiveData.observe(
            viewLifecycleOwner,
            {
                list = it
                nAdapter = NotificationAdapter(list, this::initData)
                binding.rvNotify.apply {
                    val linearLayoutManager = LinearLayoutManager(context)
                    linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
                    layoutManager = linearLayoutManager
                    adapter = nAdapter
                    adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

                }

                var category: String
                binding.spinnerNotiCategory.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        category = parent.getItemAtPosition(position).toString()
                        if(category == "All") {
                            nAdapter = NotificationAdapter(list, this@NotificationFragment::initData)
                            binding.rvNotify.apply {
                                val linearLayoutManager = LinearLayoutManager(context)
                                linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
                                layoutManager = linearLayoutManager
                                adapter = nAdapter
                                adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

                            }
                        } else {
                            val newList: MutableList<Notification> = mutableListOf()
                            for (i in list.indices) {
                                if (list[i].category == category) {
                                    newList.add(list[i])
                                }
                            }
                            nAdapter =
                                NotificationAdapter(newList, this@NotificationFragment::initData)
                            binding.rvNotify.apply {
                                val linearLayoutManager = LinearLayoutManager(context)
                                linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
                                layoutManager = linearLayoutManager
                                adapter = nAdapter
                                adapter!!.stateRestorationPolicy =
                                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                            }
                        }
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                })
            }
        )
    }

    private fun setSpinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.notiCategory_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerNotiCategory.adapter = adapter
        }
    }
}
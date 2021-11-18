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
import com.ssafy.cafe.adapter.OrderHistoryAdapter
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.databinding.FragmentPayBinding
import com.ssafy.cafe.response.LatestOrderResponse
import com.ssafy.cafe.service.OrderService
import com.ssafy.cafe.util.CommonUtils

class PayFragment : Fragment() {
    private lateinit var binding: FragmentPayBinding
    private lateinit var historyAdapter: OrderHistoryAdapter
    private lateinit var list : List<LatestOrderResponse>

    private lateinit var mainActivity: MainActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
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
        var user = ApplicationClass.sharedPreferencesUtil.getUser()
        var id = user.id
        initData(id)

    }
    fun initPay(){
        val pay = ApplicationClass.sharedPreferencesUtil.getUserPay()
        binding.tvUserPay.text = "${CommonUtils.makeComma(pay!!.toInt())}"
    }
    fun initData(id:String){
        val userOrderHistoryLiveData = OrderService().getLastMonthOrder(id)
        userOrderHistoryLiveData.observe(
            viewLifecycleOwner,{
                list = it

                historyAdapter = OrderHistoryAdapter(list)
                binding.rvOrderHistory.apply {
                    val linearLayoutManager = LinearLayoutManager(context)
                    linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
                    layoutManager = linearLayoutManager
                    adapter = historyAdapter
                    adapter!!.stateRestorationPolicy =
                        RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                }

            }
        )
    }

}
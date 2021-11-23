package com.ssafy.cafe.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.cafe.R
import com.ssafy.cafe.activity.MainActivity
import com.ssafy.cafe.adapter.OrderDetailAdapter
import com.ssafy.cafe.config.BaseFragment
import com.ssafy.cafe.databinding.FragmentOrderDetailBinding
import com.ssafy.cafe.response.OrderDetailResponse
import com.ssafy.cafe.service.OrderService
import com.ssafy.cafe.util.CommonUtils

private const val TAG = "OrderDetailFragment_싸피"
class OrderDetailFragment : BaseFragment<FragmentOrderDetailBinding>(FragmentOrderDetailBinding::bind, R.layout.fragment_order_detail) {
    private lateinit var mainActivity : MainActivity
    private lateinit var orderDetailAdapter: OrderDetailAdapter
    private lateinit var list:List<OrderDetailResponse>
    private var orderId = -1

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.hideBottomNav(true)
        arguments?.let {
            orderId = it.getInt("orderId", -1)
            Log.d(TAG, "onCreate: $orderId")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData(orderId)
    }

    private fun initData(orderId:Int){
        val orderDetailLiveData = OrderService().getOrderDetails(orderId)
        orderDetailLiveData.observe(
            viewLifecycleOwner, {
                list = it
                orderDetailAdapter = OrderDetailAdapter(list)
                binding.rvOrderDetailList.apply{
                    val linearLayoutManager = LinearLayoutManager(context)
                    linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
                    layoutManager = linearLayoutManager
                    adapter = orderDetailAdapter
                    adapter!!.stateRestorationPolicy =
                        RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                }
                val name = if(list.size == 1) {
                    list[0].productName
                } else {
                    if( list[0].productName.length <=5){
                        "${list[0].productName} 외 ${list.size - 1}잔"
                    }else{
                        "${list[0].productName}외 ..."
                    }
                }
                binding.tvOrderMenuNameShort.text = name
                binding.tvOrderDate.text = CommonUtils.getFormattedString(list[0].orderDate)
                var AllTotalPrice = 0
                for(i in 0..list.size-1){
                    AllTotalPrice += list[i].totalPrice
                }
                binding.tvAllTotalPrice.text = CommonUtils.makeComma(AllTotalPrice)
            }
        )
    }

    companion object {
        @JvmStatic
        fun newInstance(key: String, value : Int) =
            OrderDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(key, value)
                }
            }
    }
}
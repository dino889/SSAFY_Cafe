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
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.config.BaseFragment
import com.ssafy.cafe.databinding.FragmentOrderDetailBinding
import com.ssafy.cafe.dto.UserLevel
import com.ssafy.cafe.response.OrderDetailResponse
import com.ssafy.cafe.service.OrderService
import com.ssafy.cafe.service.UserService
import com.ssafy.cafe.util.CommonUtils
import org.json.JSONObject

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
                Log.d(TAG, "initData: $list")
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
                    AllTotalPrice += list[i].prodTotalPrice
                }

                var point = 0
                val userInfoLiveData = UserService().getUsers(ApplicationClass.sharedPreferencesUtil.getUser().id)
                userInfoLiveData.observe(viewLifecycleOwner) {
                    val data = JSONObject(it as Map<*, *>)
                    val rawUser = data.getJSONObject("user")

                    val userPay = rawUser.getInt("money")
                    val userStamp = rawUser.getInt("stamps")

                    // 등급 계산
                    val levelList = UserLevel.userInfoList
                    val listSize = levelList.size


                    for (i in 0 until listSize) {
                        if (userStamp <= levelList[i].max) {
                            point = (AllTotalPrice * levelList[i].point * 0.01).toInt()
                            break
                        }
                    }
                    binding.tvPayback.text = CommonUtils.makeComma(point)
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
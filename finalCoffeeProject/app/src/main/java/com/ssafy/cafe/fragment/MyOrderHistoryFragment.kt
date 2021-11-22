package com.ssafy.cafe.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.ssafy.cafe.config.BaseFragment
import com.ssafy.cafe.databinding.FragmentMyOrderHistoryBinding
import com.ssafy.cafe.dto.Order
import com.ssafy.cafe.response.LatestOrderResponse
import com.ssafy.cafe.service.OrderService
import com.ssafy.cafe.util.RetrofitCallback
import com.ssafy.cafe.util.RetrofitUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "MyOrderHistoryFragment"
class MyOrderHistoryFragment : BaseFragment<FragmentMyOrderHistoryBinding>(FragmentMyOrderHistoryBinding::bind,R.layout.fragment_my_order_history){
    private lateinit var mainActivity: MainActivity
    private lateinit var list : List<LatestOrderResponse>
    private lateinit var historyAdapter: OrderHistoryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var user = ApplicationClass.sharedPreferencesUtil.getUser()
        var id = user.id

        initData(id)
    }
    fun initData(id:String){
        val userOrderHistoryLiveData = OrderService().getLastMonthOrder(id)
        userOrderHistoryLiveData.observe(
            viewLifecycleOwner,{
                list = it

                historyAdapter = OrderHistoryAdapter(list)
                binding.rvMyhistory.apply {
                    val linearLayoutManager = LinearLayoutManager(context)
                    linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
                    layoutManager = linearLayoutManager
                    adapter = historyAdapter
                    adapter!!.stateRestorationPolicy =
                        RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                }
                historyAdapter.setItemClickListener(object: OrderHistoryAdapter.ItemClickListener{
                    override fun onClick(view: View, position: Int, orderId: Int) {
                        val orderId = list[position].orderId
                        mainActivity.openFragment(2, "orderId",orderId)

                    }
                })
            }
        )
    }


}
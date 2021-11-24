package com.ssafy.cafe.fragment

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri.parse
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
//import com.google.gson.internal.Streams.parse
//import com.google.gson.reflect.TypeToken
import com.ssafy.cafe.R
import com.ssafy.cafe.activity.LoginActivity
import com.ssafy.cafe.activity.MainActivity
import com.ssafy.cafe.adapter.BestMenuAdapter
import com.ssafy.cafe.adapter.LastOrderAdapter
import com.ssafy.cafe.adapter.WeekBestMenuAdapter
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.config.BaseFragment
import com.ssafy.cafe.databinding.FragmentHomeBinding
import com.ssafy.cafe.dto.Product
import com.ssafy.cafe.dto.ShoppingCart
import com.ssafy.cafe.dto.User
import com.ssafy.cafe.dto.UserLevel
import com.ssafy.cafe.response.BestProductResponse
import com.ssafy.cafe.response.LatestOrderResponse
import com.ssafy.cafe.service.OrderService
import com.ssafy.cafe.service.ProductService
import com.ssafy.cafe.service.UserService
import com.ssafy.cafe.util.RetrofitCallback
import com.ssafy.cafe.viewmodel.MainViewModel
import org.json.JSONObject
import java.net.HttpCookie.parse
import java.util.Locale.LanguageRange.parse
import java.util.logging.Level.parse

private const val TAG = "HomeFragment"
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::bind, R.layout.fragment_home) {
    private var lastOrderAdapter : LastOrderAdapter = LastOrderAdapter()
    private var bestMenuAdapter : BestMenuAdapter = BestMenuAdapter()
    private var weekBestMenuAdapter : WeekBestMenuAdapter = WeekBestMenuAdapter()
    private lateinit var mainActivity: MainActivity


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
//        viewModel.getUserInfo(ApplicationClass.sharedPreferencesUtil.getUser().id)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userInfoLiveData = UserService().getUsers(ApplicationClass.sharedPreferencesUtil.getUser().id)
        userInfoLiveData.observe(viewLifecycleOwner) {
            val data = JSONObject(it as Map<*, *>)
            val rawUser = data.getJSONObject("user")
            setUserLevel(rawUser.getInt("stamps"))
        }

        initAdapter()
        getData()
    }



    fun getData() {

        val liveData = OrderService().getLastMonthOrder(ApplicationClass.sharedPreferencesUtil.getUser().id)
        liveData.observe(viewLifecycleOwner) {
            lastOrderAdapter.list = it as MutableList<LatestOrderResponse>
            lastOrderAdapter.notifyDataSetChanged()
        }

        val bestLiveData = ProductService().getBestProduct()
        bestLiveData.observe(viewLifecycleOwner){
            bestMenuAdapter.list = it as MutableList<BestProductResponse>
            bestMenuAdapter.notifyDataSetChanged()
        }

        val weekBestLiveData = ProductService().getWeekBest()
        weekBestLiveData.observe(viewLifecycleOwner) {
            weekBestMenuAdapter.list = it as MutableList<Product>
            weekBestMenuAdapter.notifyDataSetChanged()
        }
    }


    private fun initAdapter(){
        // BestMenu
        bestMenuAdapter = BestMenuAdapter()
        bestMenuAdapter.setItemClickListener(object: BestMenuAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, productId: Int) {
                mainActivity.openFragment(3, "productId", productId)
            }
        })
        binding.rvBestMenuList.apply{
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = bestMenuAdapter
            adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        // 최근 주문 내역
        lastOrderAdapter = LastOrderAdapter()
        lastOrderAdapter.setItemClickListener(object: LastOrderAdapter.ItemClickListener{
            override fun onClick(view: View, position: Int) {
                if(viewModel.productList == null) {
                    showCustomToast("아직 물품 목록이 로딩되지 않았습니다.")
                } else {
                    getOrderItemsById(lastOrderAdapter.list[position].orderId)
                }
            }
        })
        binding.rvLastOrderMenuList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
            adapter = lastOrderAdapter
            adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        // 주간 베스트 메뉴
        weekBestMenuAdapter = WeekBestMenuAdapter()
        weekBestMenuAdapter.setItemClickListener(object: WeekBestMenuAdapter.ItemClickListener{
            override fun onClick(view: View, position: Int, productId : Int) {
                mainActivity.openFragment(3, "productId", productId)
            }
        })
        binding.rvWeekBestMenuList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
            adapter = weekBestMenuAdapter
            adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }


    }

    private fun setUserLevel(stamp: Int){
        val levelList = UserLevel.userInfoList
        val listSize = levelList.size

        var level = levelList[0].title
        var progressMax = levelList[0].max

        for (i in 0 until listSize) {
            if (stamp <= levelList[i].max) {    // 사용자 스탬프 수가 기준보다 작으면
                level = levelList[i].title
                progressMax = levelList[i].max
                break
            }
        }

        binding.tvStampCount.text = stamp.toString()
        binding.tvStampTotal.text = " / ${progressMax}"
        binding.tvUserLevel.text = level
        binding.progressBarStampState.progress = stamp
        binding.progressBarStampState.max = progressMax
    }

    // 주문 상세 내역 조회
    fun getOrderItemsById(orderId: Int) {
        OrderService().getOrderDetails(orderId).observe(viewLifecycleOwner) {

            it?.forEach { item ->
                for(i in viewModel.productList!!.indices) {
                    val product = viewModel.productList!![i]
                    if(item.productName.equals(product.name)) {
                           viewModel.insertShoppingCartItem(
                               ShoppingCart(product.id, product.img, product.name, item.quantity, item.unitPrice, item.totalPrice, item.productType, item.syrup, item.shot)
                           )
                        break
                    }
                }
            }
            Handler(Looper.getMainLooper()).post {
                mainActivity.openFragment(1)
            }
        }
    }
}
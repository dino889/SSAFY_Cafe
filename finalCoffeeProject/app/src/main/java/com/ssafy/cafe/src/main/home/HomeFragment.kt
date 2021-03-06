package com.ssafy.cafe.src.main.home

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
//import com.google.gson.internal.Streams.parse
//import com.google.gson.reflect.TypeToken
import com.ssafy.cafe.R
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.config.BaseFragment
import com.ssafy.cafe.databinding.FragmentHomeBinding
import com.ssafy.cafe.src.main.dto.Product
import com.ssafy.cafe.src.main.dto.ShoppingCart
import com.ssafy.cafe.src.main.dto.UserLevel
import com.ssafy.cafe.src.main.network.response.BestProductResponse
import com.ssafy.cafe.src.main.network.response.LatestOrderResponse
import com.ssafy.cafe.src.main.MainActivity
import com.ssafy.cafe.src.main.network.service.OrderService
import com.ssafy.cafe.src.main.network.service.ProductService
import com.ssafy.cafe.src.main.network.service.UserService
import org.json.JSONObject

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
        binding.userlevel.setOnClickListener {
            showLevelDialog()
        }
    }

    fun showLevelDialog(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.apply {
            setView(R.layout.dialog_userlevel)
            setCancelable(true)
            builder.setPositiveButton("??????",null)
        }
        builder.create().show()
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

        // ?????? ?????? ??????
        lastOrderAdapter = LastOrderAdapter()
        lastOrderAdapter.setItemClickListener(object: LastOrderAdapter.ItemClickListener{
            override fun onClick(view: View, position: Int) {
                if(viewModel.productList == null) {
                    showCustomToast("?????? ?????? ????????? ???????????? ???????????????.")
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

        // ?????? ????????? ??????
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
            if (stamp <= levelList[i].max) {    // ????????? ????????? ?????? ???????????? ?????????
                level = levelList[i].title
                progressMax = levelList[i].max
                break
            }
        }
        binding.tvStampCount.text = stamp.toString()
        binding.tvStampTotal.text = " / ${progressMax}"
        binding.userlevel.setAnimation("$level.json")
        binding.progressBarStampState.setProgress(stamp)
        binding.progressBarStampState.max = progressMax
    }

    // ?????? ?????? ?????? ??????
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
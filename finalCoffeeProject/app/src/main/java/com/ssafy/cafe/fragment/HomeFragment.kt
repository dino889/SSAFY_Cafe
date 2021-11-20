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
import com.google.gson.internal.Streams.parse
import com.google.gson.reflect.TypeToken
import com.ssafy.cafe.R
import com.ssafy.cafe.activity.LoginActivity
import com.ssafy.cafe.activity.MainActivity
import com.ssafy.cafe.adapter.BestMenuAdapter
import com.ssafy.cafe.adapter.LastOrderAdapter
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.databinding.FragmentHomeBinding
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
class HomeFragment : Fragment() {
    private var lastOrderAdapter : LastOrderAdapter = LastOrderAdapter()
    private var bestMenuAdapter : BestMenuAdapter = BestMenuAdapter()
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var mainActivity: MainActivity
    private lateinit var binding:FragmentHomeBinding
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUserLevel()
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
    }
    fun initAdapter(){
        bestMenuAdapter = BestMenuAdapter()
        binding.rvBestMenuList.apply{
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = bestMenuAdapter
            adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        lastOrderAdapter = LastOrderAdapter()

        lastOrderAdapter.setItemClickListener(object: LastOrderAdapter.ItemClickListener{
            override fun onClick(view: View, position: Int) {
                if(viewModel.productList == null) {
                    Toast.makeText(requireActivity(), "아직 물품목록이 로딩되지 않았습니다.", Toast.LENGTH_SHORT).show()
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
    }

    fun initUserLevel(){

        var user = ApplicationClass.sharedPreferencesUtil.getUser()
        UserService().getUsers(user.id, object : RetrofitCallback<HashMap<String, Any>>{
            override fun onError(t: Throwable) {
                Log.d(TAG, "onError: ")
            }

            override fun onSuccess(code: Int, responseData: HashMap<String, Any>) {
                Log.d(TAG, "onSuccess: $responseData")
                //val grade = responseData!!["grade"]
                val data = JSONObject(responseData as Map<*, *>)
                val rawUser = data.getJSONObject("user")
                val user = User(
                    rawUser.getString("id"),
                    rawUser.getString("name"),
                    rawUser.getString("pass"),
                    rawUser.getInt("stamps"),
                    rawUser.getString("phone"),
                    rawUser.getInt("money")
                )
//                val user = Gson().fromJson(responseData["user"].toString(), User::class.java)

                Log.d(TAG, "onSuccess: ${user.stamps}")
                binding.tvStampCount.text = "${user.stamps} /"

                viewModel.userStamp.value = user.stamps
                val tmp = UserLevel.userInfoList.size
                for(i in 0..tmp-1){
                    if(UserLevel.userInfoList.get(i).max <= user.stamps){
                        binding.tvUserLevel.text = UserLevel.userInfoList.get(i).title.toString()
                        if(i == tmp - 1) {  // 마지막 레벨
                            binding.tvStampTotal.text = "..."
                            binding.progressBarStampState.max = Int.MAX_VALUE
                        } else if(i + 1 < tmp - 1) {
                            binding.tvStampTotal.text = UserLevel.userInfoList.get(i + 1).max.toString()
                            binding.progressBarStampState.max = UserLevel.userInfoList.get(i + 1).max
                        }
                        binding.progressBarStampState.progress = user.stamps
                    }
                }

            }

            override fun onFailure(code: Int) {
                Log.d(TAG, "onFailure: ")
            }

        })
    }
    fun getOrderItemsById(orderId: Int) {
        OrderService().getOrderDetails(orderId).observe(viewLifecycleOwner) {
//            viewModel.removeAllShoppingCart()

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
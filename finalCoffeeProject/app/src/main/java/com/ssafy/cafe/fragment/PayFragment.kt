package com.ssafy.cafe.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.cafe.R
import com.ssafy.cafe.activity.MainActivity
import com.ssafy.cafe.adapter.OrderHistoryAdapter
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.config.BaseFragment
import com.ssafy.cafe.databinding.FragmentOrderDetailBinding
import com.ssafy.cafe.databinding.FragmentPayBinding
import com.ssafy.cafe.dto.*
import com.ssafy.cafe.response.LatestOrderResponse
import com.ssafy.cafe.service.OrderService
import com.ssafy.cafe.service.ProductService
import com.ssafy.cafe.service.UserService
import com.ssafy.cafe.util.CommonUtils
import com.ssafy.cafe.util.RetrofitCallback
import com.ssafy.cafe.viewmodel.MainViewModel

private const val TAG = "PayFragment"
class PayFragment : BaseFragment<FragmentPayBinding>(FragmentPayBinding::bind, R.layout.fragment_pay) {

    private lateinit var historyAdapter: OrderHistoryAdapter
    private lateinit var list : List<LatestOrderResponse>
    private val viewModel: MainViewModel by activityViewModels()
    var isChk = false


    private lateinit var mainActivity: MainActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPay()
        var user = ApplicationClass.sharedPreferencesUtil.getUser()
        var id = user.id
        initData(id)

        binding.btnStroeOrder.setOnClickListener {
            makeOrder()
        }


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

    fun makeOrder(){
        enableNfc()
        showDialogForOrderInShop()
    }
    private fun showDialogForOrderInShop() {
        Log.d(TAG, "showDialogForOrderInShop: ${viewModel.nfcTaggingData}")
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("매장결제")
        builder.setView(R.layout.dialog_nfc_order)
        builder.setCancelable(true)
        builder.setNegativeButton("확인"
        ) { dialog, _ ->
            if(viewModel.nfcTaggingData.equals("")) {   // NFC 태깅해서 테이블 데이터가 있으면
                Toast.makeText(requireContext(), "Table NFC를 찍어주세요.", Toast.LENGTH_SHORT).show()
            } else {
                isChk = true
                makeOrderDto()

            }
            dialog.cancel()
            disableNfc()
        }
        builder.create().show()
    }

    fun makeOrderDto(){
        var orderDetailList:ArrayList<OrderDetail> = arrayListOf()

        var order = Order(
            0,
            ApplicationClass.sharedPreferencesUtil.getUser().id,
            "",
            System.currentTimeMillis().toString(),
            0,
            orderDetailList
        )
        val ood = viewModel.nfcTaggingData.toString()
        Log.d(TAG, "makeOrderDto: $ood")
        val dataStr = ood.split("/")
        Log.d(TAG, "makeOrderDto: $dataStr")

        orderDetailList.add(
            OrderDetail(
                0,
                order.id,
                dataStr.get(2).toInt(), //productId
                dataStr.get(3).toInt(), //quantity
                dataStr.get(4).toInt(), //type(hot,ice)
                dataStr.get(5).toString(),  //syrup
                dataStr.get(6).toInt()  //shot
            )
        )


        ProductService().getProductById(dataStr.get(2).toInt(), GetProductCallback(dataStr.get(3).toInt(),order))
    }

    fun enableNfc() {
        // NFC 포그라운드 기능 활성화
        mainActivity.nfcAdapter!!.enableForegroundDispatch(mainActivity, mainActivity.pIntent, mainActivity.filters, null)
    }
    override fun onPause() {
        disableNfc()
        super.onPause()
    }
    fun disableNfc() {
        // NFC 포그라운드 기능 비활성화
        mainActivity.nfcAdapter!!.disableForegroundDispatch(mainActivity)
    }
    inner class GetProductCallback(val quanty: Int, val order:Order): RetrofitCallback<Product>{
        override fun onError(t: Throwable) {
            Log.d(TAG, "onError: ")
        }

        override fun onSuccess(code: Int, responseData: Product) {
            val totalPrice = responseData.price * quanty
            var point = 0
            val userPay = ApplicationClass.sharedPreferencesUtil.getUserPay()

            // 등급 계산
            for(i in 0..UserLevel.userInfoList.size-1) {
                if (UserLevel.userInfoList.get(i).max <= viewModel.userStamp.value!!) {
                    point = (totalPrice * UserLevel.userInfoList.get(i).point * 0.01).toInt()   // 등급에 따른 포인트 부여(총 금액 * 등급별 퍼센트)
                }
            }

            if (userPay!! - totalPrice >= 0) {  // 현재 잔액 - 주문 금액이 0보다 크면 주문 가능
                val balance = (userPay!! - totalPrice) + point  // 현재 잔액 - 주문 금액
                completedOrder(order, balance)
            } else{
                Toast.makeText(requireContext(), "현재 잔액이 부족합니다. 매장에서 잔액을 충전해주세요.", Toast.LENGTH_LONG).show()
            }

        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onFailure: ")
        }
    }

    private fun completedOrder(order: Order, balance:Int){

        order.orderTable = "take-out"

        OrderService().insertOrder(order ,object : RetrofitCallback<Int> {
            override fun onError(t: Throwable) {
                Log.d(TAG, "onError: ")
            }

            override fun onSuccess(code: Int, responseData: Int) {

                Toast.makeText(context,"주문이 완료되었습니다.", Toast.LENGTH_SHORT).show()
//                viewModel.shoppingCartList.clear()  // 장바구니 비우기

                isChk = false
                viewModel.nfcTaggingData = ""

                // OrderFragment 전환
                mainActivity.openFragment(2,"orderId", responseData)

                // 잔액 업데이트
                var updateUser = User(ApplicationClass.sharedPreferencesUtil.getUser().id, balance)

                UserService().updateMoney(updateUser , object : RetrofitCallback<User> {
                    override fun onError(t: Throwable) {
                        Log.d(TAG, "onError: ")
                    }

                    override fun onSuccess(code: Int, responseData: User) {
                        Log.d(TAG, "onSuccess: $responseData")
                        ApplicationClass.sharedPreferencesUtil.addUserPay(responseData.money)
                    }

                    override fun onFailure(code: Int) {
                        Log.d(TAG, "onFailure: ")
                    }

                })

            }

            override fun onFailure(code: Int) {
                Log.d(TAG, "onFailure: ")
            }

        })
    }

}
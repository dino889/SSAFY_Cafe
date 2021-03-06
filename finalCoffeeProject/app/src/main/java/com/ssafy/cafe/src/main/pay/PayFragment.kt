package com.ssafy.cafe.src.main.pay

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils.substring
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ssafy.cafe.R
import com.ssafy.cafe.src.main.order.OrderHistoryAdapter
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.config.BaseFragment
import com.ssafy.cafe.databinding.FragmentPayBinding
import com.ssafy.cafe.src.main.network.response.LatestOrderResponse
import com.ssafy.cafe.src.main.MainActivity
import com.ssafy.cafe.src.main.dto.*
import com.ssafy.cafe.src.main.network.service.OrderService
import com.ssafy.cafe.src.main.network.service.ProductService
import com.ssafy.cafe.src.main.network.service.UserService
import com.ssafy.cafe.util.CommonUtils
import com.ssafy.cafe.util.RetrofitCallback
import org.json.JSONObject

private const val TAG = "PayFragment"
class PayFragment : BaseFragment<FragmentPayBinding>(FragmentPayBinding::bind, R.layout.fragment_pay) ,
    SwipeRefreshLayout.OnRefreshListener{
    private lateinit var historyAdapter: OrderHistoryAdapter
    private lateinit var list : List<LatestOrderResponse>
    var isChk = false

    var swipeRefreshLayout: SwipeRefreshLayout? = null

    private lateinit var mainActivity: MainActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout = binding.swipeLayout
        swipeRefreshLayout!!.setOnRefreshListener(this)

        initPay()
        var user = ApplicationClass.sharedPreferencesUtil.getUser()
        var id = user.id
        initData(id)

        binding.btnStroeOrder.setOnClickListener {
            makeOrder()
        }

        binding.ibtnCard.setOnClickListener {
            enableNfc()
            showDialogForOrderInShop(1)
        }
    }

    private fun initPay(){

        val userInfoLiveData = UserService().getUsers(ApplicationClass.sharedPreferencesUtil.getUser().id)
        userInfoLiveData.observe(viewLifecycleOwner) {
            val data = JSONObject(it as Map<*, *>)
            val rawUser = data.getJSONObject("user")
            binding.tvUserPay.text = CommonUtils.makeComma(rawUser.getInt("money"))
        }

    }

    private fun initData(id:String){
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
                historyAdapter.setItemClickListener(object: OrderHistoryAdapter.ItemClickListener{
                    override fun onClick(view: View, position: Int, orderId: Int) {
                        val orderId = list[position].orderId
                        mainActivity.openFragment(2, "orderId",orderId)

                    }
                })
            }
        )
    }

    private fun makeOrder(){
        enableNfc()
        showDialogForOrderInShop(0)
    }
    //0: ???????????? 1:??????
    private fun showDialogForOrderInShop(checkType:Int) {
        Log.d(TAG, "showDialogForOrderInShop: ${viewModel.nfcTaggingData}")
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.dialog_nfc_order)
        builder.setCancelable(true)
        if(checkType==0){
            builder.setTitle("????????????")
            builder.setNegativeButton("??????"
            ) { dialog, _ ->
                if(viewModel.nfcTaggingData.equals("")) {   // NFC ???????????? ????????? ???????????? ?????????
                    Toast.makeText(requireContext(), "Table NFC??? ???????????????.", Toast.LENGTH_SHORT).show()
                } else {
                    isChk = true
                    makeOrderDto()
                }
                dialog.cancel()
                disableNfc()
                onRefresh()
            }
        }else if(checkType == 1){
            builder.setTitle("????????? ?????????????????????????")
            builder.setNegativeButton("??????"
            ) { dialog, _ ->
                if(viewModel.nfcTaggingData.equals("")){
                    Toast.makeText(requireContext(),"????????? NFC??? ??????????????????", Toast.LENGTH_SHORT).show()
                }else{
                    chargePay()
                }
                dialog.cancel()
                disableNfc()
                onRefresh()
            }
        }
        builder.create().show()
    }
    private fun chargePay(){
        val chargeMoney = viewModel.nfcTaggingData.toString().toInt()
        val user = ApplicationClass.sharedPreferencesUtil.getUser()
        val id = user.id
        val curMoneyTmp = binding.tvUserPay.text.toString()
        val curMoney = substring(curMoneyTmp.replace(",","").trim(),0,curMoneyTmp.replace(",","").trim().length-1)

        val updateMoney = curMoney.trim().toInt() + chargeMoney.toInt()
        val updateUser = User(id, updateMoney)

        Log.d(TAG, "chargePay: ${chargeMoney} ??? ?????? ?????? ${curMoney}??? ????????????????????? ??? : ${updateMoney}")
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
    private fun makeOrderDto(){
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
                dataStr.get(6).toInt(),  //shot
            0
            )
        )
        ProductService().getProductById(dataStr.get(2).toInt(), GetProductCallback(dataStr.get(3).toInt(),order))
    }

    private fun enableNfc() {
        // NFC ??????????????? ?????? ?????????
        mainActivity.nfcAdapter!!.enableForegroundDispatch(mainActivity, mainActivity.pIntent, mainActivity.filters, null)
    }

    fun disableNfc() {
        // NFC ??????????????? ?????? ????????????
        mainActivity.nfcAdapter!!.disableForegroundDispatch(mainActivity)
    }

    override fun onPause() {
        disableNfc()
        super.onPause()
    }

    inner class GetProductCallback(val quanty: Int, val order: Order): RetrofitCallback<Product>{
        override fun onError(t: Throwable) {
            Log.d(TAG, "onError: ")
        }

        override fun onSuccess(code: Int, responseData: Product) {
            var totalPrice = responseData.price * quanty   // ?????? ??????
            val od = order.details[0]
            if(od.syrup != null) {
                if(od.syrup != "??????"){
                    totalPrice += 500
                }
            }

            if(od.shot != null) {
                totalPrice += (od.shot!! * 500)
            }
            od.totalPrice = totalPrice
            var point = 0

            val userInfoLiveData = UserService().getUsers(ApplicationClass.sharedPreferencesUtil.getUser().id)
            userInfoLiveData.observe(viewLifecycleOwner) {
                val data = JSONObject(it as Map<*, *>)
                val rawUser = data.getJSONObject("user")

                val userPay = rawUser.getInt("money")
                val userStamp = rawUser.getInt("stamps")

                // ?????? ??????
                val levelList = UserLevel.userInfoList
                val listSize = levelList.size


                for (i in 0 until listSize) {
                    if (userStamp <= levelList[i].max) {
                        point = (totalPrice * levelList[i].point * 0.01).toInt()
                        break
                    }
                }

                if (userPay - totalPrice >= 0) {  // ?????? ?????? - ?????? ????????? 0?????? ?????? ?????? ??????
                    val balance = (userPay - totalPrice) + point  // ?????? ?????? - ?????? ??????
                    completedOrder(order, balance)
                } else{
                    showCustomToast("?????? ????????? ???????????????. ???????????? ????????? ??????????????????.")
                }

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

                Toast.makeText(context,"????????? ?????????????????????.", Toast.LENGTH_SHORT).show()
//                viewModel.shoppingCartList.clear()  // ???????????? ?????????

                isChk = false
                viewModel.nfcTaggingData = ""

                // OrderFragment ??????
                mainActivity.openFragment(2,"orderId", responseData)

                // ?????? ????????????
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

    override fun onRefresh() {
        updateLayoutView()
        swipeRefreshLayout!!.setRefreshing(false)
    }

    // ????????? ???????????? ?????? ??? ??? ?????? ?????????
    fun updateLayoutView() {
        initPay()
    }

}
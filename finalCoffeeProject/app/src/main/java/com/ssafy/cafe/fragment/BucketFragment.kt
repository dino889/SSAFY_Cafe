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
import com.ssafy.cafe.adapter.ShoppingCartAdapter
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.databinding.FragmentBucketBinding
import com.ssafy.cafe.dto.Order
import com.ssafy.cafe.dto.OrderDetail
import com.ssafy.cafe.service.OrderService
import com.ssafy.cafe.util.CommonUtils
import com.ssafy.cafe.util.RetrofitCallback
import com.ssafy.cafe.viewmodel.MainViewModel

class BucketFragment : Fragment() {
    private val TAG = "BucketFragment_싸피"
    private lateinit var shoppingListRecyclerView: RecyclerView
    private lateinit var shoppingListAdapter : ShoppingCartAdapter
    private lateinit var binding: FragmentBucketBinding
    private lateinit var mainActivity: MainActivity
    private val viewModel: MainViewModel by activityViewModels()
    private var hereOrTogo : Boolean = true // table : true, TakeOut : false
    var isChk = false   // Nfc 태그 데이터 chk


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.hideBottomNav(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBucketBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHereOrTogo()

        // delete Btn click - 장바구니 리스트에서 삭제
        shoppingListAdapter = ShoppingCartAdapter().apply {
            list = viewModel.liveShoppingCartList.value!!
            cancelListener = object : ShoppingCartAdapter.ItemCancelListener{
                override fun onClick(position: Int) {
                    viewModel.removeShoppingCartItem(position)
                }
            }
        }

        binding.rvShoppinglist.apply {
            val linearLayoutManager = LinearLayoutManager(context)
            linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            layoutManager = linearLayoutManager
            adapter = shoppingListAdapter
            //원래의 목록위치로 돌아오게함
            adapter!!.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        viewModel.liveShoppingCartList.observe(viewLifecycleOwner) {
            shoppingListAdapter!!.notifyDataSetChanged()
            var totalPrice = 0
            var totalCnt = 0
            for(i in it) {
                totalCnt += i.menuCnt
                totalPrice += i.totalPrice
            }

            binding.btnOrder.text = "${totalCnt}개 총 ${CommonUtils.makeComma(totalPrice)}"
        }

        makeOrder()
    }

    // 매장 vs TakeOut 선택 여부 set
    private fun setHereOrTogo() {
        binding.btnTakehere.setOnClickListener {
            hereOrTogo = true
        }
        binding.btnTakeout.setOnClickListener {
            hereOrTogo = false
        }
    }

    // 주문 버튼 클릭
    private fun makeOrder() {
        binding.btnOrder.setOnClickListener {
            if(hereOrTogo) {
                enableNfc()
                showDialogForOrderInShop()
            }
            else {
                //거리가 200이상이라면
                if(true) showDialogForOrderTakeoutOver200m()
            }
        }
    }

    private fun showDialogForOrderInShop() {
        Log.d(TAG, "showDialogForOrderInShop: ${viewModel.nfcTaggingData}")
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("알림")
        builder.setMessage(
            "Table NFC를 찍어주세요.\n"
        )
        builder.setCancelable(true)
        builder.setNegativeButton("취소"
        ) { dialog, _ ->
//            completedOrder()
            if(viewModel.nfcTaggingData.equals("")) {   // NFC 태깅해서 테이블 데이터가 있으면
                Toast.makeText(requireContext(), "Table NFC를 찍어주세요.", Toast.LENGTH_SHORT).show()
            } else {
                Log.d(TAG, "showDialogForOrderInShop: ${viewModel.nfcTaggingData}")
                isChk = true
                makeOrderDto()
            }
            dialog.cancel()
        }
        builder.create().show()
    }

    // 매장까지의 거리가 200이상일 때 확인 다이얼로그 띄우기
    private fun showDialogForOrderTakeoutOver200m() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("알림")
        builder.setMessage(
            "현재 고객님의 위치가 매장과 200m 이상 떨어져 있습니다.\n정말 주문하시겠습니까?"
        )
        builder.setCancelable(true)
        builder.setPositiveButton("확인") { _, _ ->
//            completedOrder()
            makeOrderDto()
        }
        builder.setNegativeButton("취소"
        ) { dialog, _ -> dialog.cancel() }
        builder.create().show()
    }

    fun makeOrderDto(){
        var orderDetailList:ArrayList<OrderDetail> = arrayListOf()

        var order = Order(
            0,
            ApplicationClass.sharedPreferencesUtil.getUser().id,
            "",
            System.currentTimeMillis().toString(),
            1,
            orderDetailList
        )

        val shoppingList = viewModel.shoppingCartList

        for(i in 0 .. shoppingList.size - 1){
            val tmp = shoppingList.get(i)
            orderDetailList.add(
                OrderDetail(
                    0,
                    order.id,
                    tmp.menuId,
                    tmp.menuCnt,
                    tmp.type,
                    tmp.syrup,
                    tmp.shot
                ))
        }

        completedOrder(order)
    }

    private fun completedOrder(order: Order){
//        var orderTable = mainActivity.orderTable!!
//        if(orderTable.contains("order_table")) {   // 데이터가 있으면
//            isChk = true
//        }
        if (hereOrTogo && isChk) {
//            val orderTable = ApplicationClass.sharedPreferencesUtil.getOrderTable()!!
            val orderTable = viewModel.nfcTaggingData
//            Log.d(TAG, "completedOrder: $orderTable")
            order.orderTable = orderTable!!
            Toast.makeText(context, "${orderTable.substring(orderTable.length - 2, orderTable.length)}번 테이블 번호가 등록되었습니다.", Toast.LENGTH_SHORT).show()
        } else {
            order.orderTable = "take-out"
        }

        OrderService().insertOrder(order ,object : RetrofitCallback<Int> {
            override fun onError(t: Throwable) {
                Log.d(TAG, "onError: ")
            }

            override fun onSuccess(code: Int, responseData: Int) {

                Log.d(TAG, "onSuccess: $responseData")
                //(requireContext() as MainActivity).onBackPressed()

                Toast.makeText(context,"주문이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                viewModel.shoppingCartList.clear()  // 장바구니 비우기
//                shoppingCarList.clear()
//                mainActivity.openFragment(2, "orderId", responseData)   // 주문 상세 페이지로 이동
                isChk = false
                viewModel.nfcTaggingData = ""
//                ApplicationClass.sharedPreferencesUtil.addOrderTable("x")
            }

            override fun onFailure(code: Int) {
                Log.d(TAG, "onFailure: ")
            }

        })
    }




    fun enableNfc() {
        // NFC 포그라운드 기능 활성화
        mainActivity.nfcAdapter!!.enableForegroundDispatch(mainActivity, mainActivity.pIntent, mainActivity.filters, null)
    }

    override fun onPause() {
        super.onPause()
        mainActivity.nfcAdapter!!.disableForegroundDispatch(mainActivity)
    }

}


//INSERT INTO t_order (user_id, order_table) VALUES (?, ?)
//Parameters: test(String), order_table 01(String)
//Updates: 1
//Preparing: select max(o_id) from t_order
//Parameters:
//Total: 1
//INSERT INTO t_order_detail ( order_id, product_id, quantity) VALUES ( ?, ?, ?)
//16(Integer), 4(Integer), 1(Integer)
//Updates: 1
//INSERT INTO t_stamp (user_id, order_id, quantity) VALUES (?, ?, ?)
//test(String), 16(Integer), 1(Integer)
//Updates: 1
//UPDATE t_user SET stamps = stamps + ? WHERE id = ?
//Parameters: 1(Integer), test(String)
//Updates: 1
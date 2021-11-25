package com.ssafy.cafe.src.main.usercustom

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.cafe.R
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.config.BaseFragment
import com.ssafy.cafe.databinding.FragmentUserCustomMenuBinding
import com.ssafy.cafe.src.main.MainActivity
import com.ssafy.cafe.src.main.dto.Product
import com.ssafy.cafe.src.main.dto.ShoppingCart
import com.ssafy.cafe.src.main.dto.UserCustom
import com.ssafy.cafe.src.main.network.service.ProductService
import com.ssafy.cafe.src.main.network.service.UserCustomService
import com.ssafy.cafe.util.RetrofitCallback

private const val TAG = "UserCustomMenuFragment_싸피"
class UserCustomMenuFragment : BaseFragment<FragmentUserCustomMenuBinding>(FragmentUserCustomMenuBinding::bind, R.layout.fragment_user_custom_menu) {

    private lateinit var customMenuAdapter: CustomMenuAdapter
    private lateinit var mainActivity: MainActivity
    private lateinit var list: List<UserCustom>
    private var deleteProduct = mutableListOf<Int>()

//    var swipeRefreshLayout: SwipeRefreshLayout? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        swipeRefreshLayout = binding.swipeLayout
//        swipeRefreshLayout!!.setOnRefreshListener(this)
//        getCustomMenubyId(ApplicationClass.sharedPreferencesUtil.getUser().id)
        initData()
    }

    private fun initData() {
        val userCustomLiveData = UserCustomService().getCustomWithUserId(ApplicationClass.sharedPreferencesUtil.getUser().id)

        userCustomLiveData.observe(viewLifecycleOwner, {
            list = it
            customMenuAdapter = CustomMenuAdapter(list, this::initData)
            binding.rvUserCustomList.apply {
                val linearLayoutManager = LinearLayoutManager(context)
                linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
                layoutManager = linearLayoutManager
                adapter = customMenuAdapter
                adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }

            binding.btnGotoShoppingList.setOnClickListener {
                for(i in list.indices){
                    if(list.get(i).isChecked == true){
                        ProductService().getProductById(list.get(i).productId, GetProductCallback(list.get(i)))
                    }
                }
            }

        })


    }


//    override fun onRefresh() {
//
//        //새로 고침 코드
//        updateLayoutView()
//
//        //새로 고침 완
//        swipeRefreshLayout!!.setRefreshing(false)
//    }
//
//    // 당겨서 새로고침 했을 때 뷰 변경 메서드
//    fun updateLayoutView() {
//        initData()
//    }

    inner class GetProductCallback(val uc: UserCustom): RetrofitCallback<Product>{
        override fun onError(t: Throwable) {
            Log.d(TAG, "onError: ")
        }

        override fun onSuccess(code: Int, responseData: Product) {
            var totalPrice = responseData.price   // 상품 가격

            var syrup: String? = uc.syrup
            var shot: Int? = uc.shot
            if(syrup != null) {
                if(syrup!!.contains('+')){
                    syrup = "설탕"
                }
                if(syrup == "") {
                    syrup = null
                } else {
                    totalPrice += 500
                }
            }

            if(shot != null) {
                if(shot.toString() == "" || shot == 0) {
                    shot = null
                }
                totalPrice += (shot!! * 500)
            }

            val cart = ShoppingCart(
                uc.productId,
                responseData.img,
                responseData.name,
                1,
                responseData.price,
                totalPrice,
                uc.type,
                uc.syrup,
                uc.shot
            )
            viewModel.insertShoppingCartItem(cart)
            mainActivity.openFragment(1)

        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onFailure: ")
        }
    }
}
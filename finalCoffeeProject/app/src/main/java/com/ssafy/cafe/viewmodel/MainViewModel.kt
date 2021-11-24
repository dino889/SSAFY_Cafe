package com.ssafy.cafe.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.dto.*
import com.ssafy.cafe.fragment.MenuDetailFragment
import com.ssafy.cafe.response.MenuDetailWithCommentResponse
import com.ssafy.cafe.service.ProductService
import com.ssafy.cafe.service.UserCustomService
import com.ssafy.cafe.service.UserService
import com.ssafy.cafe.util.RetrofitCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

private const val TAG = "MainViewModel"
class MainViewModel : ViewModel() {
    val shoppingCartList = mutableListOf<ShoppingCart>()
    val liveShoppingCartList = MutableLiveData<MutableList<ShoppingCart>>().apply {
        value = shoppingCartList
    }

    var productList: List<Product>? = null

    val prodService = ProductService().getProductList(object: RetrofitCallback<List<Product>> {
        override fun onError(t: Throwable) {
        }

        override fun onSuccess(code: Int, responseData: List<Product>) {
            productList = responseData
        }

        override fun onFailure(code: Int) {
        }
    })


    fun removeShoppingCartItem(position: Int) {
        shoppingCartList.removeAt(position)
        liveShoppingCartList.value = shoppingCartList
    }
    fun insertShoppingCartItem(cart : ShoppingCart){
        shoppingCartList.add(cart)
        Log.d(TAG, "insertShoppingCartItem: $cart")
        liveShoppingCartList.value = shoppingCartList
    }
    fun removeAllShoppingCart(){
        shoppingCartList.clear()
        liveShoppingCartList.value = shoppingCartList
    }



    val productWithComment = mutableListOf<MenuDetailWithCommentResponse>()

    val liveProductWithComment = MutableLiveData<List<MenuDetailWithCommentResponse>>().apply {
        value = productWithComment
    }


    // NFC 중복 태깅 방지
    var nfcTaggingData : String? = ""


//    // repository에 있는 MutableLiveData를 ViewModel의 LiveData에 넣는다.
//    private var userCustomProduct: LiveData<List<UserCustom>>
//        get() = UserCustomService().getCustomWithUserId(userId = )
//
//
//    fun loadCustomProduct(userId: String) {
//        userCustomProduct = UserCustomService().getCustomWithUserId(userId) // repository에 있는 메서드를 호출함으로써 다음 공지사항을 불러온다.
//    }
//
//    fun getAllUserCustom(): LiveData<List<UserCustom>> {
//        return userCustomProduct
//    }

}
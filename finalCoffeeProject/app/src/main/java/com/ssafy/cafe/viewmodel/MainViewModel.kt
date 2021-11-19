package com.ssafy.cafe.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.dto.Product
import com.ssafy.cafe.dto.ShoppingCart
import com.ssafy.cafe.dto.User
import com.ssafy.cafe.dto.UserLevel
import com.ssafy.cafe.response.MenuDetailWithCommentResponse
import com.ssafy.cafe.service.ProductService
import com.ssafy.cafe.service.UserService
import com.ssafy.cafe.util.RetrofitCallback

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

    var distance = Double.MAX_VALUE

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
    var nfcTaggingData : String? = null

    // stamp 변화
    var userStamp = MutableLiveData<Int>().apply {
        value = 0
    }

    fun initUserLevel() {

        var user = ApplicationClass.sharedPreferencesUtil.getUser()

        UserService().getUsers(user.id, object : RetrofitCallback<HashMap<String, Any>>{
            override fun onError(t: Throwable) {
//                Log.d(com.ssafy.cafe.fragment.TAG, "onError: ")
            }

            override fun onSuccess(code: Int, responseData: HashMap<String, Any>) {
//                Log.d(com.ssafy.cafe.fragment.TAG, "onSuccess: $responseData")
                //val grade = responseData!!["grade"]

                val user = Gson().fromJson(responseData["user"].toString(),User::class.java)

//                Log.d(com.ssafy.cafe.fragment.TAG, "onSuccess: ${user.stamps}")

                userStamp.value = user.stamps

//                binding.tvStampCount.text = "${user.stamps} /"
//
//                for(i in 0..UserLevel.userInfoList.size-1){
//                    if(UserLevel.userInfoList.get(i).max <= user.stamps){
//                        binding.tvUserLevel.text = UserLevel.userInfoList.get(i).title.toString()
//                    }
//                }

            }

            override fun onFailure(code: Int) {
//                Log.d(com.ssafy.cafe.fragment.TAG, "onFailure: ")
            }

        })
    }
}
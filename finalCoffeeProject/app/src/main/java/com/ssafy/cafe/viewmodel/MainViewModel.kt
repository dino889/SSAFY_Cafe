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
import com.ssafy.cafe.fragment.MenuDetailFragment
import com.ssafy.cafe.response.MenuDetailWithCommentResponse
import com.ssafy.cafe.service.ProductService
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


//    private val _user = MutableLiveData<User>()
//    val user = _user
//
//    fun getUserInfo(userId:String) {
//        CoroutineScope(Dispatchers.IO).launch {
//            UserService().getUsers(userId, object : RetrofitCallback<HashMap<String, Any>>{
//                override fun onError(t: Throwable) {
//                    Log.d(TAG, "onError: ")
//                }
//
//                override fun onSuccess(code: Int, responseData: HashMap<String, Any>) {
////                val user = Gson().fromJson(responseData["user"].toString(), User::class.java)
//                    val data = JSONObject(responseData as Map<*, *>)
//                    val rawUser = data.getJSONObject("user")
//                    val user = User(
//                        rawUser.getString("id"),
//                        rawUser.getString("name"),
//                        rawUser.getString("pass"),
//                        rawUser.getString("phone"),
//                        rawUser.getInt("stamps"),
//                        rawUser.getInt("money"),
//                        rawUser.getString("token")
//                    )
//                    _user.postValue(user)
//                }
//
//                override fun onFailure(code: Int) {
//                    Log.d(TAG, "onFailure: ")
//                }
//            })
//        }
//    }

}
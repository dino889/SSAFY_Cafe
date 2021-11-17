package com.ssafy.cafe.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssafy.cafe.dto.Product
import com.ssafy.cafe.dto.ShoppingCart
import com.ssafy.cafe.service.ProductService
import com.ssafy.cafe.util.RetrofitCallback

private const val TAG = "MainViewModel"
class MainViewModel : ViewModel() {
    val shoppingCartList = mutableListOf<ShoppingCart>()
    val liveShoppingCartList = MutableLiveData<MutableList<ShoppingCart>>().apply {
        value = shoppingCartList
    }

    var productList: List<Product>? = null

    val a = ProductService().getProductList(object: RetrofitCallback<List<Product>> {
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
}
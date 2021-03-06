package com.ssafy.cafe.src.main.network.service


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ssafy.cafe.src.main.dto.Product
import com.ssafy.cafe.src.main.network.response.BestProductResponse
import com.ssafy.cafe.src.main.network.response.MenuDetailWithCommentResponse
import com.ssafy.cafe.util.RetrofitCallback
import com.ssafy.cafe.util.RetrofitUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "ProductService_싸피"
class ProductService {

    fun getProductList(callback: RetrofitCallback<List<Product>>)  {
        val menuInfoRequest: Call<List<Product>> = RetrofitUtil.productService.getProductList()
        menuInfoRequest.enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                val res = response.body()
                if(response.code() == 200){
                    if (res != null) {
                        callback.onSuccess(response.code(), res)
                    }
                } else {
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                callback.onError(t)
            }
        })
    }

    fun getProductWithTypeList(productType: String, callback: RetrofitCallback<List<Product>>)  {
        val menuInfoByTypeRequest: Call<List<Product>> = RetrofitUtil.productService.getProductWithType(productType)
        menuInfoByTypeRequest.enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                val res = response.body()
                if(response.code() == 200){
                    if (res != null) {
                        callback.onSuccess(response.code(), res)
                    }
                } else {
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                callback.onError(t)
            }
        })
    }

    fun getProductWithComments(productId: Int, callback: RetrofitCallback<List<MenuDetailWithCommentResponse>>) {
        val menuInfoRequest: Call<List<MenuDetailWithCommentResponse>> = RetrofitUtil.productService.getProductWithComments(productId)

        menuInfoRequest.enqueue(object : Callback<List<MenuDetailWithCommentResponse>> {
            override fun onResponse(call: Call<List<MenuDetailWithCommentResponse>>, response: Response<List<MenuDetailWithCommentResponse>>) {
                val res = response.body()
                if(response.code() == 200){
                    if (res != null) {
                        callback.onSuccess(response.code(), res)
                    }
                } else {
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<List<MenuDetailWithCommentResponse>>, t: Throwable) {
                callback.onError(t)
            }
        })
    }

    fun getBestProduct() : LiveData<List<BestProductResponse>> {
        val responseLiveData : MutableLiveData<List<BestProductResponse>> = MutableLiveData()
        val bestProductRequest: Call<List<BestProductResponse>> = RetrofitUtil.productService.getBestProduct5()
        
        bestProductRequest.enqueue(object : Callback<List<BestProductResponse>>{
            override fun onResponse(
                call: Call<List<BestProductResponse>>,
                response: Response<List<BestProductResponse>>
            ) {
                val res = response.body()
                if(response.code() == 200){
                    if(res != null){
                        responseLiveData.value = res
                    }
                }else{
                    Log.d(TAG, "onResponse: ")
                }
            }

            override fun onFailure(call: Call<List<BestProductResponse>>, t: Throwable) {
                Log.d(TAG, "onFailure: ")
            }

        })

        return responseLiveData
    }
    fun getProductById(id:Int, callback: RetrofitCallback<Product>){
        RetrofitUtil.productService.getProductById(id).enqueue(object : Callback<Product>{
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                var res = response.body()
                if(response.code() == 200){
                    if(res != null){
                        callback.onSuccess(response.code(),res)
                    }
                }
                else{
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                callback.onError(t)
            }

        })
    }

    fun selectByName(name:String, callback: RetrofitCallback<List<Product>>){
        RetrofitUtil.productService.selectByName(name).enqueue(object :Callback<List<Product>>{
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                var res = response.body()
                if(response.code() == 200){
                    if(res != null){
                        callback.onSuccess(response.code(), res)
                    }
                }else{
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                callback.onError(t)
            }

        })
    }

    fun getWeekBest() : LiveData<List<Product>> {
        val responseLiveData : MutableLiveData<List<Product>> = MutableLiveData()
        val weekBestProductRequest: Call<List<Product>> = RetrofitUtil.productService.getWeekBestProduct()

        weekBestProductRequest.enqueue(object : Callback<List<Product>>{
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                val res = response.body()
                if(response.code() == 200){
                    if(res != null){
                        responseLiveData.value = res
                    }
                }else{
                    Log.d(TAG, "onResponse: ")
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Log.d(TAG, "onFailure: ")
            }

        })
        return responseLiveData
    }
}
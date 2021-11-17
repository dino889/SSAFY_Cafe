package com.ssafy.cafe.service


import com.ssafy.cafe.dto.Product
import com.ssafy.cafe.fragment.AllMenuFragment
import com.ssafy.cafe.response.MenuDetailWithCommentResponse
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


}
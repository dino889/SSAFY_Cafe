package com.ssafy.cafe.api

import com.ssafy.cafe.dto.Product
import com.ssafy.cafe.response.BestProductResponse
import com.ssafy.cafe.response.MenuDetailWithCommentResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApi {
    // 전체 상품의 목록을 반환한다
    @GET("rest/product")
    fun getProductList(): Call<List<Product>>

    // {productId}에 해당하는 상품의 정보를 comment와 함께 반환한다.
    // comment 조회시 사용
    @GET("rest/product/{productId}")
    fun getProductWithComments(@Path("productId") productId: Int): Call<List<MenuDetailWithCommentResponse>>


    // {productType}에 해당하는 상품의 정보를 반환한다.
    // 상품 타입별 조회
    @GET("rest/product/type/{productType}")
    fun getProductWithType(@Path("productType") productType: String): Call<List<Product>>

    @GET("rest/product/bestProduct")
    fun getBestProduct5() : Call<List<BestProductResponse>>

    @GET("rest/product/prod")
    fun getProductById(@Query("productId")productId: Int): Call<Product>

    @GET("rest/product/search/{name}")
    fun selectByName(@Path("name") name:String) : Call<List<Product>>
}
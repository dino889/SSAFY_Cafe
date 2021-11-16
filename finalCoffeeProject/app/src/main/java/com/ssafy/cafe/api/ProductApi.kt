package com.ssafy.cafe.api

import com.ssafy.cafe.dto.Product
import com.ssafy.cafe.response.MenuDetailWithCommentResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

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
}
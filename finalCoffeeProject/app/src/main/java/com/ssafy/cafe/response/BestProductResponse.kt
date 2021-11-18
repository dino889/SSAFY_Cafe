package com.ssafy.cafe.response

import com.google.gson.annotations.SerializedName


data class BestProductResponse (
    @SerializedName("img") val img: String,
    @SerializedName("price") var price: Int,
    @SerializedName("product_id")val productId: Int,
    @SerializedName("d_id")val dId:Int,
    @SerializedName("cnt")val cnt: Int,
    @SerializedName("name")val name: String,
    @SerializedName("type")val type:Int,
)
package com.ssafy.cafe.src.main.network.response

import com.google.gson.annotations.SerializedName
import java.util.*

// o_id 기준으로 분류하고, img는 그 중 하나로 사용하기
data class LatestOrderResponse(
    @SerializedName("img") val img: String,
    @SerializedName("quantity") var orderCnt: Int,
    @SerializedName("user_id") val userId: String,
    @SerializedName("o_id") val orderId: Int,
    @SerializedName("name") val productName: String,
    @SerializedName("order_time") val orderDate: Date,
    @SerializedName("completed") var orderCompleted: Char = 'N',
    @SerializedName("price") val productPrice: Int,
    @SerializedName("type") val type: String,
    @SerializedName("total_price") val prodTotalPrice: Int,
    var totalPrice: Int = 0
)
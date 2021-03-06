package com.ssafy.cafe.src.main.network.response

import com.google.gson.annotations.SerializedName
import java.util.*

data class OrderDetailResponse(
    @SerializedName("o_id") val orderId: Int,
    @SerializedName("order_table") val orderTable: String,
    @SerializedName("order_time") val orderDate: Date,
    @SerializedName("completed") val orderCompleted: Char='N',
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("p_id") val productId: Int,
    @SerializedName("name") val productName: String,
    @SerializedName("unitprice") val unitPrice: Int,
    @SerializedName("img") val img: String,
    @SerializedName("stamp") val stampCount: Int,
    @SerializedName("totalprice") val totalPrice: Int,
    @SerializedName("type") val productType: Int,   // hot Ice
    @SerializedName("syrup") val syrup: String,
    @SerializedName("shot") val shot: Int,
    @SerializedName("total_price") val prodTotalPrice: Int,
)
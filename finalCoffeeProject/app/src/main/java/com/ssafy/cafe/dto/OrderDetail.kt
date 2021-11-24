package com.ssafy.cafe.dto

data class OrderDetail(
    val id: Int,
    val orderId: Int,
    val productId: Int,
    val quantity: Int,
    val type: Int,
    val syrup: String?,
    val shot: Int?,
    var totalPrice : Int
)
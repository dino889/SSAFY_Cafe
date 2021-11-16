package com.ssafy.cafe.dto

data class OrderDetail(
    val id: Int,
    val orderId: Int,
    val productId: Int,
    val quantity: Int,
    val shot: Int,
    val syrup: String,
    val type: Int
)
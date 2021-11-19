package com.ssafy.cafe.dto

data class Order(
    val id: Int,
    val userId: String,
    var orderTable: String,
    val orderTime: String,
    val completed: Int,
    val details: List<OrderDetail>
)
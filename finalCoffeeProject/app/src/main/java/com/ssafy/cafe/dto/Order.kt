package com.ssafy.cafe.dto

data class Order(
    val completed: Int,
    val details: List<OrderDetail>,
    val id: Int,
    val orderTable: String,
    val orderTime: String,
    val userId: String
)
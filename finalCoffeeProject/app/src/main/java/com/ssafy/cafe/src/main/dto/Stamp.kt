package com.ssafy.cafe.src.main.dto

data class Stamp (
    val id: Int,
    val userId: String,
    val orderId: Int,
    val quantity: Int,
    val used: Char
)

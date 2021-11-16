package com.ssafy.cafe.dto

data class Notification(
    val category: String,
    val content: String,
    val id: Int,
    val userId: String
)
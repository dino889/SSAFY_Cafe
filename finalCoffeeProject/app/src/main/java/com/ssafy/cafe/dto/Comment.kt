package com.ssafy.cafe.dto

data class Comment(
    val comment: String,
    val id: Int,
    val productId: Int,
    val rating: Int,
    val userId: String
)
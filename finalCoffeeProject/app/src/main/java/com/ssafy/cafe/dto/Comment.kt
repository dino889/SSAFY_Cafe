package com.ssafy.cafe.dto

data class Comment(
    var comment: String,
    val id: Int,
    val productId: Int,
    val rating: Float,
    val userId: String
)
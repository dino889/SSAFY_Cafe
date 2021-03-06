package com.ssafy.cafe.src.main.dto

import java.util.*

data class Comment(
    var comment: String,
    val id: Int,
    val productId: Int,
    val rating: Float,
    val date: Date?,
    val userId: String
)
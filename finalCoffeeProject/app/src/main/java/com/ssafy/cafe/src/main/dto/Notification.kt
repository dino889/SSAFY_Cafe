package com.ssafy.cafe.src.main.dto

import java.util.*

data class Notification(
    val category: String,
    val content: String,
    val id: Int,
    val userId: String,
    val date: Date,
)
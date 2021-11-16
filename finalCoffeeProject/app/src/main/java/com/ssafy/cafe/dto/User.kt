package com.ssafy.cafe.dto

data class User(
    val id: String,
    val money: Int,
    val name: String,
    val pass: String,
    val phone: String,
    val stampList: List<Any>,
    val stamps: Int
)
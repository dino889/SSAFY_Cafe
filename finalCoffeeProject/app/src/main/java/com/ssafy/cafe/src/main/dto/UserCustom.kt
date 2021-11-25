package com.ssafy.cafe.src.main.dto

data class UserCustom(
    val id: Int,
    val productId: Int,
    val shot: Int,
    val syrup: String,
    val type: Int,
    val userId: String,

){
    var isChecked:Boolean = false
}
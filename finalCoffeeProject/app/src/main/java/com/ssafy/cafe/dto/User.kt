package com.ssafy.cafe.dto

data class User(
    val id: String,
    val name: String,
    val pass: String,
    val stamps: Int,
    val phone: String,
    val money: Int,
    val stampList: ArrayList<Stamp> = java.util.ArrayList()
){
    constructor(): this("", "", "", 0, "", 0)
    constructor(id: String, name: String, pass: String, phone: String, money: Int):this(id, name, pass, 0, phone, money)
    constructor(id:String, pass:String):this(id, "" , pass, 0, "", 0)
}
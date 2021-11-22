package com.ssafy.cafe.dto

data class User(
    val id: String,
    val name: String,
    val pass: String,
    var phone: String,
    val stamps: Int,
    val money: Int,
    val token: String,
    val stampList: ArrayList<Stamp> = java.util.ArrayList()
){
    constructor(): this("", "", "", "", 0, 0, "")
    constructor(id: String, name: String, pass: String, phone: String, money: Int, token: String):this(id, name, pass, phone, 0, money, token)
    constructor(id:String, passOrToken:String):this(id, "" , passOrToken, "", 0, 0, passOrToken)
    constructor(id:String,name:String,tel:String,pw:String) : this(id,name,pw,tel, 0,0, "")
    constructor(id:String, money: Int) : this(id, "", "","", 0,money, "")
//    constructor(id:String, token: String) : this(id, "", "", "", 0, 0, token)

}
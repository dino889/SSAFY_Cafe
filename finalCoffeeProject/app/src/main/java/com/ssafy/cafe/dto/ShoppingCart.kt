package com.ssafy.cafe.dto

data class ShoppingCart(
    val menuId: Int,
    val menuImg: String,
    val menuName: String,
    var menuCnt: Int,
    val menuPrice: Int,
    var totalPrice: Int = menuCnt*menuPrice,
    val productType: String,
    var type:Int,
    var syrup:String?,
    var shot:Int?,
){
    fun addDupMenu(shoppingCart: ShoppingCart){
        this.menuCnt += shoppingCart.menuCnt
        this.totalPrice = this.menuCnt * this.menuPrice
    }
    var id = 0
    constructor():this(0, "", "", 0, 0,0, "", 1, null, null){
        id = menuId
    }

}
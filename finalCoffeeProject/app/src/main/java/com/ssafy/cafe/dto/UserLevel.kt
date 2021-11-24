package com.ssafy.cafe.dto

data class UserLevel(
    var title: String,
    var max: Int,
    var point: Double,
){
    companion object{
        var userInfoList = arrayOf(
            UserLevel("Bronze", 10,0.01),
            UserLevel("Silver", 30,0.1),
            UserLevel("Gold", 50,5.0),
            UserLevel("Platinum", 100,10.0),
            UserLevel("Diamond", 500,15.0)
        )
    }
}
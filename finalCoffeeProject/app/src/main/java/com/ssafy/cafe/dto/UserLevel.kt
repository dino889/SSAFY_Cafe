package com.ssafy.cafe.dto

data class UserLevel(
    var title: String,
    var max: Int,
    var point: Double,
){
    companion object{
        var userInfoList = arrayOf(
            UserLevel("Bronze Level", 10,0.01),
            UserLevel("Silver Level", 30,0.1),
            UserLevel("Gold Level", 50,5.0),
            UserLevel("Platinum Level", 100,10.0),
            UserLevel("Diamond Level", 500,15.0)
        )
    }
}
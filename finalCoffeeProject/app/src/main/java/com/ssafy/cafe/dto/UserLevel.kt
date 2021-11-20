package com.ssafy.cafe.dto

data class UserLevel(
    var title: String,
    var max: Int,
    var point: Double,
){
    companion object{
        val levelTitleArr = arrayOf("Bronze Level", "Silver Level", "Gold Level", "Platinum Level", "Diamond Level")

        var userInfoList = arrayOf(
            UserLevel(levelTitleArr[0], 10,0.01),
            UserLevel(levelTitleArr[1], 30,0.1),
            UserLevel(levelTitleArr[2], 50,5.0),
            UserLevel(levelTitleArr[3], 100,10.0),
            UserLevel(levelTitleArr[4], Int.MAX_VALUE,15.0)
        )
    }
}
package com.ssafy.cafe.dto

data class UserLevel(
    var title: String,
    var max: Int,
){
    companion object{
        val levelTitleArr = arrayOf("Bronze", "Silver", "Gold", "Platinum", "Diamond")

        var userInfoList = arrayOf(
            UserLevel(levelTitleArr[0], 10),
            UserLevel(levelTitleArr[1], 30),
            UserLevel(levelTitleArr[2], 50),
            UserLevel(levelTitleArr[3], 100),
            UserLevel(levelTitleArr[4], Int.MAX_VALUE)
        )
    }
}
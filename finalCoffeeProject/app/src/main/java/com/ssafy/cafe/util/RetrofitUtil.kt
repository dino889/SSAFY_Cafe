package com.ssafy.cafe.util


import com.ssafy.cafe.api.*
import com.ssafy.cafe.config.ApplicationClass

class RetrofitUtil {
    companion object{
        val commentService = ApplicationClass.retrofit.create(CommentApi::class.java)
        val orderService = ApplicationClass.retrofit.create(OrderApi::class.java)
        val productService = ApplicationClass.retrofit.create(ProductApi::class.java)
        val userService = ApplicationClass.retrofit.create(UserApi::class.java)
        val customService = ApplicationClass.retrofit.create(UserCustomApi::class.java)
        val notificationService = ApplicationClass.retrofit.create(NotificationApi::class.java)

    }
}
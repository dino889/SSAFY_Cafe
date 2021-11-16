package com.ssafy.cafe.util


import com.ssafy.cafe.api.CommentApi
import com.ssafy.cafe.api.OrderApi
import com.ssafy.cafe.api.ProductApi
import com.ssafy.cafe.api.UserApi
import com.ssafy.cafe.config.ApplicationClass

class RetrofitUtil {
    companion object{
        val commentService = ApplicationClass.retrofit.create(CommentApi::class.java)
        val orderService = ApplicationClass.retrofit.create(OrderApi::class.java)
        val productService = ApplicationClass.retrofit.create(ProductApi::class.java)
        val userService = ApplicationClass.retrofit.create(UserApi::class.java)
    }
}
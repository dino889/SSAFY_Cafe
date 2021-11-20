package com.ssafy.cafe.api

import com.ssafy.cafe.dto.Notification
import com.ssafy.cafe.dto.UserCustom
import retrofit2.Call
import retrofit2.http.*

interface NotificationApi {

    // Notification을 추가한다.
    @POST("rest/notification")
    fun insert(@Body notification : Notification): Call<Boolean>

    // Notification을 삭제한다.
    @DELETE("rest/notification/{id}")
    fun delete(@Path("id") id : Int) : Call<Boolean>

    // 사용자별 Notification 정보를 조회한다.
    @GET("rest/notification/{userId}")
    fun gutUserWithNotifications(@Path("userId") userId : String) : Call<List<Notification>>

    // 사용자별 Notification을 category별로 조회한다.
    @GET("rest/notification/{userId}/{category}")
    fun getUserWithNotificationsAndNoti(@Path("userId") userId : String, @Path("category") category : String) : Call<List<Notification>>

    @POST("rest/token")
    fun uploadToken(@Query("token")token : String):Call<String>

    @POST("/sendMessageTo")
    fun sendMessageTo(@Query("token")token:String, @Query("title")title:String, @Query("body")body:String)
}
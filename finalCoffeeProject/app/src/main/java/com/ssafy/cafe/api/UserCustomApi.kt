package com.ssafy.cafe.api

import com.ssafy.cafe.dto.UserCustom
import retrofit2.Call
import retrofit2.http.*

interface UserCustomApi {

    // User Custom Product 을 추가한다.
    @POST("rest/custom")
    fun insert(@Body userCustom: UserCustom): Call<Boolean>

    // User Custom Product을 삭제한다.
    @DELETE("rest/custom/{id}")
    fun delete(@Path("id") id : Int) : Call<Boolean>

    // 전체 User Custom Product을 조회한다.
    @GET("rest/custom")
    fun selectUserCustomList() : Call<List<UserCustom>>

    // id에 해당하는 Custom Product 정보를 조회한다.
    @GET("rest/custom/{customId}")
    fun selectUserCustom(@Path("customId") customId : Int) : Call<List<UserCustom>>

    // 사용자 id로 사용자별 Custom Product 정보를 조회한다.
    @GET("rest/custom/uc/{userId}")
    fun getCustomWithUserId(@Path("userId") userId : String) : Call<List<UserCustom>>

}
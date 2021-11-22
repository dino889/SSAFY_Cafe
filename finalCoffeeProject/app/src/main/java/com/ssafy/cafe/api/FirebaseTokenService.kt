package com.ssafy.cafe.api
import retrofit2.Call
import retrofit2.http.*

interface FirebaseTokenService {
    // Token정보 서버로 전송
    @POST("token")
    fun uploadToken(@Query("token") token: String): Call<String>

}
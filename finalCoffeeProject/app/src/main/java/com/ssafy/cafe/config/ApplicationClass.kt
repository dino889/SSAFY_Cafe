package com.ssafy.cafe.config

import android.Manifest
import android.app.Application
import com.ssafy.cafe.intercepter.AddCookiesInterceptor
import com.ssafy.cafe.intercepter.ReceivedCookiesInterceptor
import com.ssafy.cafe.util.SharedPreferencesUtil
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApplicationClass : Application() {
    companion object{
        //boyeon serverurl
//        const val SERVER_URL = "http://1.251.96.80:8888/"

        //jiwoo serverurl
        const val SERVER_URL = "http://172.30.1.36:8888/"

        const val MENU_IMGS_URL = "${SERVER_URL}imgs/menu/"
//        const val IMGS_URL = "${SERVER_URL}imgs/"

        lateinit var sharedPreferencesUtil: SharedPreferencesUtil
        lateinit var retrofit: Retrofit

        // 모든 퍼미션 관련 배열
        val requiredPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
        )

        // 주문 준비 완료 확인 시간 1분
        const val ORDER_COMPLETED_TIME = 60*1000

    }


    override fun onCreate() {
        super.onCreate()

        //shared preference 초기화
        sharedPreferencesUtil = SharedPreferencesUtil(applicationContext)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AddCookiesInterceptor())
            .addInterceptor(ReceivedCookiesInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS).build()

        // 앱이 처음 생성되는 순간, retrofit 인스턴스를 생성
        retrofit = Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

}
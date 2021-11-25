package com.ssafy.cafe.src.main.network.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ssafy.cafe.src.main.dto.Notification
import com.ssafy.cafe.util.RetrofitCallback
import com.ssafy.cafe.util.RetrofitUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "NotificationService"
class NotificationService {
    fun getUserWithNotification(userid:String): LiveData<List<Notification>>{
        val responseLiveData : MutableLiveData<List<Notification>> = MutableLiveData()
        val notificationRequest: Call<List<Notification>> = RetrofitUtil.notificationService.getUserWithNotifications(userid)

        notificationRequest.enqueue(object : Callback<List<Notification>> {
            override fun onResponse(
                call: Call<List<Notification>>,
                response: Response<List<Notification>>
            ) {
                val res = response.body()
                if(response.code() == 200){
                    if(res!=null){
                        responseLiveData.value = res
                    }
                }else{
                    Log.d(TAG, "onResponse: ")
                }
            }

            override fun onFailure(call: Call<List<Notification>>, t: Throwable) {
                Log.d(TAG, "onFailure: ")
            }
        })

        return responseLiveData
    }

    fun deleteNoti(id:Int, callback: RetrofitCallback<Boolean>){
        RetrofitUtil.notificationService.delete(id).enqueue(object: Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val res = response.body()
                if(response.code() == 200){
                    if(res!=null){
                        callback.onSuccess(response.code(), res)
                    }
                }else{
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                callback.onError(t)
            }

        })

    }
}
package com.ssafy.cafe.src.main.network.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ssafy.cafe.src.main.dto.UserCustom
import com.ssafy.cafe.util.RetrofitCallback
import com.ssafy.cafe.util.RetrofitUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserCustomService {
    private val TAG = "UserCustomService_싸피"
    fun insertCustomMenu(userCustom: UserCustom, callback: RetrofitCallback<Boolean>){
        RetrofitUtil.customService.insert(userCustom).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val res = response.body()
                if(response.code() == 200){
                    if(res == true){
                        callback.onSuccess(response.code(), res)
                    }else{
                        callback.onFailure(response.code())
                    }
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                callback.onError(t)
            }
        })
    }

    fun getCustomWithUserId(userId:String) : LiveData<List<UserCustom>> {
        val responseLiveData : MutableLiveData<List<UserCustom>> = MutableLiveData()
        val getUserCustomRequest: Call<List<UserCustom>> = RetrofitUtil.customService.getCustomWithUserId(userId)

            getUserCustomRequest.enqueue(object : Callback<List<UserCustom>>{
                override fun onResponse(
                    call: Call<List<UserCustom>>,
                    response: Response<List<UserCustom>>
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

                override fun onFailure(call: Call<List<UserCustom>>, t: Throwable) {
                    Log.d(TAG, "onFailure: ")
                }
            })

        return responseLiveData
    }

    fun deleteCustomMenu(customId:Int, callback:RetrofitCallback<Boolean>) {
        RetrofitUtil.customService.delete(customId).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val res = response.body()
                if(response.code() == 200){
                    if(res != null){
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
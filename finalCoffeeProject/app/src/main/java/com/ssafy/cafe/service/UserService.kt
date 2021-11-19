package com.ssafy.cafe.service

import android.util.Log
import com.ssafy.cafe.dto.Grade
import com.ssafy.cafe.dto.User
import com.ssafy.cafe.util.RetrofitCallback
import com.ssafy.cafe.util.RetrofitUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

private const val TAG = "UserService"
class UserService {
    fun login(user: User, callback:RetrofitCallback<User>) {
        RetrofitUtil.userService.login(user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                val res = response.body()
                if(response.code() == 200){
                    if(res != null){
                        callback.onSuccess(response.code() , res)
                    }
                }else{
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                callback.onError(t)
            }
        })
    }

    fun join(user:User, callback: RetrofitCallback<Boolean>) {
        RetrofitUtil.userService.insert(user).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val data = response.body()
                if(response.code() == 200) {
                    if(data == true) {
                        callback.onSuccess(response.code(), data)
                    } else {
                        callback.onFailure(response.code())
                    }
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                callback.onError(t)
            }
        })
    }

    fun isUsed(id: String, callback: RetrofitCallback<Boolean>) {
        RetrofitUtil.userService.isUsedId(id).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val res = response.body()
                if(response.code() == 200) {
                    if (res != null) {
                        callback.onSuccess(response.code(), res)
                    }
                } else {
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                callback.onError(t)
            }
        })
    }

    fun getUsers(id:String, callback: RetrofitCallback<HashMap<String, Any>>){
        RetrofitUtil.userService.getInfo(id).enqueue(object : Callback<HashMap<String, Any>>{
            override fun onResponse(
                call: Call<HashMap<String, Any>>,
                response: Response<HashMap<String, Any>>
            ) {
                val data = response.body()
                if(response.code() == 200){
                    if(data!=null){
                       // Log.d(TAG, "onResponse: ${data!!["order"]}")
                        callback.onSuccess(response.code() , data)
                    }else{
                        callback.onFailure(response.code())
                    }
                }


            }

            override fun onFailure(call: Call<HashMap<String, Any>>, t: Throwable) {
                callback.onError(t)
            }

        })
    }
}
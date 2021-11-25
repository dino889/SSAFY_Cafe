package com.ssafy.cafe.src.main.network.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ssafy.cafe.src.main.dto.User
import com.ssafy.cafe.util.RetrofitCallback
import com.ssafy.cafe.util.RetrofitUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    fun join(user: User, callback: RetrofitCallback<Boolean>) {
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

    fun getUsers(id:String) : LiveData<HashMap<String, Any>> {
        val responseLiveData : MutableLiveData<HashMap<String, Any>> = MutableLiveData()
        val userInfoRequest: Call<HashMap<String, Any>> = RetrofitUtil.userService.getInfo(id)

        userInfoRequest.enqueue(object : Callback<HashMap<String, Any>> {
            override fun onResponse(
                call: Call<HashMap<String, Any>>,
                response: Response<HashMap<String, Any>>
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

            override fun onFailure(call: Call<HashMap<String, Any>>, t: Throwable) {
                Log.d(TAG, "onFailure: ")
            }

        })
        return responseLiveData
    }

    fun updateMoney(user: User, callback: RetrofitCallback<User>) {
        RetrofitUtil.userService.update(user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                val res = response.body()
                if (response.code() == 200) {
                    if (res != null) {
                        callback.onSuccess(response.code(), res)
                    }
                } else {
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                callback.onError(t)
            }
        })
    }

}
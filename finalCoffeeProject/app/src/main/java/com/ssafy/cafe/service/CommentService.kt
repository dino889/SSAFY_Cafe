package com.ssafy.cafe.service

import android.util.Log
import com.ssafy.cafe.dto.Comment
import com.ssafy.cafe.util.RetrofitCallback
import com.ssafy.cafe.util.RetrofitUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "CommentService"
class CommentService {
    fun insert(comment: Comment, callback: RetrofitCallback<Boolean>) {
        RetrofitUtil.commentService.insert(comment).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val res = response.body()
                if (response.code() == 200) {
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

    fun delete(id: Int, callback: RetrofitCallback<Boolean>) {
        RetrofitUtil.commentService.delete(id).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val res = response.body()
                if (response.code() == 200) {
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

    fun modify(comment: Comment, callback: RetrofitCallback<Boolean>) {
        RetrofitUtil.commentService.update(comment).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val res = response.body()
                if (response.code() == 200) {
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

    fun selectCommentByUser(id: String, callback: RetrofitCallback<List<Comment>>){
        RetrofitUtil.commentService.selectCommentByUser(id).enqueue(object: Callback<List<Comment>>{
            override fun onResponse(call: Call<List<Comment>>, response: Response<List<Comment>>) {
                val res = response.body()
                if(response.code() == 200){
                    if (res != null) {
                        callback.onSuccess(response.code(), res)
                    }
                }else {
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<List<Comment>>, t: Throwable) {
                callback.onError(t)
            }

        })
    }

    fun notWrittenComm(userId : String, productId: Int, callback: RetrofitCallback<Int>) {
        RetrofitUtil.commentService.notWrittenComm(userId, productId).enqueue(object: Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                val res = response.body()
                if(response.code() == 200){
                    if (res != null) {
                        callback.onSuccess(response.code(), res)
                    }
                }else {
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                callback.onError(t)
            }

        })
    }


    fun updateDupChk(dId: Int, callback: RetrofitCallback<Boolean>) {
        RetrofitUtil.commentService.updateDupChk(dId).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val res = response.body()
                if(response.code() == 200){
                    if (res != null) {
                        callback.onSuccess(response.code(), res)
                    }
                }else {
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                callback.onError(t)
            }

        })
    }




}
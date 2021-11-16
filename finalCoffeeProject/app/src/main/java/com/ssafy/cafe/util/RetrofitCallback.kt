package com.ssafy.cafe.util

interface RetrofitCallback<T> {
    fun onError(t: Throwable)

    fun onSuccess(code: Int, responseData: T)

    fun onFailure(code: Int)
}
package com.ssafy.cafe.util

import android.content.Context
import android.content.SharedPreferences
import com.ssafy.cafe.dto.User

class SharedPreferencesUtil (context: Context) {
    val SHARED_PREFERENCES_NAME = "smartstore_preference"
    val COOKIES_KEY_NAME = "cookies"

    var preferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    //사용자 정보 저장
    fun addUser(user: User){
        val editor = preferences.edit()
        editor.putString("id", user.id)
        editor.putString("name", user.name)
        editor.apply()
    }

    fun getUser(): User{
        val id = preferences.getString("id", "")
        if (id != ""){
            val name = preferences.getString("name", "")
            return User(id!!, name!!, "","",0)
        }else{
            return User()
        }
    }

    fun deleteUser(){
        //preference 지우기
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }

    fun addUserCookie(cookies: HashSet<String>) {
        val editor = preferences.edit()
        editor.putStringSet(COOKIES_KEY_NAME, cookies)
        editor.apply()
    }

    fun getUserCookie(): MutableSet<String>? {
        return preferences.getStringSet(COOKIES_KEY_NAME, HashSet())
    }

    fun deleteUserCookie() {
        preferences.edit().remove(COOKIES_KEY_NAME).apply()
    }


    //사용자 정보 저장
    fun addOrderTable(orderTable:String){
        val editor = preferences.edit()
        editor.putString("ot", orderTable)
        editor.apply()
    }

    fun getOrderTable(): String? {
        val orderTable = preferences.getString("ot", "")
        return orderTable
    }

    fun deleteOrderTable(){
        preferences.edit().remove("ot").apply()
    }

    //사용자 잔액 저장
    fun addUserPay(pay:Int){
        val editor = preferences.edit()
        editor.putInt("pay", pay)
        editor.apply()
    }
    fun getUserPay() : Int? {
        val pay = preferences.getInt("pay",0)
        return pay
    }

}
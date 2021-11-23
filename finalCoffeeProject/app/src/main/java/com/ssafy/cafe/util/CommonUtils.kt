package com.ssafy.cafe.util

import android.icu.text.StringPrepParseException
import android.util.Log
import android.widget.TextView
import com.ssafy.cafe.R
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.response.LatestOrderResponse
import com.ssafy.cafe.response.OrderDetailResponse
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object CommonUtils {

    //천단위 콤마
    fun makeComma(num: Int): String {
        var comma = DecimalFormat("#,###")
        return "${comma.format(num)} 원"
    }

    fun getFormattedString(date:Date): String {
        val dateFormat = SimpleDateFormat("yyyy.MM.dd HH시 mm분 ss초")
        dateFormat.timeZone = TimeZone.getTimeZone("Seoul/Asia")

        return dateFormat.format(date)
    }

    fun getFormattedStringDate(date:Date): String {
        val dateFormat = SimpleDateFormat("yyyy.MM.dd")
        Log.d("싸피", "getFormattedStringDate: $dateFormat")
        dateFormat.timeZone = TimeZone.getTimeZone("Seoul/Asia")

        return dateFormat.format(date)
    }

    // 시간 계산을 통해 완성된 제품인지 확인
    fun isOrderCompleted(orderDetail: OrderDetailResponse): String {
        return if( checkTime(orderDetail.orderDate.time))  "주문완료" else "진행 중.."
    }

    // 시간 계산을 통해 완성된 제품인지 확인
    fun isOrderCompleted(order: LatestOrderResponse): String {
        return if( checkTime(order.orderDate.time))  "주문완료" else "진행 중.."
    }
    fun dialogProductComent(pName: String) : String {
        return "주문하신 ${pName} 어떠셨나요?"
    }
    private fun checkTime(time:Long):Boolean{
        val curTime = (Date().time+60*60*9*1000)

        return (curTime - time) > ApplicationClass.ORDER_COMPLETED_TIME
    }
    fun convertOptionMenu(type:Int?, syrup:String?, shot:Int?) : String{
        val types = if(type == 0){
            "HOT"
        }else{
            "ICE"
        }

        val syrups = if(syrup == null || syrup.equals("null")){
            "없음"
        }else if(syrup.equals("설탕")){
            "${syrup}(+0원)"
        }else{
            "${syrup}(+500원)"
        }

        val shots = if(shot == 0 || shot.toString().equals("null") || shot == null){
            "없음"
        }else{
            "${shot}개 추가"
        }

        return "$types | $syrups | 샷(+500원) $shots"
    }
}
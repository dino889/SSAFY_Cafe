package com.ssafy.cafe.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.cafe.R
import com.ssafy.cafe.activity.MainActivity
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.dto.Notification
import com.ssafy.cafe.service.NotificationService
import com.ssafy.cafe.util.CommonUtils
import com.ssafy.cafe.util.RetrofitCallback
import kotlin.reflect.KFunction1

private const val TAG = "NotificationAdapter"
class NotificationAdapter(val list:List<Notification>, val kFunction1:() -> Unit) :
    RecyclerView.Adapter<NotificationAdapter.NotiHolder>(){

    lateinit var context: MainActivity

    inner class NotiHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val notiCotent = itemView.findViewById<TextView>(R.id.tv_notificationContent)
        val notiDate = itemView.findViewById<TextView>(R.id.tv_notificatonDate)
        val deleteBtn = itemView.findViewById<ImageButton>(R.id.ibtn_delete)
        fun bindInfo(notification: Notification){
            notiCotent.text = notification.content
            notiDate.text =  CommonUtils.getFormattedString(notification.date)

            deleteBtn.setOnClickListener {
                NotificationService().deleteNoti(notification.id, DeleteCallback())
            }
        }
        inner class DeleteCallback: RetrofitCallback<Boolean> {
            override fun onError(t: Throwable) {
                Log.d(TAG, "onError: ")
            }

            override fun onSuccess(code: Int, responseData: Boolean) {
                if(responseData){
                    Toast.makeText(context, "삭제되었습니다.",Toast.LENGTH_SHORT).show()
                    kFunction1()
                }
            }

            override fun onFailure(code: Int) {
                Log.d(TAG, "onFailure: ")
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotiHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_notification_list_item,parent,false)
        return NotiHolder(view)
    }

    override fun onBindViewHolder(holder: NotiHolder, position: Int) {
        holder.bindInfo(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

}
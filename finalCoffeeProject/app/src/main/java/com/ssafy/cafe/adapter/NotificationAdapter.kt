package com.ssafy.cafe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.cafe.R
import com.ssafy.cafe.dto.Notification

class NotificationAdapter (val list:List<Notification>) : RecyclerView.Adapter<NotificationAdapter.NotiHolder>(){
    inner class NotiHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val notiCotent = itemView.findViewById<TextView>(R.id.tv_notificationContent)

        fun bindInfo(notification: Notification){
            notiCotent.text = notification.content
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
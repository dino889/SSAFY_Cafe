package com.ssafy.cafe.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.cafe.R
import com.ssafy.cafe.dto.OrderDetail
import com.ssafy.cafe.response.OrderDetailResponse
import com.ssafy.cafe.util.CommonUtils

class OrderDetailAdapter(val list:List<OrderDetailResponse>):RecyclerView.Adapter<OrderDetailAdapter.OrderHolder>() {
    inner class OrderHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val orderName = itemView.findViewById<TextView>(R.id.tv_orderMenuName)
        val orderPrice = itemView.findViewById<TextView>(R.id.tv_orderMenuUnitPrice)
        val orderOption = itemView.findViewById<TextView>(R.id.tv_orderOption)

        fun bindInfo(orderDetail : OrderDetailResponse){
            orderName.text = "${orderDetail.productName} ${orderDetail.quantity}개"
//            var unit = orderDetail.unitPrice
//            var totalPrice = orderDetail.unitPrice
//            if(orderDetail.shot >= 1) {
//                totalPrice *= orderDetail.shot * 500
//            }
//            if(orderDetail.syrup != "설탕" || orderDetail.syrup != null) {
//                totalPrice += 500
//            }
//            Log.d("지우", "bindInfo: ${orderDetail.shot} /// ${orderDetail.syrup}")
//            totalPrice = unit * orderDetail.quantity
            Log.d("test", "bindInfo: ${orderDetail.prodTotalPrice}")
            orderPrice.text = CommonUtils.makeComma(orderDetail.prodTotalPrice)
            orderOption.text = CommonUtils.convertOptionMenu(orderDetail.productType,orderDetail.syrup, orderDetail.shot)
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderDetailAdapter.OrderHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_order_detail_list_item,parent,false)
        return OrderHolder(view)
    }

    override fun onBindViewHolder(holder: OrderDetailAdapter.OrderHolder, position: Int) {
        holder.bindInfo(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

}
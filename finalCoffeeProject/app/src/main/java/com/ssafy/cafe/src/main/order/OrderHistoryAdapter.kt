package com.ssafy.cafe.src.main.order

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.cafe.R
import com.ssafy.cafe.src.main.network.response.LatestOrderResponse
import com.ssafy.cafe.util.CommonUtils

class OrderHistoryAdapter(val list:List<LatestOrderResponse>) : RecyclerView.Adapter<OrderHistoryAdapter.HistoryListHolder>(){

    inner class HistoryListHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bindInfo(data: LatestOrderResponse){
            val name = if(data.orderCnt == 1) {
                data.productName
            } else {
                if(data.productName.length <=5){
                    "${data.productName} 외 ${data.orderCnt - 1}잔"
                }else{
                    "${data.productName}외 ..."
                }
            }
            itemView.findViewById<TextView>(R.id.tv_orderDetailMenu).text = name
            itemView.findViewById<TextView>(R.id.tv_orderDetailDate).text = CommonUtils.getFormattedString(data.orderDate)

            itemView.setOnClickListener{
                itemClickListner.onClick(it, layoutPosition, list[layoutPosition].orderId)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclervie_order_history_list_item,parent,false)
        return HistoryListHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryListHolder, position: Int) {
        holder.bindInfo(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
    interface ItemCancelListener {
        fun onClick(position: Int)
    }

    //클릭 인터페이스 정의 사용하는 곳에서 만들어준다.
    interface ItemClickListener {
        fun onClick(view: View,  position: Int, orderId:Int)
    }
    //클릭리스너 선언
    private lateinit var itemClickListner: ItemClickListener
    //클릭리스너 등록 매소드
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }
}
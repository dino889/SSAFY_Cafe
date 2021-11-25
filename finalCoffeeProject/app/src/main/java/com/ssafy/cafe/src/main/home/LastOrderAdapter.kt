package com.ssafy.cafe.src.main.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.cafe.R
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.src.main.network.response.LatestOrderResponse

class LastOrderAdapter : RecyclerView.Adapter<LastOrderAdapter.LatestOrderHolder>(){
    var list = mutableListOf<LatestOrderResponse>()

    inner class LatestOrderHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bindInfo(data: LatestOrderResponse){
            Glide.with(itemView)
                .load("${ApplicationClass.MENU_IMGS_URL}${data.img}")
                .into(itemView.findViewById(R.id.ib_cafeImg))
            val name = if(data.orderCnt == 1) {
                data.productName
            } else {
                if(data.productName.length <=5){
                    "${data.productName} 외 ${data.orderCnt - 1}잔"
                }else{
                    "${data.productName}외 ..."
                }
            }
            itemView.findViewById<TextView>(R.id.tv_listItemCafeMenuName).text = name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LatestOrderHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_menu_list_item, parent, false)
        return LatestOrderHolder(view)
    }

    override fun onBindViewHolder(holder: LatestOrderHolder, position: Int) {
//        holder.bind()
        holder.apply {
            bindInfo(list[position])
            //클릭연결
            itemView.setOnClickListener{
                itemClickListner.onClick(it, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    //클릭 인터페이스 정의 사용하는 곳에서 만들어준다.
    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }
    //클릭리스너 선언
    private lateinit var itemClickListner: ItemClickListener
    //클릭리스너 등록 매소드
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }

}

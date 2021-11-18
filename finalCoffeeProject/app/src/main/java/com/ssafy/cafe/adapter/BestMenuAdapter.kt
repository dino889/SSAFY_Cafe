package com.ssafy.cafe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.cafe.R
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.response.BestProductResponse

class BestMenuAdapter : RecyclerView.Adapter<BestMenuAdapter.BestMenuHolder>(){
    var list = mutableListOf<BestProductResponse>()
    inner class BestMenuHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bindInfo(data: BestProductResponse){
            Glide.with(itemView)
                .load("${ApplicationClass.MENU_IMGS_URL}${data.img}")
                .into(itemView.findViewById(R.id.ib_cafeImg))
            itemView.findViewById<TextView>(R.id.tv_listItemCafeMenuName).text = data.name
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BestMenuAdapter.BestMenuHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_menu_list_item,parent,false)
        return BestMenuHolder(view)
    }

    override fun onBindViewHolder(holder: BestMenuAdapter.BestMenuHolder, position: Int) {
        holder.apply {
            bindInfo(list[position])
            itemView.setOnClickListener{
                itemClickListner.onClick(it,position)
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
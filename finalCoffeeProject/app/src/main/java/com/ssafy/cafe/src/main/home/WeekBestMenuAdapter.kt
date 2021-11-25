package com.ssafy.cafe.src.main.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.cafe.R
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.src.main.dto.Product

class WeekBestMenuAdapter : RecyclerView.Adapter<WeekBestMenuAdapter.MenuHolder>(){
    var list = mutableListOf<Product>()

    inner class MenuHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bindInfo(product : Product){
            Glide.with(itemView)
                .load("${ApplicationClass.MENU_IMGS_URL}${product.img}")
                .into(itemView.findViewById(R.id.ib_cafeImg))

            itemView.findViewById<TextView>(R.id.tv_listItemCafeMenuName).text = product.name

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_menu_list_item, parent, false)
        return MenuHolder(view)
    }

    override fun onBindViewHolder(holder: MenuHolder, position: Int) {
        holder.apply{
            bindInfo(list[position])
            itemView.setOnClickListener{
                itemClickListner.onClick(it, position, list[position].id)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    //클릭 인터페이스 정의 사용하는 곳에서 만들어준다.
    interface ItemClickListener {
        fun onClick(view: View, position: Int, productId: Int)
    }
    //클릭리스너 선언
    private lateinit var itemClickListner: ItemClickListener
    //클릭리스너 등록 매소드
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }
}

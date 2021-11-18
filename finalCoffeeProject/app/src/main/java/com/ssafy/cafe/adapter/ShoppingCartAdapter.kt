package com.ssafy.cafe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.cafe.R
import com.ssafy.cafe.dto.ShoppingCart
import com.ssafy.cafe.util.CommonUtils

class ShoppingCartAdapter : RecyclerView.Adapter<ShoppingCartAdapter.ShoppingListHolder>(){

    var list = mutableListOf<ShoppingCart>()
    lateinit var cancelListener: ItemCancelListener

    inner class ShoppingListHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        init {
            itemView.findViewById<ImageView>(R.id.ib_cancleBtn).setOnClickListener{
                cancelListener.onClick(bindingAdapterPosition)
                notifyDataSetChanged()
            }
        }
        fun bindInfo(data: ShoppingCart){
            itemView.findViewById<TextView>(R.id.tv_menuName).text = data.menuName
            itemView.findViewById<TextView>(R.id.tv_cafeMenuCnt).text = "${data.menuCnt}개"
            itemView.findViewById<TextView>(R.id.tv_menuPrice).text = "${CommonUtils.makeComma(data.menuPrice)}"
            itemView.findViewById<TextView>(R.id.tv_menuTotalPrice).text = "${CommonUtils.makeComma(data.totalPrice)}"
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_shoppingcart_list_item, parent, false)
        return ShoppingListHolder(view)
    }

    override fun onBindViewHolder(holder: ShoppingListHolder, position: Int) {
        holder.bindInfo(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface ItemCancelListener {
        fun onClick(position: Int)
    }
}
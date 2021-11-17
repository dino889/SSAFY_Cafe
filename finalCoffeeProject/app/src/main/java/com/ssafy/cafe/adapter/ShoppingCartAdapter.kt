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

//class ShoppingCartAdapter : RecyclerView.Adapter<ShoppingCartAdapter.ShoppingListHolder>(){
//
//    var list = mutableListOf<ShoppingCart>()
//    lateinit var cancelListener: ItemCancelListener
//
//    inner class ShoppingListHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
//        init {
//            itemView.findViewById<ImageView>(R.id.).setOnClickListener{
//                cancelListener.onClick(bindingAdapterPosition)
//                notifyDataSetChanged()
//            }
//        }
//        fun bindInfo(data: ShoppingCart){
//
//            Glide.with(itemView)
//                .load("${ApplicationClass.MENU_IMGS_URL}${data.menuImg}")
//                .into(itemView.findViewById<ImageView>(R.id.imageShoppingMenu))
//            itemView.findViewById<TextView>(R.id.textShoppingMenuName).text = data.menuName
//            itemView.findViewById<TextView>(R.id.textShoppingMenuCount).text = "${data.menuCnt}잔"
//            itemView.findViewById<TextView>(R.id.textShoppingMenuMoney).text = "${data.menuPrice}원"
//            itemView.findViewById<TextView>(R.id.textShoppingMenuMoneyAll).text = "${data.totalPrice}원"
//        }
//
//        fun getImageId(name: String): Int {
//            return itemView.resources.getIdentifier(name, "drawable", "com.ssafy.smartstore")
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_shopping_list, parent, false)
//        return ShoppingListHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ShoppingListHolder, position: Int) {
//        holder.bindInfo(list[position])
//    }
//
//    override fun getItemCount(): Int {
//        return list.size
//    }
//
//    interface ItemCancelListener {
//        fun onClick(position: Int)
//    }
//}
package com.ssafy.cafe.adapter

import android.util.Log
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

private const val TAG = "ShoppingCartAdapter"
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

            Log.d(TAG, "bindInfo: ${data.shot} | ${data.syrup} ")

            val option = if(data.syrup == null || data.syrup.toString().equals("null")){
                    Log.d(TAG, "bindInfo: noSyrup")
                if(data.shot == 0){
                    "없음"
                }else{
                    "샷: ${data.shot}개추가"
                }
            }else if(data.syrup.equals("설탕")){
                Log.d(TAG, "bindInfo: this is sugar")

                if(data.shot == 0){
                    "시럽: ${data.syrup}(+0원) | 샷: 없음"
                }else{
                    "시럽: ${data.syrup}(+0원) | 샷: ${data.shot}개추가"
                }
            }else{
                ""
                if(data.shot == 0){
                    "시럽: ${data.syrup}(+500원) | 없음"
                }else{
                   "시럽: ${data.syrup}(+500원) | 샷: ${data.shot}개추가"
                }
            }
            itemView.findViewById<TextView>(R.id.tv_menuOption).text = option
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
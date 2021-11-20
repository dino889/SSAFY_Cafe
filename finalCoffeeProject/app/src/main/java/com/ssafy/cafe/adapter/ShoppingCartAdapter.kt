package com.ssafy.cafe.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
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
            
            itemView.findViewById<TextView>(R.id.tv_menuOption).text = CommonUtils.convertOptionMenu(data.type,data.syrup.toString(),data.shot!!.toInt())

            var tmp = itemView.findViewById<TextView>(R.id.tv_cafeMenuCnt).text.toString()
            var menuCnt = tmp[0].toString().toInt()
            
            var addBtn = itemView.findViewById<ImageButton>(R.id.ibtn_addCount)
            addBtn.setOnClickListener {
                Log.d(TAG, "bindInfo: addBtn")
                menuCnt++
                itemView.findViewById<TextView>(R.id.tv_cafeMenuCnt).text = "${menuCnt}개"
            }
            
            var minusBtn = itemView.findViewById<ImageButton>(R.id.ibtn_minusCount)
            minusBtn.setOnClickListener {
                Log.d(TAG, "bindInfo: minusBtn")
                if(menuCnt <= 0) {
                    menuCnt = 0
                    itemView.findViewById<TextView>(R.id.tv_cafeMenuCnt).text = "${menuCnt}개"
                } else {
                    menuCnt--
                    itemView.findViewById<TextView>(R.id.tv_cafeMenuCnt).text = "${menuCnt}개"
                }
            }
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
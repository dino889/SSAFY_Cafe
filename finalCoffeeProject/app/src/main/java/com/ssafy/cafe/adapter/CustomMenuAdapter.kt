package com.ssafy.cafe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.cafe.R
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.dto.OrderDetail
import com.ssafy.cafe.dto.Product
import com.ssafy.cafe.dto.UserCustom
import com.ssafy.cafe.util.CommonUtils

class CustomMenuAdapter(var customList:List<UserCustom>) : RecyclerView.Adapter<CustomMenuAdapter.MenuHolder>() {

    inner class MenuHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage = itemView.findViewById<ImageView>(R.id.iv_productImg)
        val productName = itemView.findViewById<TextView>(R.id.tv_productName)
        val productPrice = itemView.findViewById<TextView>(R.id.tv_productPrice)
        val menuType = itemView.findViewById<TextView>(R.id.tv_menuType)
        val menuSyrup = itemView.findViewById<TextView>(R.id.tv_menuSyrup)
        val menuShot = itemView.findViewById<TextView>(R.id.tv_menuShot)

        fun bindInfo(userCustom: UserCustom) {
            var pId = userCustom.productId
            var uId = userCustom.userId
//
//            Glide.with(itemView)
//                .load("${ApplicationClass.MENU_IMGS_URL}${img}")
//                .into(productImage)
//
//            productName.text =
//            productPrice.text = CommonUtils.makeComma(product.price)
            menuType.text = "|  ${userCustom.type}"
            menuSyrup.text = "|  ${userCustom.syrup}"
            menuShot.text = "|  ${userCustom.shot}"

            itemView.setOnClickListener {
                itemClickListner.onClick(it, layoutPosition,  customList[layoutPosition].id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_user_custom_menu_list_item,parent, false)
        return MenuHolder(view)
    }

    override fun onBindViewHolder(holder: MenuHolder, position: Int) {
        holder.apply {
            bindInfo(customList[position])
        }
    }

    override fun getItemCount(): Int {
        return customList.size
    }
    //클릭 인터페이스 정의 사용하는 곳에서 만들어준다.
    interface ItemClickListener {
        fun onClick(view: View,  position: Int, productId:Int)
    }
    //클릭리스너 선언
    private lateinit var itemClickListner: ItemClickListener
    //클릭리스너 등록 매소드
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }
}
package com.ssafy.cafe.src.main.allmenu

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.common.internal.service.Common
import com.ssafy.cafe.R
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.src.main.dto.Product
import com.ssafy.cafe.src.main.network.response.LatestOrderResponse
import com.ssafy.cafe.util.CommonUtils
import org.w3c.dom.Text

private const val TAG = "AllMenuAdapter_싸피"
class AllMenuAdapter(var productList:List<Product>) :RecyclerView.Adapter<AllMenuAdapter.MenuHolder>(){

    inner class MenuHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val productImage = itemView.findViewById<ImageView>(R.id.iv_productImg)
        val productName = itemView.findViewById<TextView>(R.id.tv_productName)
        val productPrice = itemView.findViewById<TextView>(R.id.tv_productPrice)

        fun bindInfo(product : Product){
            Glide.with(itemView)
                .load("${ApplicationClass.MENU_IMGS_URL}${product.img}")
                .into(productImage)

            productName.text = product.name
            productPrice.text = CommonUtils.makeComma(product.price)

            itemView.setOnClickListener{
                itemClickListner.onClick(it, layoutPosition, productList[layoutPosition].id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_allmenu_list_item, parent, false)
        return MenuHolder(view)
    }

    override fun onBindViewHolder(holder: MenuHolder, position: Int) {
        holder.apply{
            bindInfo(productList[position])
        }
    }

    override fun getItemCount(): Int {
        return productList.size
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


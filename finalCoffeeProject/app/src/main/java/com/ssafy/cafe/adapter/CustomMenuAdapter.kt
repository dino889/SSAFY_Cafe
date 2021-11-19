package com.ssafy.cafe.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.cafe.R
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.dto.OrderDetail
import com.ssafy.cafe.dto.Product
import com.ssafy.cafe.dto.UserCustom
import com.ssafy.cafe.service.ProductService
import com.ssafy.cafe.util.CommonUtils
import com.ssafy.cafe.util.RetrofitCallback

private const val TAG = "CustomMenuAdapter"
class CustomMenuAdapter(var customList:List<UserCustom>) : RecyclerView.Adapter<CustomMenuAdapter.MenuHolder>() {

    private lateinit var pImg:String
    private lateinit var pName:String
    private lateinit var pPrice:String

    inner class MenuHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val productImage = itemView.findViewById<ImageView>(R.id.iv_productImg)
//        val productName = itemView.findViewById<TextView>(R.id.tv_productName)
//        val productPrice = itemView.findViewById<TextView>(R.id.tv_productPrice)
        val menuType = itemView.findViewById<TextView>(R.id.tv_menuType)
        val menuSyrup = itemView.findViewById<TextView>(R.id.tv_menuSyrup)
        val menuShot = itemView.findViewById<TextView>(R.id.tv_menuShot)

        fun bindInfo(userCustom: UserCustom) {
            var pId = userCustom.productId
            var uId = userCustom.userId

            ProductService().getProductById(pId, GetProductCallback(itemView))

//            Glide.with(itemView)
//                .load("${ApplicationClass.MENU_IMGS_URL}${pImg}")
//                .into(productImage)
//
//            productName.text = pName
//            productPrice.text = CommonUtils.makeComma(pPrice.toInt())
            if(userCustom.type == 1){
                menuType.text = "|  ICE"
            }else{
                menuType.text = "|  HOT"
            }

            if(userCustom.syrup == null){
                menuSyrup.visibility = View.GONE
            }else{
                menuSyrup.text = "|  ${userCustom.syrup}"
            }

            if(userCustom.shot == 0){
                menuShot.isVisible = false
            }else{
                menuShot.text = "|  ${userCustom.shot}"
            }


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

    inner class GetProductCallback(val itemView: View): RetrofitCallback<Product>{
        override fun onError(t: Throwable) {
            Log.d(TAG, "onError: ")
        }

        override fun onSuccess(code: Int, responseData: Product) {

            val productImage = itemView.findViewById<ImageView>(R.id.iv_productImg)
            val productName = itemView.findViewById<TextView>(R.id.tv_productName)
            val productPrice = itemView.findViewById<TextView>(R.id.tv_productPrice)

            Glide.with(itemView)
                .load("${ApplicationClass.MENU_IMGS_URL}${responseData.img}")
                .into(productImage)

            productName.text = responseData.name
            productPrice.text = CommonUtils.makeComma(responseData.price)

//            pImg = responseData.img
//            pName = responseData.name
//            pPrice = responseData.price.toString()
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onFailure: ")
        }

    }

}
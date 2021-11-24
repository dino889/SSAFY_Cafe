package com.ssafy.cafe.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.cafe.R
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.dto.OrderDetail
import com.ssafy.cafe.dto.Product
import com.ssafy.cafe.dto.ShoppingCart
import com.ssafy.cafe.dto.UserCustom
import com.ssafy.cafe.fragment.UserCustomMenuFragment
import com.ssafy.cafe.service.ProductService
import com.ssafy.cafe.util.CommonUtils
import com.ssafy.cafe.util.RetrofitCallback
import com.ssafy.cafe.viewmodel.MainViewModel

private const val TAG = "CustomMenuAdapter"
class CustomMenuAdapter(var customList:List<UserCustom>, val kFunction1:() -> Unit) : RecyclerView.Adapter<CustomMenuAdapter.MenuHolder>() {

    inner class MenuHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val menuType = itemView.findViewById<TextView>(R.id.tv_menuType)
        val menuSyrup = itemView.findViewById<TextView>(R.id.tv_menuSyrup)
        val menuShot = itemView.findViewById<TextView>(R.id.tv_menuShot)

        fun bindInfo(userCustom: UserCustom) {
            val pId = userCustom.productId
            ProductService().getProductById(pId, GetProductCallback(itemView))

            if(userCustom.type == 1){
                menuType.text = "|  ICE"
            } else if(userCustom.type == 0){
                menuType.text = "|  HOT"
            } else if(userCustom.type == 3) {   // 프라푸치노 or 디저트
                menuType.text = ""
            }

            if(userCustom.syrup == null || userCustom.syrup.equals("null") || userCustom.syrup == ""){
                menuSyrup.visibility = View.GONE
            }else{
                menuSyrup.text = "|  ${userCustom.syrup}"
            }

            if(userCustom.shot == 0){
                menuShot.isVisible = false
            }else{
                menuShot.text = "|  ${userCustom.shot}"
            }

//            itemView.setOnClickListener {
//                itemClickListner.onClick(it, layoutPosition,  customList[layoutPosition].id)
//            }
            val check = itemView.findViewById<CheckBox>(R.id.checkBox)

            check.setOnCheckedChangeListener { buttonView, isChecked ->
                userCustom.isChecked = true
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

        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onFailure: ")
        }

    }

}
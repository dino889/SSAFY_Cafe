package com.ssafy.cafe.util



import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.ssafy.cafe.config.ApplicationClass

object BindingAdapter {

    @JvmStatic
    @BindingAdapter("imageFile")
    fun bindImg(view: ImageView, resName: String) {
        Glide.with(view.context)
            .load("${ApplicationClass.MENU_IMGS_URL}${resName}")
            .into(view)
    }

//    @JvmStatic
//    @BindingAdapter("setText")
//    fun setText(view:TextView, input:Int?) {
//        view.text = input?.toString()
//    }
}
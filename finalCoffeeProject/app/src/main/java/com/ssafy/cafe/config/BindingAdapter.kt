package com.ssafy.cafe.config

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("setInt")
fun set(view: TextView, toint:Int){
    view.text = toint.toString()
}
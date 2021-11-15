package com.ssafy.cafe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.cafe.R

class BestMenuAdapter : RecyclerView.Adapter<BestMenuAdapter.BestMenuHolder>(){
    //var bestmenuList = mutableListOf<>()
    inner class BestMenuHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bindInfo(){

        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BestMenuAdapter.BestMenuHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_menu_list_item,parent,false)
        return BestMenuHolder(view)
    }

    override fun onBindViewHolder(holder: BestMenuAdapter.BestMenuHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

}
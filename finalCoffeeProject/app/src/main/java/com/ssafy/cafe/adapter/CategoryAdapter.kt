package com.ssafy.cafe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.cafe.R
import com.ssafy.cafe.dto.Category

class CategoryAdapter(var categoryList:List<Category>) : RecyclerView.Adapter<CategoryAdapter.CategoryHolder>() {

    inner class CategoryHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val category = itemView.findViewById<TextView>(R.id.tv_categoryName)
        fun bindInfo(categorys: Category){
            category.text = categorys.category
            itemView.setOnClickListener {
                itemClickListener.onClick(it, layoutPosition, categoryList[layoutPosition].id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_menu_category_list_item,parent,false)
        return CategoryHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        holder.apply {
            bindInfo(categoryList[position])
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    interface ItemClickListener {
        fun onClick(view: View, position: Int, categoryId: Int)
    }

    private lateinit var itemClickListener: ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }
}
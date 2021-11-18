package com.ssafy.cafe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.cafe.R
import com.ssafy.cafe.dto.Comment

class MyReviewAdapter(var myReviewList:List<Comment>):RecyclerView.Adapter<MyReviewAdapter.MyReviewHolder>() {
    inner class MyReviewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val reviewPName = itemView.findViewById<TextView>(R.id.tv_reviewMenuName)
       // val reviewDate = itemView.findViewById<TextView>(R.id.tv_myReviceDate)
        val reviewRating = itemView.findViewById<RatingBar>(R.id.ratingBar2)
        val reviewContent = itemView.findViewById<TextView>(R.id.tv_myReviewContent)

        fun bindInfo(data: Comment){
            reviewPName.text = data.productId.toString()
            reviewRating.rating = data.rating
            reviewContent.text = data.comment
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyReviewAdapter.MyReviewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_myreview_list_item,parent,false)
        return MyReviewHolder(view)
    }

    override fun onBindViewHolder(holder: MyReviewAdapter.MyReviewHolder, position: Int) {
        holder.apply {
            bindInfo(myReviewList[position])
        }
    }

    override fun getItemCount(): Int {
        return myReviewList.size
    }


}
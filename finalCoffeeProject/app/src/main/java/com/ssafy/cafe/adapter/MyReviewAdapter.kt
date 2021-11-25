package com.ssafy.cafe.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.cafe.R
import com.ssafy.cafe.dto.Comment
import com.ssafy.cafe.dto.Product
import com.ssafy.cafe.service.ProductService
import com.ssafy.cafe.util.CommonUtils
import com.ssafy.cafe.util.RetrofitCallback

private const val TAG = "MyReviewAdapter"
class MyReviewAdapter(var myReviewList:List<Comment>):RecyclerView.Adapter<MyReviewAdapter.MyReviewHolder>() {

    inner class MyReviewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val reviewPName = itemView.findViewById<TextView>(R.id.tv_reviewMenuName)
       // val reviewDate = itemView.findViewById<TextView>(R.id.tv_myReviceDate)
        val reviewRating = itemView.findViewById<RatingBar>(R.id.ratingBar2)
        val reviewContent = itemView.findViewById<TextView>(R.id.tv_myReviewContent)
        val reviewDate = itemView.findViewById<TextView>(R.id.tv_reviewDate)
        fun bindInfo(data: Comment){
            ProductService().getProductById(data.productId, ProductCallback(itemView))
//            reviewPName.text = pName
            reviewRating.rating = data.rating
            reviewContent.text = data.comment
            reviewDate.text = data.date?.let { CommonUtils.getFormattedString(it!!) }
        }
    }
    inner class ProductCallback(val itemView: View):RetrofitCallback<Product>{
        override fun onError(t: Throwable) {
            Log.d(TAG, "onError: ")
        }

        override fun onSuccess(code: Int, responseData: Product) {
             itemView.findViewById<TextView>(R.id.tv_reviewMenuName).text= responseData.name
        }

        override fun onFailure(code: Int) {
            TODO("Not yet implemented")
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
package com.ssafy.cafe.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.ssafy.cafe.R
import com.ssafy.cafe.activity.MainActivity
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.dto.Comment
import com.ssafy.cafe.response.MenuDetailWithCommentResponse
import com.ssafy.cafe.service.CommentService
import com.ssafy.cafe.service.ProductService
import com.ssafy.cafe.util.RetrofitCallback

private const val TAG = "CommentAdapter_싸피"
//class CommentAdapter(val list: List<MenuDetailWithCommentResponse>, kFunction0: () -> Unit) :RecyclerView.Adapter<CommentAdapter.CommentHolder>(){
//    lateinit var context: MainActivity
//
//    inner class CommentHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
//        val userId = itemView.findViewById<TextView>(R.id.tv_userID)
//        val comment = itemView.findViewById<TextView>(R.id.tv_reviewContent)
//        val modifyComment = itemView.findViewById<TextInputEditText>(R.id.til_reviewContent)
//        val rating = itemView.findViewById<TextView>(R.id.tv_reviewCnt)
//
//        val modify = itemView.findViewById<Button>(R.id.btn_modify)
//        val delete = itemView.findViewById<Button>(R.id.btn_delete)
//        val cancel = itemView.findViewById<Button>(R.id.btn_cancel)
//        val confirm = itemView.findViewById<Button>(R.id.btn_confirm)
//        val mutableList = list.toMutableList()
//
//
//        fun bindInfo(data :MenuDetailWithCommentResponse){
//
//            comment.text = data.commentContent.toString()
//            Log.d(TAG, "bindInfo: ${data}")
//
//            delete.setOnClickListener {
//                deleteComment(data.commentId)
//                mutableList.removeAt(layoutPosition)
//            }
//
//        }
//
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
//        context = parent.context as MainActivity
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_review_list_item, parent, false)
//        return CommentHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: CommentHolder, position: Int) {
//        holder.apply{
//            bindInfo(list[position])
//
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return list.size
//    }
//
//    interface ItemClickListener{
//        fun onClick(view: View, position: Int, commentId:Int)
//    }
//
//    private lateinit var itemClickListener: ItemClickListener
//
//    fun setItemClickListener(itemClickListener: ItemClickListener){
//        this.itemClickListener = itemClickListener
//    }
//
//    fun updateComment(dto: Comment){
//        Log.d(TAG, "updateComment: $dto")
//        CommentService().modify(dto, object: RetrofitCallback<Boolean> {
//            override fun onError(t: Throwable) {
//                Log.d(TAG, "onError: ")
//            }
//
//            override fun onSuccess(code: Int, responseData: Boolean) {
//                Toast.makeText(context,"코맨트수정성공", Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onFailure(code: Int) {
//                Log.d(TAG, "onFailure: ")
//            }
//
//        })
//    }
//
//    fun deleteComment(id:Int){
//        Log.d(TAG, "deleteComment: $id")
//        CommentService().delete(id, object : RetrofitCallback<Boolean>{
//            override fun onError(t: Throwable) {
//                Log.d(TAG, "onError: ")
//            }
//
//            override fun onSuccess(code: Int, responseData: Boolean) {
//                Toast.makeText(context, "코맨트 삭제 성공",Toast.LENGTH_SHORT).show()
//
//            }
//
//            override fun onFailure(code: Int) {
//                Log.d(TAG, "onFailure: ")
//            }
//
//        })
//    }
//}


class CommentAdapter(val list: List<MenuDetailWithCommentResponse>, val kFunction0: () -> Unit) :
    RecyclerView.Adapter<CommentAdapter.CommentHolder>() {

    lateinit var context: MainActivity
    val user = ApplicationClass.sharedPreferencesUtil.getUser()


    inner class CommentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userId = itemView.findViewById<TextView>(R.id.tv_userID)
        val comment = itemView.findViewById<TextView>(R.id.tv_reviewContent)
        val modifyComment = itemView.findViewById<TextInputEditText>(R.id.til_reviewContent)
        val rating = itemView.findViewById<TextView>(R.id.tv_reviewCnt)

        val modify = itemView.findViewById<Button>(R.id.btn_modify)
        val delete = itemView.findViewById<Button>(R.id.btn_delete)
        val cancel = itemView.findViewById<Button>(R.id.btn_cancel)
        val confirm = itemView.findViewById<Button>(R.id.btn_confirm)

        private fun visibleMainButton(visible: Boolean = true) {
            if (visible) {
                modify.visibility = ImageView.VISIBLE
                delete.visibility = ImageView.VISIBLE
            } else {
                modify.visibility = ImageView.GONE
                delete.visibility = ImageView.GONE
            }
        }

        private fun visibleModifiedButton(visible: Boolean = true) {
            if (visible) {
                confirm.visibility = ImageView.VISIBLE
                cancel.visibility = ImageView.VISIBLE
                modifyComment.visibility = EditText.VISIBLE
            } else {
                confirm.visibility = EditText.GONE
                cancel.visibility = ImageView.GONE
                modifyComment.visibility = ImageView.GONE
            }
        }


        fun bindInfo(data: MenuDetailWithCommentResponse, index: Int) {
            val newComment = Comment(
                data.commentContent ?: "content X",
                data.commentId,
                data.productId,
                data.productRating.toFloat(),
                user.id
            )
            visibleModifiedButton(false)

            if (data.userId == user.id) {
                visibleMainButton()

                modify.setOnClickListener {
                    visibleModifiedButton()
                    visibleMainButton(false)
                }

                cancel.setOnClickListener {
                    visibleMainButton()
                    visibleModifiedButton(false)
                }

                confirm.setOnClickListener {
                    newComment.comment = modifyComment.text.toString()
                    CommentService().modify(newComment, ModifyCallback())
                    visibleMainButton()
                    visibleModifiedButton(false)
                }

                delete.setOnClickListener {
//                    val i = list.indexOfFirst { item -> item.commentId == data.id }
//                    commentService.deleteComment(data.id)
//                    list.removeAt(i)
                    CommentService().delete(newComment.id, DeleteCallback())
                    visibleMainButton()
                    visibleModifiedButton(false)
                }
            } else {
                visibleMainButton(false)
            }
            comment.text = data.commentContent
        }

        inner class ModifyCallback : RetrofitCallback<Boolean> {
            override fun onError(t: Throwable) {
                Log.d(TAG, "onError: ")
            }

            override fun onSuccess(code: Int, responseData: Boolean) {
                if (responseData) {
                    Toast.makeText(context, "수정되었습니다.", Toast.LENGTH_SHORT).show()
                    kFunction0()
                }
            }

            override fun onFailure(code: Int) {
                Log.d(TAG, "onFailure: ")
            }
        }

        inner class DeleteCallback : RetrofitCallback<Boolean> {
            override fun onError(t: Throwable) {
                Log.d(TAG, "onError: ")
            }

            override fun onSuccess(code: Int, responseData: Boolean) {

                if(responseData){
                    Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    kFunction0()
                }
            }

            override fun onFailure(code: Int) {
                Log.d(TAG, "onFailure: ")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
        context = parent.context as MainActivity
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_review_list_item, parent, false)
        return CommentHolder(view)
    }

    override fun onBindViewHolder(holder: CommentHolder, position: Int) {
        holder.bindInfo(list[position], position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}
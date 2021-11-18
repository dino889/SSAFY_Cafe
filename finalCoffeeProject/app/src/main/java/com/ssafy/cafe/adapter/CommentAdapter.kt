package com.ssafy.cafe.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.ssafy.cafe.R
import com.ssafy.cafe.activity.MainActivity
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.dto.Comment
import com.ssafy.cafe.response.MenuDetailWithCommentResponse
import com.ssafy.cafe.service.CommentService
import com.ssafy.cafe.service.ProductService
import com.ssafy.cafe.util.RetrofitCallback

private const val TAG = "CommentAdapter_싸피"
class CommentAdapter(val list: List<MenuDetailWithCommentResponse>, val kFunction0: () -> Unit) :
    RecyclerView.Adapter<CommentAdapter.CommentHolder>() {

    lateinit var context: MainActivity
    val user = ApplicationClass.sharedPreferencesUtil.getUser()


    inner class CommentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userId = itemView.findViewById<TextView>(R.id.tv_userID)
        val comment = itemView.findViewById<TextView>(R.id.tv_reviewContent)
        val modifyCommentLayout = itemView.findViewById<TextInputLayout>(R.id.til_reviewContent)
        val modifyComment = itemView.findViewById<TextInputEditText>(R.id.tiet_reviewContent)
        val rating = itemView.findViewById<TextView>(R.id.tv_reviewCnt)

        val modify = itemView.findViewById<Button>(R.id.btn_modify)
        val delete = itemView.findViewById<Button>(R.id.btn_delete)
        val cancel = itemView.findViewById<Button>(R.id.btn_cancel)
        val confirm = itemView.findViewById<Button>(R.id.btn_confirm)

        // 수정 삭제 버튼 전환
        private fun visibleMainButton(visible: Boolean = true) {
            if (visible) {
                modify.visibility = ImageView.VISIBLE
                delete.visibility = ImageView.VISIBLE
            } else {
                modify.visibility = ImageView.GONE
                delete.visibility = ImageView.GONE
            }
        }

        // 취소 확인 버튼 + editText 전환
        private fun visibleModifiedButton(visible: Boolean = true) {
            if (visible) {
                confirm.visibility = ImageView.VISIBLE
                cancel.visibility = ImageView.VISIBLE
                modifyCommentLayout.visibility = TextInputLayout.VISIBLE
            } else {
                confirm.visibility = ImageView.GONE
                cancel.visibility = ImageView.GONE
                modifyCommentLayout.visibility = TextInputLayout.GONE
            }
        }


        fun bindInfo(data: MenuDetailWithCommentResponse, index: Int) {

            userId.text = "${data.userId} 님"
            comment.text = data.commentContent
            rating.text = "${data.productRating} 점"

            val newComment = Comment(
                data.commentContent ?: "content X",
                data.commentId,
                data.productId,
                data.productRating.toFloat(),
                user.id
            )

            visibleModifiedButton(false)

            if (data.userId == user.id) {   // 작성자와 현재 로그인한 사용자가 같으면
                visibleMainButton() // 수정, 삭제 버튼 보여줌.

                modify.setOnClickListener {
                    comment.visibility = View.GONE
                    modifyComment.setText(data.commentContent.toString())

                    visibleModifiedButton()
                    visibleMainButton(false)
                }

                delete.setOnClickListener {
                    CommentService().delete(newComment.id, DeleteCallback())
                    visibleMainButton()
                    visibleModifiedButton(false)
                }

                cancel.setOnClickListener {
                    comment.visibility = View.VISIBLE

                    visibleMainButton()
                    visibleModifiedButton(false)
                }

                confirm.setOnClickListener {
                    comment.visibility = View.VISIBLE

                    newComment.comment = modifyComment.text.toString()
                    modifyComment.text!!.clear()
                    CommentService().modify(newComment, ModifyCallback())
                    visibleMainButton()
                    visibleModifiedButton(false)
                }

            } else {
                visibleMainButton(false)
            }

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
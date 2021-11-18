package com.ssafy.cafe.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.lifecycle.MutableLiveData
import com.ssafy.cafe.R
import com.ssafy.cafe.activity.MainActivity
import com.ssafy.cafe.adapter.CommentAdapter
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.databinding.FragmentMenuDetailBinding
import com.ssafy.cafe.databinding.FragmentReviewBinding
import com.ssafy.cafe.response.MenuDetailWithCommentResponse
import com.ssafy.cafe.service.CommentService
import com.ssafy.cafe.service.ProductService
import com.ssafy.cafe.util.RetrofitCallback

private const val TAG = "ReviewFragment_싸피"
class ReviewFragment : Fragment() {
    private lateinit var mainActivity : MainActivity
    private lateinit var binding : FragmentReviewBinding

    private var productId = -1

    val liveData = MutableLiveData<List<MenuDetailWithCommentResponse>>()
    private var commentAdapter = CommentAdapter(emptyList(), this::initData)


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.hideBottomNav(true)
        arguments?.let {
            productId = it.getInt("productId", -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun initData() {
        liveData.observe(mainActivity, {
//            Log.d(TAG, "livaData changed ${liveData.value}")
            binding.rvReview.adapter = liveData.value?.let { it ->
                CommentAdapter(it, this::initData)
            }

        })

        ProductService().getProductWithComments(productId, ProductWithCommentInsertCallback())
    }

    private fun initListener() {
        binding.btnInsertComment.setOnClickListener {
            //showDialogRatingStar()
        }
    }

    private fun showDialogRatingStar(): AlertDialog? {
        val view = layoutInflater.inflate(R.layout.dialog_add_comment, null)
        val dialog = AlertDialog.Builder(mainActivity).apply {
            setTitle("별점선택")
            setView(view)
            setPositiveButton("확인") { dialog, which ->

                val user = ApplicationClass.sharedPreferencesUtil.getUser()
                newComment = Comment(
                    -1,
                    user.id,
                    productId,
                    view.findViewById<RatingBar>(R.id.ratingBarMenuDialogComment).rating,
                    binding.newCommentEdt.text.toString()
                )
                CommentService().insert(newComment, CommentAddCallback())
            }
            setNegativeButton("취소") { dialog, which ->
                dialog.cancel()
            }
        }
        return dialog.create()
    }


    inner class ProductWithCommentInsertCallback :
        RetrofitCallback<List<MenuDetailWithCommentResponse>> {
        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "물품 정보를 받아오는 중 통신오류")
        }

        override fun onSuccess(code: Int, responseData: List<MenuDetailWithCommentResponse>) {
            commentAdapter = if(responseData.size == 1 && responseData[0].userId == null) {
                CommentAdapter(mutableListOf(), this@ReviewFragment::initData)
            } else {
                CommentAdapter(responseData, this@ReviewFragment::initData)
            }
            liveData.value = responseData

            binding.ratingBar.rating = responseData[0].productRatingAvg.toFloat()
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onFailure: Error Code $code")
        }
    }

    inner class CommentAddCallback : RetrofitCallback<Boolean> {
        override fun onError(t: Throwable) {
            Log.d(TAG, "onError: ")
        }

        override fun onSuccess(code: Int, responseData: Boolean) {
            if (responseData) {
                initData()
            }
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onFailure: ")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(key:String, value:Int) =
            MenuInfoDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(key, value)
                }
            }
    }
}
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
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.ssafy.cafe.R
import com.ssafy.cafe.activity.MainActivity
import com.ssafy.cafe.adapter.CommentAdapter
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.databinding.FragmentReviewBinding
import com.ssafy.cafe.dto.Comment
import com.ssafy.cafe.response.MenuDetailWithCommentResponse
import com.ssafy.cafe.service.CommentService
import com.ssafy.cafe.service.ProductService
import com.ssafy.cafe.util.CommonUtils
import com.ssafy.cafe.util.RetrofitCallback
import com.ssafy.cafe.viewmodel.MainViewModel
import kotlin.math.round

private const val TAG = "ReviewFragment_싸피"
class ReviewFragment : Fragment() {
    private lateinit var mainActivity : MainActivity
    private lateinit var binding : FragmentReviewBinding
    private val viewModel: MainViewModel by activityViewModels()
//    lateinit var prodWithComment: MenuDetailWithCommentResponse
    private var productId = -1

    val liveData = MutableLiveData<List<MenuDetailWithCommentResponse>>()
    private var commentAdapter = CommentAdapter(emptyList(), this::initData)
    private lateinit var newComment : Comment   // 새로 추가되는 comment


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initListener()
//        setScreen(liveData.value!![0])
    }

    // 초기 화면 설정
    private fun setScreen(menu: MenuDetailWithCommentResponse){
        Log.d(TAG, "setScreen: $menu")
        binding.ratingBar.rating = menu.productRatingAvg.toFloat()
    }

    private fun initData() {
        liveData.observe(requireActivity(), {
//            Log.d(TAG, "livaData changed ${liveData.value}")
            binding.rvReview.adapter = liveData.value?.let { it1 ->
                CommentAdapter(
                    it1,
                    this::initData
                )
            }

        })

        ProductService().getProductWithComments(productId, ProductWithCommentInsertCallback())

        binding.rvReview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = commentAdapter
            //원래의 목록위치로 돌아오게함
            adapter!!.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
    }

    private fun initListener() {
        binding.btnInsertComment.setOnClickListener {
            Log.d(TAG, "onViewCreated: ")
            showDialogRatingStar()
        }
    }

    private fun showDialogRatingStar(){
        Log.d(TAG, "showDialogRatingStar: 왜 안되냐")
        val view = layoutInflater.inflate(R.layout.dialog_add_comment, null)
        val dialog = AlertDialog.Builder(requireContext()).apply {
//            setTitle("별점선택")
            setView(view)
            val prodName = view.findViewById<TextView>(R.id.tv_productNameContent)
            prodName.text = CommonUtils.dialogProductComent(liveData.value!!.get(0).productName)
//            Log.d(TAG, "showDialogRatingStar: ${view.findViewById<TextInputEditText>(R.id.et_comment).text.toString()}")

            setPositiveButton("확인") { dialog, which ->

                val user = ApplicationClass.sharedPreferencesUtil.getUser()
                newComment = Comment(
                    view.findViewById<TextInputEditText>(R.id.et_comment).text.toString(),
                    0,
                    viewModel.liveProductWithComment.value!!.get(0).productId,
                    view.findViewById<RatingBar>(R.id.ratingBarMenuDialogComment).rating,
                    user.id
                )
                CommentService().insert(newComment, CommentAddCallback())
            }
            setNegativeButton("취소") { dialog, which ->
                dialog.cancel()
            }
        }.show()
        //return dialog.create()
    }


    inner class ProductWithCommentInsertCallback :
        RetrofitCallback<List<MenuDetailWithCommentResponse>> {
        override fun onSuccess(
            code: Int,
            responseData: List<MenuDetailWithCommentResponse>
        ) {
            if (responseData.isNotEmpty()) {

                Log.d(TAG, "initData: ${responseData[0]}")

                // comment 가 없을 경우 -> 들어온 response가 1개이고 해당 userId 가 null일 경우 빈 배열 Adapter 연결
                if (responseData.size == 1 && responseData[0].userId == null) {
                    Log.d(TAG, "onSuccess: 111")
                    commentAdapter = CommentAdapter(mutableListOf(), this@ReviewFragment::initData)
                } else {
                    Log.d(TAG, "onSuccess: 222${responseData}")
                    commentAdapter = CommentAdapter(responseData, this@ReviewFragment::initData)
                }
                liveData.value = responseData
                // 화면 정보 갱신
                setScreen(responseData[0])
            }

        }

        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "물품 정보 받아오는 중 통신오류")
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
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


//    inner class ProductWithCommentInsertCallback :
//        RetrofitCallback<List<MenuDetailWithCommentResponse>> {
//        override fun onError(t: Throwable) {
//            Log.d(TAG, t.message ?: "물품 정보를 받아오는 중 통신오류")
//        }
//
//        override fun onSuccess(code: Int, responseData: List<MenuDetailWithCommentResponse>) {
//            commentAdapter = if(responseData.size == 1 && responseData[0].userId == null) {
//                CommentAdapter(mutableListOf(), this@ReviewFragment::initData)
//            } else {
//                CommentAdapter(responseData, this@ReviewFragment::initData)
//            }
//            liveData.value = responseData
//
//            viewModel.prodWithComment = responseData[0]
////            binding.ratingBar.rating = responseData[0].productRatingAvg.toFloat()
//        }
//
//        override fun onFailure(code: Int) {
//            Log.d(TAG, "onFailure: Error Code $code")
//        }
//    }


    companion object {
        @JvmStatic
        fun newInstance(key: String, value : Int) =
            ReviewFragment().apply {
                arguments = Bundle().apply {
                    putInt(key, value)
                }
            }
    }
}
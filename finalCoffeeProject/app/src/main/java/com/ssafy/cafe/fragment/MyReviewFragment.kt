package com.ssafy.cafe.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.cafe.R
import com.ssafy.cafe.adapter.MyReviewAdapter
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.config.BaseFragment
import com.ssafy.cafe.databinding.FragmentMyReviewBinding
import com.ssafy.cafe.dto.Comment
import com.ssafy.cafe.service.CommentService
import com.ssafy.cafe.util.RetrofitCallback

private const val TAG = "MyReviewFragment"
class MyReviewFragment : BaseFragment<FragmentMyReviewBinding>(FragmentMyReviewBinding::bind, R.layout.fragment_my_review) {
    private lateinit var myReviewAdapter: MyReviewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getCommentByUsers(ApplicationClass.sharedPreferencesUtil.getUser().id)
    }

    private fun getCommentByUsers(id:String){
        CommentService().selectCommentByUser(id, object : RetrofitCallback<List<Comment>> {

            override fun onError(t: Throwable) {
                Log.d(TAG, "onError: ")
            }

            override fun onSuccess(code: Int, responseData: List<Comment>){
                responseData.let{
                    myReviewAdapter = MyReviewAdapter(responseData)
                    Log.d(TAG, "onSuccess: $responseData")
                }
                binding.rvMyReview.apply {
                    val linearLayoutManager = LinearLayoutManager(context)
                    linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
                    layoutManager = linearLayoutManager
                    adapter = myReviewAdapter
                    adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                }
            }

            override fun onFailure(code: Int) {
                Log.d(TAG, "onFailure: ")
            }
        })
    }
}
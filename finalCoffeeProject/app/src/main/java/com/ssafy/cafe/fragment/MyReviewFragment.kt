package com.ssafy.cafe.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.cafe.R
import com.ssafy.cafe.adapter.MyReviewAdapter
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.databinding.FragmentMyReviewBinding
import com.ssafy.cafe.dto.Comment
import com.ssafy.cafe.service.CommentService
import com.ssafy.cafe.util.RetrofitCallback
import com.ssafy.cafe.util.SharedPreferencesUtil

private const val TAG = "MyReviewFragment"
class MyReviewFragment : Fragment() {
    private lateinit var binding: FragmentMyReviewBinding
    private lateinit var myReviewAdapter: MyReviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMyReviewBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = ApplicationClass.sharedPreferencesUtil.getUser()
        val id = user.id
        getCommentByUsers(id)
    }
    fun getCommentByUsers(id:String){
        Log.d(TAG, "getCommentByUsers: $id")
        CommentService().selectCommentByUser(id, object : RetrofitCallback<List<Comment>> {

            override fun onError(t: Throwable) {
                Log.d(TAG, "onError: ")
            }

            override fun onSuccess(code: Int, responseData: List<Comment>){
                responseData.let{
                    myReviewAdapter = MyReviewAdapter(responseData)
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
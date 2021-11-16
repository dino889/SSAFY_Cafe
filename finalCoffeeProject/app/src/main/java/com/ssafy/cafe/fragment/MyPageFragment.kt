package com.ssafy.cafe.fragment

import android.os.Bundle
import android.util.JsonReader
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ssafy.cafe.R
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.databinding.FragmentMyPageBinding
import com.ssafy.cafe.dto.Comment
import com.ssafy.cafe.dto.Order
import com.ssafy.cafe.dto.User
import com.ssafy.cafe.service.CommentService
import com.ssafy.cafe.service.UserService
import com.ssafy.cafe.util.RetrofitCallback
import org.json.JSONObject
import java.io.StringReader

private const val TAG = "MyPageFragment"
class MyPageFragment : Fragment() {
    private lateinit var binding : FragmentMyPageBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPageBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUserInfo()
    }

    fun initUserInfo(){
        var users = ApplicationClass.sharedPreferencesUtil.getUser()
        binding.tvUserProfilName.text = "${users.name}님"
        getUsers(users.id)
        getCommentByUsers(users.id)

    }

    fun getCommentByUsers(id:String){

        CommentService().selectCommentByUser(id, object : RetrofitCallback<List<Comment>> {
            override fun onError(t: Throwable) {
                Log.d(TAG, "onError: ")
            }

            override fun onSuccess(code: Int, responseData: List<Comment>){
                binding.tvMyReviewViewCnt.text = responseData.size.toString()
            }

            override fun onFailure(code: Int) {
                Log.d(TAG, "onFailure: ")
            }
        })
    }
    fun getUsers(id:String){
        UserService().getUsers(id, object : RetrofitCallback<HashMap<String, Any>> {
            override fun onError(t: Throwable) {
                Log.d(TAG, "onError: ")
            }

            override fun onSuccess(code: Int, responseData: HashMap<String, Any>) {
                Log.d(TAG, "onSuccess: $responseData")
                //val grade = responseData!!["grade"]

                val user = Gson().fromJson(responseData["user"].toString(), User::class.java)
//                val order:List<Order> = Gson().fromJson(responseData["order"].toString(), List<Order>::class.java).toList()
                var pay = user.money
                binding.tvMyPayMoney.text = "$pay 원"
                ApplicationClass.sharedPreferencesUtil.addUserPay(pay)
            }

            override fun onFailure(code: Int) {
                Log.d(TAG, "onFailure: ")
            }

        })
    }
}
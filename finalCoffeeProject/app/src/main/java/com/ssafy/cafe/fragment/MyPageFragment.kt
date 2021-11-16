package com.ssafy.cafe.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.ssafy.cafe.R
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.databinding.FragmentMyPageBinding
import com.ssafy.cafe.dto.User
import com.ssafy.cafe.service.UserService
import com.ssafy.cafe.util.RetrofitCallback

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

        UserService().getUsers(users.id, object : RetrofitCallback<HashMap<String, Any>> {
            override fun onError(t: Throwable) {
                Log.d(TAG, "onError: ")
            }

            override fun onSuccess(code: Int, responseData: HashMap<String, Any>) {
                Log.d(TAG, "onSuccess: $responseData")
                //val grade = responseData!!["grade"]

                val user = Gson().fromJson(responseData["user"].toString(), User::class.java)
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
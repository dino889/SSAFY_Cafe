package com.ssafy.cafe.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ssafy.cafe.R
import com.ssafy.cafe.activity.LoginActivity
import com.ssafy.cafe.databinding.FragmentJoinBinding
import com.ssafy.cafe.dto.User
import com.ssafy.cafe.service.UserService
import com.ssafy.cafe.util.RetrofitCallback

private const val TAG = "JoinFragment"
class JoinFragment : Fragment() {
    lateinit var binding: FragmentJoinBinding
    private var checkedId = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentJoinBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnJoin.setOnClickListener {
            val id = binding.etUserID.text.toString().trim()
            val name = binding.etUserName.text.toString().trim()
            val tel = binding.etUserTel.text.toString().trim()
            val pw = binding.etUserPw.text.toString().trim()

            if(isValid(id,name,tel,pw)){
                join(id,name,tel,pw)
            }
        }
    }
    private fun join(id:String, name:String, tel:String, pw:String){
        UserService().join(User(id,name,tel,pw), object : RetrofitCallback<Boolean> {
            override fun onError(t: Throwable) {
                Log.d(TAG, t.message?:"회원가입 통신오류")
            }

            override fun onSuccess(code: Int, responseData: Boolean) {
                toast("회원가입이 완료되었습니다. 다시 로그인 해주세요")
                (requireActivity() as LoginActivity).onBackPressed()
            }

            override fun onFailure(code: Int) {
                Log.d(TAG, "onFailure: resCode $code")
            }

        })
    }
    private fun isValid(id: String?, pass: String?, nick: String?, tel:String?): Boolean {
        if(id.isNullOrBlank()) {
            toast("id를 입력해 주세요")
            return false
        }
        if(checkedId == false) {
            toast("id 중복체크를 해주세요")
            return false
        }
        if(pass.isNullOrBlank()) {
            toast("비밀번호를 입력해 주세요")
            return false
        }
        if(nick.isNullOrBlank()) {
            toast("이름을 입력해 주세요")
            return false
        }
        if(tel.isNullOrBlank()){
            toast("휴대전화를 입력해 주세요")
        }
        return true
    }
    fun toast(msg: String) {
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
    }
}
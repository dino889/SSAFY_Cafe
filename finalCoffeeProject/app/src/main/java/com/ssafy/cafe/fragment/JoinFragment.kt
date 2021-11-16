package com.ssafy.cafe.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.ssafy.cafe.R
import com.ssafy.cafe.activity.LoginActivity
import com.ssafy.cafe.databinding.FragmentJoinBinding
import com.ssafy.cafe.dto.User
import com.ssafy.cafe.service.UserService
import com.ssafy.cafe.util.RetrofitCallback
import retrofit2.Response.error

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
        setupListeners()
        binding.btnJoin.setOnClickListener {
            if(isValidate()){
                join(binding.etUserID.text.toString(),binding.etUserName.text.toString(), binding.etUserTel.text.toString(), binding.etUserPw.text.toString())
            }
        }
    }
    private fun isValidate():Boolean = validateUserID() && validateUserPW() && validateUserTel()
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
    private fun doubleCheckID(id:String?) : Boolean{
        UserService().checkId(id!!, object: RetrofitCallback<Boolean>{
            override fun onError(t: Throwable) {
                Log.d(TAG, t.message?: "아이디 체크 통신오류")
            }

            override fun onSuccess(code: Int, responseData: Boolean) {
                true
            }

            override fun onFailure(code: Int) {
                false
            }
        })
        return false
    }
    private fun setupListeners(){
        binding.etUserID.addTextChangedListener(TextFieldValidation(binding.etUserID))
        binding.etUserPw.addTextChangedListener(TextFieldValidation(binding.etUserPw))
        binding.etUserTel.addTextChangedListener(TextFieldValidation(binding.etUserTel))
    }

    fun toast(msg: String) {
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
    }
    inner class TextFieldValidation(private val view: View):TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            when(view.id){
                R.id.et_userID -> {
                    validateUserID()
                }
                R.id.et_userID -> {
                    validateUserPW()
                }
                R.id.et_userTel -> {
                    validateUserTel()
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {
        }

    }
    private fun validateUserID() : Boolean{
        if(binding.etUserID.text.toString().trim().isEmpty()){
            binding.userIDtextlayout.error = "Required Field"
            binding.etUserID.requestFocus()
            return false
        }else{
            if(doubleCheckID(binding.etUserID.text.toString())){
                binding.userIDtextlayout.isErrorEnabled = false
                checkedId = true
            }else{
                binding.userIDtextlayout.error = "이미 존재하는 아이디입니다."
                checkedId = false
            }
//            binding.userIDtextlayout.isErrorEnabled = false
        }
        return true
    }
    private fun validateUserPW() : Boolean{
        if(binding.etUserPw.text.toString().trim().isEmpty()){
            binding.userPWtextlayout.error = "Required Field"
            binding.etUserPw.requestFocus()
            return false
        }else{
            binding.userPWtextlayout.isErrorEnabled = false
        }
        return true
    }
    private fun validateUserTel() : Boolean{
        if(binding.etUserTel.text.toString().trim().isEmpty()){
            binding.userTeltextlayout.error = "Required Field"
            binding.etUserTel.requestFocus()
            return false
        }else{
            binding.userTeltextlayout.isErrorEnabled = false
        }
        return true
    }
}
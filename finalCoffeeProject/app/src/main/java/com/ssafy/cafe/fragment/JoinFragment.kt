package com.ssafy.cafe.fragment

import android.graphics.Color
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
import com.ssafy.cafe.config.BaseFragment
import com.ssafy.cafe.databinding.FragmentJoinBinding
import com.ssafy.cafe.dto.User
import com.ssafy.cafe.service.UserService
import com.ssafy.cafe.util.RetrofitCallback
import retrofit2.Response.error
import java.util.regex.Pattern

private const val TAG = "JoinFragment_싸피"
class JoinFragment : BaseFragment<FragmentJoinBinding>(FragmentJoinBinding::bind, R.layout.fragment_join) {
    //    lateinit var binding: FragmentJoinBinding
    private var dupChkId = false    // Id 중복 확인 여부 체크, true - 중복O, false - 중복X

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentJoinBinding.inflate(inflater,container,false)
//        return binding.root
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()

//        binding.ibtnIdDupChk.setOnClickListener{
//            validateUserID()
//        }

        // JOIN 버튼 클릭
        binding.btnJoin.setOnClickListener {
            if(isValidate()){
                join(binding.etUserID.text.toString(),binding.etUserName.text.toString(), binding.etUserTel.text.toString(), binding.etUserPw.text.toString())
            }
        }
    }

    //
    fun makeToast(msg: String) {
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
    }

    // TextInputEditText listener 등록
    private fun setupListeners(){

        binding.etUserID.addTextChangedListener(TextFieldValidation(binding.etUserID))
        binding.etUserName.addTextChangedListener(TextFieldValidation(binding.etUserName))
        binding.etUserPw.addTextChangedListener(TextFieldValidation(binding.etUserPw))
        binding.etUserTel.addTextChangedListener(TextFieldValidation(binding.etUserTel))
    }

    // id 중복, pw 기준 충족, 폰 번호 중복 체크 전부 true인 경우에 join 허용
    private fun isValidate():Boolean = validateUserID() && validateUserPW() && validateUserTel()

    // DB에 사용자 추가
    private fun join(id:String, name:String, tel:String, pw:String){
        UserService().join(User(id,name,tel,pw), object : RetrofitCallback<Boolean> {
            override fun onError(t: Throwable) {
                Log.d(TAG, t.message?:"회원가입 통신오류")
            }

            override fun onSuccess(code: Int, responseData: Boolean) {
                makeToast("회원가입이 완료되었습니다. 다시 로그인 해주세요")
                (requireActivity() as LoginActivity).onBackPressed()
            }

            override fun onFailure(code: Int) {
                Log.d(TAG, "onFailure: resCode $code")
            }

        })
    }

    // 사용자가 입력한 userId를 인자로 받아서 id 중복 체크
    inner class isUsedCallBack : RetrofitCallback<Boolean> {
        override fun onError(t: Throwable) {
            Log.d(TAG, "onError: ")
        }

        override fun onSuccess(code: Int, responseData: Boolean) {
            Log.d(TAG, "onSuccess: $responseData")  // 0 : 중복 X, 사용가능 <-> 1 : 중복되는 ID, 사용불가능
            if(responseData) {   // DB 내에 중복되는 ID가 없으면
                binding.userIDtextlayout.error = "이미 존재하는 아이디입니다."
                binding.etUserID.requestFocus()
                dupChkId = false
            } else {// DB 내에 중복되는 ID가 있으면
                binding.userIDtextlayout.error = null
                dupChkId = true
            }
//            dupChkId = responseData
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onFailure: ")
        }
    }

    // id 중복 체크
    private fun validateUserID() : Boolean{

        val inputUserId = binding.etUserID.text.toString()

        if(inputUserId.trim().isEmpty()){   // 값이 비어있으면 error
            binding.userIDtextlayout.error = "Required Field"
            binding.etUserID.requestFocus()
//                    return false
        } else {
            Log.d(TAG, "validateUserID: ${inputUserId}")
            UserService().isUsed(inputUserId, isUsedCallBack())
//            if(!dupChkId) {   // DB 내에 중복되는 ID가 없으면
//                binding.userIDtextlayout.error = null
//                dupChkId = true
//                binding.ibtnIdDupChk.setColorFilter(Color.GREEN)
//            } else {// DB 내에 중복되는 ID가 있으면
//                binding.userIDtextlayout.error = "이미 존재하는 아이디입니다."
//                binding.etUserID.requestFocus()
//                dupChkId = false
//                binding.ibtnIdDupChk.setColorFilter(Color.BLACK)
//
//            }
        }

        return dupChkId
    }

    // 비밀번호 유효성 체크
    private fun validateUserPW() : Boolean{
        val inputUserPw = binding.etUserPw.text.toString()

//        ^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&]).{8,15}.$
        if(inputUserPw.trim().isEmpty()){   // 값이 비어있으면
            binding.userPWtextlayout.error = "Required Field"
            binding.etUserPw.requestFocus()
            return false
        } else if(!Pattern.matches("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[\$@\$!%*#?&]).{8,50}.\$", inputUserPw)) {
            binding.userPWtextlayout.error = "비밀번호 형식을 확인해주세요."
            binding.etUserPw.requestFocus()
            return false
        }
        else {
            binding.userPWtextlayout.isErrorEnabled = false
        }
        return true
    }

    // 핸드폰 번호 중복 + 유효성 체크
    private fun validateUserTel() : Boolean{
        val inputUserTel = binding.etUserTel.text.toString()

        if(inputUserTel.trim().isEmpty()){
            binding.userTeltextlayout.error = "Required Field"
            binding.etUserTel.requestFocus()
            return false
        } else if(!Pattern.matches("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", inputUserTel)) {
            binding.userTeltextlayout.error = "휴대폰 번호 형식을 확인해주세요."
            binding.etUserTel.requestFocus()
            return false
        }
        else {
//            binding.userTeltextlayout.isErrorEnabled = false

            binding.userTeltextlayout.error = null
        }
        return true
    }

    // Name 텍스트 필드 값이 비어있는지 check
    private fun validateUserName() : Boolean {
        val inputUserName = binding.etUserName.text.toString()
        if(inputUserName.trim().isEmpty()){ // 값이 비어있으면
            binding.userNametextlayout.error = "Required Field"
            binding.etUserName.requestFocus()
            return false
        }else{
            binding.userNametextlayout.error = null
        }
        return true
    }


    inner class TextFieldValidation(private val view: View):TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            when(view.id){
                R.id.et_userID -> {
                    validateUserID()
                }
                R.id.et_userName -> {
                    validateUserName()
                }
                R.id.et_userTel -> {
                    validateUserTel()
                }
                R.id.et_userPw -> {
                    validateUserPW()
                }
            }
        }
    }
}
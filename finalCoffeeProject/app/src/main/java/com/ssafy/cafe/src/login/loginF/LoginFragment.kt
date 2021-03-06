package com.ssafy.cafe.src.login.loginF

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler
import com.ssafy.cafe.R
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.config.BaseFragment
import com.ssafy.cafe.databinding.FragmentLoginBinding
import com.ssafy.cafe.src.main.dto.User
import com.ssafy.cafe.src.main.network.service.UserService
import com.ssafy.cafe.util.RetrofitCallback
import org.json.JSONException

import org.json.JSONObject

import android.os.AsyncTask
import com.ssafy.cafe.src.login.LoginActivity


private const val TAG = "LoginFragment_싸피"
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::bind, R.layout.fragment_login) {
    private lateinit var loginActivity: LoginActivity
    private lateinit var mAuth: FirebaseAuth
    var mGoogleSignInClient: GoogleSignInClient? = null
    lateinit var mOAuthLoginInstance : OAuthLogin

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginActivity = context as LoginActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 구글 계정으로 로그인
        binding.btnGoogleLogin.setOnClickListener {
            initAuth()
        }

        // 카카오 계정으로 로그인
        binding.btnKakaoLogin.setOnClickListener{
            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
                UserApiClient.instance.loginWithKakaoTalk(requireContext(), callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
            }
        }

        // 네이버 계정으로 로그인
        mOAuthLoginInstance = OAuthLogin.getInstance()
        mOAuthLoginInstance.init(requireContext(), getString(R.string.naver_client_id), getString(R.string.naver_client_secret), getString(R.string.naver_client_name))
        binding.btnNaverLogin.setOAuthLoginHandler(mOAuthLoginHandler)

        //login
        binding.btnLogin.setOnClickListener {
            login(binding.etLoginID.text.toString(), binding.etLoginPW.text.toString())
        }
        //join
        binding.btnJoin.setOnClickListener {
            loginActivity.openFragment(2)
        }
    }

    // 기존 사용자 로그인
    fun login(loginId: String, loginPass: String) {

        val user = User(loginId, loginPass)
        UserService().login(user, LoginCallback())
    }

    inner class LoginCallback: RetrofitCallback<User> {
        override fun onSuccess( code: Int, user: User) {

            if (user.id != null) {
                showCustomToast("로그인 되었습니다.")

                // 로그인 시 user 정보 sp에 저장
                ApplicationClass.sharedPreferencesUtil.addUser(user)

                loginActivity.openFragment(1)
            }else{
                showCustomToast("ID 또는 패스워드를 확인해 주세요.")
            }
        }

        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "유저 정보 불러오는 중 통신오류")
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
        }
    }


    // google Login
    // 인증 초기화
    private fun initAuth() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_login_key))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)


        mAuth = FirebaseAuth.getInstance()
        signIn()
    }

    // 구글 로그인 창을 띄우는 작업
    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        requestActivity.launch(signInIntent)
    }


    // 구글 인증 결과 획득 후 동작 처리
    private val requestActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data = it.data

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    // 구글 인증 결과 성공 여부에 따른 처리
    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = mAuth.currentUser
                    if(user != null) {
                        val newUser = User(user.email.toString(), user.displayName.toString(), user.phoneNumber.toString(), user.uid)
                        UserService().isUsed(user.email!!, isUsedCallBack(newUser))
                    }
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }


    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // KAKAO 로그인
    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "로그인 실패", error)
        }
        else if (token != null) {
            Log.i(TAG, "로그인 성공 ${token.accessToken}")
            // 사용자 정보 가져오기
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Log.e(TAG, "사용자 정보 요청 실패", error)
                }
                else if (user != null) {

                    val newUser = User(user.kakaoAccount!!.email.toString(), user.kakaoAccount!!.profile!!.nickname.toString(), user.kakaoAccount!!.phoneNumber.toString(), user.id.toString())
                    UserService().isUsed(user.kakaoAccount!!.email!!,isUsedCallBack(newUser))

                    Log.i(TAG, "사용자 정보 요청 성공" +
                            "\n회원번호: ${user.id}" +
                            "\n이메일: ${user.kakaoAccount?.email}" +
                            "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                            "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}")
                }
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // NAVER 로그인
    @SuppressLint("HandlerLeak")
    val mOAuthLoginHandler: OAuthLoginHandler = object : OAuthLoginHandler() {
        override fun run(success: Boolean) {
            if (success) {
                val accessToken: String = mOAuthLoginInstance.getAccessToken(requireContext())
                Log.d(TAG, "run: $accessToken")
                RequestApiTask(requireContext(), mOAuthLoginInstance).execute()
            } else {
                val errorCode: String = mOAuthLoginInstance.getLastErrorCode(requireContext()).code
                val errorDesc = mOAuthLoginInstance.getLastErrorDesc(requireContext())
                Log.d(TAG, "run: errorCode:" + errorCode + ", errorDesc:" + errorDesc)


            }
        }
    }


    inner class RequestApiTask(private val mContext: Context, private val mOAuthLoginModule: OAuthLogin) :
        AsyncTask<Void?, Void?, String>() {
        override fun onPreExecute() {}

        override fun onPostExecute(content: String) {
            try {
                val loginResult = JSONObject(content)
                if (loginResult.getString("resultcode") == "00") {
                    val response = loginResult.getJSONObject("response")
                    val id = response.getString("id")
                    val email = response.getString("email")
                    val mobile = response.getString("mobile")
                    val nickname = response.getString("nickname")
                    Log.d(TAG, "onPostExecute: $id")
                    val newUser = User(email, nickname, mobile, id)
                    UserService().isUsed(email, isUsedCallBack(newUser))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        override fun doInBackground(vararg params: Void?): String {
            val url = "https://openapi.naver.com/v1/nid/me"
            val at = mOAuthLoginModule.getAccessToken(mContext)
            return mOAuthLoginModule.requestApi(mContext, at, url)
        }
    }


    // 사용자가 입력한 userId를 인자아서 id 중복 체크
    inner class isUsedCallBack(val user: User) : RetrofitCallback<Boolean> {
        override fun onError(t: Throwable) {
            Log.d(TAG, "onError: ")
        }

        override fun onSuccess(code: Int, responseData: Boolean) {
            Log.d(TAG, "onSuccess IsUsedId: $responseData")  // 0 : 중복 X, 사용가능 <-> 1 : 중복되는 ID, 사용불가능
            if(responseData == false){
                join(user.id, user.name, user.phone, user.pass)

            } else {
                login(user.id, user.pass)
            }
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onFailure: ")
        }
    }


    // DB에 사용자 추가
    private fun join(id:String, name:String, tel:String, pw:String){
        UserService().join(User(id,name,tel,pw), object : RetrofitCallback<Boolean> {
            override fun onError(t: Throwable) {
                Log.d(TAG, t.message?:"회원가입 통신오류")
            }

            override fun onSuccess(code: Int, responseData: Boolean) {
                login(id, pw)
            }

            override fun onFailure(code: Int) {
                Log.d(TAG, "onFailure: resCode $code")
            }

        })
    }

}
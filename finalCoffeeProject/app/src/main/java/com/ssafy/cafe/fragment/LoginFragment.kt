package com.ssafy.cafe.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ssafy.cafe.R
import com.ssafy.cafe.activity.LoginActivity
import com.ssafy.cafe.activity.MainActivity
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.databinding.FragmentLoginBinding
import com.ssafy.cafe.dto.User
import com.ssafy.cafe.service.UserService
import com.ssafy.cafe.util.RetrofitCallback
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.mindrot.jbcrypt.BCrypt
import java.time.chrono.JapaneseEra.values

private const val TAG = "LoginFragment_싸피"
class LoginFragment : Fragment() {
    private val RC_SIGN_IN = 9001
    private lateinit var auth: FirebaseAuth

    private lateinit var loginActivity: LoginActivity
    private lateinit var binding: FragmentLoginBinding
    lateinit var name:String
    lateinit var photo:String
    private lateinit var mAuth: FirebaseAuth
    var mGoogleSignInClient: GoogleSignInClient? = null

    var isDupChk = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginActivity = context as LoginActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGoogleLogin.setOnClickListener {
            initAuth()
            Log.d(TAG, "onViewCreated: 구글 로그인 버튼 클릭")
        }

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
                Toast.makeText(context,"로그인 되었습니다.", Toast.LENGTH_SHORT).show()
                // 로그인 시 user정보 sp에 저장
                ApplicationClass.sharedPreferencesUtil.addUser(user)
                loginActivity.openFragment(1)
            }else{
                Toast.makeText(context,"ID 또는 패스워드를 확인해 주세요.", Toast.LENGTH_SHORT).show()
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
        // Configure Google Sign In
        // 구글에서 로그인하는 초기 작업
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("908521205713-bjvpeaaptikbb8h0523300hdtj9p2p33.apps.googleusercontent.com")
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)


        mAuth = FirebaseAuth.getInstance()
        Log.d(TAG, "initAuth: ${mAuth}")
        signIn()
    }

    private fun signIn() {  // 구글 로그인 창을 띄우는 작업
        val signInIntent = mGoogleSignInClient!!.signInIntent
        requestActivity.launch(signInIntent)
        Log.d(TAG, "signIn: ")
//        startActivityForResult(signInIntent, 100)
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
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = mAuth.currentUser
                    Log.d(TAG, "firebaseAuthWithGoogle: ${user?.tenantId}")
                    chkUserId(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
//                    updateUI(null)
                }
            }
    }


    // 인증 성공 여부에 따른 화면 처리
    private fun chkUserId(user: FirebaseUser?) {
        if(user!=null){
            Log.d(TAG, "chkUserId: user 정보 받아옴")
            UserService().isUsed(user.email!!, isUsedCallBack(user))

//            val job = async(start = CoroutineStart.LAZY) {
//                UserService().isUsed(user.uid, isUsedCallBack(user))
//            }
//            Log.d(TAG, "chkUserId: before $isDupChk")
//            job.join()
//            Log.d(TAG, "chkUserId: after $isDupChk")

//            if(isDupChk == false) { // 중복되는 id가 없으면
//                val passwordHashed = BCrypt.hashpw(user.email, BCrypt.gensalt(20))
//                Log.d(TAG, "onSuccess: $isDupChk ${user.email}, $passwordHashed")
//                join(user.uid, user.displayName.toString(), user.phoneNumber.toString(), passwordHashed)
//            }
        }else{
            Log.d(TAG, "chkUserId: 구글 로그인이 필요합니다.")
        }
    }

    // 사용자가 입력한 userId를 인자로 받아서 id 중복 체크
    inner class isUsedCallBack(val user: FirebaseUser) : RetrofitCallback<Boolean> {
        override fun onError(t: Throwable) {
            Log.d(TAG, "onError: ")
        }

        override fun onSuccess(code: Int, responseData: Boolean) {
            Log.d(TAG, "onSuccess IsUsedId: $responseData")  // 0 : 중복 X, 사용가능 <-> 1 : 중복되는 ID, 사용불가능
            isDupChk = responseData // false 이면 중복되는 아이디가 없음.

            Log.d(TAG, "onSuccess isDupChk: $isDupChk")

            if(responseData == false){

//                val passwordHashed = BCrypt.hashpw(user.uid, BCrypt.gensalt(20))
//                Log.d(TAG, "onSuccess: $isDupChk ${user.email}, $passwordHashed")
                Log.d(TAG, "onSuccess: ${user.email.toString()} ${user.displayName.toString()} ${user.phoneNumber.toString()}")
                join(user.email.toString(), user.displayName.toString(), user.phoneNumber.toString(), user.email.toString())

            } else {
                // id가 중복되면 기존 사용자 -> id pw로 로그인 시키기
                login(user.email.toString(), user.email.toString())
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







//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if(requestCode == RC_SIGN_IN){
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            try{
//                val account = task.getResult(ApiException::class.java)!!
//                firebaseAuthWithGoogle(account.idToken!!)
//
//            }catch (e: ApiException){
//                e.printStackTrace()
//            }
//        }
//    }
//    private fun firebaseAuthWithGoogle(idToken: String?) {
//        val credential = GoogleAuthProvider.getCredential(idToken, null)
//        mAuth.signInWithCredential(credential)
//            .addOnCompleteListener(requireActivity()) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "signInWithCredential:success")
//                    val user = mAuth.currentUser
//                    updateUI(user)
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w(TAG, "signInWithCredential:failure", task.exception)
//                    updateUI(null)
//                }
//            }
//    }
//    private fun updateUI(user: FirebaseUser?) {
//        if(user!=null){
//            photo = user.photoUrl.toString()
//            name = user.displayName.toString()
//            Log.d(TAG, "updateUI: $photo, $name")
//            binding.btnGoogleLogin.isEnabled = true
////            binding.loginBtn.isEnabled = true
//        }else{
//            Toast.makeText(requireContext(),"LoginFailed",Toast.LENGTH_SHORT).show()
//        }
//    }
//    private fun initAuth(){
////         Configure Google Sign In
//        val gso = GoogleSignInOptions
//            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken("908521205713-bjvpeaaptikbb8h0523300hdtj9p2p33.apps.googleusercontent.com")
//            .requestEmail()
//            .build()
//
//        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
//        mAuth = FirebaseAuth.getInstance()
//        signIn()
//    }
//
//    private fun signIn(){
//        val signInIntent = mGoogleSignInClient!!.signInIntent
//        startActivityForResult(signInIntent, 100)
//    }
}
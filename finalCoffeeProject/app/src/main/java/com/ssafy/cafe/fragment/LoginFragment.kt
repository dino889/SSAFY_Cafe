package com.ssafy.cafe.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ssafy.cafe.activity.LoginActivity
import com.ssafy.cafe.activity.MainActivity
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.databinding.FragmentLoginBinding
import com.ssafy.cafe.dto.User
import com.ssafy.cafe.service.UserService
import com.ssafy.cafe.util.RetrofitCallback

private const val TAG = "LoginFragment"
class LoginFragment : Fragment() {
    private val RC_SIGN_IN = 9001
    private var googleSigninClient : GoogleSignInClient?= null
    private lateinit var auth: FirebaseAuth

    private lateinit var loginActivity: LoginActivity
    private lateinit var binding: FragmentLoginBinding

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
        auth = Firebase.auth
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("908521205713-bjvpeaaptikbb8h0523300hdtj9p2p33.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSigninClient = GoogleSignIn.getClient(requireContext(), gso)

        binding.btnGoogleLogin.setOnClickListener {
            val signInIntent = googleSigninClient?.getSignInIntent()
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        //login


        binding.btnLogin.setOnClickListener {
            login(binding.etLoginID.text.toString(), binding.etLoginPW.text.toString())
//            val intent = Intent(requireContext(), MainActivity::class.java)
//            startActivity(intent)
        }
        //join
        binding.btnJoin.setOnClickListener {
            loginActivity.openFragment(2)
        }
    }


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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            }catch (e: ApiException){
                e.printStackTrace()
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(idToken.toString(), null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) {
                if (it.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth!!.currentUser
                    Toast.makeText(requireContext(), "Login에 성공하였습니다.", Toast.LENGTH_SHORT).show()

                } else {

                    Toast.makeText(requireContext(), "Login에 실패하였습니다. 다시 입력해주세요", Toast.LENGTH_SHORT).show()
                    Log.w(TAG, "signInWithCredential:failure", it.exception)

                }
            }
    }
}
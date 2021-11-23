package com.ssafy.cafe.activity

import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.ssafy.cafe.R
import com.kakao.sdk.common.util.Utility
import com.ssafy.cafe.config.BaseActivity
import com.ssafy.cafe.databinding.ActivityLoadingBinding
import com.ssafy.cafe.databinding.ActivityMainBinding

class LoadingActivity : BaseActivity<ActivityLoadingBinding>(ActivityLoadingBinding::inflate) {
    private val SPLASH_TIME:Long = 4000 // 4000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_loading)

        Handler().postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, SPLASH_TIME)

    }

}
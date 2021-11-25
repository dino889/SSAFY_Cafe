package com.ssafy.cafe.src.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.ssafy.cafe.activity.LoginActivity
import com.ssafy.cafe.config.BaseActivity
import com.ssafy.cafe.databinding.ActivityLoadingBinding

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
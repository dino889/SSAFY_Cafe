package com.ssafy.cafe.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ssafy.cafe.R
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.databinding.ActivityMainBinding
import com.ssafy.cafe.fragment.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout_main, HomeFragment())
            .commit()

        binding.tabLayoutBottomNavigation.setOnNavigationItemSelectedListener {
            item ->
            when(item.itemId){
                R.id.navigation_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_main, HomeFragment())
                        .commit()
                    true
                }
                R.id.navigation_order -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_main, OrderFragment())
                        .commit()
                    true
                }
                R.id.navigation_pay -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_main, PayFragment())
                        .commit()
                    true
                }
                R.id.navigation_mypage -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_main, MyPageFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
        binding.tabLayoutBottomNavigation.setOnNavigationItemReselectedListener { item ->
            if(binding.tabLayoutBottomNavigation.selectedItemId != item.itemId){
                binding.tabLayoutBottomNavigation.selectedItemId = item.itemId
            }
        }

        initUserName()
        binding.ibtnNotificaton.setOnClickListener{
            openFragment(6)
        }
        binding.btnLogout.setOnClickListener {
            openFragment(5)
        }
    }
    fun initUserName(){
        var user = ApplicationClass.sharedPreferencesUtil.getUser()
        binding.tvUserName.text = "${user.name}님"
    }
    fun openFragment(index:Int, key:String, value:Int){
        moveFragment(index, key, value)
    }

    fun openFragment(index: Int) {
        moveFragment(index, "", 0)
    }
//
    private fun moveFragment(index:Int, key:String, value:Int){
        val transaction = supportFragmentManager.beginTransaction()
        when(index){
//            //장바구니
            1 -> transaction.replace(R.id.frame_layout_main, BucketFragment())
                .addToBackStack(null)
//            //주문 상세 보기
//            2 -> transaction.replace(R.id.frame_layout_main, OrderDetailFragment.newInstance(key, value))
//                .addToBackStack(null)
            //메뉴 상세 보기
            3 -> transaction.replace(R.id.frame_layout_main, MenuDetailFragment.newInstance(key, value))
                .addToBackStack(null)
//            //map으로 가기
//            4 -> transaction.replace(R.id.frame_layout_main, MapFragment())
//                .addToBackStack(null)
//            //logout
            5 -> {
                logout()
            }
            6->{ // 알람페이지이동
                transaction.replace(R.id.frame_layout_main, NotificationFragment())
                    .addToBackStack(null)
            }
            7 -> {
                //전체메뉴보기
                transaction.replace(R.id.tabFrameLayout, AllMenuFragment.newInstance(key, value))
                    .addToBackStack(null)
            }
            8 -> {
                //사용자메뉴보기
                transaction.replace(R.id.tabFrameLayout, UserCustomMenuFragment.newInstance(key, value))
                    .addToBackStack(null)
            }

        }
        transaction.commit()
    }
//
//    fun hideBottomNav(state : Boolean){
//        if(state) bottomNavigation.visibility =  View.GONE
//        else bottomNavigation.visibility = View.VISIBLE
//    }
//
    fun logout(){
        //preference 지우기
        ApplicationClass.sharedPreferencesUtil.deleteUser()

        //화면이동
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent)
    }
}
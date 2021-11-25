package com.ssafy.cafe.src.main.order

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.ssafy.cafe.R
import com.ssafy.cafe.activity.MainActivity
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.config.BaseFragment
import com.ssafy.cafe.databinding.FragmentMenuDetailBinding
import com.ssafy.cafe.src.main.network.response.MenuDetailWithCommentResponse
import com.ssafy.cafe.src.main.network.service.ProductService
import com.ssafy.cafe.util.RetrofitCallback

private const val TAG = "MenuDetailFragment_싸피"
class MenuDetailFragment : BaseFragment<FragmentMenuDetailBinding>(FragmentMenuDetailBinding::bind, R.layout.fragment_menu_detail) {
    private lateinit var mainActivity : MainActivity

    private var productId = -1


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.hideBottomNav(true)
        arguments?.let {
            productId = it.getInt("productId", -1)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ${productId}")
        initData()
    }

    private fun initData() {
        ProductService().getProductWithComments(productId, ProductWithCommentInsertCallback())
    }

    private fun initScreen(menu : MenuDetailWithCommentResponse) {
        Glide.with(this).load("${ApplicationClass.MENU_IMGS_URL}${menu.productImg}").into(binding.ivCafeMenuImg)
        binding.tvCafeMenuName.text = menu.productName
    }



    inner class ProductWithCommentInsertCallback :
        RetrofitCallback<List<MenuDetailWithCommentResponse>> {
        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "물품 정보를 받아오는 중 통신오류")
        }

        override fun onSuccess(code: Int, responseData: List<MenuDetailWithCommentResponse>) {
            viewModel.liveProductWithComment.value = responseData

            initScreen(viewModel.liveProductWithComment.value!![0])

            initTab()
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onFailure: Error Code $code")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNav(false)
    }


    fun initTab(){
        mainActivity.openFragment(9,"productId", productId)

        binding.menudetailTabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                Log.d(TAG, "onTabSelected: $productId")
                when(tab!!.position){
                    0 -> {
                        mainActivity.openFragment(9,"productId", productId)
                    }
                    1 -> mainActivity.openFragment(10, "productId", productId)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(key: String, value : Int) =
            MenuDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(key, value)
                }
            }
    }
}
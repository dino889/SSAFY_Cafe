package com.ssafy.cafe.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.ssafy.cafe.R
import com.ssafy.cafe.activity.MainActivity
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.databinding.FragmentMenuInfoDetailBinding
import com.ssafy.cafe.dto.ShoppingCart
import com.ssafy.cafe.response.MenuDetailWithCommentResponse
import com.ssafy.cafe.service.ProductService
import com.ssafy.cafe.util.CommonUtils
import com.ssafy.cafe.util.RetrofitCallback
import com.ssafy.cafe.viewmodel.MainViewModel

private const val TAG = "MenuInfoDetailFragment"
class MenuInfoDetailFragment : Fragment() {
    private lateinit var mainActivity : MainActivity
    private lateinit var binding:FragmentMenuInfoDetailBinding

    private var productId = -1
    private var productName = ""
    private var productImg = ""
    private var productType = ""

    private val viewModel: MainViewModel by activityViewModels()
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
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentMenuInfoDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        countProduct()
        Log.d(TAG, "onViewCreated: $productId")
        ProductService().getProductWithComments(productId, ProductWithCommentInsertCallback())

        binding.btnGotoBucket.setOnClickListener {
            var count = binding.tvCafeMenuCnt.text.toString().toInt()
            var pricetmp = binding.tvCafeMenuPrice.text.toString()
            var priceConvert = pricetmp.substring(0,pricetmp.length-1)
            var price = priceConvert.replace(",","").trim().toInt()

            val cart = ShoppingCart(productId,productImg,productName,count,price,count*price,productType)
            viewModel.insertShoppingCartItem(cart)
            mainActivity.openFragment(7)
        }
    }
    private fun initData(menu : MenuDetailWithCommentResponse) {
        binding.tvCafeMenuPrice.text = CommonUtils.makeComma(menu.productPrice)
        productImg = menu.productImg
        productName = menu.productName
        productType = menu.productType.toString()
    }

    private fun countProduct() {
        var tmp = binding.tvCafeMenuCnt.text.toString()
        var menuCnt = tmp[0].toString().toInt()
//        Log.d(TAG, "countProduct: ${tmp[0]} ****** ${tmp[1]}")
        binding.ibtnAddCount.setOnClickListener {
            menuCnt++
            binding.tvCafeMenuCnt.text = "${menuCnt}개"
        }

        binding.ibtnMinusCount.setOnClickListener {
            if(menuCnt <= 0) {
                Toast.makeText(requireContext(), "수량을 입력해주세요.", Toast.LENGTH_SHORT).show()
                binding.tvCafeMenuCnt.text = "0개"
            } else {
                menuCnt--
                binding.tvCafeMenuCnt.text = "${menuCnt}개"
            }
        }
    }
    companion object {
        @JvmStatic
        fun newInstance(key:String, value:Int) =
            MenuInfoDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(key, value)
                }
            }
    }

    inner class ProductWithCommentInsertCallback :
        RetrofitCallback<List<MenuDetailWithCommentResponse>> {
        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "물품 정보를 받아오는 중 통신오류")
        }

        override fun onSuccess(code: Int, responseData: List<MenuDetailWithCommentResponse>) {
            // comment가 없을 경우 -> 들어온 response가 1개이고 해당 userId가 null일 경우 빈 배열 adapter에 연결
//            commentAdapter = if(responseData.size == 1 && responseData[0].userId == null) {
//                CommentAdapter(mutableListOf(), this@MenuDetailFragment::initData)
//            } else {
//                CommentAdapter(responseData, this@MenuDetailFragment::initData)
//            }
//            liveData.value = responseData

            initData(responseData[0])
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onFailure: Error Code $code")
        }
    }

}
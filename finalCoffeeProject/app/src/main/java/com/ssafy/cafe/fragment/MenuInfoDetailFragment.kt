package com.ssafy.cafe.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
import android.widget.AdapterView
import android.widget.RadioButton

import android.widget.RadioGroup

private const val TAG = "MenuInfoDetailFragment_싸피"
class MenuInfoDetailFragment : Fragment() {
    private lateinit var mainActivity : MainActivity
    private lateinit var binding:FragmentMenuInfoDetailBinding

//    private var productId = -1
    private lateinit var productName : String
    private lateinit var productImg : String
    private lateinit var productType : String

    private var type: Int = 1  // hot - false, ice - true
    private var syrup : String? = null
    private var shot : Int? = null


    private val viewModel: MainViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.hideBottomNav(true)
//        arguments?.let {
//            productId = it.getInt("productId", -1)
//        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentMenuInfoDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        countProduct()
//        val product = viewModel.prodWithComment
        val product = viewModel.liveProductWithComment!!.value?.get(0)
//        Log.d(TAG, "onViewCreated: $productId")
//        ProductService().getProductWithComments(productId, ProductWithCommentInsertCallback())
        initData(product!!)

        initRadioGroup()

        initSpinner()

        // 장바구니 담기 버튼 클릭
        binding.btnGotoBucket.setOnClickListener {
            var countTmp = binding.tvCafeMenuCnt.text.toString()
            var count = countTmp.substring(0,countTmp.length-1).toInt() // 개수 선택
            var pricetmp = binding.tvCafeMenuPrice.text.toString()
            var priceConvert = pricetmp.substring(0,pricetmp.length-1)
            var price = priceConvert.replace(",","").trim().toInt() // 메뉴 가격

            var totalPrice = count * price

            if(syrup != null) {
                if(syrup!!.contains('+')){
                    syrup = "설탕"
                } else{
                    totalPrice += 500
                }
            }

            if(shot != null) {
                totalPrice += (shot!! * 500)
            }

//            val cart = ShoppingCart(product.productId, productImg, productName, count, price, totalPrice, productType, type, syrup, shot)
            val cart = ShoppingCart(product.productId, productImg, productName, count, price, totalPrice, type, syrup, shot)
            viewModel.insertShoppingCartItem(cart)
            mainActivity.openFragment(1)
        }

    }

    // 화면 set
    private fun initData(menu : MenuDetailWithCommentResponse) {
        binding.tvCafeMenuPrice.text = CommonUtils.makeComma(menu.productPrice)
        productImg = menu.productImg
        productName = menu.productName
        productType = menu.productType.toString()
    }

    // Init RadioGroup
    private fun initRadioGroup() {

        when(binding.hoticeGroup.checkedRadioButtonId) {
            binding.hot.id -> {
                type = 1
            }
            binding.ice.id -> {
                type = 0
            }
        }
    }

    // 수량 조절 버튼으로 Product 개수 조절
    private fun countProduct() {
        var tmp = binding.tvCafeMenuCnt.text.toString()
        var menuCnt = tmp[0].toString().toInt()

        binding.ibtnAddCount.setOnClickListener {
            menuCnt++
            binding.tvCafeMenuCnt.text = "${menuCnt}개"
        }

        binding.ibtnMinusCount.setOnClickListener {
            if(menuCnt <= 0) {
                Toast.makeText(requireContext(), "수량을 입력해주세요.", Toast.LENGTH_SHORT).show()
                menuCnt = 0
                binding.tvCafeMenuCnt.text = "${menuCnt}개"
            } else {
                menuCnt--
                binding.tvCafeMenuCnt.text = "${menuCnt}개"
            }
        }
    }

    // spinner
    fun initSpinner(){

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.syrup_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerSyrup.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.shot_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerShot.adapter = adapter
        }

        binding.chkBSyrup.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                binding.spinnerSyrup.visibility = View.VISIBLE

                binding.spinnerSyrup.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        syrup = parent.getItemAtPosition(position).toString()
                        Log.d(TAG, "onItemSelected: $syrup")
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                })
            } else {
                binding.spinnerSyrup.visibility = View.GONE

            }
        }

        binding.chkBShot.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                binding.spinnerShot.visibility = View.VISIBLE

                binding.spinnerShot.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        shot = parent.getItemAtPosition(position).toString().toInt()
                        Log.d(TAG, "onItemSelected: $shot")
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                })
            } else {
                binding.spinnerShot.visibility = View.GONE

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
}

package com.ssafy.cafe.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.cafe.R
import com.ssafy.cafe.activity.MainActivity
import com.ssafy.cafe.adapter.CustomMenuAdapter
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.config.BaseFragment
import com.ssafy.cafe.databinding.FragmentOrderDetailBinding
import com.ssafy.cafe.databinding.FragmentUserCustomMenuBinding
import com.ssafy.cafe.dto.Product
import com.ssafy.cafe.dto.ShoppingCart
import com.ssafy.cafe.dto.UserCustom
import com.ssafy.cafe.service.ProductService
import com.ssafy.cafe.service.UserCustomService
import com.ssafy.cafe.util.CommonUtils
import com.ssafy.cafe.util.RetrofitCallback
import com.ssafy.cafe.viewmodel.MainViewModel

private const val TAG = "UserCustomMenuFragment"
class UserCustomMenuFragment : BaseFragment<FragmentUserCustomMenuBinding>(FragmentUserCustomMenuBinding::bind, R.layout.fragment_user_custom_menu) {

//    private lateinit var binding: FragmentUserCustomMenuBinding
    private lateinit var customMenuAdapter: CustomMenuAdapter
    private lateinit var mainActivity: MainActivity
//    private val viewModel: MainViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        binding = FragmentUserCustomMenuBinding.inflate(layoutInflater,container,false)
//        return binding.root
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = ApplicationClass.sharedPreferencesUtil.getUser()
        val userId = user.id
        getCustomMenubyId(userId)

    }
    fun getCustomMenubyId(userId:String){
        UserCustomService().getCustomWithUserId(userId, object : RetrofitCallback<List<UserCustom>>{
            override fun onError(t: Throwable) {
                Log.d(TAG, "onError: ")
            }

            override fun onSuccess(code: Int, responseData: List<UserCustom>) {
                Log.d(TAG, "onSuccess: ")
                responseData.let {
                    customMenuAdapter = CustomMenuAdapter(responseData)
                }
                binding.rvUserCustomList.apply {
                    val linearLayoutManager = LinearLayoutManager(context)
                    linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
                    layoutManager =linearLayoutManager
                    adapter = customMenuAdapter
                    adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                }

                binding.btnGotoShoppingList.setOnClickListener {
                    for(i in 0.. responseData.size-1){
                        if(responseData.get(i).isChecked == true){
                            ProductService().getProductById(responseData.get(i).productId, GetProductCallback(responseData.get(i)))
                        }
                    }
                }

            }

            override fun onFailure(code: Int) {
                Log.d(TAG, "onFailure: ")
            }

        })
    }
    inner class GetProductCallback(val uc: UserCustom): RetrofitCallback<Product>{
        override fun onError(t: Throwable) {
            Log.d(TAG, "onError: ")
        }

        override fun onSuccess(code: Int, responseData: Product) {
            val cart = ShoppingCart(
                uc.productId,
                responseData.img,
                responseData.name,
                1,
                responseData.price,
                responseData.price,
                uc.type,
                uc.syrup,
                uc.shot
            )
            viewModel.insertShoppingCartItem(cart)
            mainActivity.openFragment(1)

        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onFailure: ")
        }

    }
}
package com.ssafy.cafe.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.cafe.R
import com.ssafy.cafe.activity.MainActivity
import com.ssafy.cafe.adapter.AllMenuAdapter
import com.ssafy.cafe.adapter.CategoryAdapter
import com.ssafy.cafe.config.BaseFragment
import com.ssafy.cafe.databinding.FragmentAllMenuBinding
import com.ssafy.cafe.dto.Category
import com.ssafy.cafe.dto.Product
import com.ssafy.cafe.service.ProductService
import com.ssafy.cafe.util.RetrofitCallback

private const val TAG = "AllMenuFragment_싸피"
class AllMenuFragment : BaseFragment<FragmentAllMenuBinding>(FragmentAllMenuBinding::bind, R.layout.fragment_all_menu) {
//    private lateinit var binding: FragmentAllMenuBinding
    private lateinit var categoryList:List<Category>
    private lateinit var categoryAdapter:CategoryAdapter
    private lateinit var mainActivity: MainActivity
    private lateinit var allMenuAdapter : AllMenuAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentAllMenuBinding.inflate(layoutInflater,container,false)
//        return binding.root
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCategoryAdapter()
    }

    private fun initData() {
        ProductService().getProductWithTypeList("coffee", ProductCallback())
    }

    private fun initCategoryAdapter(){
        initData()  // default : coffee

        categoryList = arrayListOf(
            Category(1,"coffee"),
            Category(2,"frappuccino"),
            Category(3,"ade"),
            Category(4,"tea"),
            Category(5,"dessert"))

        categoryAdapter = CategoryAdapter(categoryList)

        categoryAdapter.setItemClickListener(object : CategoryAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, categoryId: Int) {
                val category = categoryList[position]
                ProductService().getProductWithTypeList(category.category, ProductCallback())
            }
        })

        binding.rvCategoryMenu.apply{
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
            adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

    }


    inner class ProductCallback: RetrofitCallback<List<Product>> {
        override fun onSuccess( code: Int, productWithTypeList: List<Product>) {

            Log.d(TAG, "ProductCallback: $productWithTypeList")

            productWithTypeList.let {
                allMenuAdapter = AllMenuAdapter(productWithTypeList)

                allMenuAdapter.setItemClickListener(object : AllMenuAdapter.ItemClickListener {
                    override fun onClick(view: View, position: Int, productId:Int) {
                        Log.d(TAG, "onClick: $productId")
                        mainActivity.openFragment(3, "productId", productId)
                    }
                })
            }

            binding.rvCafeMenuList.apply {
                val linearLayoutManager = LinearLayoutManager(context)
                linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
                layoutManager = linearLayoutManager
                adapter = allMenuAdapter
                //원래의 목록위치로 돌아오게함
                adapter!!.stateRestorationPolicy =
                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }

        }

        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "상품 정보 불러오는 중 통신오류")
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
        }
    }
    
    
    
    
    
    companion object {
        @JvmStatic
        fun newInstance(key:String, value:Int) =
            AllMenuFragment().apply {
                arguments = Bundle().apply {
                    putInt(key, value)
                }
            }
    }
}
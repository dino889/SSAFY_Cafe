package com.ssafy.cafe.fragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.cafe.R
import com.ssafy.cafe.activity.MainActivity
import com.ssafy.cafe.adapter.ShoppingCartAdapter
import com.ssafy.cafe.databinding.FragmentBucketBinding
import com.ssafy.cafe.util.CommonUtils
import com.ssafy.cafe.viewmodel.MainViewModel

class BucketFragment : Fragment() {
    private lateinit var shoppingListRecyclerView: RecyclerView
    private lateinit var shoppingListAdapter : ShoppingCartAdapter
    private lateinit var binding: FragmentBucketBinding
    private lateinit var mainActivity: MainActivity
    private val viewModel: MainViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.hideBottomNav(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBucketBinding.inflate(inflater,container,false)
        return binding.root
    }

//    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // delete Btn click - 장바구니 리스트에서 삭제
        shoppingListAdapter = ShoppingCartAdapter().apply {
            list = viewModel.liveShoppingCartList.value!!
            cancelListener = object : ShoppingCartAdapter.ItemCancelListener{
                override fun onClick(position: Int) {
                    viewModel.removeShoppingCartItem(position)
                }
            }
        }

        binding.rvShoppinglist.apply {
            val linearLayoutManager = LinearLayoutManager(context)
            linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            layoutManager = linearLayoutManager
            adapter = shoppingListAdapter
            //원래의 목록위치로 돌아오게함
            adapter!!.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        viewModel.liveShoppingCartList.observe(viewLifecycleOwner) {
            shoppingListAdapter!!.notifyDataSetChanged()
            var totalPrice = 0
            var totalCnt = 0
            for(i in it) {
                totalCnt += i.menuCnt
                totalPrice += i.totalPrice
            }

            binding.btnOrder.text = "${totalCnt}개 총 ${CommonUtils.makeComma(totalPrice)}"
        }
    }


}
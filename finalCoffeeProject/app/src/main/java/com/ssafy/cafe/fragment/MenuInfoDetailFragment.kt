package com.ssafy.cafe.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ssafy.cafe.R
import com.ssafy.cafe.activity.MainActivity
import com.ssafy.cafe.databinding.FragmentMenuInfoDetailBinding

class MenuInfoDetailFragment : Fragment() {
    private lateinit var mainActivity : MainActivity
    private lateinit var binding:FragmentMenuInfoDetailBinding
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
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentMenuInfoDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        countProduct()
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
            AllMenuFragment().apply {
                arguments = Bundle().apply {
                    putInt(key, value)
                }
            }
    }

}
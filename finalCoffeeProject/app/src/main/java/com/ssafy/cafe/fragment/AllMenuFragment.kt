package com.ssafy.cafe.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.cafe.R
import com.ssafy.cafe.adapter.CategoryAdapter
import com.ssafy.cafe.databinding.FragmentAllMenuBinding
import com.ssafy.cafe.dto.Category

class AllMenuFragment : Fragment() {
    private lateinit var categoryList:List<Category>
    private lateinit var binding: FragmentAllMenuBinding
    private lateinit var categoryAdapter:CategoryAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAllMenuBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
    }
    fun initAdapter(){
        categoryList = arrayListOf(
            Category(1,"coffee"),
            Category(2,"blendded"),
            Category(3,"ade"),
            Category(4,"tea"),
            Category(5,"dessert"))

        categoryAdapter = CategoryAdapter(categoryList)
        binding.rvCategoryMenu.apply{
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
            adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
    }
    companion
    object {
        @JvmStatic
        fun newInstance(key:String, value:Int) =
            AllMenuFragment().apply {
                arguments = Bundle().apply {
                    putInt(key, value)
                }
            }
    }
}
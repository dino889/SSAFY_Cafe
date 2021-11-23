package com.ssafy.cafe.fragment

import android.content.Context
import android.os.Bundle
import android.util.JsonReader
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
//import com.google.gson.reflect.TypeToken
import com.ssafy.cafe.R
import com.ssafy.cafe.activity.MainActivity
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.config.BaseFragment
import com.ssafy.cafe.databinding.FragmentMyPageBinding
import com.ssafy.cafe.dto.Comment
import com.ssafy.cafe.dto.Order
import com.ssafy.cafe.dto.User
import com.ssafy.cafe.dto.UserLevel
import com.ssafy.cafe.service.CommentService
import com.ssafy.cafe.service.OrderService
import com.ssafy.cafe.service.UserService
import com.ssafy.cafe.util.CommonUtils
import com.ssafy.cafe.util.RetrofitCallback
import org.json.JSONException
import org.json.JSONObject
import java.io.StringReader
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

private const val TAG = "MyPageFragment"
class MyPageFragment : BaseFragment<FragmentMyPageBinding>(FragmentMyPageBinding::bind, R.layout.fragment_my_page) {
//    private lateinit var binding : FragmentMyPageBinding
    private lateinit var mainActivity: MainActivity

    var settingList = arrayListOf<Setting>(
        Setting("headphone","고객센터"),
        Setting("note","공지사항"),
        Setting("setting","환경설정"),
        Setting("version","버전정보")
    )
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentMyPageBinding.inflate(inflater,container,false)
//        return binding.root
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUserInfo()
        initAdapter()

        binding.btnPay.setOnClickListener {
            binding.btnPay.playAnimation()
            mainActivity.openFragment(11)
        }
        binding.btnMyreview.setOnClickListener {
            binding.btnMyreview.playAnimation()
            mainActivity.openFragment(12)
        }
        binding.btnOrderHistory.setOnClickListener {
            binding.btnOrderHistory.playAnimation()
            mainActivity.openFragment(13)
        }
    }

    fun initUserInfo(){
        var users = ApplicationClass.sharedPreferencesUtil.getUser()
        binding.tvUserProfilName.text = "${users.name}님"
//        getUsers(users.id)
        setUserPay()
        getCommentByUsers(users.id)
        getOrderbyUser(users.id)

    }
    fun initAdapter(){
        val settingAdapter = settingListViewAdapter(requireContext(), settingList)
        binding.lvSetting.adapter = settingAdapter
    }

    fun getCommentByUsers(id:String){

        CommentService().selectCommentByUser(id, object : RetrofitCallback<List<Comment>> {
            override fun onError(t: Throwable) {
                Log.d(TAG, "onError: ")
            }

            override fun onSuccess(code: Int, responseData: List<Comment>){
                binding.tvMyReviewViewCnt.text = responseData.size.toString()
            }

            override fun onFailure(code: Int) {
                Log.d(TAG, "onFailure: ")
            }
        })
    }
    private fun setUserPay() {

        viewModel.user.observe(viewLifecycleOwner) {
            binding.tvMyPayMoney.text = CommonUtils.makeComma(it.money)
        }

    }

//    fun getUsers(id:String){
//        UserService().getUsers(id, object : RetrofitCallback<HashMap<String, Any>> {
//            override fun onError(t: Throwable) {
//                Log.d(TAG, "onError: ")
//            }
//
//            override fun onSuccess(code: Int, responseData: HashMap<String, Any>) {
//                //Log.d(TAG, "onSuccess: $responseData")
//                //val grade = responseData!!["grade"]
//                val data = JSONObject(responseData as Map<*, *>)
//                val rawUser = data.getJSONObject("user")
//                val user = User(
//                    rawUser.getString("id"),
//                    rawUser.getString("name"),
//                    rawUser.getString("pass"),
//                    rawUser.getString("phone"),
//                    rawUser.getInt("stamps"),
//                    rawUser.getInt("money"),
//                    rawUser.getString("token")
//                )
//
//                var pay = user.money
//                binding.tvMyPayMoney.text = CommonUtils.makeComma(pay)
////                binding.tvOrderHistoryCnt.text = "${arr.length()}"
//                ApplicationClass.sharedPreferencesUtil.addUserPay(pay)
//            }
//
//            override fun onFailure(code: Int) {
//                Log.d(TAG, "onFailure: ")
//            }
//
//        })
//    }
    fun getOrderbyUser(id:String){
        OrderService().getOrderbyUser(id, object : RetrofitCallback<List<Order>>{
            override fun onError(t: Throwable) {
                Log.d(TAG, "onError: ")
            }

            override fun onSuccess(code: Int, responseData: List<Order>) {
                binding.tvOrderHistoryCnt.text = responseData.size.toString()
            }

            override fun onFailure(code: Int) {
                Log.d(TAG, "onFailure: ")
            }

        })
    }
    inner class settingListViewAdapter(val context: Context, val settingList:ArrayList<Setting>) : BaseAdapter(){
        override fun getCount(): Int {
            return settingList.size
        }

        override fun getItem(position: Int): Any {
            return settingList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, view: View?, p2: ViewGroup?): View {
            val view:View = LayoutInflater.from(context).inflate(R.layout.listview_mypage_list_item,null)
            val icon = view.findViewById<ImageView>(R.id.iconImg)
            val settingName = view.findViewById<TextView>(R.id.tv_settingName)

            val setting = settingList[position]
            val resourceImg = context.resources.getIdentifier(setting.img,"drawable",context.packageName)
            icon.setImageResource(resourceImg)
            settingName.text = setting.title.toString()

            return view
        }

    }
}
data class Setting(
    val img: String,
    val title:String
)

package com.ssafy.cafe.src.main

import android.Manifest
import android.app.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.*
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.user.UserApiClient
import com.ssafy.cafe.R
import com.ssafy.cafe.src.main.network.api.FirebaseTokenService
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.config.BaseActivity
import com.ssafy.cafe.databinding.ActivityMainBinding
import com.ssafy.cafe.src.main.network.response.LatestOrderResponse
import com.ssafy.cafe.src.main.network.service.OrderService
import com.ssafy.cafe.util.LocationPermissionManager
import com.ssafy.cafe.util.LocationServiceManager
import com.ssafy.cafe.src.main.network.service.ShakeDetector
import org.altbeacon.beacon.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.nhn.android.naverlogin.OAuthLogin

import android.os.AsyncTask
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LiveData
import com.ssafy.cafe.src.main.network.service.ProductService
import com.ssafy.cafe.src.main.network.service.UserService
import com.ssafy.cafe.src.login.LoginActivity
import com.ssafy.cafe.src.main.allmenu.AllMenuFragment
import com.ssafy.cafe.src.main.dto.*
import com.ssafy.cafe.src.main.home.HomeFragment
import com.ssafy.cafe.src.main.map.MapFragment
import com.ssafy.cafe.src.main.mypage.MyOrderHistoryFragment
import com.ssafy.cafe.src.main.mypage.MyPageFragment
import com.ssafy.cafe.src.main.mypage.MyReviewFragment
import com.ssafy.cafe.src.main.notification.NotificationFragment
import com.ssafy.cafe.src.main.order.*
import com.ssafy.cafe.src.main.pay.PayFragment
import com.ssafy.cafe.src.main.shoppinglist.ShoppingListFragment
import com.ssafy.cafe.src.main.usercustom.UserCustomMenuFragment
import com.ssafy.cafe.util.RetrofitCallback
import org.json.JSONObject


private const val TAG = "MainActivity_??????"
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate), BeaconConsumer {
    private val viewModel: MainViewModel by viewModels()
    var isChk = false
    //----------------------------------------------------------------------------------------------
    // Shake ?????? ??????
    private var accelerometerListener: Sensor? = null
    private var sensorManager: SensorManager?= null
    private var sensorEventListener: SensorEventListener?= null
    private var mShakeDetector: ShakeDetector?= null

    // ---------------------------------------------------------------------------------------------
    // Beacon ?????? ??????
    private lateinit var beaconManager: BeaconManager
    private val BEACON_UUID = "fda50693-a4e2-4fb1-afcf-c6eb07647825"
    private val BEACON_MAJOR = "10004"
    private val BEACON_MINOR = "54480"

    private val STORE_DISTANCE = 1
    private val region = Region("altbeacon",
        Identifier.parse(BEACON_UUID),
        Identifier.parse(BEACON_MAJOR),
        Identifier.parse(BEACON_MINOR))
//    private val region = Region("altbeacon", null, null, null)

    private lateinit var bluetoothManager: BluetoothManager
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var needBLERequest = true

    private val PERMISSIONS_CODE = 100
    private var isDialogCalled = false

    // ?????? ????????? ?????? ??????
    private val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

    // dialog
    private lateinit var dialog : Dialog

    // lastorder
    private var lastOrder : LatestOrderResponse? = null
    private var isLastOrderLoaded = false

    //----------------------------------------------------------------------------------------------
    // NFC ?????? ??????
    var nfcAdapter: NfcAdapter? = null
    var pIntent: PendingIntent? = null
    lateinit var filters: Array<IntentFilter>
    //    lateinit var orderTable : String
    var orderTable : String? = "x"
    var orderDetail : String?= "y"
    var charge: String? = "z"
    // Location
    lateinit var locationServiceManager: LocationServiceManager
    lateinit var locationPermissionManager: LocationPermissionManager

    lateinit var mOAuthLoginInstance : OAuthLogin

    private lateinit var userInfo : HashMap<String, Any>
    private lateinit var userInfoLiveData : LiveData<HashMap<String, Any>>


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ????????? ???????????? ?????????
        mOAuthLoginInstance = OAuthLogin.getInstance()
        mOAuthLoginInstance.init(this, getString(R.string.naver_client_id), getString(R.string.naver_client_secret), getString(R.string.naver_client_name))

        Log.d(TAG, "onCreate: DeviceToken: ")

        setNdef()

        getLastOrder()

        dialog = Dialog(this)

        setBeacon()

//        checkPermissions()

        // Location
        locationPermissionManager = LocationPermissionManager(this)
        locationServiceManager = LocationServiceManager(this)

        // user ?????? ????????????
//        viewModel.initUserLevel()
        initUserName()
        getUserInfo(ApplicationClass.sharedPreferencesUtil.getUser().id)
//        viewModel.getUserInfo(ApplicationClass.sharedPreferencesUtil.getUser().id)


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

        binding.ibtnNotificaton.setOnClickListener{
            openFragment(6)
        }
        binding.btnLogout.setOnClickListener {
            openFragment(5)
        }
        binding.ibMap.setOnClickListener {
            openFragment(4)
        }

        //shake ??????
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager?
        accelerometerListener = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mShakeDetector = ShakeDetector()
        mShakeDetector!!.setOnShakeListener(object : ShakeDetector.OnShakeListener {
            override fun onShake(count: Int) {
                //????????? ??? ??????????????? ???????????????
                //openFragment(11)
                showShakeNfcDialog()
            }
        })

        // FCM ?????? ??????
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "FCM ?????? ????????? ?????????????????????.", task.exception)
                return@OnCompleteListener
            }
            // token log ?????????
            Log.d(TAG, "token: ${task.result?:"task.result is null"}")
            uploadToken(task.result!!, ApplicationClass.sharedPreferencesUtil.getUser().id)
//            viewModel.token = task.result!!
        })
        createNotificationChannel(channel_id, "ssafy")

        // knell - notification Icon animation
        knell()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    // Notification ????????? ?????? ?????? ??????
    private fun createNotificationChannel(id: String, name: String) {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(id, name, importance)

        val notificationManager: NotificationManager
                = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun initUserName(){
        var user = ApplicationClass.sharedPreferencesUtil.getUser()
        Log.d(TAG, "initUserName: ${user.id}, ${user.name}, ${user.pass}, ${user.phone}")
        binding.tvUserName.text = "${user.name}???"
        Log.d(TAG, "initUserName: $user")
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
            // ????????????
            1 -> transaction.replace(R.id.frame_layout_main, ShoppingListFragment())
                .addToBackStack(null)
            // ?????? ?????? ??????
            2 -> transaction.replace(R.id.frame_layout_main, OrderDetailFragment.newInstance(key, value))
                .addToBackStack(null)
            // ?????? ?????? ??????
            3 -> transaction.replace(R.id.frame_layout_main, MenuDetailFragment.newInstance(key, value))
                .addToBackStack(null)
            // map?????? ??????
            4 -> transaction.replace(R.id.frame_layout_main, MapFragment())
                .addToBackStack(null)
            // logout
            5 -> {
                logout()
            }
            // ?????? ?????????
            6->{
                transaction.replace(R.id.frame_layout_main, NotificationFragment())
                    .addToBackStack(null)
            }
            // ?????? ?????? ?????????
            7 -> {
                transaction.replace(R.id.tabFrameLayout, AllMenuFragment.newInstance(key, value))
            }
            // ????????? ????????? ??????
            8 -> {
                transaction.replace(R.id.tabFrameLayout, UserCustomMenuFragment())
                    .addToBackStack(null)
            }
            // ?????? ?????? ??????
            9->{
                transaction.replace(R.id.fl_tablayout, MenuInfoDetailFragment.newInstance(key,value))
            }
            // ?????? ?????? ??????
            10->{
                transaction.replace(R.id.fl_tablayout, CommentFragment.newInstance(key, value))
            }
            // ????????? ?????? ??????
            11 -> {
                transaction.replace(R.id.frame_layout_main, PayFragment())
                    .addToBackStack(null)
            }
            // ???????????? ????????? ?????? ??????
            12 -> {
                transaction.replace(R.id.frame_layout_main, MyReviewFragment())
                    .addToBackStack(null)
            }
            // ????????? ?????? ?????? ??????
            13 ->{
                transaction.replace(R.id.frame_layout_main, MyOrderHistoryFragment())
                    .addToBackStack(null)
            }
        }
        transaction.commit()
    }

    fun hideBottomNav(state : Boolean){
        if(state) binding.tabLayoutBottomNavigation.visibility =  View.GONE
        else binding.tabLayoutBottomNavigation.visibility = View.VISIBLE
    }

    fun logout(){
        //preference ?????????
        ApplicationClass.sharedPreferencesUtil.deleteUser()

        // google ????????????
        FirebaseAuth.getInstance().signOut()

        // kakao ????????????
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e(TAG, "???????????? ??????. SDK?????? ?????? ?????????", error)
            }
            else {
                Log.i(TAG, "???????????? ??????. SDK?????? ?????? ?????????")
            }
        }

        // ????????? ????????????
        if (mOAuthLoginInstance != null) {
            mOAuthLoginInstance.logout(this)
            showCustomToast("???????????? ???????????????.")
            DeleteTokenTask(this, mOAuthLoginInstance).execute()
        }

        //????????????
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent)
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // Beacon ?????? ??????
    // ????????? ????????? ????????? 1M ???????????? dialog ????????? (?????? ?????? ????????? ???????????? ?????? ??????)

    // beacon manager, adapter ??????
    private fun setBeacon(){
        beaconManager = BeaconManager.getInstanceForApplication(this)
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"))
        bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
    }

    // ?????? ?????? ??????
    private fun checkPermissions(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                this,
                requiredPermissions,
                PERMISSIONS_CODE
            )
        }
    }

    // ???????????? ????????? ??????
    private fun isEnableBLEService(): Boolean{
        if(!bluetoothAdapter!!.isEnabled){
            Log.d(TAG, "isEnableBLEService: false ")
            return false
        }
        Log.d(TAG, "isEnableBLEService: true")
        return true
    }

    // Beacon Scan ??????
    private fun startScan() {
        // ???????????? Enable ??????
        if(!isEnableBLEService()){
            requestEnableBLE()
            Log.d(TAG, "startScan: ??????????????? ????????? ???????????????.")
            return
        }

        // ?????? ?????? ?????? ?????? ??? GPS Enable ?????? ??????
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                this,
                requiredPermissions,
                PERMISSIONS_CODE
            )
        }
        Log.d(TAG, "startScan: beacon Scan start")

        // Beacon Service bind
        beaconManager.bind(this)
    }

    // ???????????? ON/OFF ?????? ?????? ??? ????????? ?????? ??????
    private fun requestEnableBLE(){
        val callBLEEnableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        requestBLEActivity.launch(callBLEEnableIntent)
        Log.d(TAG, "requestEnableBLE: ")
    }

    private val requestBLEActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        // ???????????? ???????????? ????????? ???????????? ??????
        if (isEnableBLEService()) {
            needBLERequest = false
            startScan()
        }
    }

    // ?????? ?????? ?????? ?????? ?????? ?????? ??????
    // ActivityCompat.requestPermissions ?????? ?????? ??????
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_CODE -> {
                if(grantResults.isNotEmpty()) {
                    for((i, permission) in permissions.withIndex()) {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            //?????? ?????? ??????
                            Log.i(TAG, "$permission ?????? ????????? ?????????????????????.")
                            finish()
                        }
                    }
                }
            }
        }
    }

    override fun onBeaconServiceConnect() {
        beaconManager.addMonitorNotifier(object : MonitorNotifier {
            override fun didEnterRegion(region: Region?) {
                try {
                    Log.d(TAG, "????????? ?????????????????????.------------${region.toString()}")
                    beaconManager.startRangingBeaconsInRegion(region!!)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }

            }
            override fun didExitRegion(region: Region?) {
                try {
                    Log.d(TAG, "????????? ?????? ??? ????????????.")
                    beaconManager.stopRangingBeaconsInRegion(region!!)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
            override fun didDetermineStateForRegion(i: Int, region: Region?) {}
        })
        try {
            beaconManager.startMonitoringBeaconsInRegion(region)
            Log.d(TAG, "onBeaconServiceConnect: ${region.id1}")

        } catch (e: RemoteException){
            e.printStackTrace()
        }
        beaconManager.addRangeNotifier { beacons, region ->
            for (beacon in beacons) {
                // Major, Minor??? Beacon ??????, 1?????? ????????? ???????????? ????????? ??????

                if(isYourBeacon(beacon)){
                    if(isDialogCalled == false && isLastOrderLoaded == true) {
                        beaconScanStop()
                        runOnUiThread{
                            showDialog()
                        }

                    }
                }
            }

            if(beacons.isEmpty()){
                Log.d(TAG, "onBeaconServiceConnect: isEMpty")
            }
        }
    }

    // ssafyCafe ?????? Beacon??? ?????????, ????????? ?????? ???????????? ??????
    private fun isYourBeacon(beacon: Beacon): Boolean {
        Log.d(TAG, "isYourBeacon: ")
        return (beacon.id2.toString() == BEACON_MAJOR &&
                beacon.id3.toString() == BEACON_MINOR &&
                beacon.distance <= STORE_DISTANCE   // 1M??? ???????????? ?????? ????????????.
                )
    }


    // ---------------------------------------------------------------------------------------------
    // beacon ?????? ??? ?????? ?????? ????????? ?????? ?????? ?????? ????????? ?????????????????? ???????????? ??????
    private fun showDialog() {
        isDialogCalled = true

        dialog.setContentView(R.layout.dialog_notice_info)

        var menuImg = dialog.findViewById<ImageView>(R.id.dialog_menuImg)
        var menuName = dialog.findViewById<TextView>(R.id.dialog_menuName)
        var menuPrice = dialog.findViewById<TextView>(R.id.dialog_menuPrice)
        var confirm = dialog.findViewById<TextView>(R.id.dialog_confirm)

        if(lastOrder != null) {
            dialog.findViewById<LinearLayout>(R.id.dialog_lastOrder).visibility = View.VISIBLE
            Glide.with(this@MainActivity).load("${ApplicationClass.MENU_IMGS_URL}${lastOrder!!.img}").into(menuImg)
            menuName.text = lastOrder!!.productName
            menuPrice.text = "${lastOrder!!.orderCnt}???"
        } else {
            dialog.findViewById<LinearLayout>(R.id.dialog_lastOrder).visibility = View.GONE
            dialog.findViewById<TextView>(R.id.dialog_tmp).visibility = View.VISIBLE
        }

        confirm.setOnClickListener {
            dialog.dismiss()
        }
            dialog.show()

        if(!this.isFinishing){
            dialog.show()
        }
        if(isFinishing){
            dialog.show()
        }
    }

    fun getLastOrder() {
        val live = OrderService().getLastMonthOrder(ApplicationClass.sharedPreferencesUtil.getUser().id)
        Log.d(TAG, "getLastOrder: ${live.value.toString()}")
        live.observe(this) {
            if(it == null){
                lastOrder = null
            }
            if(it != null && it.isNotEmpty()) lastOrder = it[0] // ?????? ?????? ?????? ?????? 1???
//            Log.d(TAG, "getLastOrder: ${it[0]}")
            isLastOrderLoaded = true
        }
    }

    // beacon ?????? ??????
    private fun beaconScanStop() {
        beaconManager.stopMonitoringBeaconsInRegion(region)
        beaconManager.stopRangingBeaconsInRegion(region)
        beaconManager.unbind(this)
    }


    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // NFC ?????? ??????
    private fun setNdef(){

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        // ??????????????? ?????? ????????? ?????? ??????
        val i = Intent(this, MainActivity::class.java)  // mainActivity ?????? ????????? ???????????? ????????? ??????????????? mainA ??????
        i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        pIntent = PendingIntent.getActivity(this, 0, i, 0) // ????????? ???????????? ?????? ???????????????

        val ndf_filter = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        ndf_filter.addDataType("text/plain")

        filters = arrayOf(ndf_filter)

    }

    // NFC ??????
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent!!.action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            Log.d(TAG, "onNewIntent: ")
            getNFCData(intent = intent)
        }
    }


    // Tag Data ???????????? ??????
    private fun getNFCData(intent: Intent) {
        // Tag??? ??????????????? ??? ????????? ??????
        if (intent.action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)

            if (rawMsgs != null) {
                val message = arrayOfNulls<NdefMessage>(rawMsgs.size)
                for (i in rawMsgs.indices) {
                    message[i] = rawMsgs[i] as NdefMessage
                }
                // ?????? ???????????? ?????? ???????????? ??????
                val record_data = message[0]!!.records[0]
                val record_type = record_data.type
                val type = String(record_type)
                if (type.equals("T")) {
                    val data = message[0]!!.records[0].payload
                    Log.d(TAG, "getNFCData: ${String(data)}")
                    val type_data = String(data,1,2)
                    Log.d(TAG, "getNFCData type: $type_data")
                    if(type_data.equals("en")){ // ????????? ??????
                        Log.d(TAG, "getNFCData: $data")
                        orderTable = String(data, 3, data.size - 3)
                        viewModel.nfcTaggingData = orderTable
                        Log.d(TAG, "getNFCData: $orderTable")
                    }
                    else if(type_data.equals("kr")){    // ?????? ??????
                        orderDetail = String(data,3,data.size-3)
                        Log.d(TAG, "getNFCData_kr: $orderDetail")
                        viewModel.nfcTaggingData = orderDetail
                    }
                    else if(type_data.equals("ca")){
                        charge = String(data,3,data.size-3)
                        viewModel.nfcTaggingData = charge
                    }

                }
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // shake nfc dialog -> ???????????? ????????? nfc ?????? ????????? ??? ??? ????????? shake ?????? ??????
    fun showShakeNfcDialog(){

        var listener = DialogInterface.OnClickListener { _, p1 ->
            when(p1){
                DialogInterface.BUTTON_POSITIVE ->{
                    if(viewModel.nfcTaggingData.equals("")) {   // NFC ???????????? ????????? ???????????? ?????????
                        showCustomToast("Table NFC??? ???????????????.")
                    } else {
                        isChk = true
                        makeOrderDto()
                        dialog.cancel()

                    }
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    dialog.cancel()
                    disableNfc()
                }
            }
        }

        var builder = AlertDialog.Builder(this)
        builder.setTitle("????????????")
        builder.setView(R.layout.dialog_nfc_order)
        builder.setPositiveButton("??????",listener)
        builder.setNegativeButton("??????",listener)
        builder.show()
        enableNfc()
    }

    // ?????? ?????? ??????
    fun makeOrderDto(){
        var orderDetailList:ArrayList<OrderDetail> = arrayListOf()

        var order = Order(
            0,
            ApplicationClass.sharedPreferencesUtil.getUser().id,
            "",
            System.currentTimeMillis().toString(),
            0,
            orderDetailList
        )
        val ood = viewModel.nfcTaggingData.toString()
        Log.d(TAG, "makeOrderDto: $ood")
        val dataStr = ood.split("/")
        Log.d(TAG, "makeOrderDto: $dataStr")
        if(dataStr[5] != "??????")
        orderDetailList.add(

            OrderDetail(
                0,
                order.id,
                dataStr.get(2).toInt(), //productId
                dataStr.get(3).toInt(), //quantity
                dataStr.get(4).toInt(), //type(hot,ice)
                dataStr.get(5).toString(),  //syrup
                dataStr.get(6).toInt(),  //shot
                0
            )
        )

        ProductService().getProductById(dataStr.get(2).toInt(), GetProductCallback(dataStr.get(3).toInt(),order))
    }

    inner class GetProductCallback(val quanty: Int, val order: Order): RetrofitCallback<Product> {
        override fun onError(t: Throwable) {
            Log.d(TAG, "onError: ")
        }

        override fun onSuccess(code: Int, responseData: Product) {
            var totalPrice = responseData.price * quanty   // ?????? ??????
            val od = order.details[0]
            if(od.syrup != null) {
                if(od.syrup != "??????"){
                    totalPrice += 500
                }
            }

            if(od.shot != null) {
                totalPrice += (od.shot!! * 500)
            }
            od.totalPrice = totalPrice
            var point = 0

            userInfoLiveData.observe(this@MainActivity) {

                val data = JSONObject(it as Map<*, *>)
                val rawUser = data.getJSONObject("user")

                val userPay = rawUser.getInt("money")
                val userStamp = rawUser.getInt("stamps")

                // ?????? ??????
                val levelList = UserLevel.userInfoList
                val listSize = levelList.size


                for (i in 0 until listSize) {
                    if (userStamp <= levelList[i].max) {
                        point = (totalPrice * levelList[i].point * 0.01).toInt()
                        break
                    }
                }

                if (userPay - totalPrice >= 0) {  // ?????? ?????? - ?????? ????????? 0?????? ?????? ?????? ??????
                    val balance = (userPay - totalPrice) + point  // ?????? ?????? - ?????? ??????
                    completedOrder(order, balance)
                } else{
                    showCustomToast("?????? ????????? ???????????????. ???????????? ????????? ??????????????????.")
                }

            }

        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onFailure: ")
        }
    }

    private fun completedOrder(order: Order, balance:Int){

        order.orderTable = "take-out"

        OrderService().insertOrder(order ,object : RetrofitCallback<Int> {
            override fun onError(t: Throwable) {
                Log.d(TAG, "onError: ")
            }

            override fun onSuccess(code: Int, responseData: Int) {
                showCustomToast("????????? ?????????????????????.")

                isChk = false
                viewModel.nfcTaggingData = ""

                // OrderFragment ??????
                openFragment(2,"orderId", responseData)

                // ?????? ????????????
                var updateUser = User(ApplicationClass.sharedPreferencesUtil.getUser().id, balance)

                UserService().updateMoney(updateUser , object : RetrofitCallback<User> {
                    override fun onError(t: Throwable) {
                        Log.d(TAG, "onError: ")
                    }

                    override fun onSuccess(code: Int, responseData: User) {
                        Log.d(TAG, "onSuccess: $responseData")
                        ApplicationClass.sharedPreferencesUtil.addUserPay(responseData.money)
                    }

                    override fun onFailure(code: Int) {
                        Log.d(TAG, "onFailure: ")
                    }
                })
            }
            override fun onFailure(code: Int) {
                Log.d(TAG, "onFailure: ")
            }
        })
    }

    fun enableNfc() {
        // NFC ??????????????? ?????? ?????????
        nfcAdapter!!.enableForegroundDispatch(this, pIntent, filters, null)
    }

    fun disableNfc() {
        // NFC ??????????????? ?????? ????????????
        nfcAdapter!!.disableForegroundDispatch(this)
    }


    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // ????????? ???????????? ?????? ??????
    inner class DeleteTokenTask(
        private val mContext: Context,
        private val mOAuthLoginModule: OAuthLogin
    ) :
        AsyncTask<Void?, Void?, Boolean>() {
        override fun onPostExecute(isSuccessDeleteToken: Boolean) {}
        override fun doInBackground(vararg params: Void?): Boolean {
            val isSuccessDeleteToken = mOAuthLoginModule.logoutAndDeleteToken(mContext)
            if (!isSuccessDeleteToken) {
                Log.d(TAG, "errorCode:" + mOAuthLoginModule.getLastErrorCode(mContext))
                Log.d(TAG, "errorDesc:" + mOAuthLoginModule.getLastErrorDesc(mContext))
            }
            return isSuccessDeleteToken
        }
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // notification ?????? ??? noti icon animation play
    private fun knell() {
        val intentFilter = IntentFilter("com.ssafy.cafe")
        val receiver = object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val action = intent!!.action
                binding.ibtnNotificaton.playAnimation()
            }
        }
        this.registerReceiver(receiver, intentFilter)
    }



    private fun getUserInfo(userId:String) {
//        CoroutineScope(Dispatchers.IO).launch {
            userInfoLiveData = UserService().getUsers(userId)
//            userInfoLiveData.observe(this@MainActivity, {
//                userInfo = it
//            })
//        }
    }


    fun refreshFragment(fragment: Fragment, fragmentManager: FragmentManager) {
        var ft: FragmentTransaction = fragmentManager.beginTransaction()
        ft.detach(fragment).attach(fragment).commit()
    }

    override fun onResume() {
        super.onResume()
        startScan()
        sensorManager?.registerListener(mShakeDetector,accelerometerListener,SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        sensorManager?.unregisterListener(mShakeDetector)
        disableNfc()

        beaconScanStop()
        super.onPause()
    }

    override fun onDestroy() {
        if(dialog.isShowing) {
            dialog.dismiss()
        }
        super.onDestroy()
    }

    companion object{
        const val channel_id = "ssafy_channel"
        fun uploadToken(token:String, userId: String){
            val storeService = ApplicationClass.retrofit.create(FirebaseTokenService::class.java)
            storeService.uploadToken(token, userId).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if(response.isSuccessful){
                        val res = response.body()
                        Log.d(TAG, "onResponse: $res")
                    } else {
                        Log.d(TAG, "onResponse: Error Code ${response.code()}")
                    }
                }
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d(TAG, t.message ?: "?????? ?????? ?????? ??? ????????????")
                }
            })
        }
    }
}
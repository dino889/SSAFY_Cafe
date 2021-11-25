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
import com.ssafy.cafe.fragment.*
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
import com.ssafy.cafe.dto.*
import com.ssafy.cafe.src.main.network.service.ProductService
import com.ssafy.cafe.src.main.network.service.UserService
import com.ssafy.cafe.src.login.LoginActivity
import com.ssafy.cafe.src.main.dto.*
import com.ssafy.cafe.src.main.mypage.MyOrderHistoryFragment
import com.ssafy.cafe.src.main.mypage.MyReviewFragment
import com.ssafy.cafe.src.main.order.OrderFragment
import com.ssafy.cafe.src.main.shoppinglist.ShoppingListFragment
import com.ssafy.cafe.util.RetrofitCallback
import org.json.JSONObject


private const val TAG = "MainActivity_싸피"
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate), BeaconConsumer {
    private val viewModel: MainViewModel by viewModels()
    var isChk = false
    //----------------------------------------------------------------------------------------------
    // Shake 관련 변수
    private var accelerometerListener: Sensor? = null
    private var sensorManager: SensorManager?= null
    private var sensorEventListener: SensorEventListener?= null
    private var mShakeDetector: ShakeDetector?= null

    // ---------------------------------------------------------------------------------------------
    // Beacon 관련 변수
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

    // 모든 퍼미션 관련 배열
    private val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

    // dialog
    private lateinit var dialog : Dialog

    // lastorder
    private var lastOrder : LatestOrderResponse? = null
    private var isLastOrderLoaded = false

    //----------------------------------------------------------------------------------------------
    // NFC 관련 변수
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

        // 네이버 계정으로 로그인
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

        // user 정보 받아오기
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

        //shake 기능
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager?
        accelerometerListener = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mShakeDetector = ShakeDetector()
        mShakeDetector!!.setOnShakeListener(object : ShakeDetector.OnShakeListener {
            override fun onShake(count: Int) {
                //쉐이크 시 프래그먼트 이동시키기
                //openFragment(11)
                showShakeNfcDialog()
            }
        })

        // FCM 토큰 수신
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "FCM 토큰 얻기에 실패하였습니다.", task.exception)
                return@OnCompleteListener
            }
            // token log 남기기
            Log.d(TAG, "token: ${task.result?:"task.result is null"}")
            uploadToken(task.result!!, ApplicationClass.sharedPreferencesUtil.getUser().id)
//            viewModel.token = task.result!!
        })
        createNotificationChannel(channel_id, "ssafy")

        // knell - notification Icon animation
        knell()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    // Notification 수신을 위한 체널 추가
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
        binding.tvUserName.text = "${user.name}님"
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
            // 장바구니
            1 -> transaction.replace(R.id.frame_layout_main, ShoppingListFragment())
                .addToBackStack(null)
            // 주문 상세 보기
            2 -> transaction.replace(R.id.frame_layout_main, OrderDetailFragment.newInstance(key, value))
                .addToBackStack(null)
            // 메뉴 상세 보기
            3 -> transaction.replace(R.id.frame_layout_main, MenuDetailFragment.newInstance(key, value))
                .addToBackStack(null)
            // map으로 가기
            4 -> transaction.replace(R.id.frame_layout_main, MapFragment())
                .addToBackStack(null)
            // logout
            5 -> {
                logout()
            }
            // 알람 페이지
            6->{
                transaction.replace(R.id.frame_layout_main, NotificationFragment())
                    .addToBackStack(null)
            }
            // 전체 메뉴 리스트
            7 -> {
                transaction.replace(R.id.tabFrameLayout, AllMenuFragment.newInstance(key, value))
            }
            // 사용자 커스텀 메뉴
            8 -> {
                transaction.replace(R.id.tabFrameLayout, UserCustomMenuFragment())
                    .addToBackStack(null)
            }
            // 메뉴 상세 정보
            9->{
                transaction.replace(R.id.fl_tablayout, MenuInfoDetailFragment.newInstance(key,value))
            }
            // 메뉴 리뷰 정보
            10->{
                transaction.replace(R.id.fl_tablayout, ReviewFragment.newInstance(key, value))
            }
            // 사용자 결제 정보
            11 -> {
                transaction.replace(R.id.frame_layout_main, PayFragment())
                    .addToBackStack(null)
            }
            // 사용자가 작성한 리뷰 정보
            12 -> {
                transaction.replace(R.id.frame_layout_main, MyReviewFragment())
                    .addToBackStack(null)
            }
            // 사용자 전체 주문 내역
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
        //preference 지우기
        ApplicationClass.sharedPreferencesUtil.deleteUser()

        // google 로그아웃
        FirebaseAuth.getInstance().signOut()

        // kakao 로그아웃
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
            }
            else {
                Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
            }
        }

        // 네이버 로그아웃
        if (mOAuthLoginInstance != null) {
            mOAuthLoginInstance.logout(this)
            showCustomToast("로그아웃 하셨습니다.")
            DeleteTokenTask(this, mOAuthLoginInstance).execute()
        }

        //화면이동
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent)
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // Beacon 관련 함수
    // 비콘과 사용자 거리가 1M 이내이면 dialog 띄우기 (매장 내에 비콘이 설치되어 있다 가정)

    // beacon manager, adapter 연결
    private fun setBeacon(){
        beaconManager = BeaconManager.getInstanceForApplication(this)
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"))
        bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
    }

    // 위치 권한 체크
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

    // 블루투스 켰는지 확인
    private fun isEnableBLEService(): Boolean{
        if(!bluetoothAdapter!!.isEnabled){
            Log.d(TAG, "isEnableBLEService: false ")
            return false
        }
        Log.d(TAG, "isEnableBLEService: true")
        return true
    }

    // Beacon Scan 시작
    private fun startScan() {
        // 블루투스 Enable 확인
        if(!isEnableBLEService()){
            requestEnableBLE()
            Log.d(TAG, "startScan: 블루투스가 켜지지 않았습니다.")
            return
        }

        // 위치 정보 권한 허용 및 GPS Enable 여부 확인
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

    // 블루투스 ON/OFF 여부 확인 및 키도록 하는 함수
    private fun requestEnableBLE(){
        val callBLEEnableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        requestBLEActivity.launch(callBLEEnableIntent)
        Log.d(TAG, "requestEnableBLE: ")
    }

    private val requestBLEActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        // 사용자의 블루투스 사용이 가능한지 확인
        if (isEnableBLEService()) {
            needBLERequest = false
            startScan()
        }
    }

    // 위치 정보 권한 요청 결과 콜백 함수
    // ActivityCompat.requestPermissions 실행 이후 실행
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_CODE -> {
                if(grantResults.isNotEmpty()) {
                    for((i, permission) in permissions.withIndex()) {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            //권한 획득 실패
                            Log.i(TAG, "$permission 권한 획득에 실패하였습니다.")
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
                    Log.d(TAG, "비콘을 발견하였습니다.------------${region.toString()}")
                    beaconManager.startRangingBeaconsInRegion(region!!)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }

            }
            override fun didExitRegion(region: Region?) {
                try {
                    Log.d(TAG, "비콘을 찾을 수 없습니다.")
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
                // Major, Minor로 Beacon 구별, 1미터 이내에 들어오면 메세지 출력

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

    // ssafyCafe 매장 Beacon이 맞는지, 정해둔 거리 내부인지 확인
    private fun isYourBeacon(beacon: Beacon): Boolean {
        Log.d(TAG, "isYourBeacon: ")
        return (beacon.id2.toString() == BEACON_MAJOR &&
                beacon.id3.toString() == BEACON_MINOR &&
                beacon.distance <= STORE_DISTANCE   // 1M안 비콘들을 모두 찾으시오.
                )
    }


    // ---------------------------------------------------------------------------------------------
    // beacon 감지 시 매장 안내 정보와 가장 최근 주문 내역을 다이얼로그로 보여주는 함수
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
            menuPrice.text = "${lastOrder!!.orderCnt}잔"
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
            if(it != null && it.isNotEmpty()) lastOrder = it[0] // 가장 최근 주문 내역 1건
//            Log.d(TAG, "getLastOrder: ${it[0]}")
            isLastOrderLoaded = true
        }
    }

    // beacon 스캔 중지
    private fun beaconScanStop() {
        beaconManager.stopMonitoringBeaconsInRegion(region)
        beaconManager.stopRangingBeaconsInRegion(region)
        beaconManager.unbind(this)
    }


    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // NFC 관련 함수
    private fun setNdef(){

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        // 포그라운드 기능 설정을 위한 코드
        val i = Intent(this, MainActivity::class.java)  // mainActivity 자기 자신이 처리하기 때문에 파라미타로 mainA 부여
        i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        pIntent = PendingIntent.getActivity(this, 0, i, 0) // 위임을 해주는데 나를 넘겨주겠다

        val ndf_filter = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        ndf_filter.addDataType("text/plain")

        filters = arrayOf(ndf_filter)

    }

    // NFC 감지
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent!!.action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            Log.d(TAG, "onNewIntent: ")
            getNFCData(intent = intent)
        }
    }


    // Tag Data 추출하는 함수
    private fun getNFCData(intent: Intent) {
        // Tag가 태깅되었을 때 데이터 추출
        if (intent.action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)

            if (rawMsgs != null) {
                val message = arrayOfNulls<NdefMessage>(rawMsgs.size)
                for (i in rawMsgs.indices) {
                    message[i] = rawMsgs[i] as NdefMessage
                }
                // 실제 저장되어 있는 데이터를 추출
                val record_data = message[0]!!.records[0]
                val record_type = record_data.type
                val type = String(record_type)
                if (type.equals("T")) {
                    val data = message[0]!!.records[0].payload
                    Log.d(TAG, "getNFCData: ${String(data)}")
                    val type_data = String(data,1,2)
                    Log.d(TAG, "getNFCData type: $type_data")
                    if(type_data.equals("en")){ // 테이블 번호
                        Log.d(TAG, "getNFCData: $data")
                        orderTable = String(data, 3, data.size - 3)
                        viewModel.nfcTaggingData = orderTable
                        Log.d(TAG, "getNFCData: $orderTable")
                    }
                    else if(type_data.equals("kr")){    // 매장 결제
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
    // shake nfc dialog -> 매장에서 빠르게 nfc 태깅 결제를 할 수 있도록 shake 기능 추가
    fun showShakeNfcDialog(){

        var listener = DialogInterface.OnClickListener { _, p1 ->
            when(p1){
                DialogInterface.BUTTON_POSITIVE ->{
                    if(viewModel.nfcTaggingData.equals("")) {   // NFC 태깅해서 테이블 데이터가 있으면
                        showCustomToast("Table NFC를 찍어주세요.")
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
        builder.setTitle("매장주문")
        builder.setView(R.layout.dialog_nfc_order)
        builder.setPositiveButton("주문",listener)
        builder.setNegativeButton("취소",listener)
        builder.show()
        enableNfc()
    }

    // 주문 객체 생성
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
        if(dataStr[5] != "설탕")
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
            var totalPrice = responseData.price * quanty   // 상품 가격
            val od = order.details[0]
            if(od.syrup != null) {
                if(od.syrup != "설탕"){
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

                // 등급 계산
                val levelList = UserLevel.userInfoList
                val listSize = levelList.size


                for (i in 0 until listSize) {
                    if (userStamp <= levelList[i].max) {
                        point = (totalPrice * levelList[i].point * 0.01).toInt()
                        break
                    }
                }

                if (userPay - totalPrice >= 0) {  // 현재 잔액 - 주문 금액이 0보다 크면 주문 가능
                    val balance = (userPay - totalPrice) + point  // 현재 잔액 - 주문 금액
                    completedOrder(order, balance)
                } else{
                    showCustomToast("현재 잔액이 부족합니다. 매장에서 잔액을 충전해주세요.")
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
                showCustomToast("주문이 완료되었습니다.")

                isChk = false
                viewModel.nfcTaggingData = ""

                // OrderFragment 전환
                openFragment(2,"orderId", responseData)

                // 잔액 업데이트
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
        // NFC 포그라운드 기능 활성화
        nfcAdapter!!.enableForegroundDispatch(this, pIntent, filters, null)
    }

    fun disableNfc() {
        // NFC 포그라운드 기능 비활성화
        nfcAdapter!!.disableForegroundDispatch(this)
    }


    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // 네이버 로그아웃 토큰 삭제
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
    // notification 수신 시 noti icon animation play
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
                    Log.d(TAG, t.message ?: "토큰 정보 등록 중 통신오류")
                }
            })
        }
    }
}
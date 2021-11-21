package com.ssafy.cafe.activity

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
import android.media.MediaPlayer
import android.nfc.NfcAdapter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.kakao.sdk.user.UserApiClient
import com.ssafy.cafe.R
import com.ssafy.cafe.api.NotificationApi
import com.ssafy.cafe.config.ApplicationClass
import com.ssafy.cafe.config.BaseActivity
import com.ssafy.cafe.databinding.ActivityMainBinding
import com.ssafy.cafe.fragment.*
import com.ssafy.cafe.response.LatestOrderResponse
import com.ssafy.cafe.service.OrderService
import com.ssafy.cafe.util.LocationPermissionManager
import com.ssafy.cafe.util.LocationServiceManager
import com.ssafy.cafe.viewmodel.MainViewModel
import com.ssafy.medical.service.ShakeDetector
import org.altbeacon.beacon.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "MainActivity"
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate), BeaconConsumer {
    private val TAG = "MainActivity_싸피"
//    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

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

    // Location
    lateinit var locationServiceManager: LocationServiceManager
    lateinit var locationPermissionManager: LocationPermissionManager


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)

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

        initUserName()
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
            uploadToken(task.result!!)
        })
        createNotificationChannel(channel_id, "ssafy")

//        Firebase.messaging.subscribeToTopic("order").addOnCompleteListener{task ->
//            var msg = getString(R.string.msg_subscribed)
//            if(!task.isSuccessful){
//                msg = getString(R.string.msg_subscribe_failed)
//            }
//            Log.d(TAG, "onCreate: ")
//            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
//        }
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
//            //장바구니
            1 -> transaction.replace(R.id.frame_layout_main, BucketFragment())
                .addToBackStack(null)
//            //주문 상세 보기
            2 -> transaction.replace(R.id.frame_layout_main, OrderDetailFragment.newInstance(key, value))
                .addToBackStack(null)
            //메뉴 상세 보기
            3 -> transaction.replace(R.id.frame_layout_main, MenuDetailFragment.newInstance(key, value))
                .addToBackStack(null)
//            //map으로 가기
            4 -> transaction.replace(R.id.frame_layout_main, MapFragment())
                .addToBackStack(null)
//            //logout
            5 -> {
                logout()
            }
            6->{ // 알람페이지이동
                transaction.replace(R.id.frame_layout_main, NotificationFragment())
                    .addToBackStack(null)
            }
            7 -> {
                //전체메뉴보기
                transaction.replace(R.id.tabFrameLayout, AllMenuFragment.newInstance(key, value))
                    .addToBackStack(null)
            }
            8 -> {
                //사용자메뉴보기
                transaction.replace(R.id.tabFrameLayout, UserCustomMenuFragment())
                    .addToBackStack(null)
            }
            9->{
                //메뉴상세정보보기
                transaction.replace(R.id.fl_tablayout, MenuInfoDetailFragment.newInstance(key,value))
                    .addToBackStack(null)
            }
            10->{
                //메뉴 리뷰보기
                transaction.replace(R.id.fl_tablayout, ReviewFragment.newInstance(key, value))
                    .addToBackStack(null)
            }
            11 -> { //페이프래그먼트
                transaction.replace(R.id.frame_layout_main, PayFragment())
                    .addToBackStack(null)
            }
            12 -> {
                transaction.replace(R.id.frame_layout_main, MyReviewFragment())
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

    // 찾고자 하는 Beacon이 맞는지, 정해둔 거리 내부인지 확인
    private fun isYourBeacon(beacon: Beacon): Boolean {

        Log.d(TAG, "isYourBeacon: ")
        return (beacon.id2.toString() == BEACON_MAJOR &&
                beacon.id3.toString() == BEACON_MINOR &&
                beacon.distance <= STORE_DISTANCE   // 1M안 비콘들을 모두 찾으시오.
                )
//        return (beacon.distance <= STORE_DISTANCE)
    }



    private fun showDialog() {
        isDialogCalled = true

        dialog.setContentView(R.layout.dialog_notice_info)

        var menuImg = dialog.findViewById<ImageView>(R.id.dialog_menuImg)
        var menuName = dialog.findViewById<TextView>(R.id.dialog_menuName)
        var menuPrice = dialog.findViewById<TextView>(R.id.dialog_menuPrice)
        var confirm = dialog.findViewById<TextView>(R.id.dialog_confirm)

//        dialog.findViewById<LinearLayout>(R.id.dialog_lastOrder).visibility = View.VISIBLE

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

    //shake nfc dialog
    fun showShakeNfcDialog(){

        var listener = DialogInterface.OnClickListener { _, p1 ->
            when(p1){
                DialogInterface.BUTTON_POSITIVE ->{

                }
                DialogInterface.BUTTON_NEGATIVE -> {

                }
            }
        }

        var builder = AlertDialog.Builder(this)
        builder.setTitle("매장주문")
        builder.setView(R.layout.dialog_nfc_order)
        builder.show()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
    }
    override fun onResume() {
        super.onResume()
        startScan()
        sensorManager?.registerListener(mShakeDetector,accelerometerListener,SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        sensorManager?.unregisterListener(mShakeDetector)

        beaconScanStop()
        super.onPause()
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
                    orderTable = String(data, 3, data.size - 3)
                    viewModel.nfcTaggingData = orderTable
//                    ApplicationClass.sharedPreferencesUtil.addOrderTable(orderTable!!)
                    Log.d(TAG, "getNFCData: $orderTable")
                }
            }
        }
    }


    companion object{
        // Notification Channel ID
        const val channel_id = "ssafy_channel"
        // ratrofit  수업 후 network 에 업로드 할 수 있도록 구성
        fun uploadToken(token:String){
            // 새로운 토큰 수신 시 서버로 전송
            val storeService = ApplicationClass.retrofit.create(NotificationApi::class.java)
            storeService.uploadToken(token).enqueue(object : Callback<String> {
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
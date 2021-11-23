package com.ssafy.cafe.fragment

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.ssafy.cafe.R
import com.ssafy.cafe.activity.MainActivity
import com.ssafy.cafe.databinding.FragmentMapBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.ssafy.cafe.config.BaseFragment
import com.ssafy.cafe.util.LocationPermissionManager
import com.ssafy.cafe.util.LocationServiceManager
import java.io.IOException
import java.util.*


private const val TAG = "MapFragment_싸피"
class MapFragment : BaseFragment<FragmentMapBinding>(FragmentMapBinding::bind, R.layout.fragment_map), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener{
    private lateinit var mainActivity: MainActivity

    private val UPDATE_INTERVAL = 1000
    private val FASTEST_UPDATE_INTERVAL = 500
    private lateinit var map : SupportMapFragment

    private var mMap:GoogleMap? = null
    private var placeMarker: Marker?= null
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var mCurrentLocation: Location? = null
    private lateinit var currentPosition: LatLng
    private val STORE_LOCATION = LatLng(36.10830144233874, 128.41827450414362)


    private lateinit var locationServiceManager: LocationServiceManager
    private lateinit var locationPermissionManager: LocationPermissionManager

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

        val view = inflater.inflate(R.layout.fragment_map,null)
        map = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationServiceManager = mainActivity.locationServiceManager
        locationPermissionManager = mainActivity.locationPermissionManager

        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = UPDATE_INTERVAL.toLong()
            smallestDisplacement = 10.0f
            fastestInterval = FASTEST_UPDATE_INTERVAL.toLong()
        }

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity)

        map.getMapAsync(this)

    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        setDefaultLocation()
        if (locationPermissionManager.checkPermission()) { // 1. 위치 퍼미션을 가지고 있는지 확인
            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식)
            startLocationUpdates() // 3. 위치 업데이트 시작
        } else {  //2. 권한이 없다면
            // 3-1. 사용자가 권한이 없는 경우에는
            locationPermissionManager.requestPermission(object: PermissionListener {
                override fun onPermissionGranted() {
                    startLocationUpdates()
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    showCustomToast("위치 권한이 거부되었습니다.")
                }

            })
        }

        mMap!!.setOnInfoWindowClickListener(this)
        if (mMap != null) mMap!!.uiSettings.isMyLocationButtonEnabled = true

    }

    private fun setMarker(location: LatLng, title: String, body: String) {
        val markerOptions = MarkerOptions()
        markerOptions.position(location)
        markerOptions.title(title)
        markerOptions.snippet(body)
        markerOptions.draggable(true)
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))

        mMap!!.addMarker(markerOptions)
    }

    private fun setDefaultLocation() {
        var lastLocation :Location
        if(locationPermissionManager.checkPermission()) {
            mFusedLocationClient.lastLocation.addOnSuccessListener { location:Location? ->
                if(location != null) lastLocation = location
            }
        }

        placeMarker?.remove()

        //초기 위치를 구미 캠퍼스
        val markerTitle = "SSAFY CAFE"
        val markerSnippet = getCurrentAddress(STORE_LOCATION)
        setMarker(STORE_LOCATION, markerTitle, markerSnippet)

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(STORE_LOCATION, 15f)
        mMap!!.moveCamera(cameraUpdate)
    }

    //위치정보 요청시 호출
    var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val locationList = locationResult.locations
            Log.d(TAG, "onLocationResult: $locationList  ${locationList.size}")
            if (locationList.size > 0) {
                val location = locationList[locationList.size - 1]
                currentPosition = LatLng(location.latitude, location.longitude)
            }
        }
    }

    private fun getCurrentAddress(latlng: LatLng): String {
        //지오코더: GPS를 주소로 변환
        val geocoder = Geocoder(mainActivity, Locale.getDefault())
        val addresses: List<Address>?
        try {
            addresses = geocoder.getFromLocation(
                latlng.latitude,
                latlng.longitude,
                1
            )
        } catch (ioException: IOException) {
            //네트워크 문제
            showCustomToast("지오코더 서비스 사용 불가")
            return "지오코더 사용불가"
        } catch (illegalArgumentException: IllegalArgumentException) {
            showCustomToast("잘못된 GPS 좌표")
            return "잘못된 GPS 좌표"
        }

        return if (addresses == null || addresses.isEmpty()) {
            showCustomToast("주소 발견 불가")
            "주소 발견 불가"
        } else {
            val address = addresses[0]
            address.getAddressLine(0).toString()
        }
    }

    private fun requestDirections(current: LatLng, desti: LatLng) {
        val intent = Intent(
            Intent.ACTION_VIEW, Uri.parse(
                "http://maps.google.com/maps?saddr=${current.latitude}, ${current.longitude}&daddr=${desti.latitude}, ${desti.longitude}"
            )
        )
        startActivity(intent)
    }

    private fun startLocationUpdates() {
        // 위치서비스 활성화 여부 check
        if (!locationServiceManager.isOnLocationService()) {
            locationServiceManager.requestServiceOn()
        } else {
            if (locationPermissionManager.checkPermission()) {
                mFusedLocationClient!!.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()!!
                )
                if (mMap != null) {
                    mMap!!.isMyLocationEnabled = true
                    mMap!!.uiSettings.isZoomControlsEnabled = true
                    mMap!!.uiSettings.isMapToolbarEnabled = true
                }

            }
        }
    }

    override fun onInfoWindowClick(p0: Marker) {
        showDialogStore(p0.position)
    }

    private fun showDialogStore(location: LatLng) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        val tel = "01080194628"
        builder.apply {
            setView(R.layout.dialog_store_info)
            setCancelable(true)

            val listener = DialogInterface.OnClickListener{_, p1 ->
                when(p1){
                    DialogInterface.BUTTON_POSITIVE ->{
                        if(ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.CALL_PHONE) == PERMISSION_GRANTED) {
                            startActivity(Intent("android.intent.action.CALL", Uri.parse("tel:$tel")))
                        } else {
                            TedPermission.create().setPermissions(Manifest.permission.CALL_PHONE).setPermissionListener(object : PermissionListener{
                                override fun onPermissionGranted() {
                                    startActivity(Intent("android.intent.action.CALL", Uri.parse("tel:$tel")))
                                }

                                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                                    startActivity(Intent("android.intent.action.DIAL", Uri.parse("tel:$tel")))
                                }
                            }).check()
                        }
                    }
                    DialogInterface.BUTTON_NEUTRAL -> {
                        if(this@MapFragment::currentPosition.isInitialized)
                            requestDirections(currentPosition, location)
                        else
                            showCustomToast("현재 위치를 찾을 수 없어 길찾기를 할 수 없습니다.")
                    }

                }
            }
            builder.setNeutralButton("길찾기",listener)
            builder.setPositiveButton("전화걸기", listener)
        }
        builder.create().show()
    }
}
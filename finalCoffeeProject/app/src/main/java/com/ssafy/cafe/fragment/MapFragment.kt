package com.ssafy.cafe.fragment

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.ssafy.cafe.R
import com.ssafy.cafe.activity.MainActivity
import com.ssafy.cafe.databinding.FragmentMapBinding

private const val TAG = "MapFragment"
class MapFragment : Fragment() , OnMapReadyCallback{
    private lateinit var binding: FragmentMapBinding
    private lateinit var mainActivity: MainActivity

    private val UPDATE_INTERVAL = 1000
    private val FASTEST_UPDATE_INTERVAL = 500

    private var map:GoogleMap? = null
    private var placeMarker: Marker?= null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var locationRequest: LocationRequest
    private var mCurrentLocation: Location? = null

    private lateinit var currentPosition: LatLng
    private val STORE_LOCATION = LatLng(36.10830144233874, 128.41827450414362)

    private var firstRendering: Boolean = true
    private lateinit var mapView: MapView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    override fun onMapReady(p0: GoogleMap) {
        map = p0

        setDefaultStoreLocation()

        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            requiredMapPermission[0]
        )

        if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "onMapReady: ")
            startLocationUpdates()
        }else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    requiredMapPermission[0]
                )
            ){
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("위치 권한 허용")
                    .setMessage("위치 권한 허용이 필요합니다.")
                    .setPositiveButton("확인"){_, _ ->
                        mapPermissionResult.launch(requiredMapPermission[0])
                    }
                val alertDialog = builder.create()
                Log.d(TAG, "onMapReady: ")
                alertDialog.show()
            }else{
                Log.d(TAG, "onMapReady: ")
                mapPermissionResult.launch(requiredMapPermission[0])
            }
        }
        map!!.uiSettings.isMyLocationButtonEnabled = true
    }

    private val PERMISSIONS_CODE = 100

    private val requiredMapPermission = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

    private val requestActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if(checkLocationServicesStatus()){
            startLocationUpdates()
        }
    }

    private val mapPermissionResult = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){
            result ->
        if(result){
            Log.d(TAG, "granted: ")
        }else{
            Log.d(TAG, "denined: ")
        }
    }
    private fun moveCamera(gMap: GoogleMap?, marker: Marker){
        gMap?.let{
            it.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        marker.position.latitude,
                        marker.position.longitude
                    ),
                    16f
                )
            )
            marker.showInfoWindow()
        }

    }

    private var locationCallback: LocationCallback = object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            val locationList = p0.locations
            if(locationList.size > 0){
                val location = locationList[locationList.size - 1]
                currentPosition = LatLng(location.latitude, location.longitude)
                val markerTitle: String = currentPosition.toString()
                val markerSnippets = "위도: ${location.latitude.toString()}, 경도: ${location.longitude}"

                Log.d(TAG, "onLocationResult: ")
                setCurrentLocation(location)
                mCurrentLocation = location
            }

        }
    }

    private fun startLocationUpdates(){
        if(!checkLocationServicesStatus()){
            Log.d(TAG, "startLocationUpdates: ")
            showDialogForLocationServiceSetting()
        }else{
            Log.d(TAG, "startLocationUpdates: ")
            if(checkMapPermission()){
                mFusedLocationClient?.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()
                )
                if(map!=null)map!!.isMyLocationEnabled = true
                if(map!=null)map!!.uiSettings.isZoomControlsEnabled = true
            }
        }
    }
    private fun checkLocationServicesStatus(): Boolean {
        val locationManager = (context as MainActivity).getSystemService(LOCATION_SERVICE) as LocationManager
        Log.d(TAG, "checkLocationServicesStatus: ")
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }

    fun setCurrentLocation(location: Location){
        placeMarker?.remove()

        val currentLatLng = LatLng(location.latitude, location.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(currentLatLng)
        markerOptions.title("SSAFY Cafe")
        markerOptions.snippet("자세한 정보는 클릭해주세요")
        markerOptions.draggable(false)

        Log.d(TAG, "setCurrentLocation: ")
        placeMarker = map!!.addMarker(markerOptions)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 15F)
        map!!.moveCamera(cameraUpdate)

    }
    private fun setDefaultStoreLocation(){
        val location = Location("")
        location.latitude = STORE_LOCATION.latitude
        location.longitude = STORE_LOCATION.longitude
        Log.d(TAG, "setDefaultStoreLocation: ")
        setCurrentLocation(location)
    }

    private fun checkMapPermission():Boolean{
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        Log.d(TAG, "checkMapPermission: $hasFineLocationPermission")
        return hasFineLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    private val GPS_ENABLE_REQUEST_CODE = 2001
    private var needRequest = false

    private fun showDialogForLocationServiceSetting(){
        val builder: androidx.appcompat.app.AlertDialog.Builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage(
            "앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
        )
        builder.setCancelable(true)
        builder.setPositiveButton("설정") { _, _ ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
        }
        builder.setNegativeButton("취소"
        ) { dialog, _ -> dialog.cancel() }
        builder.create().show()
    }
    override fun onStart() {
        super.onStart()
        if(checkMapPermission()){
            Log.d(TAG, "onStart: ")
            mFusedLocationClient?.requestLocationUpdates(locationRequest,locationCallback,null)
            if(map!=null && checkLocationServicesStatus()) map!!.isMyLocationEnabled = true
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
        mFusedLocationClient?.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        if(checkMapPermission()){

        }else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),android.Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(requireContext(), "이 앱을 실행하려면 위치 권한이 필요함",Toast.LENGTH_SHORT).show()
            }
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(requiredMapPermission[0]),
                GPS_ENABLE_REQUEST_CODE
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
}
package com.ssafy.cafe.util

import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class LocationServiceManager(val context: Context) {
    val locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager

    val requestServiceLauncher = (context as AppCompatActivity).registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        if(!isOnLocationService()) {
            Toast.makeText(context, "위치 서비스가 꺼져있어 현재 위치를 알 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    fun isOnLocationService(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun requestServiceOn() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage(
            "앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
        )
        builder.setCancelable(true)
        builder.setPositiveButton("설정") { _, _ ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            requestServiceLauncher.launch(callGPSSettingIntent)
        }
        builder.setNegativeButton("취소"
        ) { dialog, _ -> dialog.cancel() }
        builder.create().show()

    }
}

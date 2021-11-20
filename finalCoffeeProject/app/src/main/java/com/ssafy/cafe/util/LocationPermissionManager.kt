package com.ssafy.cafe.util

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission

class LocationPermissionManager(val context: Context) {
    val activity = context as AppCompatActivity

    fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(activity, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
    }

    fun requestPermission(permissionListener: PermissionListener) {
        TedPermission.create().setPermissions(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)
            .setPermissionListener(permissionListener).check()
    }

    fun goToDetail() {
        var appDetail = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${activity.packageName}"))
        appDetail.addCategory(Intent.CATEGORY_DEFAULT)
        appDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(appDetail)
    }

}

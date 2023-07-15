package com.example.ocr_module

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtil {
    /**
     * 권한 체크
     */
    fun checkPermissions(context : Context, permissionList : List<String>) : Boolean {
        permissionList.forEach {
            if(ContextCompat.checkSelfPermission(
                    context,
                    it) == PackageManager.PERMISSION_DENIED
            ){
                return false
            }
        }
        return false
    }

    /**
     * 권한 요청
     */
    fun requestPermission(activity: Activity, permissionList : List<String >){
        ActivityCompat.requestPermissions(activity, permissionList.toTypedArray(), 1)
    }
}
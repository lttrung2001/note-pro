package com.lttrung.notepro.utils

import android.app.ActivityManager
import android.content.Context


object ServiceUtils {
    fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return activityManager.getRunningServices(Int.MAX_VALUE).stream().anyMatch {
            it.service.className == serviceClass.name
        }
    }
}
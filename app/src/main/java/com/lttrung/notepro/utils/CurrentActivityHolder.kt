package com.lttrung.notepro.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle

class CurrentActivityHolder : Application.ActivityLifecycleCallbacks {
    companion object {
        @SuppressLint("StaticFieldLeak")
        var currentActivity: Activity? = null
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        currentActivity = activity
    }

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }
}
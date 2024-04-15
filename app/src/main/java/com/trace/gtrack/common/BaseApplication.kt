package com.trace.gtrack.common

import android.app.Application
import com.trace.gtrack.BuildConfig
import timber.log.Timber


abstract class BaseApplication : Application(), LogError {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsTree(this))
        }
    }
}
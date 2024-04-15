package com.trace.gtrack

import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.trace.gtrack.common.BaseApplication
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class GTrackApplication : BaseApplication(), LogError {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this);
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsTree(this))
        }
    }

    override fun log(message: String) {
        FirebaseCrashlytics.getInstance().log(message)
    }

    override fun recordException(t: Throwable) {
        FirebaseCrashlytics.getInstance().recordException(t)
    }
}
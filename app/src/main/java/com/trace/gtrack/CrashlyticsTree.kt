package com.trace.gtrack

import android.util.Log
import timber.log.Timber.DebugTree

class CrashlyticsTree(private val logError: LogError) : DebugTree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.WARN) {
            logError.log(message)
        }
        if (t != null) {
            logError.recordException(t)
        }
    }
}

interface LogError {
    fun log(message: String)
    fun recordException(t: Throwable)
}
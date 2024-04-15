package com.trace.gtrack.common.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

fun CoroutineScope.safeLaunch(
    error: (e: Exception) -> Unit = { },
    block: suspend CoroutineScope.() -> Unit,
) {
    try {
        this.launch {
            block.invoke(this)
        }
    } catch (e: Exception) {
        error.invoke(e)
        Timber.e(e, "safeLaunch Error")
    }

}

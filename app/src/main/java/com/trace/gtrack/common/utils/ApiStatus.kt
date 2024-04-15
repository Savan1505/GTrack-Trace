package com.trace.gtrack.common.utils

sealed class FailureStatus {
    data class ApiFailure(val message: String) : FailureStatus()
    object General : FailureStatus()
    object Network : FailureStatus()
    object Unauthorized : FailureStatus()
}
package com.trace.gtrack.ui.assignqr.rfid.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trace.gtrack.R
import com.trace.gtrack.common.utils.safeLaunch
import com.trace.gtrack.data.IAppRepository
import com.trace.gtrack.data.model.CommonResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RFIDViewModel @Inject constructor(
    private val iAppRepository: IAppRepository,
) : ViewModel() {
    private val mState = MutableLiveData<RFIDState>()
    val state: LiveData<RFIDState> = mState

    fun postRFIDCodeAPI(
        context: Context, apiKey: String,
        projectId: String,
        siteId: String,
        qRCode: String,
        rfidCode: String,
    ) {
        mState.value = RFIDState.Loading
        viewModelScope.safeLaunch {
            when (val result = iAppRepository.postRfidQRCodeMappingAPI(
                apiKey,
                projectId,
                siteId,
                qRCode,
                rfidCode
            )) {
                is CommonResult.Error -> {
                    mState.value = RFIDState.Error(result.message)
                }

                is CommonResult.Success -> {
                    mState.value = result.strMsg?.let { RFIDState.Success(it) }
                }

                null -> mState.value =
                    RFIDState.Error(context.getString(R.string.error_message))
            }
        }
    }
}

sealed class RFIDState {
    data class Success(val rfidMsg: String) : RFIDState()
    data class Error(val msg: String) : RFIDState()

    object Loading : RFIDState()
}

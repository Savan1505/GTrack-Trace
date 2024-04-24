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
    private val mStateAM = MutableLiveData<RFIDMaterialState>()
    val state: LiveData<RFIDState> = mState
    val stateAM: LiveData<RFIDMaterialState> = mStateAM

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

    fun postDeAssignMaterialTagAPI(
        context: Context, apiKey: String,
        projectId: String,
        siteId: String,
        userId: String,
        materialCode: String
    ) {
        mStateAM.value = RFIDMaterialState.Loading
        viewModelScope.safeLaunch {
            when (val result = iAppRepository.postDeAssignMaterialTagAPI(
                apiKey,
                projectId,
                siteId,
                userId, materialCode
            )) {
                is CommonResult.Error -> {
                    mStateAM.value = RFIDMaterialState.Error(result.message)
                }

                is CommonResult.Success -> {
                    mStateAM.value = result.strMsg?.let { RFIDMaterialState.Success(it) }
                }

                null -> mStateAM.value =
                    RFIDMaterialState.Error(context.getString(R.string.error_message))
            }
        }
    }
}

sealed class RFIDState {
    data class Success(val rfidMsg: String) : RFIDState()
    data class Error(val msg: String) : RFIDState()

    object Loading : RFIDState()
}

sealed class RFIDMaterialState {
    data class Success(val message: String) : RFIDMaterialState()
    data class Error(val msg: String) : RFIDMaterialState()

    object Loading : RFIDMaterialState()
}

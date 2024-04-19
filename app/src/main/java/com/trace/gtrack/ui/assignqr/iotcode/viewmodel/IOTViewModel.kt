package com.trace.gtrack.ui.assignqr.iotcode.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trace.gtrack.R
import com.trace.gtrack.common.utils.safeLaunch
import com.trace.gtrack.data.IAppRepository
import com.trace.gtrack.data.model.CommonResult
import com.trace.gtrack.data.model.ListResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IOTViewModel @Inject constructor(
    private val iAppRepository: IAppRepository,
) : ViewModel() {
    private val mState = MutableLiveData<IOTCodeState>()
    private val mStateAS = MutableLiveData<IOTAssignState>()
    val state: LiveData<IOTCodeState> = mState
    val stateAS: LiveData<IOTAssignState> = mStateAS

    fun getIOTCodeAPI(
        context: Context, apiKey: String,
    ) {
        mState.value = IOTCodeState.Loading
        viewModelScope.safeLaunch {
            when (val result = iAppRepository.getIOTCodeAPI(
                apiKey,
            )) {
                is ListResult.Error -> {
                    mState.value = IOTCodeState.Error(result.message)
                }

                is ListResult.Success -> {
                    mState.value =
                        result.lstResponse?.let { IOTCodeState.Success(it) }
                }

                null -> mState.value =
                    IOTCodeState.Error(context.getString(R.string.error_message))
            }
        }
    }

    fun postIotQRCodeMappingAPI(
        context: Context, apiKey: String,
        projectId: String,
        siteId: String,
        iotCode: String,
        qRCode: String
    ) {
        mStateAS.value = IOTAssignState.Loading
        viewModelScope.safeLaunch {
            when (val result = iAppRepository.postIotQRCodeMappingAPI(
                apiKey,
                projectId,
                siteId,
                iotCode,
                qRCode
            )) {
                is CommonResult.Error -> {
                    mStateAS.value = IOTAssignState.Error(result.message)
                }

                is CommonResult.Success -> {
                    mStateAS.value = result.strMsg?.let { IOTAssignState.Success(it) }
                }

                null -> mStateAS.value =
                    IOTAssignState.Error(context.getString(R.string.error_message))
            }
        }
    }

    fun postIotQRCodeReMappingAPI(
        context: Context, apiKey: String,
        projectId: String,
        siteId: String,
        iotCode: String,
        qRCode: String
    ) {
        mStateAS.value = IOTAssignState.Loading
        viewModelScope.safeLaunch {
            when (val result = iAppRepository.postIotQRCodeReMappingAPI(
                apiKey,
                projectId,
                siteId,
                iotCode,
                qRCode
            )) {
                is CommonResult.Error -> {
                    mStateAS.value = IOTAssignState.Error(result.message)
                }

                is CommonResult.Success -> {
                    mStateAS.value = result.strMsg?.let { IOTAssignState.Success(it) }
                }

                null -> mStateAS.value =
                    IOTAssignState.Error(context.getString(R.string.error_message))
            }
        }
    }
}

sealed class IOTCodeState {
    data class Success(val lstIOTResponse: List<String>) :
        IOTCodeState()

    data class Error(val msg: String) : IOTCodeState()

    object Loading : IOTCodeState()
}

sealed class IOTAssignState {
    data class Success(val iosMsg: String) :
        IOTAssignState()

    data class Error(val msg: String) : IOTAssignState()

    object Loading : IOTAssignState()
}

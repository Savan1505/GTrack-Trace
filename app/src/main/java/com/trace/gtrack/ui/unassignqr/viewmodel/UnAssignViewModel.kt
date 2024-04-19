package com.trace.gtrack.ui.unassignqr.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trace.gtrack.R
import com.trace.gtrack.common.utils.safeLaunch
import com.trace.gtrack.data.IAppRepository
import com.trace.gtrack.data.model.CommonResult
import com.trace.gtrack.data.model.MaterialCodeResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UnAssignViewModel @Inject constructor(
    private val iAppRepository: IAppRepository,
) : ViewModel() {
    private val mState = MutableLiveData<UnAssignState>()
    private val mStateAM = MutableLiveData<UnAssignMaterialState>()
    val state: LiveData<UnAssignState> = mState
    val stateAM: LiveData<UnAssignMaterialState> = mStateAM

    fun postMaterialCodeByQRCodeAPI(
        context: Context, apiKey: String,
        projectId: String,
        siteId: String,
        qRCode: String
    ) {
        mState.value = UnAssignState.Loading
        viewModelScope.safeLaunch {
            when (val result = iAppRepository.postMaterialCodeByQRCodeAPI(
                apiKey,
                projectId,
                siteId,
                qRCode
            )) {
                is MaterialCodeResult.Error -> {
                    mState.value = UnAssignState.Error(result.message)
                }

                is MaterialCodeResult.Success -> {
                    mState.value = result.materialCodeResponse.materialCode?.let { UnAssignState.Success(it) }
                }

                null -> mState.value =
                    UnAssignState.Error(context.getString(R.string.error_message))
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
        mStateAM.value = UnAssignMaterialState.Loading
        viewModelScope.safeLaunch {
            when (val result = iAppRepository.postDeAssignMaterialTagAPI(
                apiKey,
                projectId,
                siteId,
                userId, materialCode
            )) {
                is CommonResult.Error -> {
                    mStateAM.value = UnAssignMaterialState.Error(result.message)
                }

                is CommonResult.Success -> {
                    mStateAM.value = result.strMsg?.let { UnAssignMaterialState.Success(it) }
                }

                null -> mStateAM.value =
                    UnAssignMaterialState.Error(context.getString(R.string.error_message))
            }
        }
    }
}

sealed class UnAssignState {
    data class Success(val materialCode: String) : UnAssignState()
    data class Error(val msg: String) : UnAssignState()

    object Loading : UnAssignState()
}

sealed class UnAssignMaterialState {
    data class Success(val message: String) : UnAssignMaterialState()
    data class Error(val msg: String) : UnAssignMaterialState()

    object Loading : UnAssignMaterialState()
}

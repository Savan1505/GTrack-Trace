package com.trace.gtrack.ui.assignqr.materialcodetracker.viewmodel

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
class AssignViewModel @Inject constructor(
    private val iAppRepository: IAppRepository,
) : ViewModel() {
    private val mState = MutableLiveData<AssignState>()
    private val mStateAM = MutableLiveData<AssignMaterialState>()
    val state: LiveData<AssignState> = mState
    var lstMaterialCode: List<String> = ArrayList()
    val stateAM: LiveData<AssignMaterialState> = mStateAM
    var pageNumber: Int = 1
    private var pageSize: Int = 10

    fun postAssignedMaterialListAPI(
        context: Context, apiKey: String,
        projectId: String,
        siteId: String,
        searchString: String
    ) {
        mState.value = AssignState.Loading
        viewModelScope.safeLaunch {
            when (val result = iAppRepository.postAssignedMaterialListAPI(
                apiKey,
                projectId,
                siteId,
                pageNumber,
                pageSize,
                searchString
            )) {
                is ListResult.Error -> {
                    mState.value = AssignState.Error(result.message)
                }

                is ListResult.Success -> {
                    if (result.lstResponse != null) {
                        mState.value = result.lstResponse.let { AssignState.Success(it) }
                    } else {
                        mState.value = AssignState.Success(ArrayList())
                    }
                }

                null -> mState.value = AssignState.Error(context.getString(R.string.error_message))
            }
        }
    }

    fun postAssignMaterialTagAPI(
        context: Context, apiKey: String,
        projectId: String,
        siteId: String,
        qRCode: String,
        materialCode: String
    ) {
        mStateAM.value = AssignMaterialState.Loading
        viewModelScope.safeLaunch {
            when (val result = iAppRepository.postAssignMaterialTagAPI(
                apiKey,
                projectId,
                siteId,
                qRCode, materialCode
            )) {
                is CommonResult.Error -> {
                    mStateAM.value = AssignMaterialState.Error(result.message)
                }

                is CommonResult.Success -> {
                    mStateAM.value = result.strMsg?.let { AssignMaterialState.Success(it) }
                }

                null -> mStateAM.value =
                    AssignMaterialState.Error(context.getString(R.string.error_message))
            }
        }
    }
}

sealed class AssignState {
    data class Success(val lstMaterialCode: List<String>) : AssignState()
    data class Error(val msg: String) : AssignState()

    object Loading : AssignState()
}

sealed class AssignMaterialState {
    data class Success(val message: String) : AssignMaterialState()
    data class Error(val msg: String) : AssignMaterialState()

    object Loading : AssignMaterialState()
}

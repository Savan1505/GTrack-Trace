package com.trace.gtrack.ui.trackmaterial.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.trace.gtrack.R
import com.trace.gtrack.common.utils.safeLaunch
import com.trace.gtrack.data.IAppRepository
import com.trace.gtrack.data.model.CommonResult
import com.trace.gtrack.data.model.SearchMaterialResult
import com.trace.gtrack.data.network.request.InsertHandHeldDataRequest
import com.trace.gtrack.data.network.response.SearchMaterialResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TrackMaterialViewModel @Inject constructor(
    private val iAppRepository: IAppRepository,
) : ViewModel() {
    private val mState = MutableLiveData<TrackMaterialMaterialState>()
    val state: LiveData<TrackMaterialMaterialState> = mState
    private val mStateHH = MutableLiveData<HandHeldDataState>()
    val stateHH: LiveData<HandHeldDataState> = mStateHH
    private val mStateRFID = MutableLiveData<InsertRFIDMapState>()
    val stateRFID: LiveData<InsertRFIDMapState> = mStateRFID
    private val mStateMapResult = MutableLiveData<InsertMapResultState>()
    val stateMapResult: LiveData<InsertMapResultState> = mStateMapResult
    var lstInsertRFIDDataRequest: MutableList<InsertHandHeldDataRequest> = mutableListOf()
    var lstTrackMaterialResponse: List<SearchMaterialResponse> = ArrayList()
    var lstHandHeldDataRequest: List<InsertHandHeldDataRequest> = ArrayList()
    var totalSearchTime: String = ""
    fun postSearchMaterialCodeAPI(
        context: Context, apiKey: String, projectId: String, siteId: String, materialCode: String
    ) {
        mState.value = TrackMaterialMaterialState.Loading
        viewModelScope.safeLaunch {
            when (val result = iAppRepository.postSearchMaterialCodeAPI(
                apiKey, projectId, siteId, materialCode
            )) {
                is SearchMaterialResult.Error -> {
                    mState.value = TrackMaterialMaterialState.Error(result.message)
                }

                is SearchMaterialResult.Success -> {
                    mState.value = result.lstSearchMaterialResponse?.let {
                        TrackMaterialMaterialState.Success(
                            it
                        )
                    }
                }

                null -> mState.value =
                    TrackMaterialMaterialState.Error(context.getString(R.string.error_message))
            }
        }
    }

    fun postInsertHandheldDataAPI(
        context: Context, apiKey: String,
        projectId: String,
        siteId: String,

        ) {
        mStateHH.value = HandHeldDataState.Loading
        viewModelScope.safeLaunch {
            when (val result = iAppRepository.postInsertHandheldDataAPI(
                apiKey, projectId, siteId, lstHandHeldDataRequest
            )) {
                is CommonResult.Error -> {
                    mStateHH.value = HandHeldDataState.Error(result.message)
                }

                is CommonResult.Success -> {
                    mStateHH.value = result.strMsg?.let { HandHeldDataState.Success(it) }
                }

                null -> mStateHH.value =
                    HandHeldDataState.Error(context.getString(R.string.error_message))
            }
        }
    }

    fun postInsertRFIDDataAPI(
        context: Context, apiKey: String,
        projectId: String,
        siteId: String
    ) {
        mStateRFID.value = InsertRFIDMapState.Loading
        viewModelScope.safeLaunch {
            when (val result = iAppRepository.postInsertRFIDDataAPI(
                apiKey,
                projectId,
                siteId,
                lstInsertRFIDDataRequest,
            )) {
                is CommonResult.Error -> {
                    mStateRFID.value = InsertRFIDMapState.Error(result.message)
                }

                is CommonResult.Success -> {
                    mStateRFID.value = result.strMsg?.let { InsertRFIDMapState.Success(it) }
                }

                null -> mStateRFID.value =
                    InsertRFIDMapState.Error(context.getString(R.string.error_message))
            }
        }
    }

    fun postInsertMAPSearchResultAPI(
        context: Context, apiKey: String,
        projectId: String,
        siteId: String,
        userId: String,
        materialCode: String,
    ) {
        mStateMapResult.value = InsertMapResultState.Loading
        viewModelScope.safeLaunch {
            when (val result = iAppRepository.postInsertMAPSearchResultAPI(
                apiKey, projectId, siteId, userId, materialCode, totalSearchTime
            )) {
                is CommonResult.Error -> {
                    mStateMapResult.value = InsertMapResultState.Error(result.message)
                }

                is CommonResult.Success -> {
                    mStateMapResult.value = result.strMsg?.let { InsertMapResultState.Success(it) }
                }

                null -> mStateMapResult.value =
                    InsertMapResultState.Error(context.getString(R.string.error_message))
            }
        }
    }
}

sealed class TrackMaterialMaterialState {
    data class Success(val lstTrackMaterialResponse: List<SearchMaterialResponse>) :
        TrackMaterialMaterialState()

    data class Error(val msg: String) : TrackMaterialMaterialState()

    object Loading : TrackMaterialMaterialState()
}

sealed class HandHeldDataState {
    data class Success(val message: String) : HandHeldDataState()

    data class Error(val msg: String) : HandHeldDataState()

    object Loading : HandHeldDataState()
}

sealed class InsertRFIDMapState {
    data class Success(val rfidMsg: String) : InsertRFIDMapState()
    data class Error(val msg: String) : InsertRFIDMapState()

    object Loading : InsertRFIDMapState()
}

sealed class InsertMapResultState {
    data class Success(val mapResultMsg: String) : InsertMapResultState()
    data class Error(val msg: String) : InsertMapResultState()

    object Loading : InsertMapResultState()
}

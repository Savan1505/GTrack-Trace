package com.trace.gtrack.ui.trackmaterial.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.trace.gtrack.R
import com.trace.gtrack.common.utils.safeLaunch
import com.trace.gtrack.data.IAppRepository
import com.trace.gtrack.data.model.SearchMaterialResult
import com.trace.gtrack.data.network.response.SearchMaterialResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TrackMaterialViewModel @Inject constructor(
    private val iAppRepository: IAppRepository,
) : ViewModel() {
    private val mState = MutableLiveData<TrackMaterialMaterialState>()
    val state: LiveData<TrackMaterialMaterialState> = mState
    var lstTrackMaterialResponse:List<SearchMaterialResponse> = ArrayList()
    fun postSearchMaterialCodeAPI(
        context: Context, apiKey: String,
        projectId: String,
        siteId: String,
        materialCode: String
    ) {
        mState.value = TrackMaterialMaterialState.Loading
        viewModelScope.safeLaunch {
            when (val result = iAppRepository.postSearchMaterialCodeAPI(
                apiKey,
                projectId,
                siteId, materialCode
            )) {
                is SearchMaterialResult.Error -> {
                    mState.value = TrackMaterialMaterialState.Error(result.message)
                }

                is SearchMaterialResult.Success -> {
                    mState.value =
                        result.lstSearchMaterialResponse?.let { TrackMaterialMaterialState.Success(it) }
                }

                null -> mState.value =
                    TrackMaterialMaterialState.Error(context.getString(R.string.error_message))
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

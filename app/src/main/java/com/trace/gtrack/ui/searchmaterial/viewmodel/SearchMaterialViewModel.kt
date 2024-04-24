package com.trace.gtrack.ui.searchmaterial.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trace.gtrack.R
import com.trace.gtrack.common.utils.safeLaunch
import com.trace.gtrack.data.IAppRepository
import com.trace.gtrack.data.model.MaterialCodeResult
import com.trace.gtrack.data.model.SearchMaterialResult
import com.trace.gtrack.data.network.response.SearchMaterialResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchMaterialViewModel @Inject constructor(
    private val iAppRepository: IAppRepository,
) : ViewModel() {
    private val mState = MutableLiveData<SearchMaterialState>()
    private val mStateAM = MutableLiveData<SearchMaterialStartState>()
    val state: LiveData<SearchMaterialState> = mState
    val stateAM: LiveData<SearchMaterialStartState> = mStateAM

    fun postMaterialCodeByQRCodeAPI(
        context: Context, apiKey: String,
        projectId: String,
        siteId: String,
        qRCode: String
    ) {
        mState.value = SearchMaterialState.Loading
        viewModelScope.safeLaunch {
            when (val result = iAppRepository.postMaterialCodeByQRCodeAPI(
                apiKey,
                projectId,
                siteId,
                qRCode
            )) {
                is MaterialCodeResult.Error -> {
                    mState.value = SearchMaterialState.Error(result.message)
                }

                is MaterialCodeResult.Success -> {
                    mState.value = result.materialCodeResponse.MaterialCode?.let { SearchMaterialState.Success(it) }
                }

                null -> mState.value =
                    SearchMaterialState.Error(context.getString(R.string.error_message))
            }
        }
    }

    fun postSearchMaterialCodeAPI(
        context: Context, apiKey: String,
        projectId: String,
        siteId: String,
        materialCode: String
    ) {
        mStateAM.value = SearchMaterialStartState.Loading
        viewModelScope.safeLaunch {
            when (val result = iAppRepository.postSearchMaterialCodeAPI(
                apiKey,
                projectId,
                siteId, materialCode
            )) {
                is SearchMaterialResult.Error -> {
                    mStateAM.value = SearchMaterialStartState.Error(result.message)
                }

                is SearchMaterialResult.Success -> {
                    mStateAM.value =
                        result.lstSearchMaterialResponse?.let { SearchMaterialStartState.Success(it) }
                }

                null -> mStateAM.value =
                    SearchMaterialStartState.Error(context.getString(R.string.error_message))
            }
        }
    }
}

sealed class SearchMaterialState {
    data class Success(val materialCode: String) : SearchMaterialState()
    data class Error(val msg: String) : SearchMaterialState()

    object Loading : SearchMaterialState()
}

sealed class SearchMaterialStartState {
    data class Success(val lstSearchMaterialResponse: List<SearchMaterialResponse>) :
        SearchMaterialStartState()

    data class Error(val msg: String) : SearchMaterialStartState()

    object Loading : SearchMaterialStartState()
}

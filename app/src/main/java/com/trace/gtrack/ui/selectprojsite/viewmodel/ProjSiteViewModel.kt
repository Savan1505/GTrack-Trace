package com.trace.gtrack.ui.selectprojsite.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trace.gtrack.R
import com.trace.gtrack.common.utils.safeLaunch
import com.trace.gtrack.data.IAppRepository
import com.trace.gtrack.data.model.ProjectDetailsResult
import com.trace.gtrack.data.model.ProjectKeysResult
import com.trace.gtrack.data.model.SiteDetailByProjectResult
import com.trace.gtrack.data.network.response.ProjectDetailsResponse
import com.trace.gtrack.data.network.response.ProjectKeysResponse
import com.trace.gtrack.data.network.response.SiteDetailsByProjectResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProjSiteViewModel @Inject constructor(
    private val iAppRepository: IAppRepository,
) : ViewModel() {
    private val mState = MutableLiveData<SiteDetailByProjectState>()
    private val mStateProj = MutableLiveData<ProjDetailByProjectState>()
    val state: LiveData<SiteDetailByProjectState> = mState
    val stateProj: LiveData<ProjDetailByProjectState> = mStateProj

    fun postSiteDetailByProjectAPI(context: Context, apiKey:String,projectId:String, userId: String) {
        mState.value = SiteDetailByProjectState.Loading
        viewModelScope.safeLaunch {
            when (val result = iAppRepository.postSiteDetailByProjectAPI(apiKey,projectId,userId)) {
                is SiteDetailByProjectResult.Error -> {
                    mState.value = SiteDetailByProjectState.Error(result.message)
                }

                is SiteDetailByProjectResult.Success -> {
                    mState.value =
                        SiteDetailByProjectState.Success(result.siteDetailsCommonResponse.siteDetailsList)
                }

                null -> mState.value =
                    SiteDetailByProjectState.Error(context.getString(R.string.error_message))
            }
        }
    }

    fun getProjectKeysAPI(context: Context) {
        mStateProj.value = ProjDetailByProjectState.Loading
        viewModelScope.safeLaunch {
            when (val result = iAppRepository.getProjectKeysAPI()) {
                is ProjectKeysResult.Error -> {
                    mStateProj.value = ProjDetailByProjectState.Error(result.message)
                }

                is ProjectKeysResult.Success -> {
                    mStateProj.value =
                        ProjDetailByProjectState.Success(result.lstProjectKeysResponse)
                }

                null -> mStateProj.value =
                    ProjDetailByProjectState.Error(context.getString(R.string.error_message))
            }
        }
    }
}

sealed class SiteDetailByProjectState {
    data class Success(val lstSite: List<SiteDetailsByProjectResponse>?) :
        SiteDetailByProjectState()

    data class Error(val msg: String) : SiteDetailByProjectState()
    object Loading : SiteDetailByProjectState()
}

sealed class ProjDetailByProjectState {
    data class Success(val lstProject: List<ProjectKeysResponse>?) : ProjDetailByProjectState()
    data class Error(val msg: String) : ProjDetailByProjectState()
    object Loading : ProjDetailByProjectState()
}

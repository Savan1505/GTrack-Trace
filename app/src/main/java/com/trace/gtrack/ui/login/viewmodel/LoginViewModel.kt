package com.trace.gtrack.ui.login.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trace.gtrack.R
import com.trace.gtrack.common.utils.safeLaunch
import com.trace.gtrack.data.IAppRepository
import com.trace.gtrack.data.model.LoginAzureResult
import com.trace.gtrack.data.model.LoginResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val iAppRepository: IAppRepository,
) : ViewModel() {
    private val mState = MutableLiveData<LoginState>()
    val state: LiveData<LoginState> = mState

    fun postAppLoginAPI(context: Context, userName: String, password: String) {
        if (userName.trim().isEmpty()) {
            mState.value = LoginState.ErrorEnterUserName
            return
        }
        if (password.trim().isEmpty()) {
            mState.value = LoginState.ErrorEnterPassword
            return
        }
        mState.value = LoginState.Loading
        viewModelScope.safeLaunch {
            when (val result = iAppRepository.postAppLogin(userName, password)) {
                is LoginResult.Error -> {
                    mState.value = LoginState.Error(result.message)
                }

                is LoginResult.Success -> {
                    mState.value = LoginState.SuccessLogin
                }

                null -> mState.value = LoginState.Error(context.getString(R.string.error_message))
            }
        }
    }

    fun postAzureLoginAPI(context: Context, azureUserID: String) {
        mState.value = LoginState.Loading
        viewModelScope.safeLaunch {
            when (val result = iAppRepository.postAzureLogin(azureUserID)) {
                is LoginAzureResult.Error -> {
                    mState.value = LoginState.Error(result.message)
                }

                is LoginAzureResult.SuccessAzure -> {
                    mState.value = LoginState.SuccessAzureLogin
                }

                null -> mState.value = LoginState.Error(context.getString(R.string.error_message))
            }
        }
    }
}

sealed class LoginState {
    object SuccessLogin : LoginState()
    object SuccessAzureLogin : LoginState()
    data class Error(val msg: String) : LoginState()
    object Loading : LoginState()

    object ErrorEnterUserName : LoginState()
    object ErrorEnterPassword : LoginState()
}

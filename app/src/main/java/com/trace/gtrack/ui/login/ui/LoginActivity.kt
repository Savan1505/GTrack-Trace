package com.trace.gtrack.ui.login.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.trace.gtrack.R
import com.trace.gtrack.common.AppProgressDialog
import com.trace.gtrack.common.utils.makeWarningToast
import com.trace.gtrack.databinding.ActivityLoginBinding
import com.trace.gtrack.ui.forgotpassword.ForgotPasswordActivity
import com.trace.gtrack.ui.login.viewmodel.LoginState
import com.trace.gtrack.ui.login.viewmodel.LoginViewModel
import com.trace.gtrack.ui.selectprojsite.SelectProSiteActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observe()
        binding.tvForgotPwd.setOnClickListener {
            ForgotPasswordActivity.launch(this@LoginActivity)
        }
        binding.btnLogin.setOnClickListener {
            loginViewModel.postAppLoginAPI(
                this@LoginActivity,
                binding.edtUserName.text.toString(),
                binding.edtPassword.text.toString()
            )
        }
    }

    private fun observe() {
        loginViewModel.state.observe(this) {
            when (it) {
                LoginState.ErrorEnterUserName -> {
                    makeWarningToast(getString(R.string.error_username))
                }

                LoginState.ErrorEnterPassword -> {
                    makeWarningToast(getString(R.string.error_password))
                }

                is LoginState.Error -> {
                    AppProgressDialog.hide()
                    makeWarningToast(it.msg)
                }

                LoginState.Loading -> {
                    AppProgressDialog.show(this)
                }

                LoginState.SuccessLogin -> {
                    loginViewModel.postAzureLoginAPI(
                        this@LoginActivity,
                        "d1a4fef9-89af-4837-bc3d-5018ce81b83f"
                    )
                }

                LoginState.SuccessAzureLogin -> {
                    AppProgressDialog.hide()
                    SelectProSiteActivity.launch(this@LoginActivity)
                    finish()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun launch(context: Context) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }

    /*private fun createAuthRequest(loginHint: String?) {
        val authRequestBuilder = AuthorizationRequest.Builder(
            authStateManager.current.authorizationServiceConfiguration!!,
            clientId.get(),
            ResponseTypeValues.CODE,
            configuration.redirectUri
        )
            .setScope(configuration.scope)
            .setPromptValues("login")
        if (!TextUtils.isEmpty(loginHint)) {
            authRequestBuilder.setLoginHint(loginHint)
        }
        authRequest.set(authRequestBuilder.build())
    }*/
}
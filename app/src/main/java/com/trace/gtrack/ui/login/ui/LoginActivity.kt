package com.trace.gtrack.ui.login.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAccount
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.IPublicClientApplication.ISingleAccountApplicationCreatedListener
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.client.exception.MsalClientException
import com.microsoft.identity.client.exception.MsalException
import com.microsoft.identity.client.exception.MsalServiceException
import com.trace.gtrack.R
import com.trace.gtrack.common.AppProgressDialog
import com.trace.gtrack.common.utils.makeWarningToast
import com.trace.gtrack.data.persistence.IPersistenceManager
import com.trace.gtrack.databinding.ActivityLoginBinding
import com.trace.gtrack.ui.forgotpassword.ForgotPasswordActivity
import com.trace.gtrack.ui.login.viewmodel.LoginState
import com.trace.gtrack.ui.login.viewmodel.LoginViewModel
import com.trace.gtrack.ui.selectprojsite.ui.SelectProSiteActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    @Inject
    internal lateinit var persistenceManager: IPersistenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = resources.getColor(R.color.colorPrimary)
        observe()
        mSingleAccountApp()
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

        binding.tvLindeLogin.setOnClickListener {
            if (mSingleAccountApp != null) {
                mSingleAccountApp!!.signIn(
                    this@LoginActivity,
                    null,
                    scopes,
                    authInteractiveCallback
                )
            } else {
                mSingleAccountApp()
            }
        }

        binding.tvGetInTouch.setOnClickListener {
            val url = "https://garimasystem.com/" // URL to open in the browser
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    private val scopes: Array<String>
        private get() = "user.read".split(" ").toTypedArray()

    private fun loadAccount() {
        if (mSingleAccountApp == null) {
            return
        }
        mSingleAccountApp!!.getCurrentAccountAsync(object :
            ISingleAccountPublicClientApplication.CurrentAccountCallback {
            override fun onAccountLoaded(activeAccount: IAccount?) {
                // You can use the account data to update your UI or your app database.
                mAccount = activeAccount
                if (persistenceManager.getUserId().isNotEmpty()) {
                    mSingleAccountApp!!.signOut(object :
                        ISingleAccountPublicClientApplication.SignOutCallback {
                        override fun onSignOut() {
                            persistenceManager.saveUserId("")
                            persistenceManager.saveUserName("")
                            mAccount = null
                        }

                        override fun onError(exception: MsalException) {
                        }
                    })
                }
//                updateUI()
            }

            override fun onAccountChanged(priorAccount: IAccount?, currentAccount: IAccount?) {
                if (currentAccount == null) {
                    // Perform a cleanup task as the signed-in account changed.
                    makeWarningToast("Sign Out...Try again")
                }
            }

            override fun onError(exception: MsalException) {
                AppProgressDialog.hide()
                Log.e("GTrack AD : ", exception.toString())
            }
        })
    }

    private val authInteractiveCallback: AuthenticationCallback
        private get() = object : AuthenticationCallback {
            override fun onSuccess(authenticationResult: IAuthenticationResult) {
                AppProgressDialog.hide()
                /* Successfully got a token, use it to call a protected resource - MSGraph */
                Log.d("Savan", "Successfully authenticated")
                Log.d("Savan", "ID Token: " + authenticationResult.account.id)

                /* Update account */mAccount = authenticationResult.account
                //updateUI()
                /* call graph */callGraphAPI()
            }

            override fun onError(exception: MsalException) {
                AppProgressDialog.hide()
                /* Failed to acquireToken */
                if (exception.message.toString() == "An account is already signed in.") {
                    if (exception.errorCode == "invalid_parameter") {
                        mSingleAccountApp!!.signOut(object :
                            ISingleAccountPublicClientApplication.SignOutCallback {
                            override fun onSignOut() {
                                persistenceManager.saveUserId("")
                                persistenceManager.saveUserName("")
                                mAccount = null
                            }

                            override fun onError(exception: MsalException) {
                            }
                        })
                    } else {
                        callGraphAPI()
                    }
                } else {
                    Log.d(
                        "Savan",
                        "Authentication failed: $exception"
                    )
                    makeWarningToast(exception.toString())
                    if (exception is MsalClientException) {
                        /* Exception inside MSAL, more info inside MsalError.java */
                    } else if (exception is MsalServiceException) {
                        /* Exception when communicating with the STS, likely config issue */
                    }
                }
            }

            override fun onCancel() {
                AppProgressDialog.hide()
                /* User canceled the authentication */
                Log.d("Savan", "User cancelled login.")
            }
        }

    private fun callGraphAPI() {
        if (mAccount != null) {
            loginViewModel.postAzureLoginAPI(
                this@LoginActivity,
                mAccount!!.id
            )
        } else {
            makeWarningToast("Authentication failed,Please try again!")
        }
    }

    override fun onResume() {
        super.onResume()

        /*
         * The account may have been removed from the device (if broker is in use).
         *
         * In shared device mode, the account might be signed in/out by other apps while this app is not in focus.
         * Therefore, we want to update the account state by invoking loadAccount() here.
         */
        loadAccount()
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
                    AppProgressDialog.hide()
                    SelectProSiteActivity.launch(this@LoginActivity)
                    finish()
                }

                LoginState.SuccessAzureLogin -> {
                    AppProgressDialog.hide()
                    SelectProSiteActivity.launch(this@LoginActivity)
                    finish()
                }
            }
        }
    }

    private fun mSingleAccountApp() {
        PublicClientApplication.createSingleAccountPublicClientApplication(
            this@LoginActivity,
            R.raw.auth_config_single_account,
            object : ISingleAccountApplicationCreatedListener {
                override fun onCreated(application: ISingleAccountPublicClientApplication) {
                    /**
                     * This test app assumes that the app is only going to support one account.
                     * This requires "account_mode" : "SINGLE" in the config json file.
                     */
                    mSingleAccountApp = application
                    loadAccount()
                }

                override fun onError(exception: MsalException) {
                    Log.e("GTrack AD : ", exception.toString())
                }
            })
    }

    companion object {
        private var mSingleAccountApp: ISingleAccountPublicClientApplication? = null
        private var mAccount: IAccount? = null

        @JvmStatic
        fun launch(context: Context) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }
}
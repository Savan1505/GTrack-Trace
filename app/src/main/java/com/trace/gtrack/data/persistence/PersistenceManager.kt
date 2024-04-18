package com.trace.gtrack.data.persistence

import android.content.Context
import android.content.SharedPreferences
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.trace.gtrack.ui.login.model.LoginModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PersistenceManager @Inject constructor(@ApplicationContext val context: Context) :
    IPersistenceManager {

    val sharePref = context.getSharedPreferences("ForwardForm", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "userId"
        private const val KEY_USER_NAME = "userName"
        private const val KEY_API_KEYS = "apiKeys"
        private const val KEY_PROJ_ID = "projId"
        private const val KEY_SITE_ID = "siteId"
        private const val KEY_PROJ_NAME = "projName"
        private const val KEY_SITE_NAME = "siteName"
        private const val KEY_USER_LOGIN_STATE = "userLoginState"

    }

    override fun saveUserId(userId: String) {
        sharePref[KEY_USER_ID] = userId
    }

    override fun getUserId(): String {
        return sharePref[KEY_USER_ID, ""]

    }

    override fun saveUserName(userName: String) {
        sharePref[KEY_USER_NAME] = userName
    }

    override fun getUserName(): String {
        return sharePref[KEY_USER_NAME, ""]

    }

    override fun saveAPIKeys(apiKeys: String) {
        sharePref[KEY_API_KEYS] = apiKeys
    }

    override fun getAPIKeys(): String {
        return sharePref[KEY_API_KEYS, ""]

    }

    override fun saveProjectId(projectId: String) {
        sharePref[KEY_PROJ_ID] = projectId
    }

    override fun getProjectId(): String {
        return sharePref[KEY_PROJ_ID, ""]

    }

    override fun saveProjectName(projectName: String) {
        sharePref[KEY_PROJ_NAME] = projectName
    }

    override fun getProjectName(): String {
        return sharePref[KEY_PROJ_NAME, ""]

    }

    override fun saveSiteId(siteId: String) {
        sharePref[KEY_SITE_ID] = siteId
    }

    override fun getSiteId(): String {
        return sharePref[KEY_SITE_ID, ""]

    }

    override fun saveSiteName(siteName: String) {
        sharePref[KEY_SITE_NAME] = siteName
    }

    override fun getSiteName(): String {
        return sharePref[KEY_SITE_NAME, ""]

    }

    private val moshi: Moshi by lazy {
        Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    private val jsonAdapter: JsonAdapter<LoginModel> = moshi.adapter(LoginModel::class.java)

    /*override fun logout() {
        sharePref[KEY_USER] = ""
        setLoginState(UserLoginState.Login)
    }*/

    override fun getLoginState(): Boolean {
        return sharePref[KEY_USER_LOGIN_STATE, false]
    }

    override fun setLoginState(isLogin: Boolean) {
        sharePref[KEY_USER_LOGIN_STATE] = isLogin
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = this.edit()
        operation(editor)
        editor.apply()
    }

    /**
     * puts a value for the given [key].
     */
    operator fun SharedPreferences.set(key: String, value: Any?) = when (value) {
        is String? -> edit { it.putString(key, value) }
        is Int -> edit { it.putInt(key, value) }
        is Boolean -> edit { it.putBoolean(key, value) }
        is Float -> edit { it.putFloat(key, value) }
        is Long -> edit { it.putLong(key, value) }
        else -> throw UnsupportedOperationException("Not yet implemented")
    }

    /**
     * finds a preference based on the given [key].
     * [T] is the type of value
     * @param defaultValue optional defaultValue - will take a default defaultValue if it is not specified
     */
    inline operator fun <reified T : Any> SharedPreferences.get(
        key: String,
        defaultValue: T? = null,
    ): T = when (T::class) {
        String::class -> getString(key, defaultValue as? String ?: "") as T
        Int::class -> getInt(key, defaultValue as? Int ?: -1) as T
        Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T
        Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T
        Long::class -> getLong(key, defaultValue as? Long ?: -1) as T
        else -> throw UnsupportedOperationException("Not yet implemented")
    }

}
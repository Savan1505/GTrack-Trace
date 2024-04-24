package com.trace.gtrack.data.persistence

interface IPersistenceManager {
    fun saveUserId(userId: String)
    fun getUserId(): String

    fun saveUserName(userName: String)
    fun getUserName(): String

    fun saveAPIKeys(apiKeys: String)
    fun getAPIKeys(): String

    fun saveProjectId(projectId: String)
    fun getProjectId(): String

    fun saveProjectName(projectName: String)
    fun getProjectName(): String

    fun saveSiteId(siteId: String)
    fun getSiteId(): String

    fun saveSiteName(siteName: String)
    fun getSiteName(): String

    fun saveIOTCode(iotCode: String)
    fun getIOTCode(): String

    fun getLoginState(): Boolean
    fun setLoginState(isLogin: Boolean)
}
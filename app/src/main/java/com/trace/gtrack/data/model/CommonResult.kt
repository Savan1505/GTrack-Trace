package com.trace.gtrack.data.model

import com.trace.gtrack.data.network.base.CommonResponse
import com.trace.gtrack.data.network.response.LocationAssignMaterialResponse
import com.trace.gtrack.data.network.response.LoginResponse
import com.trace.gtrack.data.network.response.MaterialCodeResponse
import com.trace.gtrack.data.network.response.ProjectDetailsResponse
import com.trace.gtrack.data.network.response.ProjectKeysResponse
import com.trace.gtrack.data.network.response.SearchMaterialResponse
import com.trace.gtrack.data.network.response.SendMaterialResponse
import com.trace.gtrack.data.network.response.SiteDetailsCommonResponse

sealed class CommonResult {
    data class Success(val strMsg: String?) : CommonResult()
    data class Error(val message: String) : CommonResult()
}

sealed class LoginResult {
    data class Success(val loginResponse: LoginResponse<CommonResponse>?) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

sealed class LoginAzureResult {
    data class SuccessAzure(val loginResponse: LoginResponse<CommonResponse>?) : LoginAzureResult()
    data class Error(val message: String) : LoginAzureResult()
}

sealed class SearchMaterialResult {
    data class Success(val lstSearchMaterialResponse: List<SearchMaterialResponse>?) :
        SearchMaterialResult()

    data class Error(val message: String) : SearchMaterialResult()
}

sealed class MaterialCodeResult {
    data class Success(val materialCodeResponse: MaterialCodeResponse<CommonResponse>) :
        MaterialCodeResult()

    data class Error(val message: String) : MaterialCodeResult()
}

sealed class ListResult {
    data class Success(val lstResponse: List<String>?) : ListResult()
    data class Error(val message: String) : ListResult()
}

sealed class SiteDetailByProjectResult {
    data class Success(val siteDetailsCommonResponse: SiteDetailsCommonResponse<CommonResponse>) :
        SiteDetailByProjectResult()

    data class Error(val message: String) : SiteDetailByProjectResult()
}

sealed class SendMaterialResult {
    data class Success(val lstSendMaterialResponse: List<SendMaterialResponse>?) :
        SendMaterialResult()

    data class Error(val message: String) : SendMaterialResult()
}

sealed class AssignedMaterialResult {
    data class Success(val materialCode: String?) :
        AssignedMaterialResult()

    data class Error(val message: String) : AssignedMaterialResult()
}

sealed class LocationAssignMaterialResult {
    data class Success(val lstLocationAssignMaterialResponse: List<LocationAssignMaterialResponse>?) :
        LocationAssignMaterialResult()

    data class Error(val message: String) : LocationAssignMaterialResult()
}

sealed class ProjectDetailsResult {
    data class Success(val lstProjectDetailsResponse: List<ProjectDetailsResponse>?) :
        ProjectDetailsResult()

    data class Error(val message: String) : ProjectDetailsResult()
}

sealed class ProjectKeysResult {
    data class Success(val lstProjectKeysResponse: List<ProjectKeysResponse>?) : ProjectKeysResult()

    data class Error(val message: String) : ProjectKeysResult()
}
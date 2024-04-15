package com.trace.gtrack.data

import com.trace.gtrack.data.model.AssignedMaterialResult
import com.trace.gtrack.data.model.CommonResult
import com.trace.gtrack.data.model.ListResult
import com.trace.gtrack.data.model.LocationAssignMaterialResult
import com.trace.gtrack.data.model.LoginResult
import com.trace.gtrack.data.model.MaterialCodeResult
import com.trace.gtrack.data.model.ProjectDetailsResult
import com.trace.gtrack.data.model.ProjectKeysResult
import com.trace.gtrack.data.model.SearchMaterialResult
import com.trace.gtrack.data.model.SendMaterialResult
import com.trace.gtrack.data.model.SiteDetailByProjectResult
import com.trace.gtrack.data.network.request.AssignMaterialRequest
import com.trace.gtrack.data.network.request.InsertHandHeldDataRequest
import com.trace.gtrack.data.network.request.SendMaterialRequest

interface IAppRepository {
    suspend fun postAppLogin(userName: String, password: String): LoginResult?
    suspend fun postAzureLogin(azureUserID: String): LoginResult?
    suspend fun postSearchMaterialCodeAPI(materialCode: String): SearchMaterialResult?
    suspend fun postRfidQRCodeMappingAPI(qRCode: String, rfidCode: String): CommonResult?
    suspend fun postRfidQRCodeReMappingAPI(qRCode: String, rfidCode: String): CommonResult?
    suspend fun postAssignMaterialTagAPI(qRCode: String, materialCode: String): CommonResult?
    suspend fun postDeAssignMaterialTagAPI(userId: String, materialCode: String): CommonResult?
    suspend fun postMaterialCodeByQRCodeAPI(qRCode: String): MaterialCodeResult?
    suspend fun postAssignedMaterialListAPI(searchString: String): ListResult?
    suspend fun postInsertRFIDDataAPI(lstInsertHandHeldData: List<InsertHandHeldDataRequest>): CommonResult?
    suspend fun postInsertHandheldDataAPI(lstInsertHandHeldData: List<InsertHandHeldDataRequest>): CommonResult?
    suspend fun getIOTCodeAPI(): ListResult?
    suspend fun postIotQRCodeMappingAPI(iotCode: String, qRCode: String): CommonResult?
    suspend fun postIotQRCodeReMappingAPI(iotCode: String, qRCode: String): CommonResult?
    suspend fun postSiteDetailByProjectAPI(userId: String): SiteDetailByProjectResult?
    suspend fun postSendMaterialAPI(lstSendMaterialRequest: List<SendMaterialRequest>): SendMaterialResult?
    suspend fun postAssignedMaterialAPI(qRCode: String): AssignedMaterialResult?
    suspend fun getLocationOfAllMaterialsAPI(): LocationAssignMaterialResult?
    suspend fun postLocationOfAssignMaterialsAPI(lstAssignMaterialRequest: List<AssignMaterialRequest>): LocationAssignMaterialResult?
    suspend fun getProjectDetailAPI(): ProjectDetailsResult?
    suspend fun getProjectKeysAPI(): ProjectKeysResult?
    suspend fun postInsertMAPSearchResultAPI(
        userId: String,
        materialCode: String,
        totalSearchTime: String
    ): CommonResult?
//    suspend fun logout(): Boolean
//    fun getLoginUserState(): Pair<UserLoginState, LoginModel?>
}
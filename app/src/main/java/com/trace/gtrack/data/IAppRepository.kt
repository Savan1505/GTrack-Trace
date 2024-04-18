package com.trace.gtrack.data

import com.trace.gtrack.data.model.AssignedMaterialResult
import com.trace.gtrack.data.model.CommonResult
import com.trace.gtrack.data.model.ListResult
import com.trace.gtrack.data.model.LocationAssignMaterialResult
import com.trace.gtrack.data.model.LoginAzureResult
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
    suspend fun postAzureLogin(azureUserID: String): LoginAzureResult?
    suspend fun postSearchMaterialCodeAPI(
        apiKey: String,
        projectId: String,
        siteId: String,
        materialCode: String
    ): SearchMaterialResult?

    suspend fun postRfidQRCodeMappingAPI(
        apiKey: String,
        projectId: String,
        siteId: String,
        qRCode: String,
        rfidCode: String
    ): CommonResult?

    suspend fun postRfidQRCodeReMappingAPI(
        apiKey: String,
        projectId: String,
        siteId: String,
        qRCode: String,
        rfidCode: String
    ): CommonResult?

    suspend fun postAssignMaterialTagAPI(
        apiKey: String,
        projectId: String,
        siteId: String,
        qRCode: String,
        materialCode: String
    ): CommonResult?

    suspend fun postDeAssignMaterialTagAPI(
        apiKey: String,
        projectId: String,
        siteId: String,
        userId: String,
        materialCode: String
    ): CommonResult?

    suspend fun postMaterialCodeByQRCodeAPI(
        apiKey: String,
        projectId: String,
        siteId: String,
        qRCode: String
    ): MaterialCodeResult?

    suspend fun postAssignedMaterialListAPI(
        apiKey: String,
        projectId: String,
        siteId: String,
        searchString: String
    ): ListResult?

    suspend fun postInsertRFIDDataAPI(
        apiKey: String,
        projectId: String,
        siteId: String,
        lstInsertHandHeldData: List<InsertHandHeldDataRequest>
    ): CommonResult?

    suspend fun postInsertHandheldDataAPI(
        apiKey: String,
        projectId: String,
        siteId: String,
        lstInsertHandHeldData: List<InsertHandHeldDataRequest>
    ): CommonResult?

    suspend fun getIOTCodeAPI(apiKey: String): ListResult?
    suspend fun postIotQRCodeMappingAPI(
        apiKey: String,
        projectId: String,
        siteId: String,
        iotCode: String,
        qRCode: String
    ): CommonResult?

    suspend fun postIotQRCodeReMappingAPI(
        apiKey: String,
        projectId: String,
        siteId: String,
        iotCode: String,
        qRCode: String
    ): CommonResult?

    suspend fun postSiteDetailByProjectAPI(
        apiKey: String,
        projectId: String,
        userId: String
    ): SiteDetailByProjectResult?

    suspend fun postSendMaterialAPI(
        apiKey: String,
        projectId: String,
        siteId: String,
        lstSendMaterialRequest: List<SendMaterialRequest>
    ): SendMaterialResult?

    suspend fun postAssignedMaterialAPI(
        apiKey: String,
        projectId: String,
        siteId: String,
        qRCode: String
    ): AssignedMaterialResult?

    suspend fun getLocationOfAllMaterialsAPI(
        apiKey: String,
        projectId: String,
        siteId: String,
    ): LocationAssignMaterialResult?

    suspend fun postLocationOfAssignMaterialsAPI(
        apiKey: String,
        projectId: String,
        siteId: String,
        lstAssignMaterialRequest: List<AssignMaterialRequest>
    ): LocationAssignMaterialResult?

    suspend fun getProjectDetailAPI(): ProjectDetailsResult?
    suspend fun getProjectKeysAPI(): ProjectKeysResult?
    suspend fun postInsertMAPSearchResultAPI(

        apiKey: String, projectId: String, siteId: String,
        userId: String,
        materialCode: String,
        totalSearchTime: String
    ): CommonResult?

    suspend fun logout(): Boolean
}
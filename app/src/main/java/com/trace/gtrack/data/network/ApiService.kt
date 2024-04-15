package com.trace.gtrack.data.network

import com.trace.gtrack.data.network.base.CommonMaterialDetailsResponse
import com.trace.gtrack.data.network.base.CommonMaterialResponse
import com.trace.gtrack.data.network.base.CommonProjectDetailsResponse
import com.trace.gtrack.data.network.base.CommonResponse
import com.trace.gtrack.data.network.base.CommonSendMaterialResponse
import com.trace.gtrack.data.network.request.AssignMaterialCodeRequest
import com.trace.gtrack.data.network.request.AssignMaterialRequest
import com.trace.gtrack.data.network.request.DeAssignMaterialCodeRequest
import com.trace.gtrack.data.network.request.IOTCodeRequest
import com.trace.gtrack.data.network.request.InsertHandheldRequest
import com.trace.gtrack.data.network.request.InsertRFIDRequest
import com.trace.gtrack.data.network.request.LoginAzureRequest
import com.trace.gtrack.data.network.request.LoginRequest
import com.trace.gtrack.data.network.request.MapSearchResultRequest
import com.trace.gtrack.data.network.request.QRCodeRequest
import com.trace.gtrack.data.network.request.RFIDCodeRequest
import com.trace.gtrack.data.network.request.SearchMaterialRequest
import com.trace.gtrack.data.network.request.SearchStrRequest
import com.trace.gtrack.data.network.request.SendMaterialRequest
import com.trace.gtrack.data.network.request.SiteDetailsRequest
import com.trace.gtrack.data.network.response.IOTCodeResponse
import com.trace.gtrack.data.network.response.LocationAssignMaterialResponse
import com.trace.gtrack.data.network.response.LoginResponse
import com.trace.gtrack.data.network.response.MaterialCodeListResponse
import com.trace.gtrack.data.network.response.MaterialCodeResponse
import com.trace.gtrack.data.network.response.ProjectDetailsResponse
import com.trace.gtrack.data.network.response.ProjectKeysResponse
import com.trace.gtrack.data.network.response.SearchMaterialResponse
import com.trace.gtrack.data.network.response.SendMaterialResponse
import com.trace.gtrack.data.network.response.SiteDetailsCommonResponse
import org.json.JSONArray
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    @Headers("Accept: application/json")
    @POST("api/GTrackAPI/Login")
    suspend fun postAppLoginAPI(
        @Header("APIKey") apiKey: Int,
        @Header("ProjectId") projectId: Int,
        @Header("SiteId") siteId: Int,
        @Body loginRequest: LoginRequest,
    ): LoginResponse<CommonResponse>

    @Headers("Accept: application/json")
    @GET("api/GTrackAPI/AzureLogin")
    suspend fun postAzureLoginAPI(
        @Header("APIKey") apiKey: Int,
        @Header("ProjectId") projectId: Int,
        @Header("SiteId") siteId: Int,
        @Body loginAzureRequest: LoginAzureRequest,
    ): LoginResponse<CommonResponse>

    @Headers("Accept: application/json")
    @POST("api/GTrackAPI/SearchMaterial")
    suspend fun postSearchMaterialCodeAPI(
        @Header("APIKey") apiKey: Int,
        @Header("ProjectId") projectId: Int,
        @Header("SiteId") siteId: Int,
        @Body searchMaterialRequest: SearchMaterialRequest,
    ): CommonMaterialResponse<List<SearchMaterialResponse>>

    @Headers("Accept: application/json")
    @POST("api/GTrackAPI/RFID_QRCodeMapping")
    suspend fun postRfidQRCodeMappingAPI(
        @Header("APIKey") apiKey: Int,
        @Header("ProjectId") projectId: Int,
        @Header("SiteId") siteId: Int,
        @Body rfidCodeRequest: RFIDCodeRequest,
    ): CommonResponse

    @Headers("Accept: application/json")
    @POST("api/GTrackAPI/RFID_QRCodeReMapping")
    suspend fun postRfidQRCodeReMappingAPI(
        @Header("APIKey") apiKey: Int,
        @Header("ProjectId") projectId: Int,
        @Header("SiteId") siteId: Int,
        @Body rfidCodeRequest: RFIDCodeRequest,
    ): CommonResponse

    @Headers("Accept: application/json")
    @POST("api/GTrackAPI/AssignMaterialTag")
    suspend fun postAssignMaterialTagAPI(
        @Header("APIKey") apiKey: Int,
        @Header("ProjectId") projectId: Int,
        @Header("SiteId") siteId: Int,
        @Body assignMaterialRequest: AssignMaterialCodeRequest,
    ): CommonResponse

    @Headers("Accept: application/json")
    @POST("api/GTrackAPI/DeAssignMaterialTag")
    suspend fun postDeAssignMaterialTagAPI(
        @Header("APIKey") apiKey: Int,
        @Header("ProjectId") projectId: Int,
        @Header("SiteId") siteId: Int,
        @Body deAssignMaterialCodeRequest: DeAssignMaterialCodeRequest,
    ): CommonResponse

    @Headers("Accept: application/json")
    @POST("api/GTrackAPI/GetMaterialCodeByQRcode")
    suspend fun postMaterialCodeByQRCodeAPI(
        @Header("APIKey") apiKey: Int,
        @Header("ProjectId") projectId: Int,
        @Header("SiteId") siteId: Int,
        @Body qrCodeRequest: QRCodeRequest,
    ): MaterialCodeResponse<CommonResponse>

    @Headers("Accept: application/json")
    @POST("api/GTrackAPI/GetAssignedMaterialList")
    suspend fun postAssignedMaterialListAPI(
        @Header("APIKey") apiKey: Int,
        @Header("ProjectId") projectId: Int,
        @Header("SiteId") siteId: Int,
        @Body searchStrRequest: SearchStrRequest,
    ): MaterialCodeListResponse<CommonResponse>

    @Headers("Accept: application/json")
    @POST("api/GTrackAPI/InsertRFIDData")
    suspend fun postInsertRFIDDataAPI(
        @Header("APIKey") apiKey: Int,
        @Header("ProjectId") projectId: Int,
        @Header("SiteId") siteId: Int,
        @Body insertRFIDRequest: InsertRFIDRequest,
    ): CommonResponse

    @Headers("Accept: application/json")
    @POST("api/GTrackAPI/InsertHandheldData")
    suspend fun postInsertHandheldDataAPI(
        @Header("APIKey") apiKey: Int,
        @Header("ProjectId") projectId: Int,
        @Header("SiteId") siteId: Int,
        @Body insertHandHeldRequest: InsertHandheldRequest,
    ): CommonResponse

    @Headers("Accept: application/json")
    @GET("api/GTrackAPI/GetIOTCode")
    suspend fun getIOTCodeAPI(
        @Header("APIKey") apiKey: String,
    ): IOTCodeResponse<CommonResponse>

    @Headers("Accept: application/json")
    @POST("api/GTrackAPI/IOT_QRCodeMapping")
    suspend fun postIotQRCodeMappingAPI(
        @Header("APIKey") apiKey: Int,
        @Header("ProjectId") projectId: Int,
        @Header("SiteId") siteId: Int,
        @Body iotCodeRequest: IOTCodeRequest,
    ): CommonResponse

    @Headers("Accept: application/json")
    @POST("api/GTrackAPI/IOT_QRCodeReMapping")
    suspend fun postIotQRCodeReMappingAPI(
        @Header("APIKey") apiKey: Int,
        @Header("ProjectId") projectId: Int,
        @Header("SiteId") siteId: Int,
        @Body iotCodeRequest: IOTCodeRequest,
    ): CommonResponse

    @Headers("Accept: application/json")
    @POST("api/GTrackAPI/GetSiteDetailByProject")
    suspend fun postSiteDetailByProjectAPI(
        @Header("APIKey") apiKey: Int,
        @Header("ProjectId") projectId: Int,
        @Body siteDetailsRequest: SiteDetailsRequest,
    ): SiteDetailsCommonResponse<CommonResponse>

    @Headers("Accept: application/json")
    @POST("api/GTrackAPI/SendMaterial")
    suspend fun postSendMaterialAPI(
        @Header("APIKey") apiKey: Int,
        @Header("ProjectId") projectId: Int,
        @Header("SiteId") siteId: Int,
        @Body sendMaterialRequest: JSONArray,
    ): CommonSendMaterialResponse<List<SendMaterialResponse>>

    @Headers("Accept: application/json")
    @POST("api/GTrackAPI/GetAssignedMaterial")
    suspend fun postAssignedMaterialAPI(
        @Header("APIKey") apiKey: Int,
        @Header("ProjectId") projectId: Int,
        @Header("SiteId") siteId: Int,
        @Body qrCodeRequest: QRCodeRequest,
    ): String

    @Headers("Accept: application/json")
    @GET("api/GTrackAPI/GetLocationOfAllMaterials")
    suspend fun getLocationOfAllMaterialsAPI(
        @Header("APIKey") apiKey: Int,
        @Header("ProjectId") projectId: Int,
        @Header("SiteId") siteId: Int,
    ): CommonMaterialDetailsResponse<List<LocationAssignMaterialResponse>>

    @Headers("Accept: application/json")
    @POST("api/GTrackAPI/GetLocationOfAssignMaterial")
    suspend fun postLocationOfAssignMaterialsAPI(
        @Header("APIKey") apiKey: Int,
        @Header("ProjectId") projectId: Int,
        @Header("SiteId") siteId: Int,
        @Body lstAssignMaterialRequest: JSONArray
    ): CommonMaterialDetailsResponse<List<LocationAssignMaterialResponse>>

    @Headers("Accept: application/json")
    @GET("api/GTrackAPI/GetProjectDetail")
    suspend fun getProjectDetailAPI(
        @Header("APIKey") apiKey: String,
    ): CommonProjectDetailsResponse<List<ProjectDetailsResponse>>

    @Headers("Accept: application/json")
    @GET("api/GTrackAPI/GetProjectKeys")
    suspend fun getProjectKeysAPI(
        @Header("APIKey") apiKey: String,
    ): CommonProjectDetailsResponse<List<ProjectKeysResponse>>

    @Headers("Accept: application/json")
    @POST("api/GTrackAPI/InsertMAPSearchResult")
    suspend fun postInsertMAPSearchResultAPI(
        @Header("APIKey") apiKey: Int,
        @Header("ProjectId") projectId: Int,
        @Header("SiteId") siteId: Int,
        @Body mapSearchResultRequest: MapSearchResultRequest,
    ): CommonResponse
}
package com.trace.gtrack.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
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
import com.trace.gtrack.data.network.ApiService
import com.trace.gtrack.data.network.base.CommonMaterialDetailsResponse
import com.trace.gtrack.data.network.base.CommonMaterialResponse
import com.trace.gtrack.data.network.base.CommonProjectDetailsResponse
import com.trace.gtrack.data.network.base.CommonResponse
import com.trace.gtrack.data.network.base.CommonSendMaterialResponse
import com.trace.gtrack.data.network.base.ResponseWrapper
import com.trace.gtrack.data.network.base.safeApiCall
import com.trace.gtrack.data.network.request.AssignMaterialCodeRequest
import com.trace.gtrack.data.network.request.AssignMaterialRequest
import com.trace.gtrack.data.network.request.DeAssignMaterialCodeRequest
import com.trace.gtrack.data.network.request.IOTCodeRequest
import com.trace.gtrack.data.network.request.InsertHandHeldDataRequest
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
import com.trace.gtrack.data.persistence.IPersistenceManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject


private val logger = Timber.tag("AppRepository")
private const val networkErrorMessage =
    "Oops something went wrong. Please check your internet connection."
private const val oopsMessage = "Oops something went wrong"
private const val UNAUTHORIZED_STATUS_C0DE = 401

class AppRepository @Inject constructor(
    private val apiService: ApiService,
    private val persistenceManager: IPersistenceManager,
    private val firebaseAuth: FirebaseAuth,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : IAppRepository {

    override suspend fun postAppLogin(userName: String, password: String): LoginResult? {
        return when (val response: ResponseWrapper<LoginResponse<CommonResponse>> =
            safeApiCall(dispatcher) {
                apiService.postAppLoginAPI("TGluZGUgUHZ0IEx0ZA==", LoginRequest(userName, password))
            }) {

            is ResponseWrapper.GenericError -> LoginResult.Error(
                response.error?.Message ?: oopsMessage
            )

            ResponseWrapper.NetworkError -> LoginResult.Error(networkErrorMessage)
            is ResponseWrapper.Success<LoginResponse<CommonResponse>> -> {
                val data = response.value
                when {
                    !(response.value.commonResponse?.isSuccess())!! -> response.value.commonResponse.Message?.let {
                        LoginResult.Error(
                            it
                        )
                    }

                    data == null -> LoginResult.Error(oopsMessage)
                    else -> {
                        persistenceManager.saveUserId(data.UserId ?: "")
                        persistenceManager.saveUserName(data.UserName ?: "")
                        LoginResult.Success(data)
                    }
                }
            }
        }

    }

    override suspend fun postAzureLogin(azureUserID: String): LoginAzureResult? {
        return when (val response: ResponseWrapper<LoginResponse<CommonResponse>> =
            safeApiCall(dispatcher) {
                apiService.postAzureLoginAPI("TGluZGUgUHZ0IEx0ZA==", LoginAzureRequest(azureUserID))
            }) {
            is ResponseWrapper.GenericError -> LoginAzureResult.Error(
                response.error?.Message ?: oopsMessage
            )

            ResponseWrapper.NetworkError -> LoginAzureResult.Error(networkErrorMessage)
            is ResponseWrapper.Success<LoginResponse<CommonResponse>> -> {
                val data = response.value
                when {
                    !(response.value.commonResponse?.isSuccess())!! -> response.value.commonResponse.Message?.let {
                        LoginAzureResult.Error(
                            it
                        )
                    }

                    data == null -> LoginAzureResult.Error(oopsMessage)
                    else -> {
                        persistenceManager.saveUserId(data.UserId ?: "")
                        persistenceManager.saveUserName(data.UserName ?: "")
                        LoginAzureResult.SuccessAzure(data)
                    }
                }
            }
        }
    }

    override suspend fun postSearchMaterialCodeAPI(
        apiKey: String, projectId: String, siteId: String, materialCode: String
    ): SearchMaterialResult {
        return when (val response: ResponseWrapper<CommonMaterialResponse<List<SearchMaterialResponse>>> =
            safeApiCall(dispatcher) {
                apiService.postSearchMaterialCodeAPI(
                    apiKey,
                    Integer.parseInt(projectId),
                    Integer.parseInt(siteId),
                    SearchMaterialRequest(materialCode.uppercase(Locale.ROOT))
                )
            }) {
            is ResponseWrapper.GenericError -> SearchMaterialResult.Error(
                response.error?.Error_Message ?: oopsMessage
            )

            ResponseWrapper.NetworkError -> SearchMaterialResult.Error(networkErrorMessage)
            is ResponseWrapper.Success<CommonMaterialResponse<List<SearchMaterialResponse>>> -> {
                val data = response.value
                when {
                    !(data.isSuccess()) -> SearchMaterialResult.Error(data.Message.toString())
                    data == null -> SearchMaterialResult.Error(oopsMessage)
                    else -> {
                        data.MaterialDetail.let { SearchMaterialResult.Success(it) }
                    }
                }
            }
        }

    }

    override suspend fun postRfidQRCodeMappingAPI(
        apiKey: String, projectId: String, siteId: String, qRCode: String, rfidCode: String
    ): CommonResult {
        return when (val response: ResponseWrapper<CommonResponse> = safeApiCall(dispatcher) {
            apiService.postRfidQRCodeMappingAPI(
                apiKey,
                Integer.parseInt(projectId),
                Integer.parseInt(siteId),
                RFIDCodeRequest(qRCode.uppercase(Locale.ROOT), rfidCode.uppercase(Locale.ROOT))
            )
        }) {
            is ResponseWrapper.GenericError -> CommonResult.Error(
                response.error?.Message ?: oopsMessage
            )

            ResponseWrapper.NetworkError -> CommonResult.Error(networkErrorMessage)
            is ResponseWrapper.Success<CommonResponse> -> {
                val data = response.value.Message.toString()
                when {
                    !(response.value.isSuccess()) -> CommonResult.Error(data)
                    data == null -> CommonResult.Error(oopsMessage)
                    else -> CommonResult.Success(data)
                }
            }
        }

    }

    override suspend fun postRfidQRCodeReMappingAPI(
        apiKey: String, projectId: String, siteId: String, qRCode: String, rfidCode: String
    ): CommonResult {
        return when (val response: ResponseWrapper<CommonResponse> = safeApiCall(dispatcher) {
            apiService.postRfidQRCodeReMappingAPI(
                apiKey,
                Integer.parseInt(projectId),
                Integer.parseInt(siteId),
                RFIDCodeRequest(qRCode.uppercase(Locale.ROOT), rfidCode.uppercase(Locale.ROOT))
            )
        }) {
            is ResponseWrapper.GenericError -> CommonResult.Error(
                response.error?.Message ?: oopsMessage
            )

            ResponseWrapper.NetworkError -> CommonResult.Error(networkErrorMessage)
            is ResponseWrapper.Success<CommonResponse> -> {
                val data = response.value.Message.toString()
                when {
                    !(response.value.isSuccess()) -> CommonResult.Error(data)
                    data == null -> CommonResult.Error(oopsMessage)
                    else -> CommonResult.Success(data)
                }
            }
        }

    }

    override suspend fun postAssignMaterialTagAPI(
        apiKey: String, projectId: String, siteId: String, qRCode: String, materialCode: String
    ): CommonResult {
        return when (val response: ResponseWrapper<CommonResponse> = safeApiCall(dispatcher) {
            apiService.postAssignMaterialTagAPI(
                apiKey,
                Integer.parseInt(projectId),
                Integer.parseInt(siteId),
                AssignMaterialCodeRequest(
                    qRCode.uppercase(Locale.ROOT), materialCode.uppercase(Locale.ROOT)
                )
            )
        }) {
            is ResponseWrapper.GenericError -> CommonResult.Error(
                response.error?.Message ?: oopsMessage
            )

            ResponseWrapper.NetworkError -> CommonResult.Error(networkErrorMessage)
            is ResponseWrapper.Success<CommonResponse> -> {
                val data = response.value.Message.toString()
                when {
                    !(response.value.isSuccess()) -> CommonResult.Error(data)
                    data == null -> CommonResult.Error(oopsMessage)
                    else -> CommonResult.Success(data)
                }
            }
        }

    }

    override suspend fun postDeAssignMaterialTagAPI(
        apiKey: String, projectId: String, siteId: String, userId: String, materialCode: String
    ): CommonResult {
        return when (val response: ResponseWrapper<CommonResponse> = safeApiCall(dispatcher) {
            apiService.postDeAssignMaterialTagAPI(
                apiKey,
                Integer.parseInt(projectId),
                Integer.parseInt(siteId),
                DeAssignMaterialCodeRequest(userId, materialCode.uppercase(Locale.ROOT))
            )
        }) {
            is ResponseWrapper.GenericError -> CommonResult.Error(
                response.error?.Message ?: oopsMessage
            )

            ResponseWrapper.NetworkError -> CommonResult.Error(networkErrorMessage)
            is ResponseWrapper.Success<CommonResponse> -> {
                val data = response.value.Message.toString()
                when {
                    !(response.value.isSuccess()) -> CommonResult.Error(data)
                    data == null -> CommonResult.Error(oopsMessage)
                    else -> CommonResult.Success(data)
                }
            }
        }

    }

    override suspend fun postMaterialCodeByQRCodeAPI(
        apiKey: String, projectId: String, siteId: String, qRCode: String
    ): MaterialCodeResult {
        return when (val response: ResponseWrapper<MaterialCodeResponse<CommonResponse>> =
            safeApiCall(dispatcher) {
                apiService.postMaterialCodeByQRCodeAPI(
                    apiKey,
                    Integer.parseInt(projectId),
                    Integer.parseInt(siteId),
                    QRCodeRequest(qRCode.uppercase(Locale.ROOT))
                )
            }) {
            is ResponseWrapper.GenericError -> MaterialCodeResult.Error(
                response.error?.Message ?: oopsMessage
            )

            ResponseWrapper.NetworkError -> MaterialCodeResult.Error(networkErrorMessage)
            is ResponseWrapper.Success<MaterialCodeResponse<CommonResponse>> -> {
                val data = response.value
                when {
                    !(response.value.commonResponse?.isSuccess())!! -> MaterialCodeResult.Error(
                        response.value.commonResponse.Message.toString()
                    )

                    data == null -> MaterialCodeResult.Error(oopsMessage)
                    else -> MaterialCodeResult.Success(data)
                }
            }
        }

    }

    override suspend fun postAssignedMaterialListAPI(
        apiKey: String,
        projectId: String,
        siteId: String,
        pageNumber: Int,
        pageSize: Int,
        searchString: String
    ): ListResult {
        return when (val response: ResponseWrapper<MaterialCodeListResponse<CommonResponse>> =
            safeApiCall(dispatcher) {
                apiService.postAssignedMaterialListAPI(
                    apiKey,
                    Integer.parseInt(projectId),
                    Integer.parseInt(siteId),
                    SearchStrRequest(searchString.uppercase(Locale.ROOT), pageNumber, pageSize)
                )
            }) {
            is ResponseWrapper.GenericError -> ListResult.Error(
                response.error?.Message ?: oopsMessage
            )

            ResponseWrapper.NetworkError -> ListResult.Error(networkErrorMessage)
            is ResponseWrapper.Success<MaterialCodeListResponse<CommonResponse>> -> {
                val data = response.value
                when {
                    !(data.commonResponse?.isSuccess())!! -> ListResult.Error(
                        data.commonResponse.Message.toString()
                    )

                    data == null -> ListResult.Error(oopsMessage)
                    else -> ListResult.Success(data.MaterialCode)
                }
            }
        }

    }

    override suspend fun postInsertRFIDDataAPI(
        apiKey: String, projectId: String, siteId: String,
        lstInsertHandHeldData: MutableList<InsertHandHeldDataRequest>,
    ): CommonResult {
        return when (val response: ResponseWrapper<CommonResponse> = safeApiCall(dispatcher) {
            /* // lstInsertHandHeldData = InsertHandHeldDataRequest(latitude: 123.456, longitude: 789.012, RFID: RFID123)
             *//*var jsonObjData = JSONObject()

            try {
                for (insertHandHeldData in lstInsertHandHeldData) {
                    val jsonObjInsertHandHeldData = JSONObject()
                    jsonObjInsertHandHeldData.put("latitude", insertHandHeldData.latitude)
                    jsonObjInsertHandHeldData.put("longitude", insertHandHeldData.longitude)
                    jsonObjInsertHandHeldData.put(
                        "RFID", insertHandHeldData.rfid?.uppercase(Locale.ROOT)
                    )
                    jsonObjData = JSONObject(jsonObjInsertHandHeldData.toString())
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }*/
            val copyOfList: List<InsertHandHeldDataRequest> =
                ArrayList(lstInsertHandHeldData) // Iterate over the copy of the list
            val data = Gson().toJson(copyOfList)
            apiService.postInsertRFIDDataAPI(
                apiKey,
                Integer.parseInt(projectId),
                Integer.parseInt(siteId),
                InsertRFIDRequest(data)
            )
        }) {
            is ResponseWrapper.GenericError -> CommonResult.Error(
                response.error?.Message ?: oopsMessage
            )

            ResponseWrapper.NetworkError -> CommonResult.Error(networkErrorMessage)
            is ResponseWrapper.Success<CommonResponse> -> {
                val data = response.value.Message.toString()
                when {
                    !(response.value.isSuccess()) -> CommonResult.Error(data)
                    data == null -> CommonResult.Error(oopsMessage)
                    else -> CommonResult.Success(data)
                }
            }
        }

    }

    override suspend fun postInsertHandheldDataAPI(
        apiKey: String, projectId: String, siteId: String,
        lstInsertHandHeldData: List<InsertHandHeldDataRequest>,
    ): CommonResult {
        return when (val response: ResponseWrapper<CommonResponse> = safeApiCall(dispatcher) {
            // lstInsertHandHeldData = InsertHandHeldDataRequest(latitude: 123.456, longitude: 789.012, RFID: RFID123)
            /*var jsonObjData = JSONObject()

            Log.d("Savan", Gson().toJson(lstInsertHandHeldData))
//                Log.d("Savan", lstInsertHandHeldData.toString().length.toString())
            try {
                for (insertHandHeldData in lstInsertHandHeldData) {
                    val jsonObjInsertHandHeldData = JSONObject()
                    Log.d("Savan", "insertHandHeldData : "+insertHandHeldData.toString())
                    jsonObjInsertHandHeldData.put("latitude", insertHandHeldData.latitude)
                    jsonObjInsertHandHeldData.put("longitude", insertHandHeldData.longitude)
                    jsonObjInsertHandHeldData.put("RFID", insertHandHeldData.rfid?.toUpperCase(Locale.ROOT))
                    jsonObjData = JSONObject(jsonObjInsertHandHeldData.toString())
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            Log.d("Savan", "jsonObjData : "+jsonObjData.toString())*/
            val str = Gson().toJson(lstInsertHandHeldData)
            Log.d("Savan", "jsonObjData : " + str)
            apiService.postInsertHandheldDataAPI(
                apiKey,
                Integer.parseInt(projectId),
                Integer.parseInt(siteId),
                InsertHandheldRequest(str)
            )
        }) {
            is ResponseWrapper.GenericError -> CommonResult.Error(
                response.error?.Message ?: oopsMessage
            )

            ResponseWrapper.NetworkError -> CommonResult.Error(networkErrorMessage)
            is ResponseWrapper.Success<CommonResponse> -> {
                val data = response.value.Message.toString()
                when {
                    !(response.value.isSuccess()) -> CommonResult.Error(data)
                    data == null -> CommonResult.Error(oopsMessage)
                    else -> CommonResult.Success(data)
                }
            }
        }

    }

    override suspend fun getIOTCodeAPI(apiKey: String): ListResult {
        return when (val response: ResponseWrapper<IOTCodeResponse<CommonResponse>> =
            safeApiCall(dispatcher) {
                apiService.getIOTCodeAPI(apiKey)
            }) {
            is ResponseWrapper.GenericError -> ListResult.Error(
                response.error?.Message ?: oopsMessage
            )

            ResponseWrapper.NetworkError -> ListResult.Error(networkErrorMessage)
            is ResponseWrapper.Success<IOTCodeResponse<CommonResponse>> -> {
                val data = response.value
                when {
                    !(data.commonResponse?.isSuccess())!! -> ListResult.Error(
                        data.commonResponse.Message.toString()
                    )

                    data == null -> ListResult.Error(oopsMessage)
                    else -> ListResult.Success(data.IOTCodeList)
                }
            }
        }

    }

    override suspend fun postIotQRCodeMappingAPI(
        apiKey: String, projectId: String, siteId: String, iotCode: String, qRCode: String
    ): CommonResult {
        return when (val response: ResponseWrapper<CommonResponse> = safeApiCall(dispatcher) {
            apiService.postIotQRCodeMappingAPI(
                apiKey,
                Integer.parseInt(projectId),
                Integer.parseInt(siteId),
                IOTCodeRequest(iotCode.uppercase(Locale.ROOT), qRCode.uppercase(Locale.ROOT))
            )
        }) {
            is ResponseWrapper.GenericError -> CommonResult.Error(
                response.error?.Message ?: oopsMessage
            )

            ResponseWrapper.NetworkError -> CommonResult.Error(networkErrorMessage)
            is ResponseWrapper.Success<CommonResponse> -> {
                val data = response.value.Message.toString()
                when {
                    !(response.value.isSuccess()) -> CommonResult.Error(data)
                    data == null -> CommonResult.Error(oopsMessage)
                    else -> CommonResult.Success(data)
                }
            }
        }

    }

    override suspend fun postIotQRCodeReMappingAPI(
        apiKey: String, projectId: String, siteId: String, iotCode: String, qRCode: String
    ): CommonResult {
        return when (val response: ResponseWrapper<CommonResponse> = safeApiCall(dispatcher) {
            apiService.postIotQRCodeReMappingAPI(
                apiKey,
                Integer.parseInt(projectId),
                Integer.parseInt(siteId),
                IOTCodeRequest(iotCode.uppercase(Locale.ROOT), qRCode.uppercase(Locale.ROOT))
            )
        }) {
            is ResponseWrapper.GenericError -> CommonResult.Error(
                response.error?.Message ?: oopsMessage
            )

            ResponseWrapper.NetworkError -> CommonResult.Error(networkErrorMessage)
            is ResponseWrapper.Success<CommonResponse> -> {
                val data = response.value.Message.toString()
                when {
                    !(response.value.isSuccess()) -> CommonResult.Error(data)
                    data == null -> CommonResult.Error(oopsMessage)
                    else -> CommonResult.Success(data)
                }
            }
        }

    }

    override suspend fun postSiteDetailByProjectAPI(
        apiKey: String, projectId: String, userId: String
    ): SiteDetailByProjectResult {
        return when (val response: ResponseWrapper<SiteDetailsCommonResponse<CommonResponse>> =
            safeApiCall(dispatcher) {
                apiService.postSiteDetailByProjectAPI(
                    apiKey,
                    Integer.parseInt(projectId),
                    SiteDetailsRequest(userId.uppercase(Locale.ROOT))
                )
            }) {
            is ResponseWrapper.GenericError -> SiteDetailByProjectResult.Error(
                response.error?.Message ?: oopsMessage
            )

            ResponseWrapper.NetworkError -> SiteDetailByProjectResult.Error(networkErrorMessage)
            is ResponseWrapper.Success<SiteDetailsCommonResponse<CommonResponse>> -> {
                val data = response.value
                when {
                    !(response.value.commonResponse?.isSuccess())!! -> SiteDetailByProjectResult.Error(
                        data.commonResponse?.Message.toString()
                    )

                    data == null -> SiteDetailByProjectResult.Error(oopsMessage)
                    else -> SiteDetailByProjectResult.Success(data)
                }
            }
        }

    }

    override suspend fun postSendMaterialAPI(
        apiKey: String,
        projectId: String,
        siteId: String,
        lstSendMaterialRequest: List<SendMaterialRequest>
    ): SendMaterialResult {
        return when (val response: ResponseWrapper<CommonSendMaterialResponse<List<SendMaterialResponse>>> =
            safeApiCall(dispatcher) {
                var jsonArraySendData = JSONArray()

                try {
                    for (sendMaterial in lstSendMaterialRequest) {
                        val jsonObjSendMaterial = JSONObject()
                        jsonObjSendMaterial.put(
                            "MaterialCode", sendMaterial.materialCode?.uppercase(Locale.ROOT)
                        )
                        jsonObjSendMaterial.put("Origin", sendMaterial.origin)
                        jsonObjSendMaterial.put("PONumber", sendMaterial.pONumber)
                        jsonObjSendMaterial.put("POItem", sendMaterial.pOItem)
                        jsonObjSendMaterial.put("IDType", sendMaterial.iDType)
                        jsonObjSendMaterial.put("Identifier", sendMaterial.identifier)
                        jsonObjSendMaterial.put("IDDescription", sendMaterial.iDDescription)
                        jsonObjSendMaterial.put("DateType", sendMaterial.dateType)
                        jsonObjSendMaterial.put("Split", sendMaterial.split)
                        jsonObjSendMaterial.put("TargetDate", sendMaterial.targetDate)
                        jsonObjSendMaterial.put("ActualDate", sendMaterial.actualDate)
                        jsonObjSendMaterial.put("ContractDate", sendMaterial.contractDate)
                        jsonObjSendMaterial.put("Quantity", sendMaterial.quantity)
                        jsonObjSendMaterial.put("Remarks", sendMaterial.remarks)
                        jsonObjSendMaterial.put("TagCode", sendMaterial.tagCode)
                        jsonArraySendData = JSONArray(jsonObjSendMaterial.toString())
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                apiService.postSendMaterialAPI(
                    apiKey, Integer.parseInt(projectId), Integer.parseInt(siteId), jsonArraySendData
                )
            }) {
            is ResponseWrapper.GenericError -> SendMaterialResult.Error(
                response.error?.Message ?: oopsMessage
            )

            ResponseWrapper.NetworkError -> SendMaterialResult.Error(networkErrorMessage)
            is ResponseWrapper.Success<CommonSendMaterialResponse<List<SendMaterialResponse>>> -> {
                val data = response.value
                when {
                    !(data.isSuccess()) -> SendMaterialResult.Error(data.Error_Message.toString())
                    data == null -> SendMaterialResult.Error(oopsMessage)
                    else -> {
                        data.sendMaterial.let { SendMaterialResult.Success(it) }
                    }
                }
            }
        }

    }

    override suspend fun postAssignedMaterialAPI(
        apiKey: String, projectId: String, siteId: String, qRCode: String
    ): AssignedMaterialResult {
        return when (val response: ResponseWrapper<String> = safeApiCall(dispatcher) {
            apiService.postAssignedMaterialAPI(
                apiKey,
                Integer.parseInt(projectId),
                Integer.parseInt(siteId),
                QRCodeRequest(qRCode.uppercase(Locale.ROOT))
            )
        }) {
            is ResponseWrapper.GenericError -> AssignedMaterialResult.Error(
                response.error?.Message ?: oopsMessage
            )

            ResponseWrapper.NetworkError -> AssignedMaterialResult.Error(networkErrorMessage)
            is ResponseWrapper.Success<String> -> {
                val data = response.value
                when {
                    data == null -> AssignedMaterialResult.Error(oopsMessage)
                    else -> AssignedMaterialResult.Success(data)
                }
            }
        }

    }

    override suspend fun getLocationOfAllMaterialsAPI(
        apiKey: String,
        projectId: String,
        siteId: String,
    ): LocationAssignMaterialResult? {
        return when (val response: ResponseWrapper<CommonMaterialDetailsResponse<List<LocationAssignMaterialResponse>>> =
            safeApiCall(dispatcher) {
                apiService.getLocationOfAllMaterialsAPI(
                    apiKey,
                    Integer.parseInt(projectId),
                    Integer.parseInt(siteId),
                )
            }) {
            is ResponseWrapper.GenericError -> LocationAssignMaterialResult.Error(
                response.error?.Message ?: oopsMessage
            )

            ResponseWrapper.NetworkError -> LocationAssignMaterialResult.Error(networkErrorMessage)
            is ResponseWrapper.Success<CommonMaterialDetailsResponse<List<LocationAssignMaterialResponse>>> -> {
                val data = response.value
                when {
                    !(data.isSuccess()) -> LocationAssignMaterialResult.Error(data.Error_Message.toString())
                    data == null -> LocationAssignMaterialResult.Error(oopsMessage)
                    else -> data.Material_Details?.let { LocationAssignMaterialResult.Success(it) }
                }
            }
        }
    }

    override suspend fun postLocationOfAssignMaterialsAPI(
        apiKey: String,
        projectId: String,
        siteId: String,
        lstAssignMaterialRequest: List<AssignMaterialRequest>
    ): LocationAssignMaterialResult {
        return when (val response: ResponseWrapper<CommonMaterialDetailsResponse<List<LocationAssignMaterialResponse>>> =
            safeApiCall(dispatcher) {
                var jsonArrayAssignCodeData = JSONArray()

                try {
                    for (assignMaterial in lstAssignMaterialRequest) {
                        val jsonObjSendMaterial = JSONObject()
                        jsonObjSendMaterial.put(
                            "MaterialCode", assignMaterial.materialCode?.uppercase(Locale.ROOT)
                        )
                        jsonArrayAssignCodeData = JSONArray(jsonObjSendMaterial.toString())
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                apiService.postLocationOfAssignMaterialsAPI(
                    apiKey,
                    Integer.parseInt(projectId),
                    Integer.parseInt(siteId),
                    jsonArrayAssignCodeData
                )
            }) {
            is ResponseWrapper.GenericError -> LocationAssignMaterialResult.Error(
                response.error?.Message ?: oopsMessage
            )

            ResponseWrapper.NetworkError -> LocationAssignMaterialResult.Error(networkErrorMessage)
            is ResponseWrapper.Success<CommonMaterialDetailsResponse<List<LocationAssignMaterialResponse>>> -> {
                val data = response.value
                when {
                    !(data.isSuccess()) -> LocationAssignMaterialResult.Error(data.Error_Message.toString())
                    data == null -> LocationAssignMaterialResult.Error(oopsMessage)
                    else -> {
                        data.Material_Details.let { LocationAssignMaterialResult.Success(it) }
                    }
                }
            }
        }

    }

    override suspend fun getProjectDetailAPI(): ProjectDetailsResult {
        return when (val response: ResponseWrapper<CommonProjectDetailsResponse<List<ProjectDetailsResponse>>> =
            safeApiCall(dispatcher) {
                apiService.getProjectDetailAPI("TGluZGUgUHZ0IEx0ZA==")
            }) {
            is ResponseWrapper.GenericError -> ProjectDetailsResult.Error(
                response.error?.Message ?: oopsMessage
            )

            ResponseWrapper.NetworkError -> ProjectDetailsResult.Error(networkErrorMessage)
            is ResponseWrapper.Success<CommonProjectDetailsResponse<List<ProjectDetailsResponse>>> -> {
                val data = response.value
                when {
                    !(data.isSuccess()) -> ProjectDetailsResult.Error(data.err_msg.toString())
                    data == null -> ProjectDetailsResult.Error(oopsMessage)
                    else -> {
                        data.projectDetails.let { ProjectDetailsResult.Success(it) }
                    }
                }
            }
        }

    }

    override suspend fun getProjectKeysAPI(): ProjectKeysResult {
        return when (val response: ResponseWrapper<CommonProjectDetailsResponse<List<ProjectKeysResponse>>> =
            safeApiCall(dispatcher) {
                apiService.getProjectKeysAPI("TGluZGUgUHZ0IEx0ZA==")
            }) {
            is ResponseWrapper.GenericError -> ProjectKeysResult.Error(
                response.error?.Message ?: oopsMessage
            )

            ResponseWrapper.NetworkError -> ProjectKeysResult.Error(networkErrorMessage)
            is ResponseWrapper.Success<CommonProjectDetailsResponse<List<ProjectKeysResponse>>> -> {
                val data = response.value
                when {
                    !(data.isSuccess()) -> ProjectKeysResult.Error(data.err_msg.toString())
                    data == null -> ProjectKeysResult.Error(oopsMessage)
                    else -> {
                        data.projectDetails.let { ProjectKeysResult.Success(it) }
                    }
                }
            }
        }
    }

    override suspend fun postInsertMAPSearchResultAPI(
        apiKey: String,
        projectId: String,
        siteId: String,
        userId: String,
        materialCode: String,
        totalSearchTime: String
    ): CommonResult {
        return when (val response: ResponseWrapper<CommonResponse> = safeApiCall(dispatcher) {
            apiService.postInsertMAPSearchResultAPI(
                apiKey,
                Integer.parseInt(projectId),
                Integer.parseInt(siteId),
                MapSearchResultRequest(
                    userId, materialCode.uppercase(Locale.ROOT), totalSearchTime
                )
            )
        }) {
            is ResponseWrapper.GenericError -> CommonResult.Error(
                response.error?.Message ?: oopsMessage
            )

            ResponseWrapper.NetworkError -> CommonResult.Error(networkErrorMessage)
            is ResponseWrapper.Success<CommonResponse> -> {
                val data = response.value.Message.toString()
                when {
                    !(response.value.isSuccess()) -> CommonResult.Error(data)
                    data == null -> CommonResult.Error(oopsMessage)
                    else -> CommonResult.Success(data)
                }
            }
        }

    }

    override suspend fun logout(): Boolean {/* persistenceManager.logout()
         firebaseAuth.signOut()*/
        return true
    }
}
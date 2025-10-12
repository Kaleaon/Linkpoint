package com.linkpoint.slproto.caps

import com.linkpoint.slproto.llsd.*
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

/**
 * CAPS (Capabilities) Manager
 * Handles HTTP-based capabilities for Second Life
 */
class CapsManager(private val seedCapability: String) {
    
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()
    
    private val capabilities = ConcurrentHashMap<String, String>()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    companion object {
        private val JSON_MEDIA_TYPE = "application/llsd+xml".toMediaType()
        
        // Common capability names
        const val CAP_SEED = "SeedCapability"
        const val CAP_EVENT_QUEUE = "EventQueueGet"
        const val CAP_UPLOAD_BAKED_TEXTURE = "UploadBakedTexture"
        const val CAP_FETCH_INVENTORY = "FetchInventory2"
        const val CAP_FETCH_LIB = "FetchLib2"
        const val CAP_FETCH_INVENTORY_DESCENDENTS = "FetchInventoryDescendents2"
        const val CAP_GET_TEXTURE = "GetTexture"
        const val CAP_GET_MESH = "GetMesh"
        const val CAP_GET_MESH2 = "GetMesh2"
        const val CAP_OBJECT_MEDIA = "ObjectMedia"
        const val CAP_OBJECT_MEDIA_NAVIGATE = "ObjectMediaNavigate"
        const val CAP_PARCEL_PROPERTIES = "ParcelPropertiesUpdate"
        const val CAP_PRODUCT_INFO = "ProductInfoRequest"
        const val CAP_PROVISION_VOICE = "ProvisionVoiceAccountRequest"
        const val CAP_REMOTE_PARCEL = "RemoteParcelRequest"
        const val CAP_REQUEST_TEXTURE = "RequestTextureDownload"
        const val CAP_SEARCH_STAT = "SearchStatRequest"
        const val CAP_SEND_POST = "SendPostcard"
        const val CAP_SEND_USER_REPORT = "SendUserReport"
        const val CAP_SERVER_RELEASE_NOTES = "ServerReleaseNotes"
        const val CAP_START_GROUP_PROPOSAL = "StartGroupProposal"
        const val CAP_UPDATE_AGENT_INFO = "UpdateAgentInformation"
        const val CAP_UPDATE_AGENT_LANGUAGE = "UpdateAgentLanguage"
        const val CAP_UPDATE_GESTURE_AGENT_INVENTORY = "UpdateGestureAgentInventory"
        const val CAP_UPDATE_GESTURE_TASK_INVENTORY = "UpdateGestureTaskInventory"
        const val CAP_UPDATE_NOTECARD_AGENT_INVENTORY = "UpdateNotecardAgentInventory"
        const val CAP_UPDATE_NOTECARD_TASK_INVENTORY = "UpdateNotecardTaskInventory"
        const val CAP_UPDATE_SCRIPT_AGENT = "UpdateScriptAgent"
        const val CAP_UPDATE_SCRIPT_TASK = "UpdateScriptTask"
        const val CAP_UPLOAD_ASSET = "NewFileAgentInventory"
        const val CAP_VIEW_ADMIN_OPTIONS = "ViewerStartAuction"
        const val CAP_VIEW_STATS = "ViewerStats"
    }
    
    /**
     * Initialize capabilities by requesting from seed
     */
    suspend fun initialize(): Boolean = withContext(Dispatchers.IO) {
        try {
            // Request capabilities from seed
            val capsToRequest = listOf(
                CAP_EVENT_QUEUE,
                CAP_UPLOAD_BAKED_TEXTURE,
                CAP_FETCH_INVENTORY,
                CAP_FETCH_LIB,
                CAP_FETCH_INVENTORY_DESCENDENTS,
                CAP_GET_TEXTURE,
                CAP_GET_MESH,
                CAP_GET_MESH2,
                CAP_OBJECT_MEDIA,
                CAP_PARCEL_PROPERTIES,
                CAP_PROVISION_VOICE,
                CAP_UPDATE_AGENT_INFO,
                CAP_UPLOAD_ASSET
            )
            
            val requestMap = LLSDMap()
            val capsArray = LLSDArray()
            for (cap in capsToRequest) {
                capsArray.add(LLSDString(cap))
            }
            requestMap["capabilities"] = capsArray
            
            val response = postLLSD(seedCapability, requestMap)
            
            if (response is LLSDMap) {
                for ((key, value) in response.value) {
                    if (value is LLSDString) {
                        capabilities[key] = value.value
                    }
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Get a capability URL
     */
    fun getCapability(name: String): String? {
        return capabilities[name]
    }
    
    /**
     * Check if capability exists
     */
    fun hasCapability(name: String): Boolean {
        return capabilities.containsKey(name)
    }
    
    /**
     * POST LLSD data to a capability
     */
    suspend fun postLLSD(url: String, data: LLSD): LLSD = withContext(Dispatchers.IO) {
        val jsonString = JSONObject(data.toJson() as Map<*, *>).toString()
        val requestBody = jsonString.toRequestBody(JSON_MEDIA_TYPE)
        
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        
        val response = httpClient.newCall(request).execute()
        
        if (!response.isSuccessful) {
            throw IOException("CAPS request failed: ${response.code}")
        }
        
        val responseBody = response.body?.string() ?: throw IOException("Empty response")
        val jsonResponse = JSONObject(responseBody)
        
        LLSD.fromJson(jsonResponse)
    }
    
    /**
     * GET LLSD data from a capability
     */
    suspend fun getLLSD(url: String): LLSD = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        
        val response = httpClient.newCall(request).execute()
        
        if (!response.isSuccessful) {
            throw IOException("CAPS request failed: ${response.code}")
        }
        
        val responseBody = response.body?.string() ?: throw IOException("Empty response")
        val jsonResponse = JSONObject(responseBody)
        
        LLSD.fromJson(jsonResponse)
    }
    
    /**
     * Event queue polling
     */
    fun startEventQueue(onEvent: (LLSD) -> Unit) {
        val eventQueueUrl = getCapability(CAP_EVENT_QUEUE) ?: return
        
        scope.launch {
            while (isActive) {
                try {
                    val response = getLLSD(eventQueueUrl)
                    
                    if (response is LLSDMap) {
                        val events = response["events"]
                        if (events is LLSDArray) {
                            for (event in events.value) {
                                onEvent(event)
                            }
                        }
                    }
                    
                    delay(1000) // Poll every second
                } catch (e: Exception) {
                    e.printStackTrace()
                    delay(5000) // Wait longer on error
                }
            }
        }
    }
    
    /**
     * Stop event queue
     */
    fun stopEventQueue() {
        scope.cancel()
    }
    
    /**
     * Upload asset via CAPS
     */
    suspend fun uploadAsset(
        assetType: String,
        data: ByteArray,
        name: String,
        description: String
    ): LLSD = withContext(Dispatchers.IO) {
        val uploadUrl = getCapability(CAP_UPLOAD_ASSET) 
            ?: throw IOException("Upload capability not available")
        
        // Request upload
        val requestMap = LLSDMap()
        requestMap["asset_type"] = LLSDString(assetType)
        requestMap["name"] = LLSDString(name)
        requestMap["description"] = LLSDString(description)
        
        val response = postLLSD(uploadUrl, requestMap)
        
        if (response is LLSDMap) {
            val uploaderUrl = response["uploader"]
            if (uploaderUrl is LLSDString) {
                // Upload the actual data
                uploadAssetData(uploaderUrl.value, data)
            } else {
                throw IOException("No uploader URL in response")
            }
        } else {
            throw IOException("Invalid upload response")
        }
    }
    
    private suspend fun uploadAssetData(url: String, data: ByteArray): LLSD = withContext(Dispatchers.IO) {
        val requestBody = data.toRequestBody("application/octet-stream".toMediaType())
        
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        
        val response = httpClient.newCall(request).execute()
        
        if (!response.isSuccessful) {
            throw IOException("Asset upload failed: ${response.code}")
        }
        
        val responseBody = response.body?.string() ?: throw IOException("Empty response")
        val jsonResponse = JSONObject(responseBody)
        
        LLSD.fromJson(jsonResponse)
    }
}
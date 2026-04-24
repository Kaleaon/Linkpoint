package com.linkpoint.assets

import android.util.Log
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.llsd.*
import com.linkpoint.protocol.transfer.TransferManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * ScriptManager - Handles LSL/Mono script viewing and basic management.
 * 
 * Features:
 * - View script source code
 * - Script state (running/not running)
 * - Reset script
 * - Set script running state
 * - Script memory/time usage
 * 
 * Note: Full script editing requires a desktop viewer. This provides
 * viewing and basic management functionality like the reference viewer.
 */
class ScriptManager(
    private val capabilityManager: CapabilityManager,
    private val transferManager: TransferManager
) {
    companion object {
        private const val TAG = "ScriptManager"
        private const val SCRIPT_UPLOAD_CONTENT_TYPE = "application/octet-stream"
        
        // Script types
        const val SCRIPT_TYPE_LSL = 0
        const val SCRIPT_TYPE_MONO = 1
        
        // Script states
        const val STATE_NOT_RUNNING = 0
        const val STATE_RUNNING = 1
        const val STATE_SUSPENDED = 2
    }
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()
    
    // Cached script data
    private val scriptCache = ConcurrentHashMap<UUID, ScriptData>()
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    /**
     * Fetch script source code.
     */
    suspend fun getScriptSource(assetId: UUID): String? {
        return withContext(Dispatchers.IO) {
            try {
                _isLoading.value = true
                
                // Check cache
                scriptCache[assetId]?.source?.let { return@withContext it }
                
                // Fetch via transfer - asset type 10 is LSL text
                val assetData = transferManager.fetchAsset(assetId, 10)
                    ?: return@withContext null
                
                val source = String(assetData, Charsets.UTF_8)
                
                // Cache it
                scriptCache.getOrPut(assetId) { ScriptData(assetId) }.apply {
                    this.source = source
                }
                
                Log.d(TAG, "Fetched script source for $assetId (${source.length} chars)")
                source
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch script source", e)
                null
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Get script running state via GetScriptRunning capability.
     */
    suspend fun getScriptRunning(objectId: UUID, itemId: UUID): ScriptRunningInfo? {
        return withContext(Dispatchers.IO) {
            try {
                val request = LLSDMap().apply {
                    this["object_id"] = LLSDUUID(objectId)
                    this["item_id"] = LLSDUUID(itemId)
                }
                
                val response = capabilityManager.request(CapabilityManager.CAP_GET_SCRIPT_RUNNING, request)
                
                if (response is LLSDMap) {
                    ScriptRunningInfo(
                        objectId = objectId,
                        itemId = itemId,
                        isRunning = response.getBoolean("running") ?: false,
                        isMono = response.getBoolean("mono") ?: false
                    )
                } else null
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get script running state", e)
                null
            }
        }
    }
    
    /**
     * Set script running state.
     */
    suspend fun setScriptRunning(objectId: UUID, itemId: UUID, running: Boolean): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val request = LLSDMap().apply {
                    this["object_id"] = LLSDUUID(objectId)
                    this["item_id"] = LLSDUUID(itemId)
                    this["running"] = LLSDBoolean(running)
                }
                
                val response = capabilityManager.request(CapabilityManager.CAP_SET_SCRIPT_RUNNING, request)
                
                if (response is LLSDMap) {
                    val success = response.getBoolean("success") ?: false
                    if (success) {
                        Log.i(TAG, "Set script $itemId running=$running")
                    }
                    success
                } else false
            } catch (e: Exception) {
                Log.e(TAG, "Failed to set script running state", e)
                false
            }
        }
    }
    
    /**
     * Reset script.
     */
    suspend fun resetScript(objectId: UUID, itemId: UUID): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // First stop, then start to reset
                setScriptRunning(objectId, itemId, false)
                delay(100)
                setScriptRunning(objectId, itemId, true)
                Log.i(TAG, "Reset script $itemId")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to reset script", e)
                false
            }
        }
    }
    
    /**
     * Get script info including memory usage.
     */
    suspend fun getScriptInfo(objectId: UUID): List<ScriptInfo>? {
        return withContext(Dispatchers.IO) {
            try {
                // Use GetObjectScriptInfo capability if available
                val request = LLSDMap().apply {
                    this["object_id"] = LLSDUUID(objectId)
                }
                
                val response = capabilityManager.request(CapabilityManager.CAP_GET_SCRIPT_TASK_INFO, request)
                
                if (response is LLSDMap) {
                    val scripts = response.getArray("scripts")
                    val result = mutableListOf<ScriptInfo>()
                    if (scripts != null) {
                        for (item in scripts.value) {
                            if (item is LLSDMap) {
                                result.add(ScriptInfo(
                                    itemId = item.getUUID("item_id") ?: UUID(0, 0),
                                    name = item.getString("name") ?: "Unknown",
                                    isRunning = item.getBoolean("running") ?: false,
                                    isMono = item.getBoolean("mono") ?: false,
                                    memoryUsed = item.getInt("memory") ?: 0,
                                    memoryLimit = item.getInt("memory_limit") ?: 65536,
                                    timeUsed = item.getReal("time")?.toFloat() ?: 0f
                                ))
                            }
                        }
                    }
                    if (result.isNotEmpty()) result else null
                } else null
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get script info", e)
                null
            }
        }
    }

    /**
     * Save script source via UpdateScriptAgent/UpdateScriptTask capabilities.
     */
    suspend fun saveScript(itemId: UUID, scriptText: String, taskId: UUID? = null): ScriptSaveResult {
        return withContext(Dispatchers.IO) {
            try {
                val capability = if (taskId == null) {
                    CapabilityManager.CAP_UPDATE_SCRIPT_AGENT
                } else {
                    CapabilityManager.CAP_UPDATE_SCRIPT_TASK
                }

                val request = LLSDMap().apply {
                    this["item_id"] = LLSDUUID(itemId)
                    taskId?.let { this["task_id"] = LLSDUUID(it) }
                    this["target"] = LLSDString("mono")
                    if (taskId != null) {
                        this["is_script_running"] = LLSDInteger(1)
                    }
                }

                val capResponse = capabilityManager.request(capability, request) as? LLSDMap
                    ?: return@withContext ScriptSaveResult(false, "$capability returned no payload")

                val capState = capResponse.getString("state")
                if (capState.equals("complete", ignoreCase = true)) {
                    val completedResult = buildScriptSaveResult(capResponse)
                    if (completedResult.success) {
                        Log.i(TAG, "Saved script $itemId (${scriptText.length} chars) without uploader step")
                    }
                    return@withContext completedResult
                }

                val uploaderUrl = capResponse.getString("uploader")?.takeIf { it.isNotBlank() }
                    ?: return@withContext ScriptSaveResult(false, "Uploader URL missing from capability response")

                val normalizedUploader = if (uploaderUrl.startsWith("http://", ignoreCase = true)) {
                    uploaderUrl.replaceFirst("http://", "https://", ignoreCase = true)
                } else uploaderUrl

                val uploadRequest = Request.Builder()
                    .url(normalizedUploader)
                    .addHeader("Accept", "application/llsd+xml")
                    .post(scriptText.toByteArray(Charsets.UTF_8).toRequestBody(SCRIPT_UPLOAD_CONTENT_TYPE.toMediaType()))
                    .build()

                httpClient.newCall(uploadRequest).execute().use { response ->
                    if (!response.isSuccessful) {
                        return@withContext ScriptSaveResult(false, "Script upload failed (HTTP ${response.code})")
                    }

                    val responseBody = response.body?.bytes()
                        ?: return@withContext ScriptSaveResult(false, "Script upload returned empty response")

                    val uploadResponse = LLSDParser.parseAuto(
                        responseBody,
                        response.header("Content-Type")
                    ) as? LLSDMap ?: return@withContext ScriptSaveResult(
                        false,
                        "Script upload response was not LLSD map"
                    )

                    val saveResult = buildScriptSaveResult(uploadResponse)
                    if (!saveResult.success) return@withContext saveResult

                    Log.i(TAG, "Saved script $itemId (${scriptText.length} chars)")
                    saveResult
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save script $itemId", e)
                ScriptSaveResult(success = false, error = e.message ?: "Unknown error")
            }
        }
    }

    private fun buildScriptSaveResult(response: LLSDMap): ScriptSaveResult {
        if (!response.getString("state").equals("complete", ignoreCase = true)) {
            return ScriptSaveResult(
                success = false,
                error = "Upload state was ${response.getString("state") ?: "unknown"}"
            )
        }

        val compileErrors = response.getArray("errors")
            ?.value
            ?.map { errorNode ->
                when (errorNode) {
                    is LLSDString -> errorNode.value
                    is LLSDURI -> errorNode.value
                    else -> errorNode.toXML()
                }
            }
            .orEmpty()

        if (response.getBoolean("compiled") == false) {
            return ScriptSaveResult(
                success = false,
                error = compileErrors.firstOrNull() ?: "Script failed to compile",
                compileErrors = compileErrors
            )
        }

        return ScriptSaveResult(success = true, compileErrors = compileErrors)
    }
    
    /**
     * Clear script cache.
     */
    fun clearCache() {
        scriptCache.clear()
    }
    
    /**
     * Handle script control change notification.
     * This is called when a script takes or releases control of the avatar.
     * 
     * @param controls The control flags being taken/released
     * @param takeControls Whether the script is taking controls (true) or releasing (false)
     * @param passToAgent Whether input should still be passed to the agent
     */
    fun handleScriptControlChange(controls: Int, takeControls: Boolean, passToAgent: Boolean) {
        if (takeControls) {
            Log.d(TAG, "Script taking controls: 0x${controls.toString(16)} (pass to agent: $passToAgent)")
            // In a full implementation, this would:
            // 1. Update UI to show script is controlling avatar
            // 2. Modify input handling to respect passToAgent flag
            // 3. Track which controls are captured (movement, mouse, etc.)
        } else {
            Log.d(TAG, "Script releasing controls: 0x${controls.toString(16)}")
            // Would restore normal avatar control
        }
    }
    
    fun shutdown() {
        scope.cancel()
        clearCache()
    }
}

/**
 * Cached script data.
 */
data class ScriptData(
    val assetId: UUID,
    var source: String? = null,
    var compiledData: ByteArray? = null,
    var scriptType: Int = ScriptManager.SCRIPT_TYPE_LSL
)

/**
 * Script running info.
 */
data class ScriptRunningInfo(
    val objectId: UUID,
    val itemId: UUID,
    val isRunning: Boolean,
    val isMono: Boolean
)

/**
 * Detailed script info.
 */
data class ScriptInfo(
    val itemId: UUID,
    val name: String,
    val isRunning: Boolean,
    val isMono: Boolean,
    val memoryUsed: Int,
    val memoryLimit: Int,
    val timeUsed: Float // Script time in ms
) {
    val memoryPercentage: Float get() = (memoryUsed.toFloat() / memoryLimit) * 100f
    val memoryDisplay: String get() = "${memoryUsed / 1024}KB / ${memoryLimit / 1024}KB"
}

data class ScriptSaveResult(
    val success: Boolean,
    val error: String? = null,
    val compileErrors: List<String> = emptyList()
)

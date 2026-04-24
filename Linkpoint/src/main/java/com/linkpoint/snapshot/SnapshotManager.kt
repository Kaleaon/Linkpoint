package com.linkpoint.snapshot

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.llsd.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * SnapshotManager - Handles taking and uploading snapshots.
 * 
 * Features:
 * - Take in-world snapshots
 * - Save to device gallery
 * - Upload to SL inventory
 * - Share to profile feed
 * - Screenshot of 3D view
 * 
 * Based on reference viewer/Firestorm snapshot functionality.
 */
class SnapshotManager(
    private val context: Context,
    private val capabilityManager: CapabilityManager
) {
    companion object {
        private const val TAG = "SnapshotManager"
        
        // Image formats
        const val FORMAT_JPEG = "jpeg"
        const val FORMAT_PNG = "png"
        const val FORMAT_BMP = "bmp"
        
        // Upload destinations
        const val UPLOAD_INVENTORY = 1
        const val UPLOAD_PROFILE = 2
        
        // Default quality
        private const val DEFAULT_JPEG_QUALITY = 95
        
        // Upload cost (in L$)
        const val UPLOAD_COST = 10
    }
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Reusable HTTP client for uploads
    private val httpClient = okhttp3.OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()
    
    // Snapshot state
    private val _isTakingSnapshot = MutableStateFlow(false)
    val isTakingSnapshot: StateFlow<Boolean> = _isTakingSnapshot
    
    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading
    
    private val _lastSnapshot = MutableStateFlow<Bitmap?>(null)
    val lastSnapshot: StateFlow<Bitmap?> = _lastSnapshot
    
    // Events
    private val _snapshotEvents = MutableSharedFlow<SnapshotEvent>(replay = 0, extraBufferCapacity = 8)
    val snapshotEvents: SharedFlow<SnapshotEvent> = _snapshotEvents
    
    // Settings
    private var defaultFormat = FORMAT_JPEG
    private var jpegQuality = DEFAULT_JPEG_QUALITY
    private var autoSaveToGallery = true
    
    /**
     * Capture a snapshot from the render view.
     * This should be called from the render thread with the current framebuffer.
     */
    fun captureSnapshot(bitmap: Bitmap, description: String = "") {
        scope.launch {
            try {
                _isTakingSnapshot.value = true
                _lastSnapshot.value = bitmap
                
                // Auto-save to gallery if enabled
                if (autoSaveToGallery) {
                    val uri = saveToGallery(bitmap, description)
                    if (uri != null) {
                        _snapshotEvents.emit(SnapshotEvent.SavedToGallery(uri))
                    }
                }
                
                _snapshotEvents.emit(SnapshotEvent.Captured(bitmap))
                Log.i(TAG, "Snapshot captured: ${bitmap.width}x${bitmap.height}")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to capture snapshot", e)
                _snapshotEvents.emit(SnapshotEvent.Error("Failed to capture: ${e.message}"))
            } finally {
                _isTakingSnapshot.value = false
            }
        }
    }
    
    /**
     * Save bitmap to device gallery.
     */
    suspend fun saveToGallery(bitmap: Bitmap, description: String = ""): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                val filename = generateFilename()
                val mimeType = when (defaultFormat) {
                    FORMAT_PNG -> "image/png"
                    FORMAT_BMP -> "image/bmp"
                    else -> "image/jpeg"
                }
                
                val outputStream: OutputStream?
                val imageUri: Uri?
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Use MediaStore for Android 10+
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Linkpoint")
                        put(MediaStore.Images.Media.DESCRIPTION, description)
                    }
                    
                    imageUri = context.contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                    )
                    
                    outputStream = imageUri?.let { context.contentResolver.openOutputStream(it) }
                } else {
                    // Legacy approach for older devices
                    @Suppress("DEPRECATION")
                    val directory = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        "Linkpoint"
                    )
                    if (!directory.exists()) {
                        directory.mkdirs()
                    }
                    
                    val file = File(directory, filename)
                    outputStream = FileOutputStream(file)
                    imageUri = Uri.fromFile(file)
                }
                
                outputStream?.use { stream ->
                    val format = when (defaultFormat) {
                        FORMAT_PNG -> Bitmap.CompressFormat.PNG
                        else -> Bitmap.CompressFormat.JPEG
                    }
                    bitmap.compress(format, jpegQuality, stream)
                }
                
                Log.i(TAG, "Snapshot saved to gallery: $imageUri")
                imageUri
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save to gallery", e)
                null
            }
        }
    }
    
    /**
     * Upload snapshot to SL inventory.
     */
    suspend fun uploadToInventory(
        bitmap: Bitmap,
        name: String,
        description: String = ""
    ): UploadResult {
        return withContext(Dispatchers.IO) {
            try {
                _isUploading.value = true
                _snapshotEvents.emit(SnapshotEvent.UploadStarted)
                
                // Convert bitmap to JPEG bytes
                val outputStream = java.io.ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, jpegQuality, outputStream)
                val imageBytes = outputStream.toByteArray()
                
                // Use NewFileAgentInventory capability
                val uploadCap = capabilityManager.getCapability(CapabilityManager.CAP_NEW_FILE_AGENT_INVENTORY)
                if (uploadCap == null) {
                    return@withContext UploadResult.Error("Upload capability not available")
                }
                
                // Request upload URL
                val request = LLSDMap().apply {
                    this["asset_type"] = LLSDString("texture")
                    this["inventory_type"] = LLSDString("texture")
                    this["name"] = LLSDString(name)
                    this["description"] = LLSDString(description)
                    this["expected_upload_cost"] = LLSDInteger(UPLOAD_COST)
                }
                
                val response = capabilityManager.request(CapabilityManager.CAP_NEW_FILE_AGENT_INVENTORY, request)
                
                if (response is LLSDMap) {
                    val uploaderUrl = response.getString("uploader")
                    if (uploaderUrl != null) {
                        // Upload the image data using the reusable HTTP client
                        try {
                            val requestBody = imageBytes.toRequestBody("application/octet-stream".toMediaTypeOrNull())
                            
                            val uploadRequest = okhttp3.Request.Builder()
                                .url(uploaderUrl)
                                .post(requestBody)
                                .build()
                            
                            val uploadHttpResponse = httpClient.newCall(uploadRequest).execute()
                            val uploadResponseBody = uploadHttpResponse.body?.string()
                            
                            if (uploadHttpResponse.isSuccessful && uploadResponseBody != null) {
                                // Parse LLSD response
                                val uploadResponse = LLSDParser.parse(uploadResponseBody.toByteArray())
                                
                                if (uploadResponse is LLSDMap) {
                                    val assetId = uploadResponse.getUUID("new_asset")
                                    val itemId = uploadResponse.getUUID("new_inventory_item")
                                    
                                    if (assetId != null) {
                                        _snapshotEvents.emit(SnapshotEvent.UploadComplete(assetId))
                                        Log.i(TAG, "Snapshot uploaded: asset=$assetId, item=$itemId")
                                        return@withContext UploadResult.Success(assetId, itemId)
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to upload to uploader URL", e)
                        }
                    }
                    
                    // Check for error
                    val error = response.getString("error")
                    if (error != null) {
                        return@withContext UploadResult.Error(error)
                    }
                }
                
                UploadResult.Error("Upload failed - invalid response")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to upload snapshot", e)
                _snapshotEvents.emit(SnapshotEvent.Error("Upload failed: ${e.message}"))
                UploadResult.Error("Upload failed: ${e.message}")
            } finally {
                _isUploading.value = false
            }
        }
    }
    
    /**
     * Share snapshot to profile feed.
     */
    suspend fun shareToProfile(bitmap: Bitmap, caption: String = ""): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // First upload to inventory
                val uploadResult = uploadToInventory(bitmap, "Profile Snapshot", caption)
                
                if (uploadResult is UploadResult.Success) {
                    // Then post to profile feed using ProfileImage capability
                    val postCap = capabilityManager.getCapability(CapabilityManager.CAP_UPLOAD_AGENT_PROFILE_IMAGE)
                    if (postCap != null) {
                        val request = LLSDMap().apply {
                            this["texture_id"] = LLSDUUID(uploadResult.assetId)
                            this["caption"] = LLSDString(caption)
                        }
                        
                        capabilityManager.request(CapabilityManager.CAP_UPLOAD_AGENT_PROFILE_IMAGE, request)
                        Log.i(TAG, "Snapshot shared to profile")
                        return@withContext true
                    }
                }
                
                false
            } catch (e: Exception) {
                Log.e(TAG, "Failed to share to profile", e)
                false
            }
        }
    }
    
    /**
     * Set snapshot format.
     */
    fun setFormat(format: String) {
        defaultFormat = format
    }
    
    /**
     * Set JPEG quality (1-100).
     */
    fun setJpegQuality(quality: Int) {
        jpegQuality = quality.coerceIn(1, 100)
    }
    
    /**
     * Enable/disable auto-save to gallery.
     */
    fun setAutoSaveToGallery(enabled: Boolean) {
        autoSaveToGallery = enabled
    }
    
    /**
     * Clear last snapshot from memory.
     */
    fun clearLastSnapshot() {
        _lastSnapshot.value?.recycle()
        _lastSnapshot.value = null
    }
    
    // ==================== PRIVATE METHODS ====================
    
    private fun generateFilename(): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val extension = when (defaultFormat) {
            FORMAT_PNG -> "png"
            FORMAT_BMP -> "bmp"
            else -> "jpg"
        }
        return "Linkpoint_$timestamp.$extension"
    }
    
    fun shutdown() {
        clearLastSnapshot()
        scope.cancel()
    }
}

/**
 * Snapshot events.
 */
sealed class SnapshotEvent {
    data class Captured(val bitmap: Bitmap) : SnapshotEvent()
    data class SavedToGallery(val uri: Uri) : SnapshotEvent()
    object UploadStarted : SnapshotEvent()
    data class UploadComplete(val assetId: UUID) : SnapshotEvent()
    data class Error(val message: String) : SnapshotEvent()
}

/**
 * Upload result.
 */
sealed class UploadResult {
    data class Success(val assetId: UUID, val itemId: UUID?) : UploadResult()
    data class Error(val message: String) : UploadResult()
}

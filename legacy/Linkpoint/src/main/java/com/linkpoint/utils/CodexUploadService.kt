package com.linkpoint.utils

import android.content.Context
import android.util.Log
import com.linkpoint.network.NetworkLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.util.UUID

class CodexUploadService private constructor(private val context: Context) {

    companion object {
        private const val TAG = "CodexUploadService"
        private const val PREFS_NAME = "com.linkpoint_preferences"
        private const val PREF_AUTO_UPLOAD = "codex_auto_upload"
        private const val PREF_ENDPOINT = "codex_upload_endpoint"
        private const val DEFAULT_ENDPOINT = "https://codex.openai.com/api/logs/upload"
        private const val MAX_UPLOAD_BYTES = 5 * 1024 * 1024

        @Volatile
        private var instance: CodexUploadService? = null

        fun getInstance(context: Context): CodexUploadService {
            return instance ?: synchronized(this) {
                instance ?: CodexUploadService(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val uploadMutex = Mutex()
    private val httpClient = OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .build()

    fun isAutoUploadEnabled(): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(PREF_AUTO_UPLOAD, false)
    }

    fun getEndpoint(): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val configured = prefs.getString(PREF_ENDPOINT, "").orEmpty().trim()
        return if (configured.isNotEmpty()) configured else DEFAULT_ENDPOINT
    }

    fun updateEndpoint(endpoint: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(PREF_ENDPOINT, endpoint.trim())
            .apply()
    }

    fun uploadLatestReportsAsync(trigger: String, onComplete: (UploadResult) -> Unit = {}) {
        scope.launch {
            val result = uploadLatestReports(trigger)
            withContext(Dispatchers.Main) {
                onComplete(result)
            }
        }
    }

    suspend fun uploadLatestReports(trigger: String): UploadResult {
        return uploadMutex.withLock {
            if (!isAutoUploadEnabled()) {
                return@withLock UploadResult.Disabled
            }

            val reportFiles = mutableListOf<File>()
            val debugService = DebugReportService.getInstance(context)
            val crashReporter = CrashReporter.getInstanceOrNull()
            reportFiles.addAll(debugService.getDebugReports().take(3))
            if (crashReporter != null) {
                reportFiles.addAll(crashReporter.getCrashLogs().take(3))
            }
            SessionLogRecorder.exportLog()?.let { reportFiles.add(it) }
            NetworkLogger.saveLogsToFile()?.let { reportFiles.add(it) }

            if (reportFiles.isEmpty()) {
                return@withLock UploadResult.NoData
            }

            val sanitizedFiles = reportFiles
                .mapNotNull { sanitizeForUpload(it) }
                .distinctBy { it.absolutePath }

            if (sanitizedFiles.isEmpty()) {
                return@withLock UploadResult.NoData
            }

            try {
                val request = buildMultipartRequest(trigger, sanitizedFiles)
                httpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        return@withLock UploadResult.Failed(
                            IOException("Upload failed with status ${response.code}")
                        )
                    }
                }
                return@withLock UploadResult.Success(sanitizedFiles.size)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to upload Codex reports", e)
                return@withLock UploadResult.Failed(e)
            }
        }
    }

    private fun buildMultipartRequest(trigger: String, files: List<File>): Request {
        val endpoint = getEndpoint()
        val metadataJson = """
            {
              \"client\": \"Linkpoint\",
              \"trigger\": \"$trigger\",
              \"report_id\": \"${UUID.randomUUID()}\"
            }
        """.trimIndent()

        val multipart = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "metadata",
                null,
                metadataJson.toRequestBody("application/json".toMediaType())
            )

        files.forEachIndexed { index, file ->
            multipart.addFormDataPart(
                "file_$index",
                file.name,
                file.asRequestBody("text/plain".toMediaType())
            )
        }

        return Request.Builder()
            .url(endpoint)
            .post(multipart.build())
            .build()
    }

    private fun sanitizeForUpload(file: File): File? {
        return try {
            if (!file.exists()) return null
            if (file.length() > MAX_UPLOAD_BYTES) {
                Log.w(TAG, "Skipping ${file.name}: exceeds upload size limit")
                return null
            }
            file
        } catch (e: Exception) {
            Log.w(TAG, "Unable to use file ${file.name} for upload", e)
            null
        }
    }

    sealed class UploadResult {
        data class Success(val fileCount: Int) : UploadResult()
        data class Failed(val error: Throwable) : UploadResult()
        data object Disabled : UploadResult()
        data object NoData : UploadResult()
    }
}

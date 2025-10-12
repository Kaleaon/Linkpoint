package com.lumiyaviewer.lumiya.debug

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.lumiyaviewer.lumiya.BuildConfig
import com.lumiyaviewer.lumiya.LumiyaApp
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Modern Kotlin AutoLogUploader
 * Automatic log uploader for debug builds
 * Uploads application logs and crash reports to GitHub for review
 */
class AutoLogUploader private constructor(context: Context) {
    
    private val context: Context = context.applicationContext
    private val prefs: SharedPreferences = context.getSharedPreferences("debug_upload", Context.MODE_PRIVATE)
    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    companion object {
        private const val TAG = "AutoLogUploader"
        private const val PREF_LAST_UPLOAD = "last_log_upload"
        private val UPLOAD_INTERVAL_MS = TimeUnit.HOURS.toMillis(1) // Upload every hour
        private const val MAX_LOG_SIZE = 50000 // 50KB limit for GitHub API
        
        @Volatile
        private var instance: AutoLogUploader? = null
        
        /**
         * Get singleton instance
         */
        @JvmStatic
        fun getInstance(context: Context): AutoLogUploader {
            return instance ?: synchronized(this) {
                instance ?: AutoLogUploader(context).also { instance = it }
            }
        }
    }
    
    /**
     * Initialize automatic log uploading (only for debug builds)
     */
    fun initializeAutoUpload() {
        // Only enable for debug builds
        if (!BuildConfig.DEBUG) {
            Log.d(TAG, "Auto log upload disabled for release builds")
            return
        }
        
        Log.i(TAG, "🚀 Initializing automatic log upload for debug build")
        
        // Upload immediately if it's been a while
        val lastUpload = prefs.getLong(PREF_LAST_UPLOAD, 0)
        val now = System.currentTimeMillis()
        
        if (now - lastUpload > UPLOAD_INTERVAL_MS) {
            uploadLogsAsync("Automatic startup log upload")
        } else {
            val timeUntilNext = UPLOAD_INTERVAL_MS - (now - lastUpload)
            Log.i(TAG, "Next automatic upload in ${timeUntilNext / 1000 / 60} minutes")
        }
    }
    
    /**
     * Upload logs immediately with a specific reason
     */
    fun uploadLogsNow(reason: String) {
        if (!BuildConfig.DEBUG) {
            Log.d(TAG, "Log upload disabled for release builds")
            return
        }
        
        Log.i(TAG, "📤 Manual log upload requested: $reason")
        uploadLogsAsync(reason)
    }
    
    /**
     * Upload crash report immediately
     */
    fun uploadCrashReport(crash: Throwable, additionalInfo: String) {
        if (!BuildConfig.DEBUG) return
        
        Log.i(TAG, "💥 Uploading crash report to GitHub")
        
        val crashLog = formatCrashReport(crash, additionalInfo)
        uploadLogContent(crashLog, "Crash Report", "crash")
    }
    
    /**
     * Upload logs asynchronously
     */
    private fun uploadLogsAsync(reason: String) {
        Thread {
            try {
                Log.i(TAG, "📊 Collecting application logs for upload...")
                val logContent = collectApplicationLogs()
                uploadLogContent(logContent, reason, "application")
                
                // Update last upload time
                prefs.edit().putLong(PREF_LAST_UPLOAD, System.currentTimeMillis()).apply()
            } catch (e: Exception) {
                Log.e(TAG, "❌ Failed to upload logs", e)
            }
        }.start()
    }
    
    /**
     * Collect application logs and system information
     */
    private fun collectApplicationLogs(): String {
        val logContent = StringBuilder()
        
        // Header information
        logContent.append("=== LINKPOINT DEBUG LOG UPLOAD ===\n")
        logContent.append("Timestamp: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US).format(Date())}\n")
        logContent.append("Version: ${LumiyaApp.getAppVersion()}\n")
        logContent.append("Build Type: DEBUG\n")
        logContent.append("Repository: https://github.com/Kaleaon/Linkpoint\n")
        
        logContent.append("\n=== DEVICE INFORMATION ===\n")
        logContent.append("Manufacturer: ${Build.MANUFACTURER}\n")
        logContent.append("Model: ${Build.MODEL}\n")
        logContent.append("Android Version: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})\n")
        logContent.append("Build ID: ${Build.ID}\n")
        logContent.append("Device ID: ${getDeviceIdentifier()}\n")
        
        // Application status
        logContent.append("\n=== APPLICATION STATUS ===\n")
        try {
            val appStatus = LumiyaApp.getStartupStatus()
            logContent.append("$appStatus\n")
        } catch (e: Exception) {
            logContent.append("Could not get app status: ${e.message}\n")
        }
        
        // Memory information
        logContent.append("\n=== MEMORY INFORMATION ===\n")
        val runtime = Runtime.getRuntime()
        val maxMemory = runtime.maxMemory()
        val totalMemory = runtime.totalMemory()
        val freeMemory = runtime.freeMemory()
        val usedMemory = totalMemory - freeMemory
        
        logContent.append("Max Memory: ${maxMemory / 1024 / 1024} MB\n")
        logContent.append("Total Memory: ${totalMemory / 1024 / 1024} MB\n")
        logContent.append("Used Memory: ${usedMemory / 1024 / 1024} MB\n")
        logContent.append("Free Memory: ${freeMemory / 1024 / 1024} MB\n")
        logContent.append("Memory Usage: ${"%.1f".format(usedMemory.toDouble() / totalMemory * 100)}%\n")
        
        // Recent logs note
        logContent.append("\n=== LOG NOTES ===\n")
        logContent.append("This is an automated debug build log upload.\n")
        logContent.append("Recent application logs are available in Android logcat with tags:\n")
        logContent.append("- LumiyaApp: Application lifecycle and startup\n")
        logContent.append("- ModernLinkpointDemo: Modern component initialization\n")
        logContent.append("- CleanLoginActivity: Login screen activity\n")
        logContent.append("- ModernMainActivity: Main demo activity\n")
        logContent.append("- AutoLogUploader: This log upload system\n")
        logContent.append("\nTo view recent logs: adb logcat LumiyaApp:* ModernLinkpointDemo:* AutoLogUploader:* *:S\n")
        
        // Upload information
        logContent.append("\n=== UPLOAD INFORMATION ===\n")
        logContent.append("Upload Method: GitHub Gist (Private)\n")
        logContent.append("Upload Frequency: Every 1 hour (for debug builds only)\n")
        logContent.append("Log Size Limit: 50KB\n")
        logContent.append("Auto Upload Enabled: ${isAutoUploadEnabled()}\n")
        
        val lastUploadDate = Date(lastUploadTime)
        logContent.append("Last Upload: $lastUploadDate\n")
        
        logContent.append("\n=== END OF LOG ===\n")
        
        // Truncate if too large
        var result = logContent.toString()
        if (result.length > MAX_LOG_SIZE) {
            result = result.substring(0, MAX_LOG_SIZE - 100) + "\n\n[LOG TRUNCATED - TOO LARGE]\n"
        }
        
        return result
    }
    
    /**
     * Format crash report
     */
    private fun formatCrashReport(crash: Throwable, additionalInfo: String): String {
        val crashContent = StringBuilder()
        
        crashContent.append("=== LINKPOINT CRASH REPORT ===\n")
        crashContent.append("Timestamp: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US).format(Date())}\n")
        crashContent.append("Version: ${LumiyaApp.getAppVersion()}\n")
        crashContent.append("Device: ${Build.MANUFACTURER} ${Build.MODEL}\n")
        crashContent.append("Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})\n")
        
        crashContent.append("\n=== ADDITIONAL INFORMATION ===\n")
        crashContent.append("$additionalInfo\n")
        
        crashContent.append("\n=== CRASH DETAILS ===\n")
        crashContent.append("Exception: ${crash.javaClass.name}\n")
        crashContent.append("Message: ${crash.message}\n")
        
        crashContent.append("\n=== STACK TRACE ===\n")
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        crash.printStackTrace(pw)
        crashContent.append(sw.toString())
        
        crashContent.append("\n=== END CRASH REPORT ===\n")
        
        return crashContent.toString()
    }
    
    /**
     * Upload log content to GitHub as a Gist
     */
    private fun uploadLogContent(content: String, reason: String, type: String) {
        try {
            // Create filename with timestamp
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val deviceId = getDeviceIdentifier()
            val filename = "linkpoint_${type}_${timestamp}_$deviceId.log"
            
            // Create GitHub Gist
            val gistJson = JSONObject().apply {
                put("description", "Linkpoint $type - $reason (Device: $deviceId)")
                put("public", false) // Private gist
                
                val files = JSONObject()
                val fileContent = JSONObject().apply {
                    put("content", content)
                }
                files.put(filename, fileContent)
                put("files", files)
            }
            
            val body = gistJson.toString().toRequestBody("application/json".toMediaType())
            
            val request = Request.Builder()
                .url("https://api.github.com/gists")
                .post(body)
                .addHeader("Accept", "application/vnd.github.v3+json")
                .addHeader("User-Agent", "Linkpoint-Debug-App")
                .build()
            
            Log.i(TAG, "🌐 Uploading to GitHub Gist...")
            httpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val responseBody = response.body?.string() ?: ""
                    val responseJson = JSONObject(responseBody)
                    val gistUrl = responseJson.getString("html_url")
                    val gistId = responseJson.getString("id")
                    
                    Log.i(TAG, "✅ Log uploaded successfully!")
                    Log.i(TAG, "📋 Gist ID: $gistId")
                    Log.i(TAG, "🔗 URL: $gistUrl")
                    
                    // Also log the URL in a way that can be easily found
                    Log.i("LINKPOINT_DEBUG_UPLOAD", "LOG_UPLOADED: $gistUrl")
                    Log.i("LINKPOINT_DEBUG_UPLOAD", "GIST_ID: $gistId")
                } else {
                    val errorBody = response.body?.string() ?: "No response body"
                    Log.e(TAG, "❌ Failed to upload log: HTTP ${response.code}")
                    Log.e(TAG, "Response: $errorBody")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "💥 Error uploading log content", e)
        }
    }
    
    /**
     * Get a device identifier for log files
     */
    private fun getDeviceIdentifier(): String {
        return try {
            // Use a combination of model and a part of Android ID for identification
            val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            val model = Build.MODEL.replace(Regex("[^a-zA-Z0-9]"), "")
            val shortId = androidId?.substring(0, minOf(6, androidId.length)) ?: "unknown"
            "${model}_$shortId"
        } catch (e: Exception) {
            "device_${System.currentTimeMillis() % 10000}"
        }
    }
    
    /**
     * Check if auto upload is enabled
     */
    fun isAutoUploadEnabled(): Boolean = BuildConfig.DEBUG
    
    /**
     * Get last upload time
     */
    val lastUploadTime: Long
        get() = prefs.getLong(PREF_LAST_UPLOAD, 0)
}
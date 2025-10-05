package com.linkpoint.debug

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.provider.Settings

import com.linkpoint.BuildConfig
import com.linkpoint.LinkpointApp

import okhttp3.*
import org.json.JSONObject

import java.io.StringWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Automatic log uploader for debug builds
 * Uploads application logs and crash reports to GitHub for review by copilot
 */
class AutoLogUploader {
    private const val TAG: String = "AutoLogUploader"
    
    // Configuration
    private const val PREF_LAST_UPLOAD: String = "last_log_upload"
    private const val UPLOAD_INTERVAL_MS: Long = TimeUnit.HOURS.toMillis(1); // Upload every hour
    private const val MAX_LOG_SIZE: Int = 50000; // 50KB limit for GitHub API
    
    private val Context context
    private val OkHttpClient httpClient
    private val SharedPreferences prefs
    @JvmStatic
private AutoLogUploader instance
    
    private AutoLogUploader(Context context) {
        this.context = context.getApplicationContext()
        this.prefs = context.getSharedPreferences("debug_upload", Context.MODE_PRIVATE)
        
        this.httpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    /**
     * Get singleton instance
     */
    @JvmStatic
    synchronized AutoLogUploader getInstance(Context context) {
        if (instance == null) {
            instance = AutoLogUploader(context)
        }
        return instance
    }
    
    /**
     * Initialize automatic log uploading (only for debug builds)
     */
    public Unit initializeAutoUpload() {
        // Only enable for debug builds
        if (!BuildConfig.DEBUG) {
            Log.d(TAG, "Auto log upload disabled for release builds")
            return
        }
        
        Log.i(TAG, "🚀 Initializing automatic log upload for debug build")
        
        // Upload immediately if it's been a while
        Long lastUpload = prefs.getLong(PREF_LAST_UPLOAD, 0)
        Long now = System.currentTimeMillis()
        
        if (now - lastUpload > UPLOAD_INTERVAL_MS) {
            uploadLogsAsync("Automatic startup log upload")
        } else {
            Long timeUntilNext = UPLOAD_INTERVAL_MS - (now - lastUpload)
            Log.i(TAG, "Next automatic upload in " + (timeUntilNext / 1000 / 60) + " minutes")
        }
    }
    
    /**
     * Upload logs immediately with a specific reason
     */
    public Unit uploadLogsNow(String reason) {
        if (!BuildConfig.DEBUG) {
            Log.d(TAG, "Log upload disabled for release builds")
            return
        }
        
        Log.i(TAG, "📤 Manual log upload requested: " + reason)
        uploadLogsAsync(reason)
    }
    
    /**
     * Upload crash report immediately
     */
    public Unit uploadCrashReport(Throwable crash, String additionalInfo) {
        if (!BuildConfig.DEBUG) {
            return
        }
        
        Log.i(TAG, "💥 Uploading crash report to GitHub")
        
        String crashLog = formatCrashReport(crash, additionalInfo)
        uploadLogContent(crashLog, "Crash Report", "crash")
    }
    
    /**
     * Upload logs asynchronously
     */
    private Unit uploadLogsAsync(String reason) {
        Thread(() -> {
            try {
                Log.i(TAG, "📊 Collecting application logs for upload...")
                String logContent = collectApplicationLogs()
                uploadLogContent(logContent, reason, "application")
                
                // Update last upload time
                prefs.edit().putLong(PREF_LAST_UPLOAD, System.currentTimeMillis()).apply()
                
            } catch (Exception e) {
                Log.e(TAG, "❌ Failed to upload logs", e)
            }
        }).start()
    }
    
    /**
     * Collect application logs and system information
     */
    private String collectApplicationLogs() {
        StringBuilder logContent = StringBuilder()
        
        // Header information
        logContent.append("=== LINKPOINT DEBUG LOG UPLOAD ===\n")
        logContent.append("Timestamp: ").append(SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US).format(Date())).append("\n")
        logContent.append("Version: ").append(LinkpointApp.getAppVersion()).append("\n")
        logContent.append("Build Type: DEBUG\n")
        logContent.append("Repository: https://github.com/Kaleaon/Linkpoint\n")
        logContent.append("\n=== DEVICE INFORMATION ===\n")
        logContent.append("Manufacturer: ").append(Build.MANUFACTURER).append("\n")
        logContent.append("Model: ").append(Build.MODEL).append("\n")
        logContent.append("Android Version: ").append(Build.VERSION.RELEASE).append(" (API ").append(Build.VERSION.SDK_INT).append(")\n")
        logContent.append("Build ID: ").append(Build.ID).append("\n")
        logContent.append("Device ID: ").append(getDeviceIdentifier()).append("\n")
        
        // Application status
        logContent.append("\n=== APPLICATION STATUS ===\n")
        try {
            String appStatus = LinkpointApp.getStartupStatus()
            logContent.append(appStatus).append("\n")
        } catch (Exception e) {
            logContent.append("Could not get app status: ").append(e.getMessage()).append("\n")
        }
        
        // Memory information
        logContent.append("\n=== MEMORY INFORMATION ===\n")
        Runtime runtime = Runtime.getRuntime()
        Long maxMemory = runtime.maxMemory()
        Long totalMemory = runtime.totalMemory()
        Long freeMemory = runtime.freeMemory()
        Long usedMemory = totalMemory - freeMemory
        
        logContent.append("Max Memory: ").append(maxMemory / 1024 / 1024).append(" MB\n")
        logContent.append("Total Memory: ").append(totalMemory / 1024 / 1024).append(" MB\n")
        logContent.append("Used Memory: ").append(usedMemory / 1024 / 1024).append(" MB\n")
        logContent.append("Free Memory: ").append(freeMemory / 1024 / 1024).append(" MB\n")
        logContent.append("Memory Usage: ").append(String.format("%.1f%%", (Double)usedMemory / totalMemory * 100)).append("\n")
        
        // Recent logs note
        logContent.append("\n=== LOG NOTES ===\n")
        logContent.append("This is an automated debug build log upload.\n")
        logContent.append("Recent application logs are available in Android logcat with tags:\n")
        logContent.append("- LinkpointApp: Application lifecycle and startup\n")
        logContent.append("- ModernLinkpointDemo: Modern component initialization\n")
        logContent.append("- CleanLoginActivity: Login screen activity\n")
        logContent.append("- ModernMainActivity: Main demo activity\n")
        logContent.append("- AutoLogUploader: This log upload system\n")
        logContent.append("\nTo view recent logs: adb logcat LinkpointApp:* ModernLinkpointDemo:* AutoLogUploader:* *:S\n")
        
        // Upload information
        logContent.append("\n=== UPLOAD INFORMATION ===\n")
        logContent.append("Upload Method: GitHub Gist (Private)\n")
        logContent.append("Upload Frequency: Every 1 hour (for debug builds only)\n")
        logContent.append("Log Size Limit: 50KB\n")
        logContent.append("Auto Upload Enabled: ").append(isAutoUploadEnabled()).append("\n")
        
        Date lastUploadDate = Date(getLastUploadTime())
        logContent.append("Last Upload: ").append(lastUploadDate.toString()).append("\n")
        
        logContent.append("\n=== END OF LOG ===\n")
        
        // Truncate if too large
        String result = logContent.toString()
        if (result.length() > MAX_LOG_SIZE) {
            result = result.substring(0, MAX_LOG_SIZE - 100) + "\n\n[LOG TRUNCATED - TOO LARGE]\n"
        }
        
        return result
    }
    
    /**
     * Format crash report
     */
    private String formatCrashReport(Throwable crash, String additionalInfo) {
        StringBuilder crashContent = StringBuilder()
        
        crashContent.append("=== LINKPOINT CRASH REPORT ===\n")
        crashContent.append("Timestamp: ").append(SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US).format(Date())).append("\n")
        crashContent.append("Version: ").append(LinkpointApp.getAppVersion()).append("\n")
        crashContent.append("Device: ").append(Build.MANUFACTURER).append(" ").append(Build.MODEL).append("\n")
        crashContent.append("Android: ").append(Build.VERSION.RELEASE).append(" (API ").append(Build.VERSION.SDK_INT).append(")\n")
        crashContent.append("Repository: https://github.com/Kaleaon/Linkpoint\n")
        
        if (additionalInfo != null && !additionalInfo.isEmpty()) {
            crashContent.append("\n=== ADDITIONAL INFO ===\n")
            crashContent.append(additionalInfo).append("\n")
        }
        
        crashContent.append("\n=== STACK TRACE ===\n")
        StringWriter sw = StringWriter()
        PrintWriter pw = PrintWriter(sw)
        crash.printStackTrace(pw)
        crashContent.append(sw.toString())
        
        crashContent.append("\n=== END CRASH REPORT ===\n")
        
        return crashContent.toString()
    }
    
    /**
     * Upload log content to GitHub as a Gist
     */
    private Unit uploadLogContent(String content, String reason, String type) {
        try {
            // Create filename with timestamp
            String timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            String deviceId = getDeviceIdentifier()
            String filename = String.format("linkpoint_%s_%s_%s.log", type, timestamp, deviceId)
            
            // Create GitHub Gist
            JSONObject gistJson = JSONObject()
            gistJson.put("description", String.format("Linkpoint %s - %s (Device: %s)", type, reason, deviceId))
            gistJson.put("public", false); // Private gist
            
            JSONObject files = JSONObject()
            JSONObject fileContent = JSONObject()
            fileContent.put("content", content)
            files.put(filename, fileContent)
            gistJson.put("files", files)
            
            RequestBody body = RequestBody.create(
                gistJson.toString(),
                MediaType.parse("application/json")
            )
            
            Request request = Request.Builder()
                .url("https://api.github.com/gists")
                .post(body)
                .addHeader("Accept", "application/vnd.github.v3+json")
                .addHeader("User-Agent", "Linkpoint-Debug-App")
                .build()
            
            Log.i(TAG, "🌐 Uploading to GitHub Gist...")
            Response response = httpClient.newCall(request).execute()
            
            if (response.isSuccessful()) {
                String responseBody = response.body().string()
                JSONObject responseJson = JSONObject(responseBody)
                String gistUrl = responseJson.getString("html_url")
                String gistId = responseJson.getString("id")
                
                Log.i(TAG, String.format("✅ Log uploaded successfully!"))
                Log.i(TAG, String.format("📋 Gist ID: %s", gistId))
                Log.i(TAG, String.format("🔗 URL: %s", gistUrl))
                
                // Also log the URL in a way that can be easily found
                Log.i("LINKPOINT_DEBUG_UPLOAD", String.format("LOG_UPLOADED: %s", gistUrl))
                Log.i("LINKPOINT_DEBUG_UPLOAD", String.format("GIST_ID: %s", gistId))
                
            } else {
                String errorBody = response.body() != null ? response.body().string() : "No response body"
                Log.e(TAG, String.format("❌ Failed to upload log: HTTP %d", response.code()))
                Log.e(TAG, "Response: " + errorBody)
            }
            
            response.close()
            
        } catch (Exception e) {
            Log.e(TAG, "💥 Error uploading log content", e)
        }
    }
    
    /**
     * Get a device identifier for log files
     */
    private String getDeviceIdentifier() {
        try {
            // Use a combination of model and a part of Android ID for identification
            String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID)
            String model = Build.MODEL.replaceAll("[^a-zA-Z0-9]", "")
            String shortId = androidId != null ? androidId.substring(0, Math.min(6, androidId.length())) : "unknown"
            return model + "_" + shortId
        } catch (Exception e) {
            return "device_" + System.currentTimeMillis() % 10000
        }
    }
    
    /**
     * Check if auto upload is enabled
     */
    public Boolean isAutoUploadEnabled() {
        return BuildConfig.DEBUG
    }
    
    /**
     * Get last upload time
     */
    public Long getLastUploadTime() {
        return prefs.getLong(PREF_LAST_UPLOAD, 0)
    }
}
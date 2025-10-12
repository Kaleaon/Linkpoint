package com.lumiyaviewer.lumiya

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.WindowManager
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import androidx.preference.PreferenceManager
import com.lumiyaviewer.lumiya.debug.AutoLogUploader
import com.lumiyaviewer.lumiya.fixes.ResourceConflictResolver
import com.lumiyaviewer.lumiya.modern.samples.ModernLinkpointDemo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.sqrt

/**
 * Modern Lumiya Application Class
 * Converted to Kotlin with modern Android practices
 * 
 * Features:
 * - Kotlin Coroutines for async operations
 * - Lazy initialization
 * - Null safety
 * - Extension functions
 * - Modern Android architecture
 */
class ModernLumiyaApp : MultiDexApplication() {
    
    companion object {
        private const val TAG = "ModernLumiyaApp"
        
        @Volatile
        private var instance: ModernLumiyaApp? = null
        
        private val displayMetrics = DisplayMetrics()
        
        /**
         * Get application instance
         */
        fun getInstance(): ModernLumiyaApp? = instance
        
        /**
         * Get application context
         */
        fun getContext(): Context? = instance
        
        /**
         * Get asset manager
         */
        fun getAssetManager(): AssetManager? = instance?.assets
        
        /**
         * Get default shared preferences
         */
        fun getDefaultSharedPreferences(): SharedPreferences? {
            return instance?.let { PreferenceManager.getDefaultSharedPreferences(it) }
        }
        
        /**
         * Get app version
         */
        fun getAppVersion(): String {
            return try {
                instance?.let { app ->
                    app.packageManager.getPackageInfo(app.packageName, 0).versionName
                } ?: ""
            } catch (e: Exception) {
                Log.w(TAG, "Could not get app version", e)
                ""
            }
        }
        
        /**
         * Check if split screen is needed based on device configuration
         */
        fun isSplitScreenNeeded(context: Context): Boolean {
            val prefs = getDefaultSharedPreferences() ?: return false
            val setting = prefs.getString("split_screens", "auto") ?: "auto"
            
            return when (setting) {
                "never" -> false
                "always" -> true
                "landscape" -> {
                    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                    val display = windowManager.defaultDisplay
                    display.width > display.height
                }
                else -> { // "auto"
                    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                    val display = windowManager.defaultDisplay
                    display.getMetrics(displayMetrics)
                    
                    val heightInches = displayMetrics.heightPixels / displayMetrics.ydpi
                    val widthInches = displayMetrics.widthPixels / displayMetrics.xdpi
                    val diagonalInches = sqrt((heightInches * heightInches + widthInches * widthInches).toDouble())
                    
                    if (diagonalInches <= 6.5 || widthInches < 5.0f) {
                        false
                    } else {
                        val widthDp = display.width / displayMetrics.density
                        Log.i(TAG, "Display width in dp: %.2f, xInches %.1f, diag %.1f".format(
                            widthDp, widthInches, diagonalInches
                        ))
                        widthDp >= 1000.0f
                    }
                }
            }
        }
        
        /**
         * Restart the application
         */
        fun restartApp() {
            // TODO: Implement app restart logic
            Log.i(TAG, "App restart requested")
        }
        
        /**
         * Upload debug logs immediately (debug builds only)
         */
        fun uploadDebugLogsNow(reason: String) {
            instance?.let { app ->
                try {
                    AutoLogUploader.getInstance(app).uploadLogsNow(reason)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to trigger log upload", e)
                }
            }
        }
        
        /**
         * Report a crash for automatic upload (debug builds only)
         */
        fun reportCrash(crash: Throwable, additionalInfo: String) {
            instance?.let { app ->
                try {
                    AutoLogUploader.getInstance(app).uploadCrashReport(crash, additionalInfo)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to upload crash report", e)
                }
            }
        }
        
        /**
         * Get application startup status
         */
        fun getStartupStatus(): String {
            val app = instance ?: return "Application context not initialized"
            
            return buildString {
                appendLine("Lumiya Application Status:")
                appendLine("- Context: ${if (app != null) "OK" else "NULL"}")
                appendLine("- Modern Components: ${if (app.modernDemo != null) "Active" else "Safe Mode"}")
                
                app.modernDemo?.let { demo ->
                    try {
                        appendLine("- Graphics: ${demo.graphicsInfo}")
                        appendLine("- Connection: ${if (demo.isConnected) "Connected" else "Disconnected"}")
                    } catch (e: Exception) {
                        appendLine("- Component Status: Error checking - ${e.message}")
                    }
                } ?: appendLine("- Running in Safe Mode - basic functionality only")
            }
        }
    }
    
    // Application scope for coroutines
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    // Modern components (lazy initialization)
    private var modernDemo: ModernLinkpointDemo? = null
    
    override fun onCreate() {
        super.onCreate()
        
        Log.i(TAG, "Modern Lumiya Application starting up")
        
        try {
            // Set global instance
            instance = this
            Log.i(TAG, "Global context set successfully")
            
            // Initialize resource conflict resolver
            initializeResourceConflictResolver()
            
            // Initialize modern systems asynchronously
            applicationScope.launch {
                initializeModernSystems()
            }
            
            // Initialize debug log upload for debug builds
            initializeDebugLogUpload()
            
            Log.i(TAG, "Modern Lumiya Application initialization complete")
        } catch (e: Throwable) {
            Log.e(TAG, "CRITICAL: Application initialization failed", e)
            handleInitializationFailure(e)
        }
    }
    
    /**
     * Initialize resource conflict resolver
     */
    private fun initializeResourceConflictResolver() {
        try {
            ResourceConflictResolver.initialize(this)
            Log.i(TAG, "Resource conflict resolver initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Resource conflict resolver failed - continuing anyway", e)
        }
    }
    
    /**
     * Initialize modern Linkpoint systems
     */
    private suspend fun initializeModernSystems() {
        Log.i(TAG, "Initializing modern Linkpoint components...")
        
        try {
            // Pre-flight checks
            if (!performSystemChecks()) {
                Log.w(TAG, "System compatibility checks failed - skipping modern components")
                modernDemo = null
                return
            }
            
            // Initialize modern components
            modernDemo = ModernLinkpointDemo(this@ModernLumiyaApp)
            Log.i(TAG, "Modern Linkpoint systems initialized successfully")
            
        } catch (e: NoClassDefFoundError) {
            Log.e(TAG, "Modern system class not found - likely missing dependency", e)
            Log.e(TAG, "Missing class: ${e.message}")
            modernDemo = null
            
        } catch (e: UnsatisfiedLinkError) {
            Log.e(TAG, "Native library loading failed - modern graphics features will be disabled", e)
            modernDemo = null
            
        } catch (e: SecurityException) {
            Log.e(TAG, "Security error during modern system initialization", e)
            modernDemo = null
            
        } catch (e: OutOfMemoryError) {
            Log.e(TAG, "Out of memory during modern system initialization", e)
            modernDemo = null
            System.gc()
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize modern systems - continuing with graceful degradation", e)
            modernDemo = null
            
        } catch (t: Throwable) {
            Log.e(TAG, "Unexpected error during modern system initialization", t)
            modernDemo = null
        }
        
        // Log final status
        if (modernDemo != null) {
            Log.i(TAG, "Modern components active - full feature set available")
        } else {
            Log.w(TAG, "Modern components disabled - using legacy compatibility mode")
            Log.w(TAG, "App will function with basic Second Life viewer features only")
        }
    }
    
    /**
     * Perform system compatibility checks
     */
    private fun performSystemChecks(): Boolean {
        try {
            // Check API level
            val apiLevel = android.os.Build.VERSION.SDK_INT
            if (apiLevel < 24) { // API 24 = Android 7.0 minimum
                Log.w(TAG, "Android API level $apiLevel below minimum for modern components (24)")
                return false
            }
            
            // Check available memory
            val runtime = Runtime.getRuntime()
            val freeMemory = runtime.freeMemory()
            val totalMemory = runtime.totalMemory()
            val memoryUsage = (totalMemory - freeMemory).toDouble() / totalMemory
            
            if (memoryUsage > 0.8) { // If using more than 80% of heap
                Log.w(TAG, "Memory usage too high (${memoryUsage * 100}%) - skipping modern components")
                return false
            }
            
            Log.i(TAG, "System checks passed - proceeding with modern component initialization")
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "Error during system checks", e)
            return false
        }
    }
    
    /**
     * Initialize debug log upload system
     */
    private fun initializeDebugLogUpload() {
        try {
            val logUploader = AutoLogUploader.getInstance(this)
            logUploader.initializeAutoUpload()
            Log.i(TAG, "Debug log upload system initialized")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize debug log upload", e)
        }
    }
    
    /**
     * Handle initialization failure
     */
    private fun handleInitializationFailure(error: Throwable) {
        // Upload crash report for debug builds
        try {
            AutoLogUploader.getInstance(this).uploadCrashReport(error, "Application initialization failure")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to upload crash report", e)
        }
        
        // Log specific error types
        when (error) {
            is OutOfMemoryError -> Log.e(TAG, "Out of memory during initialization")
            is NoClassDefFoundError -> Log.e(TAG, "Missing class definition: ${error.message}")
            is UnsatisfiedLinkError -> Log.e(TAG, "Native library linking failed: ${error.message}")
        }
        
        // Set safe fallback state
        modernDemo = null
        Log.w(TAG, "Application will continue in SAFE MODE with basic functionality only")
    }
    
    /**
     * Get modern components demo instance
     */
    fun getModernDemo(): ModernLinkpointDemo? = modernDemo
    
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
package com.lumiyaviewer.lumiya

import android.app.AlarmManager
import android.app.Application
import android.app.LauncherActivity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager.NameNotFoundException
import android.content.res.AssetManager
import android.preference.PreferenceManager
import androidx.core.app.NotificationCompat
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.WindowManager
import com.lumiyaviewer.lumiya.fixes.ResourceConflictResolver
import com.lumiyaviewer.lumiya.modern.samples.ModernLinkpointDemo
import com.lumiyaviewer.lumiya.debug.AutoLogUploader

/**
 * Main Application class for Lumiya Viewer.
 * 
 * Handles global application state, resource conflict resolution, and system-wide initialization.
 * Updated to use AndroidX libraries and modern Android development practices.
 * Extends MultiDexApplication to support large applications with 64K+ methods.
 */
class LumiyaApp : MultiDexApplication() {
    
    companion object {
        private const val TAG = "LumiyaApp"
        private val displayMetrics = DisplayMetrics()
        private lateinit var mContext: Context
        private val prefs: SharedPreferences by lazy { 
            PreferenceManager.getDefaultSharedPreferences(mContext) 
        }
        
        // Modern components
        private var modernDemo: ModernLinkpointDemo? = null
        
        fun getContext(): Context = mContext
        
        fun getDefaultSharedPreferences(): SharedPreferences = prefs
        
        fun getAssetManager(): AssetManager = mContext.assets

        fun getAppVersion(): String {
            return try {
                mContext.packageManager.getPackageInfo(mContext.packageName, 0)?.versionName ?: ""
            } catch (e: NameNotFoundException) {
                Log.w(TAG, "Could not get app version", e)
                ""
            }
        }
    
        /**
         * Get application startup status and any initialization errors
         */
        fun getStartupStatus(): String {
            if (!::mContext.isInitialized) {
                return "Application context not initialized"
            }
            
            return buildString {
                append("Lumiya Application Status:\n")
                append("- Context: OK\n")
                append("- Modern Components: ${if (modernDemo != null) "Active" else "Safe Mode"}\n")
                
                modernDemo?.let { demo ->
                    try {
                        append("- Graphics: ${demo.getGraphicsInfo()}\n")
                        append("- Connection: ${if (demo.isConnected()) "Connected" else "Disconnected"}\n")
                    } catch (e: Exception) {
                        append("- Component Status: Error checking - ${e.message}\n")
                    }
                } ?: run {
                    append("- Running in Safe Mode - basic functionality only\n")
                }
            }
        }
    
        /**
         * Upload debug logs immediately (for debug builds only)
         */
        fun uploadDebugLogsNow(reason: String) {
            if (::mContext.isInitialized) {
                try {
                    AutoLogUploader.getInstance(mContext).uploadLogsNow(reason)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to trigger log upload", e)
                }
            }
        }
        
        /**
         * Report a crash for automatic upload (debug builds only)
         */
        fun reportCrash(crash: Throwable, additionalInfo: String) {
            if (::mContext.isInitialized) {
                try {
                    AutoLogUploader.getInstance(mContext).uploadCrashReport(crash, additionalInfo)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to upload crash report", e)
                }
            }
        }

        @Suppress("DEPRECATION")
        fun isSplitScreenNeeded(context: Context): Boolean {
            val preference = getDefaultSharedPreferences().getString("split_screens", "auto") ?: "auto"
            return when (preference) {
                "never" -> false
                "always" -> true
                else -> {
                    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                    val defaultDisplay = windowManager.defaultDisplay
                    
                    if (preference == "landscape") {
                        defaultDisplay.width > defaultDisplay.height
                    } else {
                        defaultDisplay.getMetrics(displayMetrics)
                        val heightInches = displayMetrics.heightPixels.toFloat() / displayMetrics.ydpi
                        val widthInches = displayMetrics.widthPixels.toFloat() / displayMetrics.xdpi
                        val diagonalInches = kotlin.math.sqrt((heightInches * heightInches + widthInches * widthInches).toDouble())
                        
                        if (diagonalInches <= 6.5 || widthInches < 5.0f) {
                            return false
                        }
                        
                        val widthDp = defaultDisplay.width.toFloat() / displayMetrics.density
                        Log.i(TAG, "Display: width ${widthDp}dp, ${widthInches}in, diagonal ${diagonalInches}in")
                        widthDp >= 1000.0f
                    }
                }
            }
        }

        fun restartApp() {
            try {
                val alarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val pendingIntent = PendingIntent.getActivity(
                    mContext, 
                    0, 
                    Intent(mContext, LauncherActivity::class.java), 
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, pendingIntent)
                System.exit(0)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to restart app", e)
            }
        }
        
        /**
         * Get modern components demo instance
         */
        fun getModernDemo(): ModernLinkpointDemo? = modernDemo
    }

    override fun onCreate() {
        super.onCreate()
        
        Log.i(TAG, "Lumiya Application starting up")
        
        try {
            // Set global context first - this is critical for all other operations
            mContext = this
            Log.i(TAG, "Global context set successfully")
            
            // CRITICAL: Initialize resource conflict resolver before any other initialization
            // This fixes build-breaking resource conflicts between AndroidX and support libraries
            try {
                ResourceConflictResolver.initialize(this)
                Log.i(TAG, "Resource conflict resolver initialized successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Resource conflict resolver failed - continuing anyway", e)
            }
            
            // Initialize global options after resource conflicts are resolved
            // GlobalOptions.getInstance().initialize();  // Temporarily disabled due to dependencies
            
            // Initialize modern Linkpoint components with comprehensive exception handling
            initializeModernSystems()
            
            // Initialize automatic log upload for debug builds
            initializeDebugLogUpload()
            
            Log.i(TAG, "Lumiya Application initialization complete")
        } catch (e: Throwable) {
            // Catch all throwables including OutOfMemoryError, LinkageError, etc.
            Log.e(TAG, "CRITICAL: Application initialization failed", e)
            
            // Upload crash report for debug builds
            try {
                AutoLogUploader.getInstance(this).uploadCrashReport(e, "Application initialization failure")
            } catch (uploadError: Exception) {
                Log.e(TAG, "Failed to upload crash report", uploadError)
            }
            
            // Log specific error types for debugging
            when (e) {
                is OutOfMemoryError -> Log.e(TAG, "Out of memory during initialization")
                is NoClassDefFoundError -> Log.e(TAG, "Missing object definition: ${e.message}")
                is UnsatisfiedLinkError -> Log.e(TAG, "Native library linking failed: ${e.message}")
            }
            
            // Set a safe fallback state
            modernDemo = null
            Log.w(TAG, "Application will continue in SAFE MODE with basic functionality only")
            
            // Don't re-throw - allow the app to continue running
        }
    }
    
    /**
     * Initialize modern Second Life protocol and rendering systems
     * Uses defensive programming to prevent crashes if modern components fail
     */
    private fun initializeModernSystems() {
        Log.i(TAG, "Initializing modern Linkpoint components...")
        
        try {
            // Pre-flight checks before initializing modern components
            if (!performSystemChecks()) {
                Log.w(TAG, "System compatibility checks failed - skipping modern components")
                modernDemo = null
                return
            }
            
            // Initialize modern components with individual exception handling
            modernDemo = ModernLinkpointDemo(this)
            Log.i(TAG, "Modern Linkpoint systems initialized successfully")
            
        } catch (e: NoClassDefFoundError) {
            Log.e(TAG, "Modern system object not found - likely missing dependency", e)
            Log.e(TAG, "Missing class: ${e.message}")
            modernDemo = null
            
        } catch (e: UnsatisfiedLinkError) {
            Log.e(TAG, "Native library loading failed - modern graphics features will be disabled", e)
            Log.e(TAG, "Library loading error: ${e.message}")
            modernDemo = null
            
        } catch (e: SecurityException) {
            Log.e(TAG, "Security error during modern system initialization", e)
            modernDemo = null
            
        } catch (e: OutOfMemoryError) {
            Log.e(TAG, "Out of memory during modern system initialization", e)
            modernDemo = null
            // Force garbage collection
            System.gc()
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize modern systems - continuing with graceful degradation", e)
            modernDemo = null
            
            // Log specific error for debugging
            Log.e(TAG, "Modern component initialization failed: ${e.javaClass.simpleName} - ${e.message}")
            
        } catch (t: Throwable) {
            // Catch any other throwables that might occur
            Log.e(TAG, "Unexpected error during modern system initialization", t)
            modernDemo = null
        }
        
        // Log status
        if (modernDemo != null) {
            Log.i(TAG, "Modern components active - full feature set available")
        } else {
            Log.w(TAG, "Modern components disabled - using legacy compatibility mode")
            Log.w(TAG, "App will function with basic Second Life viewer features only")
        }
    }
    
    /**
     * Perform system compatibility checks before initializing modern components
     */
    private fun performSystemChecks(): Boolean {
        try {
            // Check if we have a valid context
            if (!::mContext.isInitialized) {
                Log.e(TAG, "Context not initialized - cannot initialize modern components")
                return false
            }
            
            // Check if we have basic Android API requirements
            val apiLevel = android.os.Build.VERSION.SDK_INT
            if (apiLevel < 21) { // API 21 = Android 5.0 minimum
                Log.w(TAG, "Android API level $apiLevel below minimum for modern components (21)")
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
     * Initialize debug log upload system for debug builds only
     */
    private fun initializeDebugLogUpload() {
        try {
            val logUploader = AutoLogUploader.getInstance(this)
            logUploader.initializeAutoUpload()
            Log.i(TAG, "Debug log upload system initialized")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize debug log upload", e)
            // Don't crash the app if log upload fails
        }
    }
    
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}

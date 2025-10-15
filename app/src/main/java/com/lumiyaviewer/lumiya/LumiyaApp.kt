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
 * Main Application object for Lumiya Viewer.
 * 
 * Handles global application state, resource conflict resolution, and system-wide initialization.
 * Updated to use AndroidX libraries and modern Android development practices.
 * Extends MultiDexApplication to support large applications with 64K+ methods.
 */
object LumiyaApp extends MultiDexApplication {
    private String TAG = "LumiyaApp"
    private DisplayMetrics displayMetrics = new DisplayMetrics()
    private Context mContext
    private SharedPreferences prefs
    
    // Modern components
    private ModernLinkpointDemo modernDemo

    fun getAppVersion(): String {
        try {
            return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName
        } catch (NameNotFoundException e) {
            Log.w(TAG, "Could not get app version", e)
            return ""
        }
    }
    
    /**
     * Get application startup status and any initialization errors
     */
    fun getStartupStatus(): String {
        if (mContext == null) {
            return "Application context not initialized"
        }
        
        StringBuilder status = new StringBuilder()
        status.append("Lumiya Application Status:\n")
        status.append("- Context: ").append(mContext != null ? "OK" : "NULL").append("\n")
        status.append("- Modern Components: ").append(modernDemo != null ? "Active" : "Safe Mode").append("\n")
        
        if (modernDemo != null) {
            try {
                status.append("- Graphics: ").append(modernDemo.getGraphicsInfo()).append("\n")
                status.append("- Connection: ").append(modernDemo.isConnected() ? "Connected" : "Disconnected").append("\n")
            } catch (Exception e) {
                status.append("- Component Status: Error checking - ").append(e.getMessage()).append("\n")
            }
        } else {
            status.append("- Running in Safe Mode - basic functionality only\n")
        }
        
        return status.toString()
    }
    
    /**
     * Upload debug logs immediately (for debug builds only)
     */
    fun uploadDebugLogsNow(reason: String): Unit {
        if (mContext != null) {
            try {
                AutoLogUploader.getInstance(mContext).uploadLogsNow(reason)
            } catch (Exception e) {
                Log.e(TAG, "Failed to trigger log upload", e)
            }
        }
    }
    
    /**
     * Report a crash for automatic upload (debug builds only)
     */
    fun reportCrash(crash: Throwable, additionalInfo: String): Unit {
        if (mContext != null) {
            try {
                AutoLogUploader.getInstance(mContext).uploadCrashReport(crash, additionalInfo)
            } catch (Exception e) {
                Log.e(TAG, "Failed to upload crash report", e)
            }
        }
    }

    fun getAssetManager(): AssetManager {
        return mContext != null ? mContext.getAssets() : null
    }

    fun getContext(): Context {
        return mContext
    }

    fun getDefaultSharedPreferences(): SharedPreferences {
        if (prefs == null) {
            prefs = PreferenceManager.getDefaultSharedPreferences(getContext())
        }
        return prefs
    }

    fun isSplitScreenNeeded(context: Context): Boolean {
        String string = getDefaultSharedPreferences().getString("split_screens", "auto")
        if (string.equals("never")) {
            return false
        }
        if (string.equals("always")) {
            return true
        }
        Display defaultDisplay = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
        if (string.equals("landscape")) {
            return defaultDisplay.getWidth() > defaultDisplay.getHeight()
        } else {
            defaultDisplay.getMetrics(displayMetrics)
            float f = ((float) displayMetrics.heightPixels) / displayMetrics.ydpi
            float f2 = ((float) displayMetrics.widthPixels) / displayMetrics.xdpi
            double diagonalInches = Math.sqrt((double) ((f * f) + (f2 * f2)))
            if (diagonalInches <= 6.5d || f2 < 5.0f) {
                return false
            }
            Log.i(TAG, String.format("LumiyaApp: Display width in dp: %.2f, xInches %.1f, diag %.1f", 
                ((float) defaultDisplay.getWidth()) / displayMetrics.density, f2, diagonalInches))
            return ((float) defaultDisplay.getWidth()) / displayMetrics.density >= 1000.0f
        }
    }

    fun restartApp(): Unit {
        try {
            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE)
            PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, 
                new Intent(getContext(), LauncherActivity.class), 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE)
            alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, pendingIntent)
            System.exit(0)
        } catch (Exception e) {
            Log.e(TAG, "Failed to restart app", e)
        }
    }

    override fun onCreate(): Unit {
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
            } catch (Exception e) {
                Log.e(TAG, "Resource conflict resolver failed - continuing anyway", e)
            }
            
            // Initialize global options after resource conflicts are resolved
            // GlobalOptions.getInstance().initialize();  // Temporarily disabled due to dependencies
            
            // Initialize modern Linkpoint components with comprehensive exception handling
            initializeModernSystems()
            
            // Initialize automatic log upload for debug builds
            initializeDebugLogUpload()
            
            Log.i(TAG, "Lumiya Application initialization complete")
        } catch (Throwable e) {
            // Catch all throwables including OutOfMemoryError, LinkageError, etc.
            Log.e(TAG, "CRITICAL: Application initialization failed", e)
            
            // Upload crash report for debug builds
            try {
                AutoLogUploader.getInstance(this).uploadCrashReport(e, "Application initialization failure")
            } catch (Exception uploadError) {
                Log.e(TAG, "Failed to upload crash report", uploadError)
            }
            
            // Log specific error types for debugging
            if (e instanceof OutOfMemoryError) {
                Log.e(TAG, "Out of memory during initialization")
            } else if (e instanceof NoClassDefFoundError) {
                Log.e(TAG, "Missing object definition: " + e.getMessage())
            } else if (e instanceof UnsatisfiedLinkError) {
                Log.e(TAG, "Native library linking failed: " + e.getMessage())
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
    private fun initializeModernSystems(): Unit {
        Log.i(TAG, "Initializing modern Linkpoint components...")
        
        try {
            // Pre-flight checks before initializing modern components
            if (!performSystemChecks()) {
                Log.w(TAG, "System compatibility checks failed - skipping modern components")
                modernDemo = null
                return
            }
            
            // Initialize modern components with individual exception handling
            modernDemo = new ModernLinkpointDemo(this)
            Log.i(TAG, "Modern Linkpoint systems initialized successfully")
            
        } catch (NoClassDefFoundError e) {
            Log.e(TAG, "Modern system object not found - likely missing dependency", e)
            Log.e(TAG, "Missing class: " + e.getMessage())
            modernDemo = null
            
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Native library loading failed - modern graphics features will be disabled", e)
            Log.e(TAG, "Library loading error: " + e.getMessage())
            modernDemo = null
            
        } catch (SecurityException e) {
            Log.e(TAG, "Security error during modern system initialization", e)
            modernDemo = null
            
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "Out of memory during modern system initialization", e)
            modernDemo = null
            // Force garbage collection
            System.gc()
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize modern systems - continuing with graceful degradation", e)
            modernDemo = null
            
            // Log specific error for debugging
            Log.e(TAG, "Modern component initialization failed: " + e.getClass().getSimpleName() + " - " + e.getMessage())
            
        } catch (Throwable t) {
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
            if (mContext == null) {
                Log.e(TAG, "Context is null - cannot initialize modern components")
                return false
            }
            
            // Check if we have basic Android API requirements
            int apiLevel = android.os.Build.VERSION.SDK_INT
            if (apiLevel < 21) { // API 21 = Android 5.0 minimum
                Log.w(TAG, "Android API level " + apiLevel + " below minimum for modern components (21)")
                return false
            }
            
            // Check available memory
            Runtime runtime = Runtime.getRuntime()
            long freeMemory = runtime.freeMemory()
            long totalMemory = runtime.totalMemory()
            double memoryUsage = (double)(totalMemory - freeMemory) / totalMemory
            
            if (memoryUsage > 0.8) { // If using more than 80% of heap
                Log.w(TAG, "Memory usage too high (" + (memoryUsage * 100) + "%) - skipping modern components")
                return false
            }
            
            Log.i(TAG, "System checks passed - proceeding with modern component initialization")
            return true
            
        } catch (Exception e) {
            Log.e(TAG, "Error during system checks", e)
            return false
        }
    }
    
    /**
     * Initialize debug log upload system for debug builds only
     */
    private fun initializeDebugLogUpload(): Unit {
        try {
            AutoLogUploader logUploader = AutoLogUploader.getInstance(this)
            logUploader.initializeAutoUpload()
            Log.i(TAG, "Debug log upload system initialized")
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize debug log upload", e)
            // Don't crash the app if log upload fails
        }
    }
    
    /**
     * Get modern components demo instance
     */
    fun getModernDemo(): ModernLinkpointDemo {
        return modernDemo
    }

    override protected fun attachBaseContext(base: Context): Unit {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}

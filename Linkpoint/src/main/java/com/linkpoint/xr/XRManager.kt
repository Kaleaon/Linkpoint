package com.linkpoint.xr

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.Surface

/**
 * Manages XR (VR/AR) functionality for Linkpoint
 * 
 * Supports:
 * - Android XR (Google's new XR platform)
 * - OpenXR compatible devices
 * - Cardboard/Daydream legacy (deprecated but supported)
 */
class XRManager(private val context: Context) {
    
    companion object {
        private const val TAG = "XRManager"
        
        // XR mode types
        const val MODE_NONE = 0
        const val MODE_CARDBOARD = 1      // Legacy Google Cardboard
        const val MODE_OPENXR = 2          // OpenXR standard
        const val MODE_ANDROID_XR = 3      // Android XR (new Google platform)
    }
    
    private var isInitialized = false
    private var currentMode = MODE_NONE
    private var xrSession: XRSession? = null
    
    // XR capabilities
    private var hasCardboard = false
    private var hasOpenXR = false
    private var hasAndroidXR = false
    
    init {
        detectCapabilities()
    }
    
    private fun detectCapabilities() {
        val pm = context.packageManager
        
        // Check for Google Cardboard
        hasCardboard = pm.hasSystemFeature("android.software.vr.mode") ||
                       pm.hasSystemFeature("android.hardware.vr.high_performance")
        
        // Check for OpenXR runtime
        hasOpenXR = try {
            Class.forName("org.khronos.openxr.XrInstance")
            true
        } catch (e: ClassNotFoundException) {
            false
        }
        
        // Check for Android XR (new platform)
        hasAndroidXR = try {
            // Android XR is available on Android 15+ with XR hardware
            android.os.Build.VERSION.SDK_INT >= 35 && 
            pm.hasSystemFeature("android.hardware.xr.immersive")
        } catch (e: Exception) {
            false
        }
        
        Log.i(TAG, "XR Capabilities - Cardboard: $hasCardboard, OpenXR: $hasOpenXR, AndroidXR: $hasAndroidXR")
    }
    
    /**
     * Check if any XR mode is available
     */
    fun isAvailable(): Boolean = hasCardboard || hasOpenXR || hasAndroidXR
    
    /**
     * Get the best available XR mode
     */
    fun getBestMode(): Int {
        return when {
            hasAndroidXR -> MODE_ANDROID_XR
            hasOpenXR -> MODE_OPENXR
            hasCardboard -> MODE_CARDBOARD
            else -> MODE_NONE
        }
    }
    
    /**
     * Initialize XR session
     */
    fun initSession(mode: Int = getBestMode()): Boolean {
        if (isInitialized && currentMode == mode) {
            return true
        }
        
        shutdown()
        
        currentMode = mode
        
        xrSession = when (mode) {
            MODE_CARDBOARD -> CardboardSession(context)
            MODE_OPENXR -> OpenXRSession(context)
            MODE_ANDROID_XR -> AndroidXRSession(context)
            else -> null
        }
        
        isInitialized = xrSession?.initialize() == true
        
        if (isInitialized) {
            Log.i(TAG, "XR session initialized with mode: $mode")
        } else {
            Log.w(TAG, "Failed to initialize XR session")
        }
        
        return isInitialized
    }
    
    /**
     * Begin XR frame
     */
    fun beginFrame(): XRFrameData? {
        return xrSession?.beginFrame()
    }
    
    /**
     * End XR frame
     */
    fun endFrame() {
        xrSession?.endFrame()
    }
    
    /**
     * Get head pose for current frame
     */
    fun getHeadPose(): HeadPose? {
        return xrSession?.getHeadPose()
    }
    
    /**
     * Get controller states
     */
    fun getControllers(): List<ControllerState> {
        return xrSession?.getControllers() ?: emptyList()
    }
    
    /**
     * Shutdown XR session
     */
    fun shutdown() {
        xrSession?.shutdown()
        xrSession = null
        isInitialized = false
        currentMode = MODE_NONE
        Log.i(TAG, "XR session shutdown")
    }
    
    /**
     * Check if currently in XR mode
     */
    fun isInXRMode(): Boolean = isInitialized && xrSession != null
    
    /**
     * Get current XR mode
     */
    fun getCurrentMode(): Int = currentMode
}

/**
 * XR session interface
 */
interface XRSession {
    fun initialize(): Boolean
    fun beginFrame(): XRFrameData?
    fun endFrame()
    fun getHeadPose(): HeadPose?
    fun getControllers(): List<ControllerState>
    fun shutdown()
}

/**
 * Frame data for XR rendering
 */
data class XRFrameData(
    val leftEyeMatrix: FloatArray,
    val rightEyeMatrix: FloatArray,
    val leftProjection: FloatArray,
    val rightProjection: FloatArray,
    val predictedDisplayTime: Long
)

/**
 * Head pose data
 */
data class HeadPose(
    val position: FloatArray,      // x, y, z
    val orientation: FloatArray,   // quaternion x, y, z, w
    val timestamp: Long
)

/**
 * Controller state
 */
data class ControllerState(
    val hand: Hand,
    val position: FloatArray,
    val orientation: FloatArray,
    val triggerValue: Float,
    val gripValue: Float,
    val thumbstick: FloatArray,    // x, y
    val buttons: Int
) {
    enum class Hand { LEFT, RIGHT }
}

// Session implementations (stubs for now)
class CardboardSession(private val context: Context) : XRSession {
    override fun initialize(): Boolean {
        Log.d("CardboardSession", "Initializing Cardboard session")
        return true
    }
    override fun beginFrame(): XRFrameData? = null
    override fun endFrame() {}
    override fun getHeadPose(): HeadPose? = null
    override fun getControllers(): List<ControllerState> = emptyList()
    override fun shutdown() {}
}

class OpenXRSession(private val context: Context) : XRSession {
    override fun initialize(): Boolean {
        Log.d("OpenXRSession", "Initializing OpenXR session")
        return false // Not implemented yet
    }
    override fun beginFrame(): XRFrameData? = null
    override fun endFrame() {}
    override fun getHeadPose(): HeadPose? = null
    override fun getControllers(): List<ControllerState> = emptyList()
    override fun shutdown() {}
}

class AndroidXRSession(private val context: Context) : XRSession {
    override fun initialize(): Boolean {
        Log.d("AndroidXRSession", "Initializing Android XR session")
        return false // Not implemented yet - requires Android 15+
    }
    override fun beginFrame(): XRFrameData? = null
    override fun endFrame() {}
    override fun getHeadPose(): HeadPose? = null
    override fun getControllers(): List<ControllerState> = emptyList()
    override fun shutdown() {}
}

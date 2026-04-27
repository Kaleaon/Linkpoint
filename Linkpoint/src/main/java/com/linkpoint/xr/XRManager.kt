package com.linkpoint.xr

import android.content.Context
import android.util.Log
import com.linkpoint.BuildConfig

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
    private var sessionCapability: XRSessionCapability = XRSessionCapability.Unsupported("XR backend not evaluated")
    
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
    fun isAvailable(): Boolean = getPreferredCapability() !is XRSessionCapability.Unsupported

    fun isUiEntryAvailable(): Boolean {
        return when (val capability = getPreferredCapability()) {
            is XRSessionCapability.Ready -> true
            is XRSessionCapability.Experimental -> BuildConfig.XR_EXPERIMENTAL_MODE
            is XRSessionCapability.Unsupported -> false
        }
    }

    fun getEntryCapability(): XRSessionCapability = getPreferredCapability()
    
    /**
     * Get the best available XR mode
     */
    fun getBestMode(): Int {
        val candidates = listOf(MODE_ANDROID_XR, MODE_OPENXR, MODE_CARDBOARD)
        val readyMode = candidates.firstOrNull { getModeCapability(it) is XRSessionCapability.Ready }
        if (readyMode != null) {
            return readyMode
        }

        if (BuildConfig.XR_EXPERIMENTAL_MODE) {
            val experimentalMode = candidates.firstOrNull { getModeCapability(it) is XRSessionCapability.Experimental }
            if (experimentalMode != null) {
                return experimentalMode
            }
        }

        return MODE_NONE
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

        sessionCapability = getModeCapability(mode)

        if (sessionCapability is XRSessionCapability.Unsupported) {
            Log.w(TAG, "Cannot initialize unsupported XR mode: ${(sessionCapability as XRSessionCapability.Unsupported).reason}")
            xrSession = null
            return false
        }

        if (sessionCapability is XRSessionCapability.Experimental && !BuildConfig.XR_EXPERIMENTAL_MODE) {
            Log.w(TAG, "Experimental XR mode blocked by build config")
            xrSession = null
            return false
        }
        
        isInitialized = xrSession?.initialize() == true
        
        if (isInitialized) {
            Log.i(TAG, "XR session initialized with mode: $mode")
        } else {
            Log.w(TAG, "Failed to initialize XR session")
        }
        
        return isInitialized
    }

    private fun getPreferredCapability(): XRSessionCapability {
        val candidates = listOf(MODE_ANDROID_XR, MODE_OPENXR, MODE_CARDBOARD)
        candidates.forEach { mode ->
            val capability = getModeCapability(mode)
            if (capability is XRSessionCapability.Ready) {
                return capability
            }
        }

        candidates.forEach { mode ->
            val capability = getModeCapability(mode)
            if (capability is XRSessionCapability.Experimental) {
                return capability
            }
        }

        return XRSessionCapability.Unsupported("No XR runtime or compatible hardware detected")
    }

    private fun getModeCapability(mode: Int): XRSessionCapability {
        return when (mode) {
            MODE_CARDBOARD -> {
                if (hasCardboard) XRSessionCapability.Ready("Cardboard/VR mode")
                else XRSessionCapability.Unsupported("Cardboard capability missing")
            }
            MODE_OPENXR -> {
                if (!hasOpenXR) XRSessionCapability.Unsupported("OpenXR runtime not present")
                else XRSessionCapability.Experimental("OpenXR backend is still experimental")
            }
            MODE_ANDROID_XR -> {
                if (!hasAndroidXR) XRSessionCapability.Unsupported("Android XR hardware/runtime not detected")
                else XRSessionCapability.Experimental("Android XR integration is experimental")
            }
            else -> XRSessionCapability.Unsupported("No XR mode selected")
        }
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
        sessionCapability = XRSessionCapability.Unsupported("XR session not running")
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

sealed class XRSessionCapability {
    data class Unsupported(val reason: String) : XRSessionCapability()
    data class Experimental(val reason: String) : XRSessionCapability()
    data class Ready(val detail: String) : XRSessionCapability()
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
    private var lastPose = HeadPose(
        position = floatArrayOf(0f, 1.6f, 0f),
        orientation = floatArrayOf(0f, 0f, 0f, 1f),
        timestamp = System.nanoTime()
    )

    override fun initialize(): Boolean {
        Log.d("CardboardSession", "Initializing Cardboard session")
        return true
    }

    override fun beginFrame(): XRFrameData {
        val now = System.nanoTime()
        lastPose = lastPose.copy(timestamp = now)

        return XRFrameData(
            leftEyeMatrix = buildViewMatrix(-0.032f),
            rightEyeMatrix = buildViewMatrix(0.032f),
            leftProjection = buildProjectionMatrix(),
            rightProjection = buildProjectionMatrix(),
            predictedDisplayTime = now + 11_000_000L
        )
    }

    override fun endFrame() {}
    override fun getHeadPose(): HeadPose = lastPose
    override fun getControllers(): List<ControllerState> = listOf(
        ControllerState(
            hand = ControllerState.Hand.LEFT,
            position = floatArrayOf(-0.15f, 1.25f, -0.35f),
            orientation = floatArrayOf(0f, 0f, 0f, 1f),
            triggerValue = 0f,
            gripValue = 0f,
            thumbstick = floatArrayOf(0f, 0f),
            buttons = 0
        ),
        ControllerState(
            hand = ControllerState.Hand.RIGHT,
            position = floatArrayOf(0.15f, 1.25f, -0.35f),
            orientation = floatArrayOf(0f, 0f, 0f, 1f),
            triggerValue = 0f,
            gripValue = 0f,
            thumbstick = floatArrayOf(0f, 0f),
            buttons = 0
        )
    )
    override fun shutdown() {}

    private fun buildViewMatrix(eyeOffsetX: Float): FloatArray {
        return floatArrayOf(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            -eyeOffsetX, -1.6f, 0f, 1f
        )
    }

    private fun buildProjectionMatrix(
        fovYDegrees: Float = 90f,
        aspect: Float = 1f,
        near: Float = 0.05f,
        far: Float = 100f
    ): FloatArray {
        val f = (1f / kotlin.math.tan(Math.toRadians((fovYDegrees / 2f).toDouble()))).toFloat()
        return floatArrayOf(
            f / aspect, 0f, 0f, 0f,
            0f, f, 0f, 0f,
            0f, 0f, (far + near) / (near - far), -1f,
            0f, 0f, (2f * far * near) / (near - far), 0f
        )
    }
}

/**
 * OpenXR-backed session for headsets exposing the OpenXR runtime
 * (Quest, Pico, Steam Link, etc.).
 *
 * `initialize()` performs a real availability check before returning
 * — the body of the OpenXR session loop is not implemented in this
 * Kotlin layer because OpenXR is a C ABI, and the Linkpoint codebase
 * does not currently link the OpenXR loader (`libopenxr_loader.so`)
 * or hold a JNI bridge for the swapchain / pose APIs. We surface the
 * availability state via [isRuntimeAvailable] so the
 * [DeviceCapabilityDetector] gives an honest answer ("device has the
 * runtime; viewer is not yet wired") instead of a silent stub.
 *
 * Wiring path (left as a follow-up): build `libopenxr_loader.so` into
 * `app/src/main/jniLibs/`, add the OpenXR Android loader package to
 * `AndroidManifest.xml` (`org.khronos.openxr.OpenXRRuntime` queries
 * action), then thread JNI calls for `xrCreateInstance`,
 * `xrCreateSession`, and the per-frame `xrWaitFrame` /
 * `xrLocateViews` / `xrEndFrame` cycle.
 */
class OpenXRSession(private val context: Context) : XRSession {

    /**
     * True when this device exposes an OpenXR runtime (the queries-tag
     * for `org.khronos.openxr.OpenXRRuntime` resolves to a system
     * package, OR the `libopenxr_loader.so` lookup succeeds).
     */
    val isRuntimeAvailable: Boolean by lazy { detectOpenXrRuntime() }

    private fun detectOpenXrRuntime(): Boolean {
        // PackageManager query: every OpenXR runtime declares an
        // `org.khronos.openxr.OpenXRRuntime` content provider so apps
        // can locate it without dlopen-ing the loader directly.
        return try {
            val pm = context.packageManager
            val intent = android.content.Intent("org.khronos.openxr.OpenXRRuntimeService")
            val resolved = pm.queryIntentServices(intent, 0)
            if (resolved.isNotEmpty()) {
                Log.d("OpenXRSession", "OpenXR runtime detected: ${resolved.first().serviceInfo.packageName}")
                return true
            }
            // Fallback: probe the loader native lib.
            try {
                System.loadLibrary("openxr_loader")
                Log.d("OpenXRSession", "OpenXR loader dlopen succeeded")
                true
            } catch (_: UnsatisfiedLinkError) {
                false
            }
        } catch (e: Exception) {
            Log.d("OpenXRSession", "OpenXR runtime detection failed: ${e.message}")
            false
        }
    }

    override fun initialize(): Boolean {
        if (!isRuntimeAvailable) {
            Log.i("OpenXRSession", "OpenXR runtime absent — falling back (no headset / no loader)")
            return false
        }
        Log.w("OpenXRSession",
            "OpenXR runtime available but Linkpoint's JNI bridge is not yet wired " +
                "(libopenxr_loader.so + xrCreateInstance/Session/Views chain). " +
                "Refusing to claim a session we cannot drive — see class kdoc.")
        return false
    }
    override fun beginFrame(): XRFrameData? = null
    override fun endFrame() {}
    override fun getHeadPose(): HeadPose? = null
    override fun getControllers(): List<ControllerState> = emptyList()
    override fun shutdown() {}
}

/**
 * Android XR (Jetpack XR) session for the Android 15+ device class.
 * Like [OpenXRSession], real session wiring requires linking against
 * the Jetpack XR runtime libraries (`androidx.xr.runtime:runtime-*`)
 * and the AndroidX scene model — none of which are currently on the
 * Linkpoint classpath. This class detects platform availability and
 * reports honestly.
 */
class AndroidXRSession(private val context: Context) : XRSession {

    /**
     * True when this device runs Android 14+ AND exposes the
     * `android.software.xr.api.openxr` system feature (Pixel XR
     * companion devices). Earlier Android versions can host the
     * Jetpack XR libraries but only emulate the runtime; we treat
     * those as not available so we don't end up driving a
     * software-only path that would compete with [CardboardSession].
     */
    val isPlatformAvailable: Boolean by lazy { detectAndroidXr() }

    private fun detectAndroidXr(): Boolean {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) return false
        return try {
            context.packageManager.hasSystemFeature("android.software.xr.api.openxr")
        } catch (e: Exception) {
            Log.d("AndroidXRSession", "Android XR feature query failed: ${e.message}")
            false
        }
    }

    override fun initialize(): Boolean {
        if (!isPlatformAvailable) {
            Log.i("AndroidXRSession",
                "Android XR not available — sdk=${android.os.Build.VERSION.SDK_INT}, " +
                    "openxr feature absent. Falling back.")
            return false
        }
        Log.w("AndroidXRSession",
            "Android XR platform available but Jetpack XR runtime " +
                "dependencies (androidx.xr.runtime:runtime-openxr, " +
                "androidx.xr.scenecore) are not yet on the classpath. See class kdoc.")
        return false
    }
    override fun beginFrame(): XRFrameData? = null
    override fun endFrame() {}
    override fun getHeadPose(): HeadPose? = null
    override fun getControllers(): List<ControllerState> = emptyList()
    override fun shutdown() {}
}

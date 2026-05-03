package com.linkpoint.render.driver

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log

/**
 * Pre-context graphics-driver probe. Runs on the main thread *before*
 * any GL context is created, so the renderer can decide whether the
 * device is even capable of running the Lumiya pipeline before
 * burning surface state on a doomed initialisation.
 *
 * Sources of information (all available without a GL context):
 *  - [PackageManager.hasSystemFeature] for Vulkan + OpenGL ES feature
 *    flags declared by the device manufacturer.
 *  - [ActivityManager.getDeviceConfigurationInfo] for the maximum GL
 *    ES version the device can negotiate.
 *  - [Build.HARDWARE] / [Build.MANUFACTURER] for vendor-family hints
 *    used by [GpuCapabilities] heuristics later on.
 *
 * The probe is deliberately conservative — it never claims a feature
 * is present unless the platform reports it. Producers can override
 * (e.g. force-disable Vulkan) by passing custom flags into
 * [DriverProfile.from], but the default path is "what the device tells
 * us" and the renderer respects that.
 */
object GraphicsDriverProbe {

    private const val TAG = "GraphicsDriverProbe"

    /**
     * Android system features used by the probe. Constants live here
     * so tests can inject a fake [PackageManager] without depending
     * on Android-runtime constants that aren't available under the
     * JVM unit test classpath.
     */
    private const val FEATURE_VULKAN_HARDWARE_LEVEL = "android.hardware.vulkan.level"
    private const val FEATURE_VULKAN_HARDWARE_VERSION = "android.hardware.vulkan.version"
    private const val FEATURE_VULKAN_DEQP_LEVEL = "android.software.vulkan.deqp.level"
    private const val FEATURE_OPENGLES_EXTENSION_PACK = "android.hardware.opengles.aep"

    /**
     * Snapshot of pre-context driver state. Immutable so it can be
     * passed across threads.
     *
     * @property glesMajorVersion / [glesMinorVersion] from
     *           [ActivityManager.getDeviceConfigurationInfo]'s reqGlEsVersion.
     *           Format on the wire is 0xMMMMmmmm (M=major, m=minor); we
     *           split it into two ints so callers can use them
     *           directly. Values of 0 mean the device didn't advertise
     *           a version (very old devices, or test mocks).
     * @property vulkanHardwareLevel Vulkan hardware feature level (0,
     *           1, …) or -1 if not advertised. -1 means the device has
     *           no Vulkan driver at all; 0 means "Vulkan exists but
     *           with restrictions" (no compute on graphics queue,
     *           etc.); ≥1 means a reasonably full driver. From the
     *           Android Vulkan compat doc.
     * @property vulkanHardwareVersion Vulkan API version exposed by
     *           the driver. Encoded the same way Vulkan itself does:
     *           0xVVVRRRPPP_PPPP where V/R/P are major/minor/patch.
     *           Matches the system-feature value. -1 if absent.
     * @property vulkanDeqpLevel Date-encoded dEQP conformance level
     *           (e.g. 132317184 = 2019-03-01). -1 if absent. Useful
     *           for deciding whether the driver can be trusted with
     *           features that historically had vendor bugs (e.g.
     *           timestamp queries).
     * @property hasOpenGlesAep True if the device advertises
     *           "android.hardware.opengles.aep" — the Android
     *           Extension Pack on top of GLES 3.1 that brings in
     *           tessellation, geometry shaders, and ASTC.
     * @property hardwareName / manufacturer Lower-cased
     *           [Build.HARDWARE] / [Build.MANUFACTURER]; used by
     *           [vendorFamily] to pick a known-bad-driver workaround.
     */
    data class DriverProfile(
        val glesMajorVersion: Int,
        val glesMinorVersion: Int,
        val vulkanHardwareLevel: Int,
        val vulkanHardwareVersion: Int,
        val vulkanDeqpLevel: Int,
        val hasOpenGlesAep: Boolean,
        val hardwareName: String,
        val manufacturer: String
    ) {
        /** True if the device exposes any Vulkan driver at all. */
        val hasVulkan: Boolean
            get() = vulkanHardwareLevel >= 0

        /** True if the device meets our minimum requirement (GL ES 3.0). */
        val meetsMinimumGles: Boolean
            get() = glesMajorVersion > 3 ||
                (glesMajorVersion == 3 && glesMinorVersion >= 0)

        /**
         * Vendor family inferred from the hardware/manufacturer
         * strings. Used to pick known-bug workarounds. Not a
         * judgement of quality — every family ships excellent and
         * buggy drivers.
         */
        val vendorFamily: VendorFamily
            get() {
                val hay = "$hardwareName $manufacturer"
                return when {
                    hay.contains("qualcomm") || hay.contains("adreno") || hay.contains("snapdragon") -> VendorFamily.ADRENO
                    hay.contains("mali") || hay.contains("arm") -> VendorFamily.MALI
                    hay.contains("powervr") || hay.contains("imagination") -> VendorFamily.POWERVR
                    hay.contains("tegra") || hay.contains("nvidia") -> VendorFamily.TEGRA
                    hay.contains("intel") -> VendorFamily.INTEL
                    hay.contains("samsung") || hay.contains("xclipse") -> VendorFamily.XCLIPSE
                    else -> VendorFamily.UNKNOWN
                }
            }

        /** Compact human-readable summary for logs / RenderDiagnostics. */
        fun summary(): String = buildString {
            append("GLES $glesMajorVersion.$glesMinorVersion")
            if (hasOpenGlesAep) append(" (AEP)")
            if (hasVulkan) {
                append(", Vulkan L$vulkanHardwareLevel v$vulkanHardwareVersion")
                if (vulkanDeqpLevel > 0) append(" deqp=$vulkanDeqpLevel")
            } else {
                append(", no Vulkan")
            }
            append(", vendor=$vendorFamily")
        }

        companion object {
            /** Empty profile — used when we have no PackageManager (tests). */
            val UNKNOWN = DriverProfile(
                glesMajorVersion = 0,
                glesMinorVersion = 0,
                vulkanHardwareLevel = -1,
                vulkanHardwareVersion = -1,
                vulkanDeqpLevel = -1,
                hasOpenGlesAep = false,
                hardwareName = "",
                manufacturer = ""
            )

            /**
             * Construct a profile from raw values. Exposed for tests
             * so capability decisions can be exercised without
             * mocking [Context]. Production code should use
             * [GraphicsDriverProbe.probe].
             */
            fun from(
                glesMajor: Int,
                glesMinor: Int,
                vulkanLevel: Int = -1,
                vulkanVersion: Int = -1,
                vulkanDeqp: Int = -1,
                aep: Boolean = false,
                hardware: String = "",
                manufacturer: String = ""
            ): DriverProfile = DriverProfile(
                glesMajor, glesMinor,
                vulkanLevel, vulkanVersion, vulkanDeqp,
                aep,
                hardware.lowercase(),
                manufacturer.lowercase()
            )
        }
    }

    enum class VendorFamily { ADRENO, MALI, POWERVR, TEGRA, INTEL, XCLIPSE, UNKNOWN }

    /**
     * Probe the device. Safe to call from any thread; returns
     * [DriverProfile.UNKNOWN] on any failure rather than throwing,
     * so a misconfigured app context never breaks renderer init.
     */
    fun probe(context: Context): DriverProfile = try {
        val pm = context.packageManager
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val configInfo = am?.deviceConfigurationInfo
        val reqGles = configInfo?.reqGlEsVersion ?: 0
        val major = (reqGles ushr 16) and 0xFFFF
        val minor = reqGles and 0xFFFF

        val vulkanLevel = featureLevel(pm, FEATURE_VULKAN_HARDWARE_LEVEL)
        val vulkanVersion = featureLevel(pm, FEATURE_VULKAN_HARDWARE_VERSION)
        val vulkanDeqp = featureLevel(pm, FEATURE_VULKAN_DEQP_LEVEL)
        val hasAep = pm.hasSystemFeature(FEATURE_OPENGLES_EXTENSION_PACK)

        DriverProfile(
            glesMajorVersion = major,
            glesMinorVersion = minor,
            vulkanHardwareLevel = vulkanLevel,
            vulkanHardwareVersion = vulkanVersion,
            vulkanDeqpLevel = vulkanDeqp,
            hasOpenGlesAep = hasAep,
            hardwareName = (Build.HARDWARE ?: "").lowercase(),
            manufacturer = (Build.MANUFACTURER ?: "").lowercase()
        ).also {
            Log.i(TAG, "Driver probe: ${it.summary()}")
        }
    } catch (t: Throwable) {
        Log.w(TAG, "Driver probe failed; using UNKNOWN profile", t)
        DriverProfile.UNKNOWN
    }

    /**
     * Read a versioned system feature. The Android API is awkward:
     * when a feature has a "version" int (Vulkan does), it's a
     * separate FeatureInfo entry rather than a getSystemFeature
     * return value. We walk [PackageManager.getSystemAvailableFeatures]
     * once and pick out what we need.
     *
     * Returns -1 if the feature isn't present.
     */
    private fun featureLevel(pm: PackageManager, name: String): Int {
        return try {
            val features = pm.systemAvailableFeatures ?: return -1
            for (fi in features) {
                if (fi.name == name) return fi.version
            }
            // Some features (e.g. dEQP level) are exposed via
            // hasSystemFeature only; fall back so we can at least
            // tell "present / not present".
            if (pm.hasSystemFeature(name)) 0 else -1
        } catch (t: Throwable) {
            Log.v(TAG, "featureLevel($name) lookup failed: ${t.message}")
            -1
        }
    }
}

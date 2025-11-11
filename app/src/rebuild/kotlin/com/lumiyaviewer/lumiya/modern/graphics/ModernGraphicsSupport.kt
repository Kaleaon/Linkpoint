package com.lumiyaviewer.lumiya.modern.graphics

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.util.Log
import kotlin.math.max

/**
 * Utility helpers for detecting and reporting modern (OpenGL ES 3.0+) graphics support.
 *
 * The goal is to give Lumiya a single place to reason about whether the modernised renderer
 * can run safely on the current device.  The checks are intentionally defensive, providing
 * human readable diagnostics for crash reports and UI fallbacks.
 */
object ModernGraphicsSupport {

    private const val TAG = "ModernGraphicsSupport"
    private const val REQUIRED_MAJOR_VERSION = 3

    data class Status(
        val supported: Boolean,
        val reportedVersion: String,
        val reqGlEsVersion: Int,
        val renderer: String,
        val vendor: String,
        val deviceSummary: String,
        val reason: String?
    ) {
        fun describe(): String = buildString {
            append("OpenGL ES ")
            append(reportedVersion.ifEmpty { "unknown" })
            append(" -> ")
            append(if (supported) "SUPPORTED" else "NOT SUPPORTED")
            reason?.let {
                append(" (")
                append(it)
                append(')')
            }
        }
    }

    /**
     * Evaluate device capabilities. Never throws – returns best-effort diagnostics instead.
     */
    fun evaluate(context: Context): Status {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val config = manager?.deviceConfigurationInfo
        val reported = config?.glEsVersion ?: shortVersionString(config?.reqGlEsVersion)
        val reqVersion = config?.reqGlEsVersion ?: 0

        val majorFromReq = reqVersion ushr 16
        val minorFromReq = reqVersion and 0xFFFF

        val majorVersion = when {
            majorFromReq > 0 -> majorFromReq
            reported.isNotEmpty() -> reported.substringBefore('.').toIntOrNull() ?: 0
            else -> 0
        }
        val minorVersion = when {
            majorFromReq > 0 -> minorFromReq
            reported.contains('.') -> reported.substringAfter('.').toIntOrNull()
            else -> 0
        } ?: 0

        val isEmulator = isProbablyEmulator()
        val supported = majorVersion >= REQUIRED_MAJOR_VERSION
        val renderer = if (isEmulator) "Emulated Renderer" else Build.HARDWARE ?: "Unknown"
        val vendor = Build.MANUFACTURER ?: "Unknown"
        val deviceSummary = "${Build.BRAND ?: "?"} ${Build.MODEL ?: "?"}".trim()

        val reason = when {
            supported -> null
            manager == null -> "ActivityManager unavailable"
            config == null -> "DeviceConfigInfo unavailable"
            majorVersion == 0 -> "Unreported GL ES version"
            else -> "Requires OpenGL ES 3.0+, found $majorVersion.$minorVersion"
        }

        val status = Status(
            supported = supported,
            reportedVersion = reported.ifEmpty { "$majorVersion.$minorVersion" },
            reqGlEsVersion = reqVersion,
            renderer = renderer,
            vendor = vendor,
            deviceSummary = deviceSummary,
            reason = reason
        )

        Log.i(TAG, status.describe())
        return status
    }

    private fun shortVersionString(reqVersion: Int?): String {
        if (reqVersion == null || reqVersion == 0) return ""
        val major = reqVersion ushr 16
        val minor = reqVersion and 0xFFFF
        return "${max(major, 0)}.${max(minor, 0)}"
    }

    private fun isProbablyEmulator(): Boolean {
        val fingerprint = Build.FINGERPRINT ?: ""
        val model = Build.MODEL ?: ""
        val product = Build.PRODUCT ?: ""

        return fingerprint.startsWith("generic") ||
            fingerprint.lowercase().contains("emulator") ||
            fingerprint.lowercase().contains("sdk") ||
            model.contains("Emulator") ||
            model.contains("Android SDK built for x86") ||
            product == "google_sdk"
    }
}

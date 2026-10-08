package com.linkpoint.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.preference.PreferenceManager

/**
 * User-configurable runtime controls for background orchestration.
 */
object BackgroundRuntimeConfig {
    const val PREF_BACKGROUND_RESUME_ENABLED = "background_resume_enabled"
    const val PREF_PUSH_WAKEUPS_ENABLED = "push_wakeups_enabled"
    const val PREF_BACKGROUND_PROFILE = "background_intensity_profile"

    enum class IntensityProfile(
        val keepAliveIntervalMs: Long,
        val idleIntervalMs: Long,
        val reconnectBackoffMs: Long,
        val wakeLockTimeoutMs: Long
    ) {
        CONSERVATIVE(
            keepAliveIntervalMs = 60_000L,
            idleIntervalMs = 120_000L,
            reconnectBackoffMs = 60 * 60_000L,
            wakeLockTimeoutMs = 3 * 60_000L
        ),
        BALANCED(
            keepAliveIntervalMs = 30_000L,
            idleIntervalMs = 60_000L,
            reconnectBackoffMs = 30 * 60_000L,
            wakeLockTimeoutMs = 6 * 60_000L
        ),
        AGGRESSIVE(
            keepAliveIntervalMs = 15_000L,
            idleIntervalMs = 30_000L,
            reconnectBackoffMs = 15 * 60_000L,
            wakeLockTimeoutMs = 10 * 60_000L
        ),

        /**
         * Cellular-tuned profile. Carrier UDP NAT timeouts on LTE/5G are
         * commonly 30 seconds (T-Mobile/Verizon docs cite 25-60s; in the
         * wild 30s is the modal value). Anything ≥30s risks the NAT mapping
         * being torn down between keepalives, after which the simulator's
         * replies fall on the floor and the unanswered-pings watchdog has
         * to escalate to a full re-login (visible in the 2026-04-26
         * Athanasia capture as four UseCircuitCode-from-seq-1 cycles).
         *
         * 20s gives margin under the 30s NAT timeout while still being
         * gentler on battery than AGGRESSIVE's 15s. Used automatically when
         * the active network is cellular (see [getEffectiveProfile]).
         */
        CELLULAR_AGGRESSIVE(
            keepAliveIntervalMs = 20_000L,
            idleIntervalMs = 25_000L,
            reconnectBackoffMs = 15 * 60_000L,
            wakeLockTimeoutMs = 8 * 60_000L
        )
    }

    fun isBackgroundResumeEnabled(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(PREF_BACKGROUND_RESUME_ENABLED, true)
    }

    fun isPushWakeupsEnabled(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(PREF_PUSH_WAKEUPS_ENABLED, true)
    }

    fun getIntensityProfile(context: Context): IntensityProfile {
        val raw = PreferenceManager.getDefaultSharedPreferences(context)
            .getString(PREF_BACKGROUND_PROFILE, "balanced")
        return mapIntensityProfile(raw)
    }

    /**
     * Like [getIntensityProfile] but auto-promotes the user's selected
     * profile to [IntensityProfile.CELLULAR_AGGRESSIVE] when the active
     * network is cellular AND the user hasn't explicitly chosen the
     * already-aggressive profile. Wi-Fi NAT timeouts are typically ≥10
     * minutes so the user-selected profile passes through unchanged.
     *
     * Falls back to [getIntensityProfile] on any error reading the network
     * state — defensive, never throws into the keepalive loop.
     */
    fun getEffectiveProfile(context: Context): IntensityProfile {
        val userChoice = getIntensityProfile(context)
        // If the user explicitly picked AGGRESSIVE (15s) we honour that —
        // it's already tighter than CELLULAR_AGGRESSIVE.
        if (userChoice == IntensityProfile.AGGRESSIVE) return userChoice
        return if (isOnCellular(context)) IntensityProfile.CELLULAR_AGGRESSIVE else userChoice
    }

    private fun isOnCellular(context: Context): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return false
            val active = cm.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(active) ?: return false
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) &&
                !caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) &&
                !caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } catch (e: Throwable) {
            false
        }
    }

    internal fun mapIntensityProfile(raw: String?): IntensityProfile {
        return when (raw) {
            "conservative" -> IntensityProfile.CONSERVATIVE
            "aggressive" -> IntensityProfile.AGGRESSIVE
            "cellular_aggressive" -> IntensityProfile.CELLULAR_AGGRESSIVE
            else -> IntensityProfile.BALANCED
        }
    }
}

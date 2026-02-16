package com.linkpoint.service

import android.content.Context
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

    internal fun mapIntensityProfile(raw: String?): IntensityProfile {
        return when (raw) {
            "conservative" -> IntensityProfile.CONSERVATIVE
            "aggressive" -> IntensityProfile.AGGRESSIVE
            else -> IntensityProfile.BALANCED
        }
    }
}

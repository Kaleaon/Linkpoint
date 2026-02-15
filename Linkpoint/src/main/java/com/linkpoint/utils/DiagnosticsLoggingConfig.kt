package com.linkpoint.utils

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.preference.PreferenceManager
import java.io.File
import java.util.concurrent.TimeUnit

object DiagnosticsLoggingConfig {
    private const val TAG = "DiagnosticsLoggingConfig"
    const val PREF_VERBOSE_PACKET_LOGGING = "diagnostic_verbose_packet_logging"
    const val PREF_VERBOSE_BODY_LOGGING = "diagnostic_verbose_body_logging"
    const val PREF_LOG_RETENTION_DAYS = "diagnostic_log_retention_days"

    const val DEFAULT_RETENTION_DAYS = 7
    const val MAX_RETENTION_DAYS = 30

    enum class StorageTarget {
        APP_PRIVATE_INTERNAL
    }

    fun storageTargetForApi(apiLevel: Int = Build.VERSION.SDK_INT): StorageTarget {
        return StorageTarget.APP_PRIVATE_INTERNAL
    }

    fun isVerbosePacketLoggingEnabled(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(PREF_VERBOSE_PACKET_LOGGING, false)
    }

    fun isVerboseBodyLoggingEnabled(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(PREF_VERBOSE_BODY_LOGGING, false)
    }

    fun getRetentionDays(context: Context): Int {
        val raw = PreferenceManager.getDefaultSharedPreferences(context)
            .getString(PREF_LOG_RETENTION_DAYS, DEFAULT_RETENTION_DAYS.toString())
            ?.toIntOrNull()
            ?: DEFAULT_RETENTION_DAYS
        return raw.coerceIn(1, MAX_RETENTION_DAYS)
    }

    fun diagnosticsDirectory(context: Context): File {
        return File(context.filesDir, "diagnostics/logs")
    }

    fun purgeExpiredLogs(logDir: File, retentionDays: Int, nowMs: Long = System.currentTimeMillis()) {
        if (!logDir.exists()) return
        val cutoff = nowMs - TimeUnit.DAYS.toMillis(retentionDays.toLong())
        logDir.listFiles()?.forEach { file ->
            if (file.isFile && file.lastModified() < cutoff && !file.delete()) {
                Log.w(TAG, "Failed to delete expired log file: ${file.absolutePath}")
            }
        }
    }
}

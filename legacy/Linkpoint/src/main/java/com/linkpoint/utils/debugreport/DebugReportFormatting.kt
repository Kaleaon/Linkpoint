package com.linkpoint.utils.debugreport

import android.os.Build
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DebugReportFormatting {
    fun formatTimestamp(timestamp: Long): String = formatDateWithPattern(timestamp, "yyyy-MM-dd HH:mm:ss.SSS Z")
    fun formatFilenameTimestamp(timestamp: Long): String = formatDateWithPattern(timestamp, "yyyy-MM-dd_HH-mm-ss")
    fun truncate(value: String, max: Int): String = if (value.length > max) value.take(max) + "..." else value
    fun formatDuration(ms: Long): String = when {
        ms < 1000 -> "${ms}ms"
        ms < 60000 -> String.format(Locale.US, "%.1fs", ms / 1000.0)
        ms < 3600000 -> String.format(Locale.US, "%.1fm", ms / 60000.0)
        else -> String.format(Locale.US, "%.1fh", ms / 3600000.0)
    }

    private fun formatDateWithPattern(timestamp: Long, pattern: String): String =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            java.time.Instant.ofEpochMilli(timestamp)
                .atZone(java.time.ZoneId.systemDefault())
                .format(java.time.format.DateTimeFormatter.ofPattern(pattern))
        } else {
            SimpleDateFormat(pattern, Locale.US).format(Date(timestamp))
        }
}

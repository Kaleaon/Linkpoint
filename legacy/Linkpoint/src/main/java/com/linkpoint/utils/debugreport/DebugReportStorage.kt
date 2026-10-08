package com.linkpoint.utils.debugreport

import android.content.Context
import android.util.Log
import java.io.File

class DebugReportStorage(private val context: Context) {
    private val dirName = "debug_reports"
    private val prefix = "debug_report_"
    private val suffix = ".txt"
    private val maxReports = 20
    private val tag = "DebugReportStorage"

    private val reportDirectory: File by lazy {
        File(context.filesDir, dirName).apply { if (!exists()) mkdirs() }
    }

    fun save(content: String): File? = runCatching {
        val file = File(reportDirectory, "$prefix${DebugReportFormatting.formatFilenameTimestamp(System.currentTimeMillis())}$suffix")
        file.writeText(content)
        cleanupRetention()
        file
    }.onFailure { Log.e(tag, "Failed to save debug report", it) }.getOrNull()

    fun list(): List<File> = reportDirectory.listFiles { file ->
        file.name.startsWith(prefix) && file.name.endsWith(suffix)
    }?.sortedByDescending { it.lastModified() } ?: emptyList()

    fun clear() { reportDirectory.listFiles()?.forEach { it.delete() } }

    private fun cleanupRetention() {
        val reports = list()
        if (reports.size > maxReports) reports.drop(maxReports).forEach { it.delete() }
    }
}

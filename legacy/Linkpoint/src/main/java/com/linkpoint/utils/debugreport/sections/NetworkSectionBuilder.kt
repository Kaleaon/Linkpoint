package com.linkpoint.utils.debugreport.sections

import com.linkpoint.network.NetworkLogger
import com.linkpoint.utils.debugreport.DebugReportContext

class NetworkSectionBuilder : DebugReportSectionBuilder {
    override fun build(context: DebugReportContext): String = buildString {
        appendLine("┌──────────────────────────────────────────────────────────────────┐")
        appendLine("│ NETWORK ACTIVITY & PACKET STATUS                                  │")
        appendLine("└──────────────────────────────────────────────────────────────────┘")
        appendLine()
        runCatching {
            val s = NetworkLogger.getStatistics()
            appendLine("HTTP Requests: ${s.requestCount}")
            appendLine("HTTP Responses: ${s.responseCount}")
            appendLine("Errors: ${s.errorCount}")
            appendLine("Warnings: ${s.warningCount}")
        }.onFailure { appendLine("Network stats unavailable: ${it.message}") }
        appendLine()
    }
}

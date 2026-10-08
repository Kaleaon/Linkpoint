package com.linkpoint.utils.debugreport.sections

import com.linkpoint.utils.debugreport.DebugReportContext

class ConnectionSectionBuilder : DebugReportSectionBuilder {
    override fun build(context: DebugReportContext): String = buildString {
        appendLine("┌──────────────────────────────────────────────────────────────────┐")
        appendLine("│ CONNECTION STATUS                                                 │")
        appendLine("└──────────────────────────────────────────────────────────────────┘")
        appendLine()
        val app = context.app
        if (app != null) {
            appendLine("Connected: ${app.isConnected()}")
            appendLine("Current Region: ${app.getCurrentRegion() ?: "None"}")
            appendLine("Agent ID: ${app.agentId ?: "Not logged in"}")
            runCatching {
                appendLine("Avatar Name: ${app.sessionManager.getAvatarName()}")
                appendLine("Connection State: ${app.sessionManager.connectionState.value}")
            }.onFailure {
                appendLine("Session Info: Unable to retrieve - ${it.message}")
            }
        } else appendLine("App instance not available")
        appendLine()
    }
}

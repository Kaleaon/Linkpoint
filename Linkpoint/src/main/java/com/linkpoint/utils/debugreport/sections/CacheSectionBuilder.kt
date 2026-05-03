package com.linkpoint.utils.debugreport.sections

import com.linkpoint.assets.CacheManager
import com.linkpoint.utils.debugreport.DebugReportContext
import kotlinx.coroutines.runBlocking

class CacheSectionBuilder : DebugReportSectionBuilder {
    override fun build(context: DebugReportContext): String = buildString {
        appendLine("┌──────────────────────────────────────────────────────────────────┐")
        appendLine("│ CACHE STATISTICS                                                  │")
        appendLine("└──────────────────────────────────────────────────────────────────┘")
        appendLine()
        runCatching {
            val cacheStats = runBlocking { CacheManager(context.androidContext).getCacheStats() }
            appendLine("Total Cache Size: ${cacheStats.getFormattedTotalSize()} / ${cacheStats.getFormattedMaxSize()} (${cacheStats.usagePercent}%)")
            appendLine("Total Files: ${cacheStats.totalFileCount}")
        }.onFailure { appendLine("Cache statistics unavailable: ${it.message}") }
        appendLine()
    }
}

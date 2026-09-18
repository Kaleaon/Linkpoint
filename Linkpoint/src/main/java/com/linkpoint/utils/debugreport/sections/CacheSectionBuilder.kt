package com.linkpoint.utils.debugreport.sections

import com.linkpoint.assets.CacheManager
import com.linkpoint.assets.TextureMemoryTracker
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

            val textureMem = TextureMemoryTracker.snapshot()
            appendLine()
            appendLine("Texture Memory (In-Flight):")
            appendLine("  Native Heap: ${formatBytes(textureMem.nativeHeapBytes)}")
            appendLine("  Mmapped:     ${formatBytes(textureMem.mmappedBytes)}")
            appendLine("  GPU (Est):   ${formatBytes(textureMem.gpuBytes)}")
            appendLine("  Total:       ${formatBytes(textureMem.totalBytes)}")
            appendLine("  Live Tex:    ${textureMem.liveTextures}")

        }.onFailure { appendLine("Cache statistics unavailable: ${it.message}") }
        appendLine()
    }

    private fun formatBytes(bytes: Long): String {
        val mb = bytes / (1024.0 * 1024.0)
        return String.format("%.2f MB", mb)
    }
}

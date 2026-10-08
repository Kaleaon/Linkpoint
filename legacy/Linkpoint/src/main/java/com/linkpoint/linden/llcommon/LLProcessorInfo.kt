package com.linkpoint.linden.llcommon

enum class CpuFeature { SSE, SSE2, SSE3, SSSE3, SSE4_1, SSE4_2, AVX, AVX2, FMA }

object LLProcessorInfo {
    private val detectedFeatures: Set<CpuFeature> = detectFeatures()

    private fun detectFeatures(): Set<CpuFeature> {
        val features = mutableSetOf<CpuFeature>()
        val arch = System.getProperty("os.arch")?.lowercase() ?: ""
        if (arch.contains("amd64") || arch.contains("x86_64") || arch.contains("x86")) {
            features += CpuFeature.SSE
            features += CpuFeature.SSE2
        }
        return features
    }

    fun hasFeature(feature: CpuFeature): Boolean = feature in detectedFeatures
    fun getFeatures(): Set<CpuFeature> = detectedFeatures
    fun getProcessorCount(): Int = Runtime.getRuntime().availableProcessors()
    fun getArchitecture(): String = System.getProperty("os.arch") ?: "unknown"
}

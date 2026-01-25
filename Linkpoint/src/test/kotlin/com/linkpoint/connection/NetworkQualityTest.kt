package com.linkpoint.connection

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for network quality detection and measurement logic
 * These tests verify latency calculation, quality classification, and timeout adaptation
 */
class NetworkQualityTest {

    companion object {
        // Network quality thresholds (in milliseconds)
        const val LATENCY_EXCELLENT = 50
        const val LATENCY_GOOD = 150
        const val LATENCY_FAIR = 300
        const val LATENCY_POOR = 1000
        
        // Timeout multipliers based on network quality
        const val TIMEOUT_MULTIPLIER_EXCELLENT = 1.0
        const val TIMEOUT_MULTIPLIER_GOOD = 1.5
        const val TIMEOUT_MULTIPLIER_FAIR = 2.0
        const val TIMEOUT_MULTIPLIER_POOR = 3.0
    }

    @Test
    fun `test latency averaging with valid samples`() {
        val samples = listOf(100L, 120L, 110L, 130L, 90L)
        val average = samples.average().toLong()
        
        assertEquals("Average should be 110ms", 110L, average)
    }

    @Test
    fun `test latency averaging ignores outliers`() {
        // With one extreme outlier
        val samplesWithOutlier = listOf(100L, 120L, 5000L, 110L, 130L)
        
        // Filter outliers (> 3x median)
        val sorted = samplesWithOutlier.sorted()
        val median = sorted[sorted.size / 2]
        val filtered = samplesWithOutlier.filter { it <= median * 3 }
        val average = filtered.average().toLong()
        
        assertTrue("Average without outlier should be under 200ms", average < 200)
    }

    @Test
    fun `test network quality classification - EXCELLENT`() {
        val quality = classifyQuality(30)
        assertEquals("30ms latency should be EXCELLENT", NetworkQuality.EXCELLENT, quality)
    }

    @Test
    fun `test network quality classification - GOOD`() {
        val quality = classifyQuality(100)
        assertEquals("100ms latency should be GOOD", NetworkQuality.GOOD, quality)
    }

    @Test
    fun `test network quality classification - FAIR`() {
        val quality = classifyQuality(200)
        assertEquals("200ms latency should be FAIR", NetworkQuality.FAIR, quality)
    }

    @Test
    fun `test network quality classification - POOR`() {
        val quality = classifyQuality(500)
        assertEquals("500ms latency should be POOR", NetworkQuality.POOR, quality)
    }

    @Test
    fun `test timeout multiplier for EXCELLENT quality`() {
        val baseTimeout = 3000L
        val multiplier = getTimeoutMultiplier(NetworkQuality.EXCELLENT)
        val adjustedTimeout = (baseTimeout * multiplier).toLong()
        
        assertEquals("EXCELLENT quality should use 1x timeout", 3000L, adjustedTimeout)
    }

    @Test
    fun `test timeout multiplier for POOR quality`() {
        val baseTimeout = 3000L
        val multiplier = getTimeoutMultiplier(NetworkQuality.POOR)
        val adjustedTimeout = (baseTimeout * multiplier).toLong()
        
        assertEquals("POOR quality should use 3x timeout", 9000L, adjustedTimeout)
    }

    @Test
    fun `test jitter calculation`() {
        val samples = listOf(100L, 150L, 80L, 200L, 120L)
        val average = samples.average()
        
        // Calculate jitter (average deviation from mean)
        val jitter = samples.map { kotlin.math.abs(it - average) }.average().toLong()
        
        assertTrue("Jitter should be positive", jitter > 0)
        assertTrue("Jitter should be less than max-min range", jitter < 200 - 80)
    }

    @Test
    fun `test packet loss estimation from ACK ratio`() {
        val packetsSent = 100
        val acksReceived = 95
        
        val lossRate = (packetsSent - acksReceived).toDouble() / packetsSent * 100
        
        assertEquals("Loss rate should be 5%", 5.0, lossRate, 0.01)
    }

    @Test
    fun `test bandwidth estimation from throughput`() {
        val bytesTransferred = 1024 * 1024L // 1 MB
        val durationMs = 1000L // 1 second
        
        // 1 MB/s = 8 Mbps = 8192 kbps
        // But 1024 * 1024 = 1,048,576 bytes
        // 1,048,576 * 8 = 8,388,608 bits
        // 8,388,608 / 1000 = 8,388.608 kbps ≈ 8389 kbps
        val bitsTransferred = bytesTransferred * 8
        val bandwidthKbps = bitsTransferred / durationMs // bits per ms = kbps
        
        // 1024 * 1024 * 8 / 1000 = 8388.608, truncates to 8388
        assertEquals("Bandwidth should be ~8388 kbps (1 MiB/s)", 8388L, bandwidthKbps)
    }

    @Test
    fun `test mobile network detection affects timeout strategy`() {
        // WiFi: use standard timeouts
        val wifiTimeout = calculateAdaptiveTimeout(NetworkType.WIFI, 100)
        
        // Cellular: use longer timeouts
        val cellularTimeout = calculateAdaptiveTimeout(NetworkType.CELLULAR, 100)
        
        assertTrue("Cellular timeout should be longer than WiFi", cellularTimeout > wifiTimeout)
    }

    @Test
    fun `test connection quality score calculation`() {
        // Score from 0-100 based on latency, jitter, and packet loss
        
        // Excellent connection
        val excellentScore = calculateQualityScore(latencyMs = 30, jitterMs = 5, packetLossPercent = 0.0)
        assertTrue("Excellent connection should score > 90", excellentScore > 90)
        
        // Poor connection
        val poorScore = calculateQualityScore(latencyMs = 500, jitterMs = 100, packetLossPercent = 5.0)
        assertTrue("Poor connection should score < 50", poorScore < 50)
    }

    @Test
    fun `test keep-alive interval based on network type`() {
        // WiFi can use longer intervals
        val wifiInterval = getKeepAliveInterval(NetworkType.WIFI)
        
        // Mobile needs shorter intervals due to NAT
        val mobileInterval = getKeepAliveInterval(NetworkType.CELLULAR)
        
        assertTrue("Mobile interval should be shorter than WiFi", mobileInterval < wifiInterval)
        assertTrue("Mobile interval should be <= 5 seconds", mobileInterval <= 5000)
    }

    @Test
    fun `test connection stability detection`() {
        // Stable: consistent latency, low packet loss
        val stableHistory = listOf(100L, 105L, 98L, 102L, 101L)
        val isStable = isConnectionStable(stableHistory, packetLossPercent = 0.1)
        assertTrue("Should detect stable connection", isStable)
        
        // Unstable: high variance in latency
        val unstableHistory = listOf(100L, 500L, 50L, 800L, 200L)
        val isUnstable = isConnectionStable(unstableHistory, packetLossPercent = 2.0)
        assertFalse("Should detect unstable connection", isUnstable)
    }

    @Test
    fun `test timeout should not exceed maximum`() {
        val maxTimeout = 30000L
        val calculatedTimeout = calculateAdaptiveTimeout(NetworkType.CELLULAR, 10000) // Very high latency
        
        assertTrue("Timeout should be capped at max", calculatedTimeout <= maxTimeout)
    }

    @Test
    fun `test timeout should not be below minimum`() {
        val minTimeout = 1000L
        val calculatedTimeout = calculateAdaptiveTimeout(NetworkType.WIFI, 5) // Very low latency
        
        assertTrue("Timeout should be at least min", calculatedTimeout >= minTimeout)
    }

    // ============================================================
    // Helper enums and functions
    // ============================================================

    enum class NetworkQuality {
        EXCELLENT, GOOD, FAIR, POOR
    }

    enum class NetworkType {
        WIFI, CELLULAR, ETHERNET, UNKNOWN
    }

    private fun classifyQuality(latencyMs: Int): NetworkQuality {
        return when {
            latencyMs < LATENCY_EXCELLENT -> NetworkQuality.EXCELLENT
            latencyMs < LATENCY_GOOD -> NetworkQuality.GOOD
            latencyMs < LATENCY_FAIR -> NetworkQuality.FAIR
            else -> NetworkQuality.POOR
        }
    }

    private fun getTimeoutMultiplier(quality: NetworkQuality): Double {
        return when (quality) {
            NetworkQuality.EXCELLENT -> TIMEOUT_MULTIPLIER_EXCELLENT
            NetworkQuality.GOOD -> TIMEOUT_MULTIPLIER_GOOD
            NetworkQuality.FAIR -> TIMEOUT_MULTIPLIER_FAIR
            NetworkQuality.POOR -> TIMEOUT_MULTIPLIER_POOR
        }
    }

    private fun calculateAdaptiveTimeout(networkType: NetworkType, latencyMs: Int): Long {
        val baseTimeout = 3000L
        val minTimeout = 1000L
        val maxTimeout = 30000L
        
        // Network type multiplier
        val networkMultiplier = when (networkType) {
            NetworkType.WIFI -> 1.0
            NetworkType.ETHERNET -> 1.0
            NetworkType.CELLULAR -> 2.0 // Mobile networks need longer timeouts
            NetworkType.UNKNOWN -> 1.5
        }
        
        // Quality-based multiplier
        val qualityMultiplier = getTimeoutMultiplier(classifyQuality(latencyMs))
        
        val calculated = (baseTimeout * networkMultiplier * qualityMultiplier + latencyMs * 2).toLong()
        
        return calculated.coerceIn(minTimeout, maxTimeout)
    }

    private fun calculateQualityScore(latencyMs: Int, jitterMs: Int, packetLossPercent: Double): Int {
        // Weight factors
        val latencyWeight = 0.4
        val jitterWeight = 0.3
        val lossWeight = 0.3
        
        // Normalize each metric to 0-100
        val latencyScore = (100 - (latencyMs / 5).coerceAtMost(100)).coerceAtLeast(0)
        val jitterScore = (100 - (jitterMs / 2).coerceAtMost(100)).coerceAtLeast(0)
        val lossScore = (100 - (packetLossPercent * 10).toInt().coerceAtMost(100)).coerceAtLeast(0)
        
        return ((latencyScore * latencyWeight) + (jitterScore * jitterWeight) + (lossScore * lossWeight)).toInt()
    }

    private fun getKeepAliveInterval(networkType: NetworkType): Long {
        return when (networkType) {
            NetworkType.WIFI -> 15000L      // 15 seconds for WiFi
            NetworkType.ETHERNET -> 20000L   // 20 seconds for Ethernet
            NetworkType.CELLULAR -> 5000L    // 5 seconds for cellular (NAT issues)
            NetworkType.UNKNOWN -> 10000L    // 10 seconds default
        }
    }

    private fun isConnectionStable(latencyHistory: List<Long>, packetLossPercent: Double): Boolean {
        if (latencyHistory.size < 3) return true // Not enough data
        
        val average = latencyHistory.average()
        val variance = latencyHistory.map { (it - average) * (it - average) }.average()
        val standardDeviation = kotlin.math.sqrt(variance)
        
        // Connection is stable if:
        // - Standard deviation is less than 50% of average
        // - Packet loss is below 1%
        val coefficientOfVariation = standardDeviation / average
        
        return coefficientOfVariation < 0.5 && packetLossPercent < 1.0
    }
}

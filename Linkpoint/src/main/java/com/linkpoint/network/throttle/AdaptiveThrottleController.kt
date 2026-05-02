package com.linkpoint.network.throttle

import android.util.Log
import com.linkpoint.network.SecondLifeProtocol
import com.linkpoint.protocol.messages.UDPConnectionFixed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AdaptiveThrottleController(
    private val scope: CoroutineScope,
    private val protocol: SecondLifeProtocol,
    private val udpConnection: UDPConnectionFixed
) {
    private enum class ThrottleBand { HIGH, LOW }
    @Volatile private var currentThrottleBand: ThrottleBand? = null
    @Volatile private var started = false

    fun apply(force: Boolean = false) {
        if (!udpConnection.isConnected.value) return
        val q = try { protocol.qualityManager.quality.value } catch (_: Exception) { null }
        val targetBand = when (q) {
            com.linkpoint.network.core.ConnectionQualityManager.Quality.EXCELLENT,
            com.linkpoint.network.core.ConnectionQualityManager.Quality.GOOD -> ThrottleBand.HIGH
            else -> ThrottleBand.LOW
        }
        if (!force && targetBand == currentThrottleBand) return
        try {
            if (targetBand == ThrottleBand.LOW) {
                udpConnection.sendAgentThrottle(50_000f,50_000f,5_000f,5_000f,100_000f,50_000f,50_000f)
            } else udpConnection.sendAgentThrottle()
            currentThrottleBand = targetBand
        } catch (e: Exception) {
            Log.w("AdaptiveThrottleController", "apply failed: ${e.message}")
        }
    }

    fun ensureObserver() {
        if (started) return
        started = true
        scope.launch {
            protocol.qualityManager.quality.collect { apply(false) }
        }
    }
}

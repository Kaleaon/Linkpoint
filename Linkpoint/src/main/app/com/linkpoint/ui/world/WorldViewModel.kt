package com.linkpoint.ui.world

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class FrameMetrics(
    val fps: Float = 0f,
    val frameTimeMs: Float = 0f
) {
    val fpsFormatted: String = if (fps <= 0f) "--" else "%.1f".format(fps)
    val frameTimeFormatted: String = if (frameTimeMs <= 0f) "--" else "%.2f ms".format(frameTimeMs)
}

class WorldViewModel : ViewModel() {

    private val _frameMetrics = MutableStateFlow(FrameMetrics())
    val frameMetrics: StateFlow<FrameMetrics> = _frameMetrics.asStateFlow()

    fun updateMetrics(fps: Float, frameTimeMs: Float) {
        _frameMetrics.value = FrameMetrics(fps, frameTimeMs)
    }
}

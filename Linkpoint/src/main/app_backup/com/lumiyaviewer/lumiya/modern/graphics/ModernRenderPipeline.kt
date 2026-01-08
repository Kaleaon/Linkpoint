package com.lumiyaviewer.lumiya.modern.graphics

class ModernRenderPipeline {
    class RenderParams {
        val modelMatrix = FloatArray(16)
        val viewMatrix = FloatArray(16)
        val projectionMatrix = FloatArray(16)
    }

    fun initialize(): Boolean {
        return true
    }

    fun renderFrame(params: RenderParams) {
        // Stub
    }

    fun isModernPipelineAvailable(): Boolean {
        return true
    }

    fun cleanup() {
        // Stub
    }
}

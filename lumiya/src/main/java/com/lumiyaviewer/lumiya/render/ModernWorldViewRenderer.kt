package com.lumiyaviewer.lumiya.render

data class ModernWorldViewRenderer(
    var renderContext: ModernRenderContext,
    var initialized: Boolean = false,
    var surfaceWidth: Int = 0,
    var surfaceHeight: Int = 0,
)

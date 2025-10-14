package com.lumiyaviewer.lumiya.res.terrain

data class TerrainTextureCache(
    var layerNeededMask: Int = 0,
    var layerReadyMask: Int = 0,
) {
    companion object {
        const val TextureResolution = 256
    }
}

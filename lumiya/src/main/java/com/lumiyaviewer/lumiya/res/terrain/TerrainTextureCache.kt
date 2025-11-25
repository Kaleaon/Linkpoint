package com.lumiyaviewer.lumiya.res.terrain

import com.lumiyaviewer.lumiya.res.ResourceConsumer
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchInfo

class TerrainTextureCache {
    var layerNeededMask: Int = 0
    var layerReadyMask: Int = 0

    companion object {
        const val TextureResolution = 256
    }

    fun CancelRequest(consumer: ResourceConsumer) {
        // Stub
    }

    fun RequestResource(params: TerrainPatchInfo, consumer: ResourceConsumer) {
        // Stub
    }
}

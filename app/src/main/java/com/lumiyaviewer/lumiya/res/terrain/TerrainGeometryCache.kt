package com.lumiyaviewer.lumiya.res.terrain
import java.util.*

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.render.terrain.TerrainPatchGeometry
import com.lumiyaviewer.lumiya.res.ResourceManager
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache
import com.lumiyaviewer.lumiya.res.ResourceRequest
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchHeightMap

class TerrainGeometryCache : ResourceMemoryCache<TerrainPatchHeightMap, TerrainPatchGeometry> {

    private class TerrainGeometryRequest : ResourceRequest<TerrainPatchHeightMap, TerrainPatchGeometry> : Runnable {
        TerrainGeometryRequest(TerrainPatchHeightMap terrainPatchHeightMap, ResourceManager<TerrainPatchHeightMap, TerrainPatchGeometry> resourceManager) {
            super(terrainPatchHeightMap, resourceManager)
        }

        fun cancelRequest(): Unit {
            PrimComputeExecutor.getInstance().remove(this)
            super.cancelRequest()
        }

        fun execute(): Unit {
            PrimComputeExecutor.getInstance().execute(this)
        }

        fun run(): Unit {
            try {
                completeRequest(TerrainPatchGeometry((TerrainPatchHeightMap) getParams()))
            } catch (Exception e) {
                Debug.Warning(e)
                completeRequest(null)
            }
        }
    }

    /* access modifiers changed from: protected */
    fun CreateNewRequest(terrainPatchHeightMap: TerrainPatchHeightMap, resourceManager: TerrainPatchGeometry>): ResourceRequest<TerrainPatchHeightMap, TerrainPatchGeometry> {
        return TerrainGeometryRequest(terrainPatchHeightMap, resourceManager)
    }
}

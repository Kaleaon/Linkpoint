package com.linkpoint.res.terrain
import java.util.*

import com.linkpoint.Debug
import com.linkpoint.render.terrain.TerrainPatchGeometry
import com.linkpoint.res.ResourceManager
import com.linkpoint.res.ResourceMemoryCache
import com.linkpoint.res.ResourceRequest
import com.linkpoint.res.executors.PrimComputeExecutor
import com.linkpoint.slproto.terrain.TerrainPatchHeightMap

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

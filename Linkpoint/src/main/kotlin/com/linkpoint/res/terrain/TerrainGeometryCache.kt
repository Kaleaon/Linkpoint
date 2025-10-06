package com.linkpoint.res.terrain
import java.util.*

import com.linkpoint.Debug
import com.linkpoint.render.terrain.TerrainPatchGeometry
import com.linkpoint.res.ResourceManager
import com.linkpoint.res.ResourceMemoryCache
import com.linkpoint.res.ResourceRequest
import com.linkpoint.res.executors.PrimComputeExecutor
import com.linkpoint.slproto.terrain.TerrainPatchHeightMap

class TerrainGeometryCache : ResourceMemoryCache()<TerrainPatchHeightMap, TerrainPatchGeometry> {

    @JvmStatic
private class TerrainGeometryRequest : ResourceRequest()<TerrainPatchHeightMap, TerrainPatchGeometry> : Runnable {
        public TerrainGeometryRequest(TerrainPatchHeightMap terrainPatchHeightMap, ResourceManager<TerrainPatchHeightMap, TerrainPatchGeometry> resourceManager) {
            super(terrainPatchHeightMap, resourceManager)
        }

        fun cancelRequest() {
            PrimComputeExecutor.getInstance().remove(this)
            super.cancelRequest()
        }

        fun execute() {
            PrimComputeExecutor.getInstance().execute(this)
        }

        fun run() {
            try {
                completeRequest(TerrainPatchGeometry((TerrainPatchHeightMap) getParams()))
            } catch (Exception e) {
                Debug.Warning(e)
                completeRequest(null)
            }
        }
    }

    /* access modifiers changed from: protected */
    public ResourceRequest<TerrainPatchHeightMap, TerrainPatchGeometry> CreateNewRequest(TerrainPatchHeightMap terrainPatchHeightMap, ResourceManager<TerrainPatchHeightMap, TerrainPatchGeometry> resourceManager) {
        return TerrainGeometryRequest(terrainPatchHeightMap, resourceManager)
    }
}

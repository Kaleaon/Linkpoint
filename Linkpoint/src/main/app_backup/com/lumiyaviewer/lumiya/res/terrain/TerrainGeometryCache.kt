package com.lumiyaviewer.lumiya.res.terrain

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.render.terrain.TerrainPatchGeometry
import com.lumiyaviewer.lumiya.res.ResourceManager
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache
import com.lumiyaviewer.lumiya.res.ResourceRequest
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchHeightMap
import com.lumiyaviewer.lumiya.memory.MemoryManager

class TerrainGeometryCache(memoryManager: MemoryManager) : ResourceMemoryCache<TerrainPatchHeightMap, TerrainPatchGeometry>(memoryManager) {

    companion object {
        private var instance: TerrainGeometryCache? = null
        
        fun getInstance(): TerrainGeometryCache {
            if (instance == null) {
                instance = TerrainGeometryCache(MemoryManager())
            }
            return instance!!
        }
    }

    private inner class TerrainGeometryRequest(
        params: TerrainPatchHeightMap,
        manager: ResourceManager<TerrainPatchHeightMap, TerrainPatchGeometry>
    ) : ResourceRequest<TerrainPatchHeightMap, TerrainPatchGeometry>(params, manager), Runnable {
        
        override fun cancelRequest() {
            PrimComputeExecutor.getInstance().remove(this)
            super.cancelRequest()
        }

        override fun execute() {
            PrimComputeExecutor.getInstance().execute(this)
        }

        override fun run() {
            try {
                completeRequest(TerrainPatchGeometry(getParams()))
            } catch (e: Exception) {
                Debug.Warning(e)
                completeRequest(null)
            }
        }
    }

    override fun CreateNewRequest(
        params: TerrainPatchHeightMap,
        manager: ResourceManager<TerrainPatchHeightMap, TerrainPatchGeometry>
    ): ResourceRequest<TerrainPatchHeightMap, TerrainPatchGeometry> {
        return TerrainGeometryRequest(params, manager)
    }
}

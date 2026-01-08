package com.lumiyaviewer.lumiya.res.geometry

import com.lumiyaviewer.lumiya.memory.MemoryManager
import com.lumiyaviewer.lumiya.render.drawable.DrawableGeometry
import com.lumiyaviewer.lumiya.res.ResourceConsumer
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache
import com.lumiyaviewer.lumiya.res.ResourceManager
import com.lumiyaviewer.lumiya.res.ResourceRequest
import com.lumiyaviewer.lumiya.slproto.prims.PrimDrawParams

class PrimCache(memoryManager: MemoryManager) : ResourceMemoryCache<PrimDrawParams, DrawableGeometry>(memoryManager) {
    
    companion object {
        private var instance: PrimCache? = null
        
        fun getInstance(): PrimCache {
            if (instance == null) {
                instance = PrimCache(MemoryManager()) // Stub
            }
            return instance!!
        }
    }

    override fun CreateNewRequest(
        params: PrimDrawParams,
        manager: ResourceManager<PrimDrawParams, DrawableGeometry>
    ): ResourceRequest<PrimDrawParams, DrawableGeometry> {
        return PrimRequest(params, manager)
    }
    
    override fun RequestResource(params: PrimDrawParams, consumer: ResourceConsumer) {
        // Stub
    }

    private inner class PrimRequest(
        params: PrimDrawParams,
        manager: ResourceManager<PrimDrawParams, DrawableGeometry>
    ) : ResourceRequest<PrimDrawParams, DrawableGeometry>(params, manager) {
        
        override fun execute() {
            // Stub
            completeRequest(null)
        }
    }
}

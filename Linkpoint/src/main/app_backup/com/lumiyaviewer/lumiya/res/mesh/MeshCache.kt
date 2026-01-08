package com.lumiyaviewer.lumiya.res.mesh

import com.lumiyaviewer.lumiya.memory.MemoryManager
import com.lumiyaviewer.lumiya.res.ResourceConsumer
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache
import com.lumiyaviewer.lumiya.res.ResourceManager
import com.lumiyaviewer.lumiya.res.ResourceRequest
import java.util.UUID

class MeshCache(memoryManager: MemoryManager) : ResourceMemoryCache<MeshCache.MeshParams, Any>(memoryManager) {
    
    data class MeshParams(
        val uuid: UUID,
        val lod: Int
    )

    companion object {
        private var instance: MeshCache? = null
        
        fun getInstance(): MeshCache {
            if (instance == null) {
                instance = MeshCache(MemoryManager())
            }
            return instance!!
        }
        
        @JvmStatic
        fun onCacheDirChanged() {
            // Stub
        }
    }

    override fun CreateNewRequest(
        params: MeshParams,
        manager: ResourceManager<MeshParams, Any>
    ): ResourceRequest<MeshParams, Any> {
        return MeshRequest(params, manager)
    }
    
    override fun RequestResource(params: MeshParams, consumer: ResourceConsumer) {
        // Stub
    }

    private inner class MeshRequest(
        params: MeshParams,
        manager: ResourceManager<MeshParams, Any>
    ) : ResourceRequest<MeshParams, Any>(params, manager) {
        
        override fun execute() {
            // Stub
            completeRequest(null)
        }
    }
}

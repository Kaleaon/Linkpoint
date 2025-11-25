package com.lumiyaviewer.lumiya.res.anim

import com.lumiyaviewer.lumiya.memory.MemoryManager
import com.lumiyaviewer.lumiya.res.ResourceConsumer
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache
import java.util.UUID

class AnimationCache(memoryManager: MemoryManager) : ResourceMemoryCache<UUID, Any>(memoryManager) {
    
    companion object {
        private var instance: AnimationCache? = null
        
        fun getInstance(): AnimationCache {
            if (instance == null) {
                instance = AnimationCache(MemoryManager())
            }
            return instance!!
        }
    }

    override fun RequestResource(params: UUID, consumer: ResourceConsumer) {
        // Stub
    }
}

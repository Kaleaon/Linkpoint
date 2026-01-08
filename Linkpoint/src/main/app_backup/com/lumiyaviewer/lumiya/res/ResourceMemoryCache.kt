package com.lumiyaviewer.lumiya.res

import com.lumiyaviewer.lumiya.memory.MemoryManager

open class ResourceMemoryCache<Params, Resource>(memoryManager: MemoryManager) {
    open fun CreateNewRequest(params: Params, manager: ResourceManager<Params, Resource>): ResourceRequest<Params, Resource> {
        return ResourceRequest(params, manager)
    }
    
    open fun CancelRequest(consumer: ResourceConsumer) {
        // Stub
    }
    
    open fun RequestResource(params: Params, consumer: ResourceConsumer) {
        // Stub
    }
}

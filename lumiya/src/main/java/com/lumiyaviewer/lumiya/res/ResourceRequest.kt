package com.lumiyaviewer.lumiya.res

open class ResourceRequest<Params, Resource>(
    val params: Params,
    val manager: ResourceManager<Params, Resource>
) {
    open fun collectReferences() {}
    open fun completeRequest(resource: Resource?) {}
    open fun intermediateResult(resource: Resource?) {}
    open fun cancelRequest() {}
    open fun execute() {}
    
    fun getParams(): Params = params
}

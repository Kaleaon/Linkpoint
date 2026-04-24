package com.linkpoint.render

import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.capabilities.CapabilityRequester
import com.linkpoint.protocol.llsd.LLSDArray
import com.linkpoint.protocol.llsd.LLSDMap
import com.linkpoint.protocol.llsd.LLSDString
import java.util.UUID

class RenderMaterialsManager(
    private val capabilityRequester: CapabilityRequester
) {
    suspend fun fetchRenderMaterials(objectIds: List<UUID>): LLSDMap? {
        val request = LLSDMap().apply {
            this["object_ids"] = LLSDArray().apply {
                objectIds.forEach { add(LLSDString(it.toString())) }
            }
        }
        return capabilityRequester.request(CapabilityManager.CAP_RENDER_MATERIALS, request) as? LLSDMap
    }
}

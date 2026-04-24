package com.linkpoint.media

import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.capabilities.CapabilityRequester
import com.linkpoint.protocol.llsd.LLSDBoolean
import com.linkpoint.protocol.llsd.LLSDInteger
import com.linkpoint.protocol.llsd.LLSDMap
import com.linkpoint.protocol.llsd.LLSDString
import com.linkpoint.protocol.llsd.LLSDUUID
import java.util.UUID

/**
 * Capability-backed media navigation requests for in-world MOAP surfaces.
 */
class InWorldMediaNavigationService(
    private val capabilityRequester: CapabilityRequester
) {
    suspend fun navigateSurface(objectId: UUID, face: Int, url: String): Boolean {
        val request = LLSDMap().apply {
            this["object_id"] = LLSDUUID(objectId)
            this["face"] = LLSDInteger(face)
            this["current_url"] = LLSDString(url)
            this["navigate"] = LLSDBoolean(true)
        }

        val response = capabilityRequester.request(CapabilityManager.CAP_OBJECT_MEDIA_NAVIGATE, request)
        return response != null
    }
}

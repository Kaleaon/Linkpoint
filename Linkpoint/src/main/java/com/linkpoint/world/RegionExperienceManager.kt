package com.linkpoint.world

import android.util.Log
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.capabilities.CapabilityRequester
import com.linkpoint.protocol.llsd.LLSDBoolean
import com.linkpoint.protocol.llsd.LLSDMap
import com.linkpoint.protocol.llsd.LLSDString
import com.linkpoint.protocol.llsd.LLSDUUID
import java.util.UUID

/**
 * Region-experience capability consumer.
 *
 * Fetches region experience metadata and forwards user permission decisions.
 */
class RegionExperienceManager(
    private val capabilityRequester: CapabilityRequester
) {
    companion object {
        private const val TAG = "RegionExperienceManager"
    }

    suspend fun fetchRegionExperiences(regionId: UUID? = null): LLSDMap? {
        val request = LLSDMap().apply {
            this["op"] = LLSDString("get")
            regionId?.let { this["region_id"] = LLSDUUID(it) }
        }

        val response = capabilityRequester.request(CapabilityManager.CAP_REGION_EXPERIENCE, request) as? LLSDMap
        if (response == null) {
            Log.w(TAG, "RegionExperiences returned non-map response")
        }
        return response
    }

    suspend fun submitExperiencePermission(
        experienceId: UUID,
        permissionToken: String,
        allow: Boolean
    ): Boolean {
        val request = LLSDMap().apply {
            this["op"] = LLSDString("permission")
            this["experience_id"] = LLSDUUID(experienceId)
            this["permission_token"] = LLSDString(permissionToken)
            this["allow"] = LLSDBoolean(allow)
        }

        val response = capabilityRequester.request(CapabilityManager.CAP_REGION_EXPERIENCE, request)
        return response != null
    }
}

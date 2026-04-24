package com.linkpoint.world

import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.capabilities.CapabilityRequester
import com.linkpoint.protocol.llsd.LLSDInteger
import com.linkpoint.protocol.llsd.LLSDMap

class SimulatorFeaturesManager(
    private val capabilityRequester: CapabilityRequester
) {
    suspend fun fetchSimulatorFeatures(regionFlags: Int? = null): SimulatorFeatureProjection? {
        val request = LLSDMap().apply {
            regionFlags?.let { this["region_flags"] = LLSDInteger(it) }
        }
        val response = capabilityRequester.request(CapabilityManager.CAP_SIMULATOR_FEATURES, request) as? LLSDMap
            ?: return null

        val pbrEnabled = response.getBoolean("pbr_materials") ?: false
        val meshUpload = response.getBoolean("mesh_upload_enabled") ?: false
        val projectedAdultRegion = regionFlags?.let { (it and REGION_FLAG_ADULT) != 0 } ?: false

        return SimulatorFeatureProjection(
            raw = response,
            pbrEnabled = pbrEnabled,
            meshUploadEnabled = meshUpload,
            isAdultRegion = projectedAdultRegion
        )
    }

    companion object {
        // Mirrors commonly used region-flag bit for mature/adult projection.
        const val REGION_FLAG_ADULT = 0x2000
    }
}

data class SimulatorFeatureProjection(
    val raw: LLSDMap,
    val pbrEnabled: Boolean,
    val meshUploadEnabled: Boolean,
    val isAdultRegion: Boolean
)

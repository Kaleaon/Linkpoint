package com.linkpoint.world

import android.util.Log
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.capabilities.CapabilityRequester
import com.linkpoint.protocol.llsd.LLSDInteger
import com.linkpoint.protocol.llsd.LLSDMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

/**
 * Fetches and caches the simulator's `SimulatorFeatures` capability response.
 *
 * The `SimulatorFeatures` cap is the simulator's self-description: which
 * protocol-level features it supports (PBR materials, animated objects,
 * bakes-on-mesh, mesh upload), its limits (max attachments, max groups,
 * max texture resolution), and a few region-flavour signals (mature/adult
 * gating).
 *
 * Modern viewers (Linden 2024+, Firestorm) consult this response to feature-
 * gate behaviour rather than assuming the protocol baseline. Until this
 * cycle Linkpoint had a stub manager that read only `pbr_materials` and
 * `mesh_upload_enabled`, was never instantiated, and the projection it
 * produced was discarded — so every region was treated identically. This
 * version parses the full known field set and publishes it as a
 * [StateFlow] so renderer / inventory / UI consumers can bind to it.
 *
 * Field reference: SL wiki "SimulatorFeatures" + observed responses from
 * agni / aditi sims.
 */
class SimulatorFeaturesManager(
    private val capabilityRequester: CapabilityRequester
) {

    private val _features = MutableStateFlow<SimulatorFeatureProjection?>(null)
    /** Latest features for the current region. `null` until first successful fetch. */
    val features: StateFlow<SimulatorFeatureProjection?> = _features

    /**
     * Fetch the SimulatorFeatures cap, parse all known fields, and publish
     * to [features]. Returns the projection for callers that want it
     * directly. Caller is expected to invoke after a [RegionHandshake]
     * (or after caps are confirmed ready), and again on region change.
     */
    suspend fun fetchSimulatorFeatures(regionFlags: Int? = null): SimulatorFeatureProjection? {
        val request = LLSDMap().apply {
            regionFlags?.let { this["region_flags"] = LLSDInteger(it) }
        }
        val response = try {
            capabilityRequester.request(CapabilityManager.CAP_SIMULATOR_FEATURES, request) as? LLSDMap
        } catch (e: Exception) {
            Log.w(TAG, "SimulatorFeatures fetch failed: ${e.message}")
            null
        } ?: run {
            Log.w(TAG, "SimulatorFeatures returned null/non-map response")
            return null
        }

        val projection = parse(response, regionFlags)
        _features.value = projection
        Log.i(TAG, "✓ SimulatorFeatures: $projection")
        return projection
    }

    /**
     * Pure parsing helper, unit-testable. Defensive about field types and
     * absence — old/forked simulators omit many fields.
     */
    private fun parse(response: LLSDMap, regionFlags: Int?): SimulatorFeatureProjection {
        // Animated objects sub-map: present on most modern sims, absent on older ones.
        val animatedObjectsMap = (response["AnimatedObjects"] as? LLSDMap)
        val animatedObjects = animatedObjectsMap?.let {
            AnimatedObjectsLimits(
                allowed = it.getBoolean("AllowedAnimatedObjects") ?: false,
                maxAgentAttachments = it.getInt("MaxAgentAnimatedObjectAttachments") ?: 0,
                maxTris = it.getInt("AnimatedObjectMaxTris") ?: 0
            )
        }

        // Extended-mesh sub-map: holds rigging/skin extension flags.
        val extendedMesh = (response["ExtendedMesh"] as? LLSDMap)?.getInt("flags") ?: 0

        return SimulatorFeatureProjection(
            raw = response,
            // Mesh / asset upload pipeline
            meshRezEnabled = response.getBoolean("MeshRezEnabled") ?: true,
            meshUploadEnabled = response.getBoolean("MeshUploadEnabled")
                ?: response.getBoolean("mesh_upload_enabled") ?: false,
            physicsMaterialsEnabled = response.getBoolean("PhysicsMaterialsEnabled") ?: false,
            maxTextureResolution = response.getInt("MaxTextureResolution") ?: 1024,

            // PBR / materials (Linden 2024+ ModifyMaterial flow)
            pbrEnabled = response.getBoolean("PBRMaterialsEnabled")
                ?: response.getBoolean("pbr_materials")
                ?: false,
            modifyMaterialEnabled = response.getBoolean("ModifyMaterialEnabled") ?: false,
            renderMaterialsCapability = response.getInt("RenderMaterialsCapability") ?: 0,

            // Avatar / wearable limits
            bakesOnMeshEnabled = response.getBoolean("BakesOnMeshEnabled") ?: false,
            maxAttachments = response.getInt("MaxAttachments") ?: 38,
            maxAgentAttachments = response.getInt("MaxAgentAttachments") ?: 38,
            maxAgentGroups = response.getInt("MaxAgentGroups") ?: 42,

            // Chat / voice ranges (sims occasionally tune these for events)
            whisperDistance = response.getInt("WhisperDistance") ?: 10,
            sayDistance = response.getInt("SayDistance") ?: 20,
            shoutDistance = response.getInt("ShoutDistance") ?: 100,

            // Pathfinding / animated content
            dynamicPathfindingEnabled = response.getBoolean("DynamicPathfindingEnabled") ?: false,
            animatedObjects = animatedObjects,
            extendedMeshFlags = extendedMesh,

            // Sim altitude bounds (relevant for vehicles + flight clamping)
            minSimHeight = response.getReal("MinSimHeight") ?: 0.0,
            maxSimHeight = response.getReal("MaxSimHeight") ?: 4096.0,

            // LSL syntax cache key — viewers fetch a separate JSON when this UUID changes.
            // Sims encode it as either an LLSDUUID or a string, so accept both.
            lslSyntaxId = response.getUUID("LSLSyntaxId")
                ?: response.getString("LSLSyntaxId")?.let { runCatching { UUID.fromString(it) }.getOrNull() },

            // Voice (modern WebRTC vs legacy Vivox)
            voiceServerType = response.getString("voice_server_type"),
            voiceServerVersion = response.getString("voice-server-version"),

            // Region maturity (projected from region_flags param when SimFeatures doesn't echo it)
            isAdultRegion = regionFlags?.let { (it and REGION_FLAG_ADULT) != 0 } ?: false
        )
    }

    fun reset() {
        _features.value = null
    }

    companion object {
        private const val TAG = "SimulatorFeatures"
        const val REGION_FLAG_ADULT = 0x2000
    }
}

/**
 * Snapshot of a simulator's advertised features. Defaults are conservative
 * (assume modern but not bleeding-edge) so callers can safely consult
 * fields when [SimulatorFeaturesManager.features] is null and treat
 * everything as "best effort".
 */
data class SimulatorFeatureProjection(
    val raw: LLSDMap,
    // Mesh / upload pipeline
    val meshRezEnabled: Boolean,
    val meshUploadEnabled: Boolean,
    val physicsMaterialsEnabled: Boolean,
    val maxTextureResolution: Int,
    // PBR
    val pbrEnabled: Boolean,
    val modifyMaterialEnabled: Boolean,
    val renderMaterialsCapability: Int,
    // Avatar limits
    val bakesOnMeshEnabled: Boolean,
    val maxAttachments: Int,
    val maxAgentAttachments: Int,
    val maxAgentGroups: Int,
    // Chat distances
    val whisperDistance: Int,
    val sayDistance: Int,
    val shoutDistance: Int,
    // Pathfinding / animated objects
    val dynamicPathfindingEnabled: Boolean,
    val animatedObjects: AnimatedObjectsLimits?,
    val extendedMeshFlags: Int,
    // Sim height
    val minSimHeight: Double,
    val maxSimHeight: Double,
    // LSL syntax cache key
    val lslSyntaxId: UUID?,
    // Voice transport
    val voiceServerType: String?,
    val voiceServerVersion: String?,
    // Maturity
    val isAdultRegion: Boolean
) {
    override fun toString(): String = "SimFeat(" +
        "pbr=$pbrEnabled, modifyMat=$modifyMaterialEnabled, " +
        "meshUpload=$meshUploadEnabled, bom=$bakesOnMeshEnabled, " +
        "maxAttach=$maxAttachments, maxGroups=$maxAgentGroups, " +
        "voice=$voiceServerType v$voiceServerVersion, " +
        "lslSyntaxId=$lslSyntaxId, adult=$isAdultRegion)"
}

data class AnimatedObjectsLimits(
    val allowed: Boolean,
    val maxAgentAttachments: Int,
    val maxTris: Int
)

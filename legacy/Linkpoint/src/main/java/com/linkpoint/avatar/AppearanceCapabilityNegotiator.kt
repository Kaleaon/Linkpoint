package com.linkpoint.avatar

import android.util.Log
import com.linkpoint.protocol.capabilities.CapabilityManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Mode of avatar appearance processing determined through region capability negotiation.
 */
enum class AppearanceMode {
    /** Region supports Second Life Server-Side Appearance (SSA) */
    SERVER_SIDE,

    /** Region uses Client-Side Baking (CSB) with local texture compositor engine */
    CLIENT_SIDE_BAKING
}

/**
 * Result of capability negotiation for region entry and appearance updates.
 */
data class CapabilityNegotiationResult(
    val mode: AppearanceMode,
    val resolutionTimeMs: Long,
    val ssaCapUrl: String?,
    val uploadBakedTextureCapUrl: String?,
    val agentPreferencesCapUrl: String?,
    val isBomSupported: Boolean
)

/**
 * Dynamic capability negotiator for region entry and appearance updates.
 *
 * Queries region capabilities (`ServerSideAppearance`, `UpdateAvatarAppearance`,
 * `UploadBakedTexture`, `AgentPreferences`) during seed capability resolution
 * upon region entry and determines within 200ms whether to use SSA or fall back
 * to local Client-Side Baking (CSB) texture compositing.
 */
class AppearanceCapabilityNegotiator(
    private val capabilityManager: CapabilityManager
) {
    companion object {
        private const val TAG = "AppearanceCapabilityNegotiator"

        /** Maximum acceptable negotiation delay per requirement (200ms) */
        const val MAX_NEGOTIATION_LATENCY_MS = 200L
    }

    /**
     * Negotiates and resolves appearance protocol capabilities for the current region.
     *
     * @return [CapabilityNegotiationResult] detailing the resolved mode and capability endpoints.
     */
    suspend fun negotiateCapabilities(): CapabilityNegotiationResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()

        val ssaCap = capabilityManager.getCapability(CapabilityManager.CAP_SERVER_SIDE_APPEARANCE)
            ?: capabilityManager.getCapability(CapabilityManager.CAP_UPDATE_AVATAR_APPEARANCE)
        val uploadBakedCap = capabilityManager.getCapability(CapabilityManager.CAP_UPLOAD_BAKED_TEXTURE)
        val agentPrefCap = capabilityManager.getCapability(CapabilityManager.CAP_AGENT_PREFERENCES)

        val isSsaSupported = !ssaCap.isNullOrBlank()
        val resolutionTimeMs = System.currentTimeMillis() - startTime

        val mode = if (isSsaSupported) {
            AppearanceMode.SERVER_SIDE
        } else {
            AppearanceMode.CLIENT_SIDE_BAKING
        }

        if (resolutionTimeMs > MAX_NEGOTIATION_LATENCY_MS) {
            Log.w(TAG, "Capability negotiation exceeded target threshold: ${resolutionTimeMs}ms > ${MAX_NEGOTIATION_LATENCY_MS}ms")
        } else {
            Log.i(TAG, "Capability negotiation resolved in ${resolutionTimeMs}ms (Mode: $mode, SSA: $isSsaSupported)")
        }

        CapabilityNegotiationResult(
            mode = mode,
            resolutionTimeMs = resolutionTimeMs,
            ssaCapUrl = ssaCap,
            uploadBakedTextureCapUrl = uploadBakedCap,
            agentPreferencesCapUrl = agentPrefCap,
            isBomSupported = uploadBakedCap != null
        )
    }
}

package com.linkpoint.world.profile

import com.linkpoint.world.AvatarProfile
import com.linkpoint.world.ProfileInterests
import java.util.UUID

/**
 * Strategy interface for resolving avatar profiles and display names across
 * various protocols (Capabilities, UDP, OpenSim REST).
 */
interface ProfileResolverStrategy {
    /**
     * Unique identifier for this strategy instance.
     */
    val name: String

    /**
     * Priority tier (lower values denote higher priority, e.g., 1 = Capability, 2 = UDP, 3 = REST).
     */
    val priority: Int

    /**
     * Checks if this strategy is active/available in the current grid session.
     */
    fun isAvailable(): Boolean

    /**
     * Attempts to resolve an avatar profile for [agentId].
     */
    suspend fun getAvatarProfile(agentId: UUID): AvatarProfile?

    /**
     * Attempts to resolve a display name for [agentId].
     */
    suspend fun getDisplayName(agentId: UUID): String?

    /**
     * Attempts to resolve display names in batch for [agentIds].
     */
    suspend fun getDisplayNames(agentIds: List<UUID>): Map<UUID, String>

    /**
     * Attempts to update profile fields for the active agent.
     */
    suspend fun updateProfile(
        aboutText: String? = null,
        firstLifeText: String? = null,
        profileImage: UUID? = null,
        firstLifeImage: UUID? = null,
        interests: ProfileInterests? = null
    ): Boolean
}

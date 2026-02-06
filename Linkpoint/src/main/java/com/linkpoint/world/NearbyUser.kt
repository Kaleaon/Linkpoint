package com.linkpoint.world

import java.util.UUID

/**
 * Represents a nearby avatar/user in the current region
 * Based on reference viewer/Firestorm nearby people tracking
 */
data class NearbyUser(
    val agentId: UUID,
    val name: String,
    val distance: Float,
    val isFriend: Boolean = false,
    val position: FloatArray? = null,
    val profilePictureId: UUID? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NearbyUser

        if (agentId != other.agentId) return false
        if (name != other.name) return false
        if (distance != other.distance) return false
        if (isFriend != other.isFriend) return false
        if (position != null) {
            if (other.position == null) return false
            if (!position.contentEquals(other.position)) return false
        } else if (other.position != null) return false
        if (profilePictureId != other.profilePictureId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = agentId.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + distance.hashCode()
        result = 31 * result + isFriend.hashCode()
        result = 31 * result + (position?.contentHashCode() ?: 0)
        result = 31 * result + (profilePictureId?.hashCode() ?: 0)
        return result
    }
}

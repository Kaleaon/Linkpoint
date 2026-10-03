package com.linkpoint.protocol.core

import java.util.UUID

/**
 * Shared outbound identity required for packet builders.
 *
 * At minimum, agent/session IDs must be non-zero UUIDs. For circuit-routed UDP
 * messages we also require a non-zero circuit code.
 */
data class AgentIdentity(
    val agentId: UUID,
    val sessionId: UUID,
    val regionId: UUID? = null,
    val circuitCode: Int? = null
) {
    companion object {
        val ZERO_UUID: UUID = UUID(0L, 0L)
    }

    fun validate(requireCircuitOrRegion: Boolean = true): String? {
        if (agentId == ZERO_UUID) return "agentId is zero UUID"
        if (sessionId == ZERO_UUID) return "sessionId is zero UUID"
        if (requireCircuitOrRegion) {
            val hasRegion = regionId != null && regionId != ZERO_UUID
            val hasCircuit = circuitCode != null && circuitCode != 0
            if (!hasRegion && !hasCircuit) {
                return "regionId/circuit is missing or invalid"
            }
        }
        return null
    }

    fun requireValid(context: String, requireCircuitOrRegion: Boolean = true): AgentIdentity {
        val error = validate(requireCircuitOrRegion)
        require(error == null) { "$context: invalid AgentIdentity ($error)" }
        return this
    }
}


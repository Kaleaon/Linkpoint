package com.linkpoint.protocol.auth

import java.util.UUID

/**
 * Authentication Reply
 * 
 * Contains the response from successful Second Life grid authentication.
 * Based on Lumiya's SLAuthReply implementation.
 * 
 * Mobile-First Considerations:
 * - Lightweight data structure
 * - Efficient UUID handling
 * - Minimal memory footprint
 */
data class AuthReply(
    val sessionId: UUID,
    val agentId: UUID,
    val circuitCode: Int,
    val simIP: String,
    val simPort: Int,
    val regionX: Int = 0,
    val regionY: Int = 0,
    val regionName: String = "Unknown",
    val lookAt: FloatArray = floatArrayOf(128f, 128f, 20f)
) {
    /**
     * Get the region coordinates as a string
     */
    fun getRegionCoordinates(): String = "$regionX, $regionY"
    
    /**
     * Get the full region name with coordinates
     */
    fun getFullRegionName(): String = "$regionName ($regionX, $regionY)"
    
    /**
     * Validate the reply
     */
    fun validate(): Boolean {
        return sessionId != UUID(0, 0) &&
               agentId != UUID(0, 0) &&
               circuitCode > 0 &&
               simIP.isNotBlank() &&
               simPort > 0
    }
    
    /**
     * Get authentication statistics
     */
    fun getStatistics(): Map<String, Any> {
        return mapOf(
            "sessionId" to sessionId.toString(),
            "agentId" to agentId.toString(),
            "circuitCode" to circuitCode,
            "simIP" to simIP,
            "simPort" to simPort,
            "regionName" to regionName,
            "regionCoordinates" to getRegionCoordinates()
        )
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as AuthReply
        
        if (sessionId != other.sessionId) return false
        if (agentId != other.agentId) return false
        if (circuitCode != other.circuitCode) return false
        if (simIP != other.simIP) return false
        if (simPort != other.simPort) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = sessionId.hashCode()
        result = 31 * result + agentId.hashCode()
        result = 31 * result + circuitCode
        result = 31 * result + simIP.hashCode()
        result = 31 * result + simPort
        return result
    }
}
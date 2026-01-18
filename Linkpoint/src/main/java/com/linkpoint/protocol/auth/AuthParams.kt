package com.linkpoint.protocol.auth

/**
 * Authentication Parameters
 * 
 * Contains all parameters needed for Second Life grid authentication.
 * Based on Lumiya's SLAuthParams implementation.
 * 
 * Mobile-First Considerations:
 * - Minimal data overhead
 * - Efficient serialization
 * - Memory-conscious design
 */
data class AuthParams(
    val firstName: String,
    val lastName: String,
    val password: String,
    val startLocation: String = "home",
    val gridUrl: String = "https://login.agni.lindenlab.com/cgi-bin/login.cgi",
    val viewerChannel: String = "Lumiya",
    val viewerVersion: String = "1.0.0",
    val viewerId: String = "linkpoint-android",
    val mac: String? = null,
    val id0: String? = null
) {
    /**
     * Get the complete username
     */
    fun getUsername(): String = "$firstName $lastName"
    
    /**
     * Get the grid URL
     */
    fun getGridUrl(): String = gridUrl
    
    /**
     * Validate the parameters
     */
    fun validate(): Boolean {
        return firstName.isNotBlank() &&
               lastName.isNotBlank() &&
               password.isNotBlank() &&
               gridUrl.isNotBlank()
    }
    
    /**
     * Get authentication statistics
     */
    fun getStatistics(): Map<String, Any> {
        return mapOf(
            "username" to getUsername(),
            "startLocation" to startLocation,
            "gridUrl" to gridUrl,
            "viewerChannel" to viewerChannel,
            "hasMac" to (mac != null),
            "hasId0" to (id0 != null)
        )
    }
}
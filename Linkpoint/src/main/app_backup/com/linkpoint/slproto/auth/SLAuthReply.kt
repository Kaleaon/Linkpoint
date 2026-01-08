package com.linkpoint.slproto.auth

/**
 * Response from Second Life login authentication
 * 
 * Complete implementation based on Second Life XMLRPC login protocol
 */
data class SLAuthReply(
    val success: Boolean,
    val reason: String? = null,
    val message: String? = null,
    
    // Session information
    val sessionId: String? = null,
    val secureSessionId: String? = null,
    val agentId: String? = null,
    val agentAccess: String? = null,
    
    // Region information
    val simIp: String? = null,
    val simPort: Int? = null,
    val regionX: Int? = null,
    val regionY: Int? = null,
    val seedCapability: String? = null,
    
    // Avatar information
    val firstName: String? = null,
    val lastName: String? = null,
    val startLocation: String? = null,
    val lookAt: String? = null,
    val home: String? = null,
    
    // Circuit information
    val circuitCode: Int? = null,
    
    // Inventory - stored as raw data for now
    val inventoryRoot: List<Map<String, String>>? = null,
    val inventorySkeleton: List<Map<String, String>>? = null,
    val inventoryLibRoot: List<Map<String, String>>? = null,
    val inventoryLibOwner: List<Map<String, String>>? = null,
    val inventorySkel: List<Map<String, String>>? = null,
    
    // Friends/Buddies
    val buddyList: List<Map<String, String>>? = null,
    
    // UI and configuration
    val uiConfig: List<Map<String, String>>? = null,
    val loginFlags: List<Map<String, String>>? = null,
    val globalTextures: List<Map<String, String>>? = null,
    
    // Events and categories
    val eventCategories: List<Map<String, String>>? = null,
    val eventNotifications: List<Map<String, String>>? = null,
    val classifiedCategories: List<Map<String, String>>? = null,
    
    // Limits and settings
    val maxAgentGroups: Int? = null,
    val secondsSinceEpoch: Long? = null
)
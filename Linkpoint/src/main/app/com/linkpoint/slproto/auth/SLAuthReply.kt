package com.linkpoint.slproto.auth

/**
 * Complete response from the Second Life XML-RPC login endpoint.
 */
data class SLAuthReply(
    val success: Boolean,
    val reason: String? = null,
    val message: String? = null,

    // Session
    val sessionId: String? = null,
    val secureSessionId: String? = null,
    val agentId: String? = null,
    val agentAccess: String? = null,

    // Region
    val simIp: String? = null,
    val simPort: Int? = null,
    val regionX: Int? = null,
    val regionY: Int? = null,
    val seedCapability: String? = null,

    // Avatar
    val firstName: String? = null,
    val lastName: String? = null,
    val startLocation: String? = null,
    val lookAt: String? = null,
    val home: String? = null,

    // Circuit
    val circuitCode: Int? = null,

    // Inventory
    val inventoryRoot: List<Map<String, String>>? = null,
    val inventorySkeleton: List<Map<String, String>>? = null,
    val inventoryLibRoot: List<Map<String, String>>? = null,
    val inventoryLibOwner: List<Map<String, String>>? = null,
    val inventorySkel: List<Map<String, String>>? = null,

    // Friends / Config
    val buddyList: List<Map<String, String>>? = null,
    val uiConfig: List<Map<String, String>>? = null,
    val loginFlags: List<Map<String, String>>? = null,
    val globalTextures: List<Map<String, String>>? = null,

    // Categories
    val eventCategories: List<Map<String, String>>? = null,
    val eventNotifications: List<Map<String, String>>? = null,
    val classifiedCategories: List<Map<String, String>>? = null,

    // Limits
    val maxAgentGroups: Int? = null,
    val secondsSinceEpoch: Long? = null
)

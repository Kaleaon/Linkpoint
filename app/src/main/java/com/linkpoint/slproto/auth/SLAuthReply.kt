package com.linkpoint.slproto.auth

/**
 * Response from Second Life login authentication
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
    
    // Inventory
    val inventoryRoot: List<InventoryFolder>? = null,
    val inventorySkeleton: List<InventoryFolder>? = null,
    val inventoryLibRoot: List<InventoryFolder>? = null,
    val inventoryLibOwner: List<InventoryFolder>? = null,
    
    // Additional data
    val circuitCode: Int? = null,
    val udpBlackList: String? = null,
    val maxAgentGroups: Int? = null,
    val agentAppearanceService: String? = null,
    val buddyList: List<BuddyInfo>? = null,
    
    // Grid info
    val homeRegion: String? = null,
    val homePosition: String? = null,
    val homeLookAt: String? = null
) {
    data class InventoryFolder(
        val folderId: String,
        val parentId: String,
        val name: String,
        val typeDefault: Int,
        val version: Int
    )
    
    data class BuddyInfo(
        val buddyId: String,
        val buddyRightsGiven: Int,
        val buddyRightsHas: Int
    )
}
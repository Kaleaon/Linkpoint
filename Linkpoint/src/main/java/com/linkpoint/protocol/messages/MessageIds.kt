package com.linkpoint.protocol.messages

/**
 * Second Life Protocol Message IDs
 * 
 * Message ID encoding (wire format -> internal representation):
 * - High frequency: Single byte (0x01-0xFE) -> stored as-is (1-254)
 * - Medium frequency: 0xFF + byte -> stored as (byte | 0xFF00), e.g., 0xFF06 -> 65286
 * - Low frequency: 0xFF 0xFF + short -> stored as 0xFFFFxxxx as signed Int (negative values)
 *   For example: wire format FF FF 00 03 -> internal value 0xFFFF0003 as Int = -65533
 * 
 * These values match Lumiya's internal representation for compatibility.
 */
object MessageIds {
    // =====================================
    // High Frequency Messages (0x01-0xFE)
    // =====================================
    
    /** StartPingCheck - Simulator initiates ping */
    const val START_PING_CHECK = 1
    
    /** CompletePingCheck - Response to ping */
    const val COMPLETE_PING_CHECK = 2
    
    /** AgentUpdate - Agent position/rotation/flags update */
    const val AGENT_UPDATE = 4
    
    /** LayerData - Terrain, wind, and cloud data */
    const val LAYER_DATA = 11
    
    /** ObjectUpdate - Full object properties */
    const val OBJECT_UPDATE = 12
    
    /** ObjectUpdateCompressed - Compressed object update */
    const val OBJECT_UPDATE_COMPRESSED = 13
    
    /** ImprovedTerseObjectUpdate - Minimal position update */
    const val IMPROVED_TERSE_OBJECT_UPDATE = 15
    
    /** KillObject - Object removed from scene */
    const val KILL_OBJECT = 16
    
    /** AvatarAnimation - Avatar animation state */
    const val AVATAR_ANIMATION = 20
    
    /** PacketAck - Packet acknowledgment (special: 0xFB as signed byte = -5) */
    const val PACKET_ACK = -5

    // =====================================
    // Medium Frequency Messages (0xFF + byte)
    // Format: byte | 65280
    // =====================================
    
    /** CoarseLocationUpdate - Mini-map avatars */
    const val COARSE_LOCATION_UPDATE = 65286 // 0xFF06 -> 6 | 65280

    // =====================================
    // Low Frequency Messages (0xFF 0xFF + short)
    // Format: 0xFFFFxxxx as signed Int (negative)
    // =====================================
    
    /** UseCircuitCode - Establish circuit with simulator */
    const val USE_CIRCUIT_CODE = (0xFFFF0003).toInt()          // Wire: FF FF 00 03
    
    /** RegionHandshake - Simulator sends region info */
    const val REGION_HANDSHAKE = (0xFFFF0094).toInt()          // Wire: FF FF 00 94
    
    /** RegionHandshakeReply - Client acknowledges region */
    const val REGION_HANDSHAKE_REPLY = (0xFFFF0095).toInt()    // Wire: FF FF 00 95
    
    /** AgentThrottle - Set bandwidth throttles */
    const val AGENT_THROTTLE = (0xFFFF0099).toInt()            // Wire: FF FF 00 99
    
    /** ChatFromSimulator - Chat message from simulator */
    const val CHAT_FROM_SIMULATOR = (0xFFFF00A3).toInt()       // Wire: FF FF 00 A3
    
    /** CompleteAgentMovement - Agent fully entered region */
    const val COMPLETE_AGENT_MOVEMENT = (0xFFFF00F9).toInt()   // Wire: FF FF 00 F9
    
    /** AgentMovementComplete - Sim confirms agent movement */
    const val AGENT_MOVEMENT_COMPLETE = (0xFFFF00FA).toInt()   // Wire: FF FF 00 FA
    
    /** LogoutRequest - Client requests logout */
    const val LOGOUT_REQUEST = (0xFFFF00FC).toInt()            // Wire: FF FF 00 FC
    
    /** ImprovedInstantMessage - IM/Group notice/Teleport request */
    const val IMPROVED_INSTANT_MESSAGE = (0xFFFF00FE).toInt()  // Wire: FF FF 00 FE

    // =====================================
    // Sound Messages
    // =====================================
    
    /** SoundTrigger - Sound triggered by script (llTriggerSound) */
    const val SOUND_TRIGGER = (0xFFFF009E).toInt()             // Wire: FF FF 00 9E

    // =====================================
    // Chat Messages
    // =====================================
    
    /** ChatFromViewer - Chat message from client */
    const val CHAT_FROM_VIEWER = (0xFFFF0050).toInt()          // Wire: FF FF 00 50

    // =====================================
    // Agent Messages
    // =====================================
    
    /** AgentAnimation - Start/stop animations */
    const val AGENT_ANIMATION = (0xFFFF0104).toInt()           // Wire: FF FF 01 04
    
    /** AgentSetAppearance - Update avatar appearance */
    const val AGENT_SET_APPEARANCE = (0xFFFF0084).toInt()      // Wire: FF FF 00 84
    
    /** AgentIsNowWearing - Notify what agent is wearing */
    const val AGENT_IS_NOW_WEARING = (0xFFFF0107).toInt()      // Wire: FF FF 01 07
    
    /** AgentRequestSit - Request to sit on object */
    const val AGENT_REQUEST_SIT = (0xFFFF010B).toInt()         // Wire: FF FF 01 0B
    
    /** AgentSit - Confirm sitting */
    const val AGENT_SIT = (0xFFFF010C).toInt()                 // Wire: FF FF 01 0C

    // =====================================
    // Object Messages
    // =====================================
    
    /** ObjectSelect - Select objects */
    const val OBJECT_SELECT = (0xFFFF0070).toInt()             // Wire: FF FF 00 70
    
    /** MultipleObjectUpdate - Update multiple objects */
    const val MULTIPLE_OBJECT_UPDATE = (0xFFFF0073).toInt()    // Wire: FF FF 00 73
    
    /** RezObject - Rez object from inventory */
    const val REZ_OBJECT = (0xFFFF0127).toInt()                // Wire: FF FF 01 27
    
    /** DeRezObject - Take/delete object */
    const val DEREZ_OBJECT = (0xFFFF0128).toInt()              // Wire: FF FF 01 28
    
    /** ObjectDelete - Delete objects */
    const val OBJECT_DELETE = (0xFFFF006D).toInt()             // Wire: FF FF 00 6D
    
    /** ObjectLink - Link objects */
    const val OBJECT_LINK = (0xFFFF006F).toInt()               // Wire: FF FF 00 6F
    
    /** ObjectDelink - Unlink objects */
    const val OBJECT_DELINK = (0xFFFF0071).toInt()             // Wire: FF FF 00 71
    
    /** ObjectName - Set object name */
    const val OBJECT_NAME = (0xFFFF007D).toInt()               // Wire: FF FF 00 7D
    
    /** ObjectDescription - Set object description */
    const val OBJECT_DESCRIPTION = (0xFFFF007E).toInt()        // Wire: FF FF 00 7E
    
    /** ObjectGrab - Grab/touch object */
    const val OBJECT_GRAB = (0xFFFF0082).toInt()               // Wire: FF FF 00 82
    
    /** ObjectDeGrab - Release object */
    const val OBJECT_DEGRAB = (0xFFFF0083).toInt()             // Wire: FF FF 00 83

    // =====================================
    // Inventory Messages
    // =====================================

    /** MoveInventoryItem - Move items between folders */
    const val MOVE_INVENTORY_ITEM = (0xFFFF0077).toInt()       // Wire: FF FF 00 77

    // =====================================
    // Teleport Messages
    // =====================================
    
    /** TeleportLandmarkRequest - Teleport to landmark */
    const val TELEPORT_LANDMARK_REQUEST = (0xFFFF0149).toInt() // Wire: FF FF 01 49
    
    /** TeleportHomeRequest - Teleport home */
    const val TELEPORT_HOME_REQUEST = (0xFFFF0148).toInt()     // Wire: FF FF 01 48
    
    /** TeleportLocationRequest - Teleport to location */
    const val TELEPORT_LOCATION_REQUEST = (0xFFFF0147).toInt() // Wire: FF FF 01 47
    
    /** TeleportLureRequest - Accept teleport lure */
    const val TELEPORT_LURE_REQUEST = (0xFFFF014A).toInt()     // Wire: FF FF 01 4A
    
    /** StartLure - Send teleport offer */
    const val START_LURE = (0xFFFF014B).toInt()                // Wire: FF FF 01 4B

    // =====================================
    // Group Messages
    // =====================================
    
    /** ActivateGroup - Set active group */
    const val ACTIVATE_GROUP = (0xFFFF0162).toInt()            // Wire: FF FF 01 62
    
    /** LeaveGroupRequest - Leave a group */
    const val LEAVE_GROUP_REQUEST = (0xFFFF0163).toInt()       // Wire: FF FF 01 63
    
    /** GroupProfileRequest - Request group profile */
    const val GROUP_PROFILE_REQUEST = (0xFFFF0164).toInt()     // Wire: FF FF 01 64

    // =====================================
    // Friends Messages
    // =====================================
    
    /** TerminateFriendship - Remove friend */
    const val TERMINATE_FRIENDSHIP = (0xFFFF014D).toInt()      // Wire: FF FF 01 4D
    
    /** GrantUserRights - Grant/revoke friend permissions */
    const val GRANT_USER_RIGHTS = (0xFFFF014E).toInt()         // Wire: FF FF 01 4E
    
    /** FindAgent - Find agent location */
    const val FIND_AGENT = (0xFFFF014F).toInt()                // Wire: FF FF 01 4F

    // =====================================
    // Parcel Messages
    // =====================================
    
    /** ParcelBuy - Purchase parcel */
    const val PARCEL_BUY = (0xFFFF0185).toInt()                // Wire: FF FF 01 85
    
    /** ParcelDeedToGroup - Deed parcel to group */
    const val PARCEL_DEED_TO_GROUP = (0xFFFF0186).toInt()      // Wire: FF FF 01 86
    
    /** ParcelRelease - Abandon parcel */
    const val PARCEL_RELEASE = (0xFFFF0188).toInt()            // Wire: FF FF 01 88
    
    /** ParcelPropertiesUpdate - Update parcel properties */
    const val PARCEL_PROPERTIES_UPDATE = (0xFFFF0189).toInt()  // Wire: FF FF 01 89
    
    /** ParcelReturnObjects - Return objects on parcel */
    const val PARCEL_RETURN_OBJECTS = (0xFFFF018A).toInt()     // Wire: FF FF 01 8A
    
    /** ParcelAccessListUpdate - Update parcel access list */
    const val PARCEL_ACCESS_LIST_UPDATE = (0xFFFF018B).toInt() // Wire: FF FF 01 8B

    // =====================================
    // Estate Messages
    // =====================================
    
    /** EstateOwnerMessage - Estate management commands */
    const val ESTATE_OWNER_MESSAGE = (0xFFFF019A).toInt()      // Wire: FF FF 01 9A
    
    /** FreezeUser - Freeze/unfreeze user */
    const val FREEZE_USER = (0xFFFF019B).toInt()               // Wire: FF FF 01 9B

    // =====================================
    // Gesture Messages
    // =====================================

    /** ActivateGestures - Activate a gesture */
    const val ACTIVATE_GESTURES = (0xFFFF0096).toInt()         // Wire: FF FF 00 96

    /** DeactivateGestures - Deactivate a gesture */
    const val DEACTIVATE_GESTURES = (0xFFFF0097).toInt()       // Wire: FF FF 00 97

    // =====================================
    // Inventory Messages
    // =====================================

    /** CopyInventoryItem - Copy item to new folder */
    const val COPY_INVENTORY_ITEM = (0xFFFF0076).toInt()       // Wire: FF FF 00 76
}

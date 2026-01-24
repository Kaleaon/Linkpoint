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
    
    /** ObjectUpdateCached - Server indicates cached object, client must request full data */
    const val OBJECT_UPDATE_CACHED = 14
    
    /** ImprovedTerseObjectUpdate - Minimal position update */
    const val IMPROVED_TERSE_OBJECT_UPDATE = 15
    
    /** KillObject - Object removed from scene */
    const val KILL_OBJECT = 16
    
    /** AvatarAnimation - Avatar animation state */
    const val AVATAR_ANIMATION = 20
    
    /** SoundTrigger - Sound triggered by script (llTriggerSound) */
    const val SOUND_TRIGGER = 29  // Wire: 0x1D (high frequency message)
    
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
    
    /** AgentDataUpdate - Agent data updated (groups, title, active group) */
    const val AGENT_DATA_UPDATE = (0xFFFF0183).toInt()         // Wire: FF FF 01 83 = -65149
    
    /** HealthMessage - Agent health status from simulator */
    const val HEALTH_MESSAGE = (0xFFFF008A).toInt()            // Wire: FF FF 00 8A = -65398

    // =====================================
    // Friends/Online Status Messages
    // =====================================
    
    /** OnlineNotification - Friend came online (via UDP) */
    const val ONLINE_NOTIFICATION = (0xFFFF0142).toInt()       // Wire: FF FF 01 42 = -65214
    
    /** OfflineNotification - Friend went offline (via UDP) */
    const val OFFLINE_NOTIFICATION = (0xFFFF0143).toInt()      // Wire: FF FF 01 43 = -65213
    
    /** ChangeUserRights - Friend permissions changed */
    const val CHANGE_USER_RIGHTS = (0xFFFF0141).toInt()        // Wire: FF FF 01 41 = -65215

    // =====================================
    // Parcel Messages (Overlay)
    // =====================================
    
    /** ParcelOverlay - Parcel boundary data for minimap/rendering */
    const val PARCEL_OVERLAY = (0xFFFF00C4).toInt()            // Wire: FF FF 00 C4 = -65340

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
    
    /** RequestMultipleObjects - Request full object data for cached objects */
    const val REQUEST_MULTIPLE_OBJECTS = (0xFFFF006C).toInt()  // Wire: FF FF 00 6C = -65428
    
    /** ObjectProperties - Server sends object metadata (name, description, owner, etc.) */
    const val OBJECT_PROPERTIES = 65289                        // Wire: FF 09 = 9 | 65280 (medium frequency)

    // =====================================
    // Script Messages
    // =====================================
    
    /** ScriptControlChange - Script control permissions changed */
    const val SCRIPT_CONTROL_CHANGE = (0xFFFF00BD).toInt()     // Wire: FF FF 00 BD = -65347

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
    
    /** TeleportFinish - Teleport completed successfully, connect to new sim */
    const val TELEPORT_FINISH = (0xFFFF0045).toInt()           // Wire: FF FF 00 45 = -65467
    
    /** TeleportFailed - Teleport failed with reason */
    const val TELEPORT_FAILED = (0xFFFF004A).toInt()           // Wire: FF FF 00 4A = -65462
    
    /** TeleportProgress - Teleport status update */
    const val TELEPORT_PROGRESS = (0xFFFF0042).toInt()         // Wire: FF FF 00 42 = -65470
    
    /** TeleportStart - Teleport sequence starting */
    const val TELEPORT_START = (0xFFFF0049).toInt()            // Wire: FF FF 00 49 = -65463

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

    /** UpdateInventoryItem - Update item properties */
    const val UPDATE_INVENTORY_ITEM = (0xFFFF010A).toInt()     // Wire: FF FF 01 0A

    /** CreateInventoryFolder - Create a new inventory folder */
    const val CREATE_INVENTORY_FOLDER = (0xFFFF0111).toInt()   // Wire: FF FF 01 11

    /** RezSingleAttachmentFromInv - Wear object from inventory */
    const val REZ_SINGLE_ATTACHMENT_FROM_INV = (0xFFFF012A).toInt() // Wire: FF FF 01 2A

    // =====================================
    // Alert Messages
    // =====================================
    
    /** AlertMessage - System alert message */
    const val ALERT_MESSAGE = (0xFFFF0086).toInt()              // Wire: FF FF 00 86 = -65402
    
    /** AgentAlertMessage - Agent-specific alert with modal flag */
    const val AGENT_ALERT_MESSAGE = (0xFFFF0087).toInt()        // Wire: FF FF 00 87 = -65401

    // =====================================
    // Simulator/Region Messages
    // =====================================
    
    /** EnableSimulator - Enable connection to neighbor sim */
    const val ENABLE_SIMULATOR = (0xFFFF0097).toInt()           // Wire: FF FF 00 97 = -65385
    
    /** DisableSimulator - Disable connection to neighbor sim */
    const val DISABLE_SIMULATOR = (0xFFFF0098).toInt()          // Wire: FF FF 00 98 = -65384
    
    /** CrossedRegion - Agent crossed into new region (medium frequency) */
    const val CROSSED_REGION = 65287                            // Wire: FF 07 = 7 | 65280 (medium frequency)

    // =====================================
    // Parcel Messages (High Frequency)
    // =====================================
    
    /** ParcelProperties - Full parcel information (high frequency) */
    const val PARCEL_PROPERTIES = 23                            // Wire: 0x17 (high frequency)

    // =====================================
    // PHASE 2: Social & Communication (50 new handlers)
    // =====================================

    // --- Script/Dialog Messages ---
    /** ScriptDialog - LSL script dialog popup */
    const val SCRIPT_DIALOG = (0xFFFF00BE).toInt()              // Wire: FF FF 00 BE = -65346
    
    /** ScriptDialogReply - Reply to script dialog */
    const val SCRIPT_DIALOG_REPLY = (0xFFFF00BF).toInt()        // Wire: FF FF 00 BF = -65345
    
    /** ScriptQuestion - Script permission request */
    const val SCRIPT_QUESTION = (0xFFFF00AC).toInt()            // Wire: FF FF 00 AC = -65348
    
    /** LoadURL - Open URL request from script */
    const val LOAD_URL = (0xFFFF00C6).toInt()                   // Wire: FF FF 00 C6 = -65342
    
    // --- Economy Messages ---
    /** MoneyBalanceReply - L$ balance update */
    const val MONEY_BALANCE_REPLY = (0xFFFF00E2).toInt()        // Wire: FF FF 00 E2 = -65310
    
    /** MoneyBalanceRequest - Request L$ balance */
    const val MONEY_BALANCE_REQUEST = (0xFFFF00E1).toInt()      // Wire: FF FF 00 E1 = -65311
    
    /** EconomyData - Region economy info */
    const val ECONOMY_DATA = (0xFFFF0019).toInt()               // Wire: FF FF 00 19 = -65511
    
    // --- Inventory Messages ---
    /** InventoryDescendents - Folder contents */
    const val INVENTORY_DESCENDENTS = (0xFFFF00F6).toInt()      // Wire: FF FF 00 F6 = -65290
    
    /** FetchInventoryReply - Item details */
    const val FETCH_INVENTORY_REPLY = (0xFFFF00F8).toInt()      // Wire: FF FF 00 F8 = -65288
    
    /** BulkUpdateInventory - Batch inventory update */
    const val BULK_UPDATE_INVENTORY = (0xFFFF00F9).toInt()      // Wire: FF FF 00 F9 = -65287
    
    /** UpdateCreateInventoryItem - Item created notification */
    const val UPDATE_CREATE_INVENTORY_ITEM = (0xFFFF010B).toInt() // Wire: FF FF 01 0B = -65269
    
    /** RemoveInventoryItem - Item removed */
    const val REMOVE_INVENTORY_ITEM = (0xFFFF010E).toInt()      // Wire: FF FF 01 0E = -65266
    
    /** RemoveInventoryFolder - Folder removed */
    const val REMOVE_INVENTORY_FOLDER = (0xFFFF010F).toInt()    // Wire: FF FF 01 0F = -65265
    
    // --- Avatar/Appearance Messages ---
    /** AvatarAppearance - Full avatar appearance */
    const val AVATAR_APPEARANCE = (0xFFFF009E).toInt()          // Wire: FF FF 00 9E = -65378
    
    /** AgentWearablesUpdate - Outfit update */
    const val AGENT_WEARABLES_UPDATE = (0xFFFF0102).toInt()     // Wire: FF FF 01 02 = -65278
    
    /** AgentCachedTexture - Baked texture cache request */
    const val AGENT_CACHED_TEXTURE = (0xFFFF0100).toInt()       // Wire: FF FF 01 00 = -65280
    
    /** AgentCachedTextureResponse - Baked texture cache response */
    const val AGENT_CACHED_TEXTURE_RESPONSE = (0xFFFF0101).toInt() // Wire: FF FF 01 01 = -65279
    
    /** AvatarPropertiesReply - Avatar profile data */
    const val AVATAR_PROPERTIES_REPLY = (0xFFFF00A7).toInt()    // Wire: FF FF 00 A7 = -65369
    
    /** AvatarPropertiesRequest - Request avatar profile */
    const val AVATAR_PROPERTIES_REQUEST = (0xFFFF00A8).toInt()  // Wire: FF FF 00 A8 = -65368
    
    /** AvatarInterestsReply - Avatar interests/picks */
    const val AVATAR_INTERESTS_REPLY = (0xFFFF00A9).toInt()     // Wire: FF FF 00 A9 = -65367
    
    /** AvatarGroupsReply - Avatar group memberships */
    const val AVATAR_GROUPS_REPLY = (0xFFFF00AB).toInt()        // Wire: FF FF 00 AB = -65365
    
    // --- Group Messages ---
    /** GroupProfileReply - Group profile data */
    const val GROUP_PROFILE_REPLY = (0xFFFF0160).toInt()        // Wire: FF FF 01 60 = -65184
    
    /** GroupMembersReply - Group member list */
    const val GROUP_MEMBERS_REPLY = (0xFFFF015F).toInt()        // Wire: FF FF 01 5F = -65185
    
    /** GroupRoleDataReply - Group roles */
    const val GROUP_ROLE_DATA_REPLY = (0xFFFF015C).toInt()      // Wire: FF FF 01 5C = -65188
    
    /** GroupTitlesReply - Group titles */
    const val GROUP_TITLES_REPLY = (0xFFFF015B).toInt()         // Wire: FF FF 01 5B = -65189
    
    /** GroupNoticeAdd - New group notice */
    const val GROUP_NOTICE_ADD = (0xFFFF0035).toInt()           // Wire: FF FF 00 35 = -65483
    
    /** AgentGroupDataUpdate - Agent's group list */
    const val AGENT_GROUP_DATA_UPDATE = (0xFFFF0181).toInt()    // Wire: FF FF 01 81 = -65151
    
    // --- Friends Messages ---
    /** AcceptFriendship - Accept friend request */
    const val ACCEPT_FRIENDSHIP = (0xFFFF0119).toInt()          // Wire: FF FF 01 19 = -65255
    
    /** DeclineFriendship - Decline friend request */
    const val DECLINE_FRIENDSHIP = (0xFFFF011A).toInt()         // Wire: FF FF 01 1A = -65254
    
    /** FormFriendship - Friend request sent */
    const val FORM_FRIENDSHIP = (0xFFFF011B).toInt()            // Wire: FF FF 01 1B = -65253
    
    // --- Map Messages ---
    /** MapBlockReply - Map tile data */
    const val MAP_BLOCK_REPLY = (0xFFFF0195).toInt()            // Wire: FF FF 01 95 = -65131
    
    /** MapItemReply - Map markers/items */
    const val MAP_ITEM_REPLY = (0xFFFF0197).toInt()             // Wire: FF FF 01 97 = -65129
    
    /** MapLayerReply - Map layer data */
    const val MAP_LAYER_REPLY = (0xFFFF0192).toInt()            // Wire: FF FF 01 92 = -65134
    
    // --- Search Messages ---
    /** DirPlacesReply - Places search results */
    const val DIR_PLACES_REPLY = (0xFFFF001F).toInt()           // Wire: FF FF 00 1F = -65505
    
    /** DirPeopleReply - People search results */
    const val DIR_PEOPLE_REPLY = (0xFFFF0020).toInt()           // Wire: FF FF 00 20 = -65504
    
    /** DirGroupsReply - Groups search results */
    const val DIR_GROUPS_REPLY = (0xFFFF0022).toInt()           // Wire: FF FF 00 22 = -65502
    
    /** DirEventsReply - Events search results */
    const val DIR_EVENTS_REPLY = (0xFFFF0021).toInt()           // Wire: FF FF 00 21 = -65503
    
    /** DirLandReply - Land search results */
    const val DIR_LAND_REPLY = (0xFFFF0026).toInt()             // Wire: FF FF 00 26 = -65498
    
    /** DirClassifiedReply - Classified search results */
    const val DIR_CLASSIFIED_REPLY = (0xFFFF0029).toInt()       // Wire: FF FF 00 29 = -65495
    
    // --- Region/Estate Messages ---
    /** RegionInfo - Region settings */
    const val REGION_INFO = (0xFFFF00A2).toInt()                // Wire: FF FF 00 A2 = -65374
    
    /** SimStats - Simulator statistics */
    const val SIM_STATS = (0xFFFF00A4).toInt()                  // Wire: FF FF 00 A4 = -65372
    
    /** EstateCovenantReply - Estate covenant text */
    const val ESTATE_COVENANT_REPLY = (0xFFFF00CC).toInt()      // Wire: FF FF 00 CC = -65332
    
    // --- Parcel Messages ---
    /** ParcelInfoReply - Parcel information */
    const val PARCEL_INFO_REPLY = (0xFFFF0037).toInt()          // Wire: FF FF 00 37 = -65481
    
    /** ParcelAccessListReply - Parcel access list */
    const val PARCEL_ACCESS_LIST_REPLY = (0xFFFF00D8).toInt()   // Wire: FF FF 00 D8 = -65320
    
    /** ParcelDwellReply - Parcel traffic/dwell */
    const val PARCEL_DWELL_REPLY = (0xFFFF00DB).toInt()         // Wire: FF FF 00 DB = -65317
    
    // --- Object Messages ---
    /** ObjectPropertiesFamily - Quick object properties */
    const val OBJECT_PROPERTIES_FAMILY = 65290                  // Wire: FF 0A = 10 | 65280 (medium frequency)
    
    /** RequestObjectPropertiesFamily - Request quick props */
    const val REQUEST_OBJECT_PROPERTIES_FAMILY = 65285          // Wire: FF 05 = 5 | 65280 (medium frequency)
    
    /** ObjectAdd - Create object */
    const val OBJECT_ADD = 65281                                // Wire: FF 01 = 1 | 65280 (medium frequency)
    
    // --- Sound Messages ---
    /** AttachedSound - Sound attached to object */
    const val ATTACHED_SOUND = 65293                            // Wire: FF 0D = 13 | 65280 (medium frequency)
    
    /** AttachedSoundGainChange - Sound volume change */
    const val ATTACHED_SOUND_GAIN_CHANGE = 65294                // Wire: FF 0E = 14 | 65280 (medium frequency)
    
    /** PreloadSound - Preload sound asset */
    const val PRELOAD_SOUND = 65295                             // Wire: FF 0F = 15 | 65280 (medium frequency)
    
    // --- Effect Messages ---
    /** ViewerEffect - Visual effects (beams, particles) */
    const val VIEWER_EFFECT = 65297                             // Wire: FF 11 = 17 | 65280 (medium frequency)
    
    // --- Transfer/Asset Messages ---
    /** TransferInfo - Asset transfer info */
    const val TRANSFER_INFO = (0xFFFF009A).toInt()              // Wire: FF FF 00 9A = -65382
    
    /** TransferPacket - Asset transfer data (high freq) */
    const val TRANSFER_PACKET = 17                              // Wire: 0x11 (high frequency)
    
    /** AbortXfer - Cancel transfer */
    const val ABORT_XFER = (0xFFFF009D).toInt()                 // Wire: FF FF 00 9D = -65379
    
    /** ImageNotInDatabase - Texture not found */
    const val IMAGE_NOT_IN_DATABASE = (0xFFFF0086).toInt()      // Wire: FF FF 00 56 = -65450
    
    // --- Misc Messages ---
    /** MeanCollisionAlert - Physical collision warning */
    const val MEAN_COLLISION_ALERT = (0xFFFF0088).toInt()       // Wire: FF FF 00 88 = -65400
    
    /** AvatarSitResponse - Sit response */
    const val AVATAR_SIT_RESPONSE = 21                          // Wire: 0x15 (high frequency)
    
    /** CameraConstraint - Camera limits */
    const val CAMERA_CONSTRAINT = 22                            // Wire: 0x16 (high frequency)
    
    /** ConfirmEnableSimulator - Confirm neighbor sim */
    const val CONFIRM_ENABLE_SIMULATOR = 65288                  // Wire: FF 08 = 8 | 65280 (medium frequency)
    
    /** SimStatus - Sim status message */
    const val SIM_STATUS = 65292                                // Wire: FF 0C = 12 | 65280 (medium frequency)
    
    /** LogoutReply - Logout confirmation */
    const val LOGOUT_REPLY = (0xFFFF00FD).toInt()               // Wire: FF FF 00 FD = -65283
    
    /** UUIDNameReply - UUID to name lookup result */
    const val UUID_NAME_REPLY = (0xFFFF00EC).toInt()            // Wire: FF FF 00 EC = -65300
    
    /** UUIDGroupNameReply - UUID to group name result */
    const val UUID_GROUP_NAME_REPLY = (0xFFFF00EE).toInt()      // Wire: FF FF 00 EE = -65298
}

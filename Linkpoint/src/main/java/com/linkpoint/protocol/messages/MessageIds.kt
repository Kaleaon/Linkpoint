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

    // =====================================
    // PHASE 3: 100 Additional Message Handlers
    // =====================================

    // --- High Frequency Messages (3-10, 24-28) ---
    /** NeighborList - List of neighboring regions */
    const val NEIGHBOR_LIST = 3                                 // Wire: 0x03 (high frequency)
    
    /** AgentAnimation - Animation control (high freq) */
    const val AGENT_ANIMATION_HF = 5                            // Wire: 0x05 (high frequency)
    
    /** RequestImage - Request texture from sim */
    const val REQUEST_IMAGE = 8                                 // Wire: 0x08 (high frequency)
    
    /** ImageData - Texture header data */
    const val IMAGE_DATA = 9                                    // Wire: 0x09 (high frequency)
    
    /** ImagePacket - Texture data packet */
    const val IMAGE_PACKET = 10                                 // Wire: 0x0A (high frequency)
    
    /** EdgeDataPacket - Region edge data */
    const val EDGE_DATA_PACKET = 24                             // Wire: 0x18 (high frequency)
    
    /** ChildAgentUpdate - Child agent data */
    const val CHILD_AGENT_UPDATE = 25                           // Wire: 0x19 (high frequency)
    
    /** ChildAgentAlive - Child agent keepalive */
    const val CHILD_AGENT_ALIVE = 26                            // Wire: 0x1A (high frequency)
    
    /** ChildAgentPositionUpdate - Child agent position */
    const val CHILD_AGENT_POSITION_UPDATE = 27                  // Wire: 0x1B (high frequency)
    
    /** AtomicPassObject - Cross-sim object pass */
    const val ATOMIC_PASS_OBJECT = 28                           // Wire: 0x1C (high frequency)
    
    /** SendXferPacket - Send transfer data */
    const val SEND_XFER_PACKET = 18                             // Wire: 0x12 (high frequency)
    
    /** ConfirmXferPacket - Confirm transfer receipt */
    const val CONFIRM_XFER_PACKET = 19                          // Wire: 0x13 (high frequency)

    // --- Agent Messages ---
    /** AgentPause - Pause agent updates */
    const val AGENT_PAUSE = (0xFFFF0056).toInt()                // Wire: FF FF 00 56 = -65450
    
    /** AgentResume - Resume agent updates */
    const val AGENT_RESUME = (0xFFFF0057).toInt()               // Wire: FF FF 00 57 = -65449
    
    /** AgentFOV - Agent field of view */
    const val AGENT_FOV = (0xFFFF005A).toInt()                  // Wire: FF FF 00 5A = -65446
    
    /** AgentHeightWidth - Agent viewport size */
    const val AGENT_HEIGHT_WIDTH = (0xFFFF005B).toInt()         // Wire: FF FF 00 5B = -65445
    
    /** AgentQuitCopy - Agent quit notification */
    const val AGENT_QUIT_COPY = (0xFFFF005D).toInt()            // Wire: FF FF 00 5D = -65443
    
    /** AgentDropGroup - Agent dropped from group */
    const val AGENT_DROP_GROUP = (0xFFFF0182).toInt()           // Wire: FF FF 01 82 = -65150
    
    /** AgentWearablesRequest - Request wearables */
    const val AGENT_WEARABLES_REQUEST = (0xFFFF0103).toInt()    // Wire: FF FF 01 03 = -65277
    
    /** AgentDataUpdateRequest - Request agent data */
    const val AGENT_DATA_UPDATE_REQUEST = (0xFFFF0184).toInt()  // Wire: FF FF 01 84 = -65148

    // --- Avatar Messages ---
    /** AvatarTextureUpdate - Avatar texture update */
    const val AVATAR_TEXTURE_UPDATE = (0xFFFF0004).toInt()      // Wire: FF FF 00 04 = -65532
    
    /** AvatarPickerReply - Avatar picker results */
    const val AVATAR_PICKER_REPLY = (0xFFFF001C).toInt()        // Wire: FF FF 00 1C = -65508
    
    /** AvatarPickerRequest - Request avatar picker */
    const val AVATAR_PICKER_REQUEST = (0xFFFF001B).toInt()      // Wire: FF FF 00 1B = -65509
    
    /** AvatarNotesReply - Avatar notes */
    const val AVATAR_NOTES_REPLY = (0xFFFF00B0).toInt()         // Wire: FF FF 00 B0 = -65360
    
    /** AvatarNotesUpdate - Update avatar notes */
    const val AVATAR_NOTES_UPDATE = (0xFFFF00B1).toInt()        // Wire: FF FF 00 B1 = -65359
    
    /** AvatarInterestsUpdate - Update avatar interests */
    const val AVATAR_INTERESTS_UPDATE = (0xFFFF00AF).toInt()    // Wire: FF FF 00 AF = -65361
    
    /** AvatarPropertiesUpdate - Update avatar properties */
    const val AVATAR_PROPERTIES_UPDATE = (0xFFFF00AE).toInt()   // Wire: FF FF 00 AE = -65362
    
    /** AvatarPicksReply - Avatar picks list */
    const val AVATAR_PICKS_REPLY = (0xFFFF00B2).toInt()         // Wire: FF FF 00 B2 = -65358
    
    /** AvatarClassifiedReply - Avatar classified ads */
    const val AVATAR_CLASSIFIED_REPLY = (0xFFFF002A).toInt()    // Wire: FF FF 00 2A = -65494

    // --- Classified Messages ---
    /** ClassifiedInfoReply - Classified ad details */
    const val CLASSIFIED_INFO_REPLY = (0xFFFF002C).toInt()      // Wire: FF FF 00 2C = -65492
    
    /** ClassifiedInfoRequest - Request classified info */
    const val CLASSIFIED_INFO_REQUEST = (0xFFFF002B).toInt()    // Wire: FF FF 00 2B = -65493
    
    /** ClassifiedInfoUpdate - Update classified ad */
    const val CLASSIFIED_INFO_UPDATE = (0xFFFF002D).toInt()     // Wire: FF FF 00 2D = -65491
    
    /** ClassifiedDelete - Delete classified ad */
    const val CLASSIFIED_DELETE = (0xFFFF002E).toInt()          // Wire: FF FF 00 2E = -65490

    // --- Pick Messages ---
    /** PickInfoReply - Pick details */
    const val PICK_INFO_REPLY = (0xFFFF00B8).toInt()            // Wire: FF FF 00 B8 = -65352
    
    /** PickInfoUpdate - Update pick */
    const val PICK_INFO_UPDATE = (0xFFFF00B9).toInt()           // Wire: FF FF 00 B9 = -65351
    
    /** PickDelete - Delete pick */
    const val PICK_DELETE = (0xFFFF00BA).toInt()                // Wire: FF FF 00 BA = -65350

    // --- Event Messages ---
    /** EventInfoReply - Event details */
    const val EVENT_INFO_REPLY = (0xFFFF00B4).toInt()           // Wire: FF FF 00 B4 = -65356
    
    /** EventInfoRequest - Request event info */
    const val EVENT_INFO_REQUEST = (0xFFFF00B3).toInt()         // Wire: FF FF 00 B3 = -65357
    
    /** EventNotificationAddRequest - Subscribe to event */
    const val EVENT_NOTIFICATION_ADD_REQUEST = (0xFFFF00B5).toInt() // Wire: FF FF 00 B5 = -65355
    
    /** EventNotificationRemoveRequest - Unsubscribe from event */
    const val EVENT_NOTIFICATION_REMOVE_REQUEST = (0xFFFF00B6).toInt() // Wire: FF FF 00 B6 = -65354

    // --- Group Messages (Extended) ---
    /** GroupRoleMembersReply - Group role members */
    const val GROUP_ROLE_MEMBERS_REPLY = (0xFFFF015E).toInt()   // Wire: FF FF 01 5E = -65186
    
    /** GroupNoticesListReply - Group notices list */
    const val GROUP_NOTICES_LIST_REPLY = (0xFFFF0033).toInt()   // Wire: FF FF 00 33 = -65485
    
    /** GroupNoticeRequest - Request group notice */
    const val GROUP_NOTICE_REQUEST = (0xFFFF0034).toInt()       // Wire: FF FF 00 34 = -65484
    
    /** CreateGroupReply - Create group result */
    const val CREATE_GROUP_REPLY = (0xFFFF0167).toInt()         // Wire: FF FF 01 67 = -65177
    
    /** JoinGroupReply - Join group result */
    const val JOIN_GROUP_REPLY = (0xFFFF0168).toInt()           // Wire: FF FF 01 68 = -65176
    
    /** LeaveGroupReply - Leave group result */
    const val LEAVE_GROUP_REPLY = (0xFFFF016C).toInt()          // Wire: FF FF 01 6C = -65172
    
    /** EjectGroupMemberReply - Eject member result */
    const val EJECT_GROUP_MEMBER_REPLY = (0xFFFF016A).toInt()   // Wire: FF FF 01 6A = -65174
    
    /** InviteGroupResponse - Group invite response */
    const val INVITE_GROUP_RESPONSE = (0xFFFF016E).toInt()      // Wire: FF FF 01 6E = -65170
    
    /** GroupAccountSummaryReply - Group account summary */
    const val GROUP_ACCOUNT_SUMMARY_REPLY = (0xFFFF0172).toInt() // Wire: FF FF 01 72 = -65166
    
    /** GroupAccountDetailsReply - Group account details */
    const val GROUP_ACCOUNT_DETAILS_REPLY = (0xFFFF0174).toInt() // Wire: FF FF 01 74 = -65164
    
    /** GroupAccountTransactionsReply - Group transactions */
    const val GROUP_ACCOUNT_TRANSACTIONS_REPLY = (0xFFFF0176).toInt() // Wire: FF FF 01 76 = -65162
    
    /** GroupActiveProposalItemReply - Active proposal */
    const val GROUP_ACTIVE_PROPOSAL_ITEM_REPLY = (0xFFFF0178).toInt() // Wire: FF FF 01 78 = -65160
    
    /** GroupVoteHistoryItemReply - Vote history */
    const val GROUP_VOTE_HISTORY_ITEM_REPLY = (0xFFFF017A).toInt() // Wire: FF FF 01 7A = -65158

    // --- Calling Card Messages ---
    /** OfferCallingCard - Offer calling card */
    const val OFFER_CALLING_CARD = (0xFFFF011D).toInt()         // Wire: FF FF 01 1D = -65251
    
    /** AcceptCallingCard - Accept calling card */
    const val ACCEPT_CALLING_CARD = (0xFFFF011E).toInt()        // Wire: FF FF 01 1E = -65250
    
    /** DeclineCallingCard - Decline calling card */
    const val DECLINE_CALLING_CARD = (0xFFFF011F).toInt()       // Wire: FF FF 01 1F = -65249

    // --- Inventory Messages (Extended) ---
    /** FetchInventoryDescendents - Fetch folder contents */
    const val FETCH_INVENTORY_DESCENDENTS = (0xFFFF00F5).toInt() // Wire: FF FF 00 F5 = -65291
    
    /** FetchInventory - Fetch item details */
    const val FETCH_INVENTORY = (0xFFFF00F7).toInt()            // Wire: FF FF 00 F7 = -65289
    
    /** PurgeInventoryDescendents - Purge folder */
    const val PURGE_INVENTORY_DESCENDENTS = (0xFFFF00FD).toInt() // Wire: FF FF 00 FD = -65283
    
    /** RemoveInventoryObjects - Remove multiple items */
    const val REMOVE_INVENTORY_OBJECTS = (0xFFFF00FC).toInt()   // Wire: FF FF 00 FC = -65284
    
    /** InventoryAssetResponse - Asset fetch response */
    const val INVENTORY_ASSET_RESPONSE = (0xFFFF00FB).toInt()   // Wire: FF FF 00 FB = -65285
    
    /** UpdateInventoryFolder - Update folder */
    const val UPDATE_INVENTORY_FOLDER = (0xFFFF0110).toInt()    // Wire: FF FF 01 10 = -65264
    
    /** MoveInventoryFolder - Move folder */
    const val MOVE_INVENTORY_FOLDER = (0xFFFF010D).toInt()      // Wire: FF FF 01 0D = -65267
    
    /** CopyInventoryFromNotecard - Copy from notecard */
    const val COPY_INVENTORY_FROM_NOTECARD = (0xFFFF0109).toInt() // Wire: FF FF 01 09 = -65271
    
    /** CreateInventoryItem - Create item */
    const val CREATE_INVENTORY_ITEM = (0xFFFF0121).toInt()      // Wire: FF FF 01 21 = -65247
    
    /** SaveAssetIntoInventory - Save asset to inventory */
    const val SAVE_ASSET_INTO_INVENTORY = (0xFFFF0108).toInt()  // Wire: FF FF 01 08 = -65272

    // --- Task/Object Inventory Messages ---
    /** RequestTaskInventory - Request object contents */
    const val REQUEST_TASK_INVENTORY = (0xFFFF00FF).toInt()     // Wire: FF FF 00 FF = -65281
    
    /** ReplyTaskInventory - Object contents reply */
    const val REPLY_TASK_INVENTORY = (0xFFFF0100).toInt()       // Wire: FF FF 01 00 = -65280
    
    /** UpdateTaskInventory - Update object contents */
    const val UPDATE_TASK_INVENTORY = (0xFFFF00FE).toInt()      // Wire: FF FF 00 FE = -65282
    
    /** RemoveTaskInventory - Remove from object */
    const val REMOVE_TASK_INVENTORY = (0xFFFF00FD).toInt()      // Wire: FF FF 00 FD = -65283
    
    /** MoveTaskInventory - Move in object */
    const val MOVE_TASK_INVENTORY = (0xFFFF00FC).toInt()        // Wire: FF FF 00 FC = -65284

    // --- Object Messages (Extended) ---
    /** ObjectDuplicate - Duplicate object */
    const val OBJECT_DUPLICATE = (0xFFFF006E).toInt()           // Wire: FF FF 00 6E = -65426
    
    /** ObjectDuplicateOnRay - Duplicate on ray */
    const val OBJECT_DUPLICATE_ON_RAY = (0xFFFF006F).toInt()    // Wire: FF FF 00 6F = -65425
    
    /** ObjectScale - Scale object */
    const val OBJECT_SCALE = (0xFFFF0074).toInt()               // Wire: FF FF 00 74 = -65420
    
    /** ObjectRotation - Rotate object */
    const val OBJECT_ROTATION = (0xFFFF0075).toInt()            // Wire: FF FF 00 75 = -65419
    
    /** ObjectPosition - Position object (low freq) */
    const val OBJECT_POSITION = 65284                           // Wire: FF 04 = 4 | 65280 (medium frequency)
    
    /** ObjectFlagUpdate - Update object flags */
    const val OBJECT_FLAG_UPDATE = (0xFFFF0078).toInt()         // Wire: FF FF 00 78 = -65416
    
    /** ObjectClickAction - Set click action */
    const val OBJECT_CLICK_ACTION = (0xFFFF0079).toInt()        // Wire: FF FF 00 79 = -65415
    
    /** ObjectImage - Set object texture */
    const val OBJECT_IMAGE = (0xFFFF007A).toInt()               // Wire: FF FF 00 7A = -65414
    
    /** ObjectMaterial - Set object material */
    const val OBJECT_MATERIAL = (0xFFFF007B).toInt()            // Wire: FF FF 00 7B = -65413
    
    /** ObjectShape - Set object shape */
    const val OBJECT_SHAPE = (0xFFFF007C).toInt()               // Wire: FF FF 00 7C = -65412
    
    /** ObjectExtraParams - Set extra params */
    const val OBJECT_EXTRA_PARAMS = (0xFFFF007D).toInt()        // Wire: FF FF 00 7D = -65411
    
    /** ObjectOwner - Set object owner */
    const val OBJECT_OWNER = (0xFFFF007E).toInt()               // Wire: FF FF 00 7E = -65410
    
    /** ObjectGroup - Set object group */
    const val OBJECT_GROUP = (0xFFFF007F).toInt()               // Wire: FF FF 00 7F = -65409
    
    /** ObjectBuy - Buy object */
    const val OBJECT_BUY = (0xFFFF0080).toInt()                 // Wire: FF FF 00 80 = -65408
    
    /** ObjectPermissions - Set permissions */
    const val OBJECT_PERMISSIONS = (0xFFFF0081).toInt()         // Wire: FF FF 00 81 = -65407
    
    /** ObjectSaleInfo - Set sale info */
    const val OBJECT_SALE_INFO = (0xFFFF0082).toInt()           // Wire: FF FF 00 82 = -65406
    
    /** ObjectCategory - Set category */
    const val OBJECT_CATEGORY = (0xFFFF0083).toInt()            // Wire: FF FF 00 83 = -65405
    
    /** ObjectDeselect - Deselect object */
    const val OBJECT_DESELECT = (0xFFFF008F).toInt()            // Wire: FF FF 00 8F = -65393
    
    /** ObjectAttach - Attach object */
    const val OBJECT_ATTACH = (0xFFFF0090).toInt()              // Wire: FF FF 00 90 = -65392
    
    /** ObjectDetach - Detach object */
    const val OBJECT_DETACH = (0xFFFF0091).toInt()              // Wire: FF FF 00 91 = -65391
    
    /** ObjectDrop - Drop object */
    const val OBJECT_DROP = (0xFFFF0092).toInt()                // Wire: FF FF 00 92 = -65390
    
    /** ObjectSpinStart - Start spinning */
    const val OBJECT_SPIN_START = (0xFFFF0094).toInt()          // Wire: FF FF 00 94 = -65388
    
    /** ObjectSpinUpdate - Update spin */
    const val OBJECT_SPIN_UPDATE = (0xFFFF0095).toInt()         // Wire: FF FF 00 95 = -65387
    
    /** ObjectSpinStop - Stop spinning */
    const val OBJECT_SPIN_STOP = (0xFFFF0096).toInt()           // Wire: FF FF 00 96 = -65386
    
    /** ObjectGrabUpdate - Update grab */
    const val OBJECT_GRAB_UPDATE = (0xFFFF0085).toInt()         // Wire: FF FF 00 85 = -65403

    // --- Land/Terrain Messages ---
    /** ModifyLand - Modify terrain */
    const val MODIFY_LAND = (0xFFFF009C).toInt()                // Wire: FF FF 00 9C = -65380
    
    /** UndoLand - Undo terrain change */
    const val UNDO_LAND = (0xFFFF0053).toInt()                  // Wire: FF FF 00 53 = -65453

    // --- Parcel Messages (Extended) ---
    /** ParcelPropertiesRequest - Request parcel props */
    const val PARCEL_PROPERTIES_REQUEST = 65291                 // Wire: FF 0B = 11 | 65280 (medium frequency)
    
    /** ParcelPropertiesRequestByID - Request by ID */
    const val PARCEL_PROPERTIES_REQUEST_BY_ID = (0xFFFF00CB).toInt() // Wire: FF FF 00 CB = -65333
    
    /** ParcelDisableObjects - Disable objects */
    const val PARCEL_DISABLE_OBJECTS = (0xFFFF00CD).toInt()     // Wire: FF FF 00 CD = -65331
    
    /** ParcelSelectObjects - Select parcel objects */
    const val PARCEL_SELECT_OBJECTS = (0xFFFF00CE).toInt()      // Wire: FF FF 00 CE = -65330
    
    /** ParcelMediaCommandMessage - Media command */
    const val PARCEL_MEDIA_COMMAND_MESSAGE = (0xFFFF019B).toInt() // Wire: FF FF 01 9B = -65125
    
    /** ParcelMediaUpdate - Media update */
    const val PARCEL_MEDIA_UPDATE = (0xFFFF019C).toInt()        // Wire: FF FF 01 9C = -65124
    
    /** ParcelObjectOwnersReply - Object owners list */
    const val PARCEL_OBJECT_OWNERS_REPLY = (0xFFFF0039).toInt() // Wire: FF FF 00 39 = -65479
    
    /** ForceObjectSelect - Force select object */
    const val FORCE_OBJECT_SELECT = (0xFFFF00CF).toInt()        // Wire: FF FF 00 CF = -65329

    // --- Money/Economy Messages ---
    /** MoneyTransferRequest - Transfer L$ */
    const val MONEY_TRANSFER_REQUEST = (0xFFFF00E7).toInt()     // Wire: FF FF 00 E7 = -65305
    
    /** RoutedMoneyBalanceReply - Routed balance */
    const val ROUTED_MONEY_BALANCE_REPLY = (0xFFFF00E3).toInt() // Wire: FF FF 00 E3 = -65309
    
    /** PayPriceReply - Pay price info */
    const val PAY_PRICE_REPLY = (0xFFFF00A2).toInt()            // Wire: FF FF 00 A2 = -65374
    
    /** RequestPayPrice - Request pay info */
    const val REQUEST_PAY_PRICE = (0xFFFF00A1).toInt()          // Wire: FF FF 00 A1 = -65375
    
    /** BuyObjectInventory - Buy object contents */
    const val BUY_OBJECT_INVENTORY = (0xFFFF0081).toInt()       // Wire: FF FF 00 81 = -65407

    // --- Script Messages (Extended) ---
    /** ScriptRunningReply - Script status reply */
    const val SCRIPT_RUNNING_REPLY = (0xFFFF00E4).toInt()       // Wire: FF FF 00 E4 = -65308
    
    /** GetScriptRunning - Get script status */
    const val GET_SCRIPT_RUNNING = (0xFFFF00E3).toInt()         // Wire: FF FF 00 E3 = -65309
    
    /** SetScriptRunning - Set script status */
    const val SET_SCRIPT_RUNNING = (0xFFFF00E5).toInt()         // Wire: FF FF 00 E5 = -65307
    
    /** ScriptReset - Reset script */
    const val SCRIPT_RESET = (0xFFFF00E6).toInt()               // Wire: FF FF 00 E6 = -65306
    
    /** ScriptSensorReply - Sensor results */
    const val SCRIPT_SENSOR_REPLY = (0xFFFF00E8).toInt()        // Wire: FF FF 00 E8 = -65304
    
    /** ScriptTeleportRequest - Script teleport */
    const val SCRIPT_TELEPORT_REQUEST = (0xFFFF00C7).toInt()    // Wire: FF FF 00 C7 = -65337
    
    /** ForceScriptControlRelease - Release controls */
    const val FORCE_SCRIPT_CONTROL_RELEASE = (0xFFFF00C0).toInt() // Wire: FF FF 00 C0 = -65344
    
    /** RevokePermissions - Revoke perms */
    const val REVOKE_PERMISSIONS = (0xFFFF00C1).toInt()         // Wire: FF FF 00 C1 = -65343

    // --- Asset/Transfer Messages ---
    /** TransferRequest - Request transfer */
    const val TRANSFER_REQUEST = (0xFFFF0099).toInt()           // Wire: FF FF 00 99 = -65383
    
    /** TransferAbort - Abort transfer */
    const val TRANSFER_ABORT = (0xFFFF009B).toInt()             // Wire: FF FF 00 9B = -65381
    
    /** RequestXfer - Request xfer */
    const val REQUEST_XFER = (0xFFFF009C).toInt()               // Wire: FF FF 00 9C = -65380
    
    /** AssetUploadRequest - Upload asset */
    const val ASSET_UPLOAD_REQUEST = (0xFFFF0139).toInt()       // Wire: FF FF 01 39 = -65223
    
    /** AssetUploadComplete - Upload complete */
    const val ASSET_UPLOAD_COMPLETE = (0xFFFF013A).toInt()      // Wire: FF FF 01 3A = -65222

    // --- Region/Sim Messages ---
    /** RequestRegionInfo - Request region info */
    const val REQUEST_REGION_INFO = (0xFFFF00A5).toInt()        // Wire: FF FF 00 A5 = -65371
    
    /** GodUpdateRegionInfo - God region update */
    const val GOD_UPDATE_REGION_INFO = (0xFFFF00A7).toInt()     // Wire: FF FF 00 A7 = -65369
    
    /** SimulatorViewerTimeMessage - Sim time */
    const val SIMULATOR_VIEWER_TIME_MESSAGE = (0xFFFF00AA).toInt() // Wire: FF FF 00 AA = -65366
    
    /** TeleportLocal - Local teleport */
    const val TELEPORT_LOCAL = (0xFFFF0044).toInt()             // Wire: FF FF 00 44 = -65468
    
    /** TeleportCancel - Cancel teleport */
    const val TELEPORT_CANCEL = (0xFFFF0048).toInt()            // Wire: FF FF 00 48 = -65464
    
    /** TeleportRequest - Teleport request */
    const val TELEPORT_REQUEST = (0xFFFF0046).toInt()           // Wire: FF FF 00 46 = -65466
    
    /** SimCrashed - Sim crash notification */
    const val SIM_CRASHED = (0xFFFF0138).toInt()                // Wire: FF FF 01 38 = -65224

    // --- Map Messages (Extended) ---
    /** MapBlockRequest - Request map block */
    const val MAP_BLOCK_REQUEST = (0xFFFF0193).toInt()          // Wire: FF FF 01 93 = -65133
    
    /** MapNameRequest - Request map by name */
    const val MAP_NAME_REQUEST = (0xFFFF0194).toInt()           // Wire: FF FF 01 94 = -65132
    
    /** MapLayerRequest - Request map layer */
    const val MAP_LAYER_REQUEST = (0xFFFF0191).toInt()          // Wire: FF FF 01 91 = -65135
    
    /** MapItemRequest - Request map items */
    const val MAP_ITEM_REQUEST = (0xFFFF0196).toInt()           // Wire: FF FF 01 96 = -65130

    // --- Mute Messages ---
    /** MuteListRequest - Request mute list */
    const val MUTE_LIST_REQUEST = (0xFFFF0106).toInt()          // Wire: FF FF 01 06 = -65274
    
    /** UpdateMuteListEntry - Update mute */
    const val UPDATE_MUTE_LIST_ENTRY = (0xFFFF0107).toInt()     // Wire: FF FF 01 07 = -65273
    
    /** RemoveMuteListEntry - Remove mute */
    const val REMOVE_MUTE_LIST_ENTRY = (0xFFFF0108).toInt()     // Wire: FF FF 01 08 = -65272
    
    /** MuteListUpdate - Mute list updated */
    const val MUTE_LIST_UPDATE = (0xFFFF0114).toInt()           // Wire: FF FF 01 14 = -65260
    
    /** UseCachedMuteList - Use cached list */
    const val USE_CACHED_MUTE_LIST = (0xFFFF0115).toInt()       // Wire: FF FF 01 15 = -65259

    // --- User Info Messages ---
    /** UserInfoRequest - Request user info */
    const val USER_INFO_REQUEST = (0xFFFF017F).toInt()          // Wire: FF FF 01 7F = -65153
    
    /** UserInfoReply - User info reply */
    const val USER_INFO_REPLY = (0xFFFF0180).toInt()            // Wire: FF FF 01 80 = -65152
    
    /** UpdateUserInfo - Update user info */
    const val UPDATE_USER_INFO = (0xFFFF0181).toInt()           // Wire: FF FF 01 81 = -65151

    // --- Godlike Messages ---
    /** GodlikeMessage - Godlike command */
    const val GODLIKE_MESSAGE = (0xFFFF0105).toInt()            // Wire: FF FF 01 05 = -65275
    
    /** GrantGodlikePowers - Grant god powers */
    const val GRANT_GODLIKE_POWERS = (0xFFFF0102).toInt()       // Wire: FF FF 01 02 = -65278
    
    /** RequestGodlikePowers - Request powers */
    const val REQUEST_GODLIKE_POWERS = (0xFFFF0101).toInt()     // Wire: FF FF 01 01 = -65279
    
    /** GodKickUser - God kick user */
    const val GOD_KICK_USER = (0xFFFF00A1).toInt()              // Wire: FF FF 00 A1 = -65375
    
    /** SystemKickUser - System kick */
    const val SYSTEM_KICK_USER = (0xFFFF00A2).toInt()           // Wire: FF FF 00 A2 = -65374
    
    /** EjectUser - Eject user */
    const val EJECT_USER = (0xFFFF00A3).toInt()                 // Wire: FF FF 00 A3 = -65373
    
    /** FreezeUser_God - God freeze user */
    const val FREEZE_USER_GOD = (0xFFFF00A4).toInt()            // Wire: FF FF 00 A4 = -65372
    
    /** KickUser - Kick user */
    const val KICK_USER = (0xFFFF009F).toInt()                  // Wire: FF FF 00 9F = -65377
    
    /** KickUserAck - Kick acknowledged */
    const val KICK_USER_ACK = (0xFFFF00A0).toInt()              // Wire: FF FF 00 A0 = -65376

    // --- Generic/System Messages ---
    /** GenericMessage - Generic message */
    const val GENERIC_MESSAGE = (0xFFFF0105).toInt()            // Wire: FF FF 01 05 = -65275
    
    /** SystemMessage - System message */
    const val SYSTEM_MESSAGE = (0xFFFF018C).toInt()             // Wire: FF FF 01 8C = -65140
    
    /** Error - Error message */
    const val ERROR_MESSAGE = (0xFFFF019F).toInt()              // Wire: FF FF 01 9F = -65121
    
    /** FeatureDisabled - Feature disabled */
    const val FEATURE_DISABLED = (0xFFFF0013).toInt()           // Wire: FF FF 00 13 = -65517
    
    /** ViewerFrozenMessage - Viewer frozen */
    const val VIEWER_FROZEN_MESSAGE = (0xFFFF0089).toInt()      // Wire: FF FF 00 89 = -65399
    
    /** ViewerStats - Viewer statistics */
    const val VIEWER_STATS = (0xFFFF0093).toInt()               // Wire: FF FF 00 93 = -65389

    // --- Attachment Messages ---
    /** RezMultipleAttachmentsFromInv - Rez multiple */
    const val REZ_MULTIPLE_ATTACHMENTS_FROM_INV = (0xFFFF012C).toInt() // Wire: FF FF 01 2C = -65236
    
    /** DetachAttachmentIntoInv - Detach to inventory */
    const val DETACH_ATTACHMENT_INTO_INV = (0xFFFF012D).toInt() // Wire: FF FF 01 2D = -65235
    
    /** CreateNewOutfitAttachments - Create outfit */
    const val CREATE_NEW_OUTFIT_ATTACHMENTS = (0xFFFF012E).toInt() // Wire: FF FF 01 2E = -65234
    
    /** UpdateAttachment - Update attachment */
    const val UPDATE_ATTACHMENT = (0xFFFF0137).toInt()          // Wire: FF FF 01 37 = -65225
    
    /** RemoveAttachment - Remove attachment */
    const val REMOVE_ATTACHMENT = (0xFFFF0136).toInt()          // Wire: FF FF 01 36 = -65226
    
    /** RebakeAvatarTextures - Rebake textures */
    const val REBAKE_AVATAR_TEXTURES = (0xFFFF0056).toInt()     // Wire: FF FF 00 56 = -65450

    // --- Rez/DeRez Messages ---
    /** RezObjectFromNotecard - Rez from notecard */
    const val REZ_OBJECT_FROM_NOTECARD = (0xFFFF0126).toInt()   // Wire: FF FF 01 26 = -65242
    
    /** RezRestoreToWorld - Restore to world */
    const val REZ_RESTORE_TO_WORLD = (0xFFFF019E).toInt()       // Wire: FF FF 01 9E = -65122
    
    /** RezScript - Rez script */
    const val REZ_SCRIPT = (0xFFFF0120).toInt()                 // Wire: FF FF 01 20 = -65248
    
    /** DeRezAck - DeRez acknowledged */
    const val DEREZ_ACK = (0xFFFF0124).toInt()                  // Wire: FF FF 01 24 = -65244

    // --- Misc Messages ---
    /** Undo - Undo action */
    const val UNDO = (0xFFFF0051).toInt()                       // Wire: FF FF 00 51 = -65455
    
    /** Redo - Redo action */
    const val REDO = (0xFFFF0052).toInt()                       // Wire: FF FF 00 52 = -65454
    
    /** SetAlwaysRun - Set always run */
    const val SET_ALWAYS_RUN = (0xFFFF0058).toInt()             // Wire: FF FF 00 58 = -65448
    
    /** SetFollowCamProperties - Set follow cam */
    const val SET_FOLLOW_CAM_PROPERTIES = (0xFFFF009F).toInt()  // Wire: FF FF 00 9F = -65377
    
    /** ClearFollowCamProperties - Clear follow cam */
    const val CLEAR_FOLLOW_CAM_PROPERTIES = (0xFFFF00A0).toInt() // Wire: FF FF 00 A0 = -65376
    
    /** SetStartLocationRequest - Set start location */
    const val SET_START_LOCATION_REQUEST = (0xFFFF013C).toInt() // Wire: FF FF 01 3C = -65220
    
    /** InitiateDownload - Start download */
    const val INITIATE_DOWNLOAD = (0xFFFF018D).toInt()          // Wire: FF FF 01 8D = -65139
    
    /** DataHomeLocationReply - Home location reply */
    const val DATA_HOME_LOCATION_REPLY = (0xFFFF0043).toInt()   // Wire: FF FF 00 43 = -65469

    // --- Places/Directory Messages ---
    /** PlacesReply - Places search reply */
    const val PLACES_REPLY = (0xFFFF001E).toInt()               // Wire: FF FF 00 1E = -65506
    
    /** DirPopularReply - Popular places reply */
    const val DIR_POPULAR_REPLY = (0xFFFF0027).toInt()          // Wire: FF FF 00 27 = -65497

    // =====================================
    // PHASE 4: 100 Additional Message Handlers
    // =====================================

    // --- Circuit/Connection Messages ---
    /** CloseCircuit - Close UDP circuit */
    const val CLOSE_CIRCUIT = (0xFFFF0001).toInt()              // Wire: FF FF 00 01 = -65535
    
    /** OpenCircuit - Open UDP circuit */
    const val OPEN_CIRCUIT = (0xFFFF0002).toInt()               // Wire: FF FF 00 02 = -65534
    
    /** AddCircuitCode - Add circuit code */
    const val ADD_CIRCUIT_CODE = (0xFFFF0005).toInt()           // Wire: FF FF 00 05 = -65531
    
    /** CreateTrustedCircuit - Create trusted circuit */
    const val CREATE_TRUSTED_CIRCUIT = (0xFFFF0006).toInt()     // Wire: FF FF 00 06 = -65530
    
    /** DenyTrustedCircuit - Deny trusted circuit */
    const val DENY_TRUSTED_CIRCUIT = (0xFFFF0007).toInt()       // Wire: FF FF 00 07 = -65529
    
    /** RequestTrustedCircuit - Request trusted circuit */
    const val REQUEST_TRUSTED_CIRCUIT = (0xFFFF0008).toInt()    // Wire: FF FF 00 08 = -65528

    // --- Auction Messages ---
    /** CancelAuction - Cancel auction */
    const val CANCEL_AUCTION = (0xFFFF003B).toInt()             // Wire: FF FF 00 3B = -65477
    
    /** CompleteAuction - Complete auction */
    const val COMPLETE_AUCTION = (0xFFFF003C).toInt()           // Wire: FF FF 00 3C = -65476
    
    /** ConfirmAuctionStart - Confirm auction start */
    const val CONFIRM_AUCTION_START = (0xFFFF003D).toInt()      // Wire: FF FF 00 3D = -65475
    
    /** StartAuction - Start auction */
    const val START_AUCTION = (0xFFFF003E).toInt()              // Wire: FF FF 00 3E = -65474
    
    /** ViewerStartAuction - Viewer start auction */
    const val VIEWER_START_AUCTION = (0xFFFF003F).toInt()       // Wire: FF FF 00 3F = -65473
    
    /** CheckParcelAuctions - Check parcel auctions */
    const val CHECK_PARCEL_AUCTIONS = (0xFFFF0040).toInt()      // Wire: FF FF 00 40 = -65472
    
    /** CheckParcelSales - Check parcel sales */
    const val CHECK_PARCEL_SALES = (0xFFFF0041).toInt()         // Wire: FF FF 00 41 = -65471
    
    /** ParcelAuctions - Parcel auctions */
    const val PARCEL_AUCTIONS = (0xFFFF00D1).toInt()            // Wire: FF FF 00 D1 = -65327
    
    /** ParcelSales - Parcel sales */
    const val PARCEL_SALES = (0xFFFF00D2).toInt()               // Wire: FF FF 00 D2 = -65326

    // --- Parcel Extended Messages ---
    /** ParcelBuyPass - Buy parcel pass */
    const val PARCEL_BUY_PASS = (0xFFFF00D3).toInt()            // Wire: FF FF 00 D3 = -65325
    
    /** ParcelClaim - Claim parcel */
    const val PARCEL_CLAIM = (0xFFFF00D4).toInt()               // Wire: FF FF 00 D4 = -65324
    
    /** ParcelDivide - Divide parcel */
    const val PARCEL_DIVIDE = (0xFFFF00D5).toInt()              // Wire: FF FF 00 D5 = -65323
    
    /** ParcelJoin - Join parcel */
    const val PARCEL_JOIN = (0xFFFF00D6).toInt()                // Wire: FF FF 00 D6 = -65322
    
    /** ParcelReclaim - Reclaim parcel */
    const val PARCEL_RECLAIM = (0xFFFF00D7).toInt()             // Wire: FF FF 00 D7 = -65321
    
    /** ParcelRename - Rename parcel */
    const val PARCEL_RENAME = (0xFFFF00DA).toInt()              // Wire: FF FF 00 DA = -65318
    
    /** ParcelSetOtherCleanTime - Set clean time */
    const val PARCEL_SET_OTHER_CLEAN_TIME = (0xFFFF00DC).toInt() // Wire: FF FF 00 DC = -65316
    
    /** ParcelGodForceOwner - God force owner */
    const val PARCEL_GOD_FORCE_OWNER = (0xFFFF00DD).toInt()     // Wire: FF FF 00 DD = -65315
    
    /** ParcelGodMarkAsContent - God mark as content */
    const val PARCEL_GOD_MARK_AS_CONTENT = (0xFFFF00DE).toInt() // Wire: FF FF 00 DE = -65314
    
    /** MergeParcel - Merge parcel */
    const val MERGE_PARCEL = (0xFFFF00DF).toInt()               // Wire: FF FF 00 DF = -65313
    
    /** RemoveParcel - Remove parcel */
    const val REMOVE_PARCEL = (0xFFFF00E0).toInt()              // Wire: FF FF 00 E0 = -65312

    // --- Land Statistics Messages ---
    /** LandStatRequest - Request land stats */
    const val LAND_STAT_REQUEST = (0xFFFF019D).toInt()          // Wire: FF FF 01 9D = -65123
    
    /** LandStatReply - Land stats reply */
    const val LAND_STAT_REPLY = (0xFFFF019E).toInt()            // Wire: FF FF 01 9E = -65122

    // --- Simulator Messages ---
    /** SimulatorLoad - Simulator load info */
    const val SIMULATOR_LOAD = (0xFFFF0010).toInt()             // Wire: FF FF 00 10 = -65520
    
    /** SimulatorReady - Simulator ready */
    const val SIMULATOR_READY = (0xFFFF0011).toInt()            // Wire: FF FF 00 11 = -65519
    
    /** SimulatorShutdownRequest - Request shutdown */
    const val SIMULATOR_SHUTDOWN_REQUEST = (0xFFFF0012).toInt() // Wire: FF FF 00 12 = -65518
    
    /** SimulatorMapUpdate - Sim map update */
    const val SIMULATOR_MAP_UPDATE = (0xFFFF0014).toInt()       // Wire: FF FF 00 14 = -65516
    
    /** SimulatorSetMap - Set sim map */
    const val SIMULATOR_SET_MAP = (0xFFFF0015).toInt()          // Wire: FF FF 00 15 = -65515
    
    /** SimulatorPresentAtLocation - Present at location */
    const val SIMULATOR_PRESENT_AT_LOCATION = (0xFFFF0016).toInt() // Wire: FF FF 00 16 = -65514
    
    /** UpdateSimulator - Update simulator */
    const val UPDATE_SIMULATOR = (0xFFFF0017).toInt()           // Wire: FF FF 00 17 = -65513
    
    /** SimWideDeletes - Sim-wide delete */
    const val SIM_WIDE_DELETES = (0xFFFF018E).toInt()           // Wire: FF FF 01 8E = -65138

    // --- Child Agent Messages ---
    /** ChildAgentDying - Child agent dying */
    const val CHILD_AGENT_DYING = (0xFFFF0018).toInt()          // Wire: FF FF 00 18 = -65512
    
    /** ChildAgentUnknown - Child agent unknown */
    const val CHILD_AGENT_UNKNOWN = (0xFFFF001A).toInt()        // Wire: FF FF 00 1A = -65510
    
    /** KillChildAgents - Kill child agents */
    const val KILL_CHILD_AGENTS = (0xFFFF002F).toInt()          // Wire: FF FF 00 2F = -65489

    // --- Postcard/Email Messages ---
    /** SendPostcard - Send postcard */
    const val SEND_POSTCARD = (0xFFFF0030).toInt()              // Wire: FF FF 00 30 = -65488
    
    /** EmailMessageRequest - Email request */
    const val EMAIL_MESSAGE_REQUEST = (0xFFFF0031).toInt()      // Wire: FF FF 00 31 = -65487
    
    /** EmailMessageReply - Email reply */
    const val EMAIL_MESSAGE_REPLY = (0xFFFF0032).toInt()        // Wire: FF FF 00 32 = -65486

    // --- RPC Messages ---
    /** RpcChannelRequest - RPC channel request */
    const val RPC_CHANNEL_REQUEST = (0xFFFF00E9).toInt()        // Wire: FF FF 00 E9 = -65303
    
    /** RpcChannelReply - RPC channel reply */
    const val RPC_CHANNEL_REPLY = (0xFFFF00EA).toInt()          // Wire: FF FF 00 EA = -65302
    
    /** RpcScriptRequestInbound - RPC script request */
    const val RPC_SCRIPT_REQUEST_INBOUND = (0xFFFF00EB).toInt() // Wire: FF FF 00 EB = -65301
    
    /** RpcScriptReplyInbound - RPC script reply */
    const val RPC_SCRIPT_REPLY_INBOUND = (0xFFFF00ED).toInt()   // Wire: FF FF 00 ED = -65299

    // --- Script Messages Extended ---
    /** ScriptDataRequest - Script data request */
    const val SCRIPT_DATA_REQUEST = (0xFFFF00EF).toInt()        // Wire: FF FF 00 EF = -65297
    
    /** ScriptDataReply - Script data reply */
    const val SCRIPT_DATA_REPLY = (0xFFFF00F0).toInt()          // Wire: FF FF 00 F0 = -65296
    
    /** ScriptMailRegistration - Script mail registration */
    const val SCRIPT_MAIL_REGISTRATION = (0xFFFF00F1).toInt()   // Wire: FF FF 00 F1 = -65295
    
    /** ScriptSensorRequest - Sensor request */
    const val SCRIPT_SENSOR_REQUEST = (0xFFFF00F2).toInt()      // Wire: FF FF 00 F2 = -65294
    
    /** ScriptAnswerYes - Script permission answer */
    const val SCRIPT_ANSWER_YES = (0xFFFF00C2).toInt()          // Wire: FF FF 00 C2 = -65342
    
    /** InternalScriptMail - Internal script mail */
    const val INTERNAL_SCRIPT_MAIL = 65296                      // Wire: FF 10 = 16 | 65280 (medium frequency)

    // --- Tracking Messages ---
    /** TrackAgent - Track agent location */
    const val TRACK_AGENT = (0xFFFF0150).toInt()                // Wire: FF FF 01 50 = -65200
    
    /** FindAgent - Find agent (extended) */
    const val FIND_AGENT_EXTENDED = (0xFFFF0151).toInt()        // Wire: FF FF 01 51 = -65199

    // --- Region Messages Extended ---
    /** RegionHandleRequest - Request region handle */
    const val REGION_HANDLE_REQUEST = (0xFFFF0198).toInt()      // Wire: FF FF 01 98 = -65128
    
    /** RegionIDAndHandleReply - Region ID reply */
    const val REGION_ID_AND_HANDLE_REPLY = (0xFFFF0199).toInt() // Wire: FF FF 01 99 = -65127
    
    /** RegionPresenceRequestByHandle - Region presence */
    const val REGION_PRESENCE_REQUEST_BY_HANDLE = (0xFFFF019A).toInt() // Wire: FF FF 01 9A = -65126
    
    /** RegionPresenceRequestByRegionID - Region presence by ID */
    const val REGION_PRESENCE_REQUEST_BY_REGION_ID = (0xFFFF019B).toInt() // Wire: FF FF 01 9B = -65125
    
    /** RegionPresenceResponse - Region presence response */
    const val REGION_PRESENCE_RESPONSE = (0xFFFF019C).toInt()   // Wire: FF FF 01 9C = -65124
    
    /** NearestLandingRegionRequest - Nearest landing request */
    const val NEAREST_LANDING_REGION_REQUEST = (0xFFFF0052).toInt() // Wire: FF FF 00 52 = -65454
    
    /** NearestLandingRegionReply - Nearest landing reply */
    const val NEAREST_LANDING_REGION_REPLY = (0xFFFF0053).toInt() // Wire: FF FF 00 53 = -65453
    
    /** NearestLandingRegionUpdated - Landing region updated */
    const val NEAREST_LANDING_REGION_UPDATED = (0xFFFF0054).toInt() // Wire: FF FF 00 54 = -65452
    
    /** TelehubInfo - Telehub info */
    const val TELEHUB_INFO = (0xFFFF0047).toInt()               // Wire: FF FF 00 47 = -65465
    
    /** TeleportLandingStatusChanged - Landing status changed */
    const val TELEPORT_LANDING_STATUS_CHANGED = (0xFFFF004B).toInt() // Wire: FF FF 00 4B = -65461

    // --- User Reports Messages ---
    /** UserReport - User report */
    const val USER_REPORT = (0xFFFF01A0).toInt()                // Wire: FF FF 01 A0 = -65120
    
    /** UserReportInternal - Internal report */
    const val USER_REPORT_INTERNAL = (0xFFFF01A1).toInt()       // Wire: FF FF 01 A1 = -65119
    
    /** ReportAutosaveCrash - Autosave crash report */
    const val REPORT_AUTOSAVE_CRASH = (0xFFFF01A2).toInt()      // Wire: FF FF 01 A2 = -65118

    // --- Event Messages Extended ---
    /** EventLocationRequest - Event location request */
    const val EVENT_LOCATION_REQUEST = (0xFFFF00B7).toInt()     // Wire: FF FF 00 B7 = -65353
    
    /** EventLocationReply - Event location reply */
    const val EVENT_LOCATION_REPLY = (0xFFFF00B8).toInt()       // Wire: FF FF 00 B8 = -65352
    
    /** EventGodDelete - God delete event */
    const val EVENT_GOD_DELETE = (0xFFFF00BB).toInt()           // Wire: FF FF 00 BB = -65349
    
    /** CreateLandmarkForEvent - Create landmark for event */
    const val CREATE_LANDMARK_FOR_EVENT = (0xFFFF00BC).toInt()  // Wire: FF FF 00 BC = -65348

    // --- Pick Messages Extended ---
    /** PickGodDelete - God delete pick */
    const val PICK_GOD_DELETE = (0xFFFF00BD).toInt()            // Wire: FF FF 00 BD = -65347

    // --- Classified Messages Extended ---
    /** ClassifiedGodDelete - God delete classified */
    const val CLASSIFIED_GOD_DELETE = (0xFFFF002F).toInt()      // Wire: FF FF 00 2F = -65489

    // --- Directory Query Messages ---
    /** DirFindQuery - Directory find query */
    const val DIR_FIND_QUERY = (0xFFFF001D).toInt()             // Wire: FF FF 00 1D = -65507
    
    /** DirPlacesQuery - Places query */
    const val DIR_PLACES_QUERY = (0xFFFF001F).toInt()           // Wire: FF FF 00 1F = -65505
    
    /** DirClassifiedQuery - Classified query */
    const val DIR_CLASSIFIED_QUERY = (0xFFFF0028).toInt()       // Wire: FF FF 00 28 = -65496
    
    /** DirLandQuery - Land query */
    const val DIR_LAND_QUERY = (0xFFFF0025).toInt()             // Wire: FF FF 00 25 = -65499
    
    /** DirPopularQuery - Popular query */
    const val DIR_POPULAR_QUERY = (0xFFFF0024).toInt()          // Wire: FF FF 00 24 = -65500
    
    /** PlacesQuery - Places query */
    const val PLACES_QUERY = (0xFFFF001C).toInt()               // Wire: FF FF 00 1C = -65508

    // --- Inventory Messages Extended ---
    /** LinkInventoryItem - Link inventory item */
    const val LINK_INVENTORY_ITEM = (0xFFFF0112).toInt()        // Wire: FF FF 01 12 = -65262
    
    /** ChangeInventoryItemFlags - Change item flags */
    const val CHANGE_INVENTORY_ITEM_FLAGS = (0xFFFF0113).toInt() // Wire: FF FF 01 13 = -65261
    
    /** RequestInventoryAsset - Request asset */
    const val REQUEST_INVENTORY_ASSET = (0xFFFF013D).toInt()    // Wire: FF FF 01 3D = -65219
    
    /** TransferInventory - Transfer inventory */
    const val TRANSFER_INVENTORY = (0xFFFF013E).toInt()         // Wire: FF FF 01 3E = -65218
    
    /** TransferInventoryAck - Transfer ACK */
    const val TRANSFER_INVENTORY_ACK = (0xFFFF013F).toInt()     // Wire: FF FF 01 3F = -65217
    
    /** RetrieveInstantMessages - Retrieve IMs */
    const val RETRIEVE_INSTANT_MESSAGES = (0xFFFF0140).toInt()  // Wire: FF FF 01 40 = -65216

    // --- Group Request Messages ---
    /** CreateGroupRequest - Create group */
    const val CREATE_GROUP_REQUEST = (0xFFFF0165).toInt()       // Wire: FF FF 01 65 = -65179
    
    /** JoinGroupRequest - Join group */
    const val JOIN_GROUP_REQUEST = (0xFFFF0169).toInt()         // Wire: FF FF 01 69 = -65175
    
    /** EjectGroupMemberRequest - Eject member */
    const val EJECT_GROUP_MEMBER_REQUEST = (0xFFFF016B).toInt() // Wire: FF FF 01 6B = -65173
    
    /** InviteGroupRequest - Invite to group */
    const val INVITE_GROUP_REQUEST = (0xFFFF016D).toInt()       // Wire: FF FF 01 6D = -65171
    
    /** GroupTitlesRequest - Request titles */
    const val GROUP_TITLES_REQUEST = (0xFFFF015A).toInt()       // Wire: FF FF 01 5A = -65190
    
    /** GroupMembersRequest - Request members */
    const val GROUP_MEMBERS_REQUEST = (0xFFFF015D).toInt()      // Wire: FF FF 01 5D = -65187
    
    /** GroupRoleMembersRequest - Request role members */
    const val GROUP_ROLE_MEMBERS_REQUEST = (0xFFFF0159).toInt() // Wire: FF FF 01 59 = -65191
    
    /** GroupRoleDataRequest - Request role data */
    const val GROUP_ROLE_DATA_REQUEST = (0xFFFF0158).toInt()    // Wire: FF FF 01 58 = -65192
    
    /** GroupNoticesListRequest - Request notices list */
    const val GROUP_NOTICES_LIST_REQUEST = (0xFFFF0036).toInt() // Wire: FF FF 00 36 = -65482
    
    /** GroupActiveProposalsRequest - Active proposals */
    const val GROUP_ACTIVE_PROPOSALS_REQUEST = (0xFFFF0177).toInt() // Wire: FF FF 01 77 = -65161
    
    /** GroupVoteHistoryRequest - Vote history */
    const val GROUP_VOTE_HISTORY_REQUEST = (0xFFFF0179).toInt() // Wire: FF FF 01 79 = -65159
    
    /** GroupAccountSummaryRequest - Account summary */
    const val GROUP_ACCOUNT_SUMMARY_REQUEST = (0xFFFF0171).toInt() // Wire: FF FF 01 71 = -65167
    
    /** GroupAccountDetailsRequest - Account details */
    const val GROUP_ACCOUNT_DETAILS_REQUEST = (0xFFFF0173).toInt() // Wire: FF FF 01 73 = -65165
    
    /** GroupAccountTransactionsRequest - Transactions */
    const val GROUP_ACCOUNT_TRANSACTIONS_REQUEST = (0xFFFF0175).toInt() // Wire: FF FF 01 75 = -65163
    
    /** GroupDataUpdate - Group data update */
    const val GROUP_DATA_UPDATE = (0xFFFF017B).toInt()          // Wire: FF FF 01 7B = -65157
    
    /** GroupRoleUpdate - Role update */
    const val GROUP_ROLE_UPDATE = (0xFFFF0157).toInt()          // Wire: FF FF 01 57 = -65193
    
    /** GroupRoleChanges - Role changes */
    const val GROUP_ROLE_CHANGES = (0xFFFF0156).toInt()         // Wire: FF FF 01 56 = -65194
    
    /** GroupTitleUpdate - Title update */
    const val GROUP_TITLE_UPDATE = (0xFFFF0155).toInt()         // Wire: FF FF 01 55 = -65195
    
    /** UpdateGroupInfo - Update group info */
    const val UPDATE_GROUP_INFO = (0xFFFF017C).toInt()          // Wire: FF FF 01 7C = -65156
    
    /** GroupProposalBallot - Proposal ballot */
    const val GROUP_PROPOSAL_BALLOT = (0xFFFF0178).toInt()      // Wire: FF FF 01 78 = -65160
    
    /** StartGroupProposal - Start proposal */
    const val START_GROUP_PROPOSAL = (0xFFFF017D).toInt()       // Wire: FF FF 01 7D = -65155
    
    /** SetGroupAcceptNotices - Set accept notices */
    const val SET_GROUP_ACCEPT_NOTICES = (0xFFFF0166).toInt()   // Wire: FF FF 01 66 = -65178
    
    /** SetGroupContribution - Set contribution */
    const val SET_GROUP_CONTRIBUTION = (0xFFFF017E).toInt()     // Wire: FF FF 01 7E = -65154
    
    /** TallyVotes - Tally votes */
    const val TALLY_VOTES = (0xFFFF0054).toInt()                // Wire: FF FF 00 54 = -65452

    // --- Live Help Messages ---
    /** LiveHelpGroupRequest - Live help request */
    const val LIVE_HELP_GROUP_REQUEST = (0xFFFF0055).toInt()    // Wire: FF FF 00 55 = -65451
    
    /** LiveHelpGroupReply - Live help reply */
    const val LIVE_HELP_GROUP_REPLY = (0xFFFF0057).toInt()      // Wire: FF FF 00 57 = -65449

    // --- Avatar Messages Extended ---
    /** AvatarTextureUpdate - Texture update */
    const val AVATAR_TEXTURE_UPDATE_MSG = (0xFFFF0004).toInt()  // Wire: FF FF 00 04 = -65532
    
    /** AvatarPickerRequest - Picker request */
    const val AVATAR_PICKER_REQUEST_MSG = (0xFFFF001B).toInt()  // Wire: FF FF 00 1B = -65509
    
        
        
        
    /** AvatarPropertiesRequest - Properties request */
    const val AVATAR_PROPERTIES_REQUEST_MSG = (0xFFFF00A8).toInt() // Wire: FF FF 00 A8 = -65368
    
    /** RebakeAvatarTextures - Rebake textures */
    const val REBAKE_AVATAR_TEXTURES_MSG = (0xFFFF0056).toInt() // Wire: FF FF 00 56 = -65450

    // --- Velocity Interpolation Messages ---
    /** VelocityInterpolateOn - Enable interpolation */
    const val VELOCITY_INTERPOLATE_ON = (0xFFFF005E).toInt()    // Wire: FF FF 00 5E = -65442
    
    /** VelocityInterpolateOff - Disable interpolation */
    const val VELOCITY_INTERPOLATE_OFF = (0xFFFF005F).toInt()   // Wire: FF FF 00 5F = -65441

    // --- Object Messages Extended ---
    /** ObjectIncludeInSearch - Include in search */
    const val OBJECT_INCLUDE_IN_SEARCH = (0xFFFF0089).toInt()   // Wire: FF FF 00 89 = -65399
    
    /** ObjectExportSelected - Export selected */
    const val OBJECT_EXPORT_SELECTED = (0xFFFF008A).toInt()     // Wire: FF FF 00 8A = -65398
    
    /** DerezContainer - Derez container */
    const val DEREZ_CONTAINER = (0xFFFF0125).toInt()            // Wire: FF FF 01 25 = -65243
    
    /** BuyObjectInventory - Buy object inventory */
    const val BUY_OBJECT_INVENTORY_MSG = (0xFFFF0132).toInt()   // Wire: FF FF 01 32 = -65230

    // --- Test/Debug Messages ---
    /** TestMessage - Test message */
    const val TEST_MESSAGE = (0xFFFF0060).toInt()               // Wire: FF FF 00 60 = -65440
    
    /** NetTest - Network test */
    const val NET_TEST = (0xFFFF0061).toInt()                   // Wire: FF FF 00 61 = -65439
    
    /** StateSave - State save */
    const val STATE_SAVE = (0xFFFF0062).toInt()                 // Wire: FF FF 00 62 = -65438
    
    /** SubscribeLoad - Subscribe load */
    const val SUBSCRIBE_LOAD = (0xFFFF0063).toInt()             // Wire: FF FF 00 63 = -65437
    
    /** UnsubscribeLoad - Unsubscribe load */
    const val UNSUBSCRIBE_LOAD = (0xFFFF0064).toInt()           // Wire: FF FF 00 64 = -65436

    // --- Logging Messages ---
    /** LogTextMessage - Log text message */
    const val LOG_TEXT_MESSAGE = (0xFFFF0065).toInt()           // Wire: FF FF 00 65 = -65435
    
    /** LogDwellTime - Log dwell time */
    const val LOG_DWELL_TIME = (0xFFFF0066).toInt()             // Wire: FF FF 00 66 = -65434
    
    /** LogFailedMoneyTransaction - Log failed transaction */
    const val LOG_FAILED_MONEY_TRANSACTION = (0xFFFF0067).toInt() // Wire: FF FF 00 67 = -65433
    
    /** LogParcelChanges - Log parcel changes */
    const val LOG_PARCEL_CHANGES = (0xFFFF0068).toInt()         // Wire: FF FF 00 68 = -65432
    
    /** DataServerLogout - Data server logout */
    const val DATA_SERVER_LOGOUT = (0xFFFF0069).toInt()         // Wire: FF FF 00 69 = -65431

    // --- Kick/Eject Messages ---
    /** KickUser - Kick user */
    const val KICK_USER_MSG = (0xFFFF006A).toInt()              // Wire: FF FF 00 6A = -65430
    
    
    // --- Money Messages Extended ---
    /** MoneyBalanceRequest - Balance request */
    const val MONEY_BALANCE_REQUEST_MSG = (0xFFFF00E1).toInt()  // Wire: FF FF 00 E1 = -65311
    
    /** MoneyTransferBackend - Transfer backend */
    const val MONEY_TRANSFER_BACKEND = (0xFFFF00E8).toInt()     // Wire: FF FF 00 E8 = -65304
    
    /** EconomyDataRequest - Economy request */
    const val ECONOMY_DATA_REQUEST = (0xFFFF0018).toInt()       // Wire: FF FF 00 18 = -65512
    
    /** RequestPayPrice - Request pay price */
    const val REQUEST_PAY_PRICE_MSG = (0xFFFF00A1).toInt()      // Wire: FF FF 00 A1 = -65375

    // --- Parcel Request Messages ---
    /** ParcelAccessListRequest - Access list request */
    const val PARCEL_ACCESS_LIST_REQUEST = (0xFFFF00C9).toInt() // Wire: FF FF 00 C9 = -65335
    
    /** ParcelDwellRequest - Dwell request */
    const val PARCEL_DWELL_REQUEST = (0xFFFF00CA).toInt()       // Wire: FF FF 00 CA = -65334
    
    /** ParcelInfoRequest - Info request */
    const val PARCEL_INFO_REQUEST = (0xFFFF0038).toInt()        // Wire: FF FF 00 38 = -65480
    
    /** ParcelObjectOwnersRequest - Owners request */
    const val PARCEL_OBJECT_OWNERS_REQUEST = (0xFFFF003A).toInt() // Wire: FF FF 00 3A = -65478
    
    /** RequestParcelTransfer - Parcel transfer */
    const val REQUEST_PARCEL_TRANSFER = (0xFFFF00E4).toInt()    // Wire: FF FF 00 E4 = -65308
    
    /** UpdateParcel - Update parcel */
    const val UPDATE_PARCEL = (0xFFFF00E5).toInt()              // Wire: FF FF 00 E5 = -65307

    // --- Estate Request Messages ---
    /** EstateCovenantRequest - Covenant request */
    const val ESTATE_COVENANT_REQUEST = (0xFFFF00CB).toInt()    // Wire: FF FF 00 CB = -65333

    // --- Name/Value Messages ---
    /** NameValuePair - Name value pair */
    const val NAME_VALUE_PAIR = (0xFFFF0091).toInt()            // Wire: FF FF 00 91 = -65391
    
    /** RemoveNameValuePair - Remove name value */
    const val REMOVE_NAME_VALUE_PAIR = (0xFFFF0092).toInt()     // Wire: FF FF 00 92 = -65390

    // --- CPU/System Messages ---
    /** SetCPURatio - Set CPU ratio */
    const val SET_CPU_RATIO = (0xFFFF0093).toInt()              // Wire: FF FF 00 93 = -65389
    
    /** SetSimPresenceInDatabase - Sim presence */
    const val SET_SIM_PRESENCE_IN_DATABASE = (0xFFFF00F3).toInt() // Wire: FF FF 00 F3 = -65293
    
    /** SetSimStatusInDatabase - Sim status */
    const val SET_SIM_STATUS_IN_DATABASE = (0xFFFF00F4).toInt() // Wire: FF FF 00 F4 = -65292

    // =====================================
    // PHASE 5: 100 Additional Message Handlers
    // =====================================

    // --- Agent Movement Messages ---
        
        
        
        
        
    
    // --- Object Request Messages ---
        
        
    /** ObjectDeselect - Deselect object */
    const val OBJECT_DESELECT_MSG = (0xFFFF0081).toInt()         // Wire: FF FF 00 81 = -65407
    
    /** ObjectGrab - Start grabbing object */
    const val OBJECT_GRAB_MSG = (0xFFFF0082).toInt()             // Wire: FF FF 00 82 = -65406
    
    /** ObjectGrabUpdate - Update grab position */
    const val OBJECT_GRAB_UPDATE_MSG = (0xFFFF0083).toInt()      // Wire: FF FF 00 83 = -65405
    
    /** ObjectDeGrab - Release grabbed object */
    const val OBJECT_DE_GRAB = (0xFFFF0084).toInt()              // Wire: FF FF 00 84 = -65404
    
    /** ObjectSpinStart - Start spinning object */
    const val OBJECT_SPIN_START_MSG = (0xFFFF0085).toInt()       // Wire: FF FF 00 85 = -65403
    
    /** ObjectSpinUpdate - Update spin */
    const val OBJECT_SPIN_UPDATE_MSG = (0xFFFF0086).toInt()      // Wire: FF FF 00 86 = -65402
    
    /** ObjectSpinStop - Stop spinning object */
    const val OBJECT_SPIN_STOP_MSG = (0xFFFF0087).toInt()        // Wire: FF FF 00 87 = -65401
    
        
        
        
        
        
    
    // --- Object Update Messages Extended ---
        
        
    /** ObjectPosition - Update position */
    const val OBJECT_POSITION_MSG = 65284                        // Wire: FF 04 (medium frequency)

    // --- Disable Simulator ---
    
    // --- Sound Messages Extended ---
    /** SoundTrigger - High frequency sound trigger */
    const val SOUND_TRIGGER_HF = 29                              // Wire: 1D (high frequency)
    
    /** StopSound - Stop playing sound */
    const val STOP_SOUND = (0xFFFF00C3).toInt()                  // Wire: FF FF 00 C3 = -65341
    
    /** SoundPreload - Preload sound */
    const val SOUND_PRELOAD = (0xFFFF00C4).toInt()               // Wire: FF FF 00 C4 = -65340
    
    /** SoundGainChange - Sound volume change */
    const val SOUND_GAIN_CHANGE = (0xFFFF00C5).toInt()           // Wire: FF FF 00 C5 = -65339

    // --- Animation Messages ---
        
    /** AgentRequestAnimation - Request animation */
    const val AGENT_REQUEST_ANIMATION = (0xFFFF0074).toInt()     // Wire: FF FF 00 74 = -65420
    
    /** AvatarAnimationDone - Animation completed */
    const val AVATAR_ANIMATION_DONE = (0xFFFF0075).toInt()       // Wire: FF FF 00 75 = -65419

    // --- Gesture Messages ---
        
        
    /** GestureRequest - Request gesture */
    const val GESTURE_REQUEST = (0xFFFF0185).toInt()             // Wire: FF FF 01 85 = -65147
    
    /** GestureResponse - Gesture response */
    const val GESTURE_RESPONSE = (0xFFFF0186).toInt()            // Wire: FF FF 01 86 = -65146

    // --- Appearance Messages Extended ---
        
    /** SetFollowCamPropertiesMsg - Follow cam properties */
    const val SET_FOLLOW_CAM_PROPERTIES_MSG = (0xFFFF009F).toInt() // Wire: FF FF 00 9F = -65377
    
    /** ClearFollowCamPropertiesMsg - Clear follow cam */
    const val CLEAR_FOLLOW_CAM_PROPERTIES_MSG = (0xFFFF00A0).toInt() // Wire: FF FF 00 A0 = -65376

    // --- Attachment Messages Extended ---
    /** ObjectAttachResponse - Attachment response */
    const val OBJECT_ATTACH_RESPONSE = (0xFFFF0124).toInt()      // Wire: FF FF 01 24 = -65244
    
    /** AttachmentIntoInventory - Attachment to inventory */
    const val ATTACHMENT_INTO_INVENTORY = (0xFFFF0126).toInt()   // Wire: FF FF 01 26 = -65242
    
    /** AttachFromInventory - Attach from inventory */
    const val ATTACH_FROM_INVENTORY = (0xFFFF0127).toInt()       // Wire: FF FF 01 27 = -65241

    // --- User Data Messages ---
    /** UserInfoReqMsg - User info request */
    const val USER_INFO_REQ_MSG = (0xFFFF01AF).toInt()           // Wire: FF FF 01 AF = -65105
    
    /** UserInfoReplyMsg - User info reply */
    const val USER_INFO_REPLY_MSG = (0xFFFF01B0).toInt()         // Wire: FF FF 01 B0 = -65104
    
    /** UpdateUserInfoMsg - Update user info */
    const val UPDATE_USER_INFO_MSG = (0xFFFF01B1).toInt()        // Wire: FF FF 01 B1 = -65103

    // --- Friendship Messages Extended ---
        
        
    /** TrackAgentSession - Track agent session */
    const val TRACK_AGENT_SESSION = (0xFFFF014A).toInt()         // Wire: FF FF 01 4A = -65206
    
    /** OfferFriendship - Offer friendship */
    const val OFFER_FRIENDSHIP = (0xFFFF014B).toInt()            // Wire: FF FF 01 4B = -65205
    
    /** FriendshipOffered - Friendship offered */
    const val FRIENDSHIP_OFFERED = (0xFFFF014C).toInt()          // Wire: FF FF 01 4C = -65204

    // --- Group Messages Extended ---
        
        
        
    /** GroupNoticeRequest - Group notice request */
    const val GROUP_NOTICE_REQUEST_MSG = (0xFFFF0035).toInt()    // Wire: FF FF 00 35 = -65483
    
    /** GroupNoticesResponse - Group notices response */
    const val GROUP_NOTICES_RESPONSE = (0xFFFF0037).toInt()      // Wire: FF FF 00 37 = -65481

    // --- Script Control Messages ---
    /** ScriptControlChange - Control change */
    const val SCRIPT_CONTROL_CHANGE_MSG = (0xFFFF00C1).toInt()   // Wire: FF FF 00 C1 = -65343

    // --- Environment Messages ---
    /** SimulatorViewerTimeMessageMsg - Viewer time */
    const val SIMULATOR_VIEWER_TIME_MESSAGE_MSG = (0xFFFF0013).toInt() // Wire: FF FF 00 13 = -65517
    
    /** WindLightSettingsUpdate - Windlight settings */
    const val WINDLIGHT_SETTINGS_UPDATE = (0xFFFF0019).toInt()   // Wire: FF FF 00 19 = -65511
    
    /** ParcelEnvironmentBlock - Parcel environment */
    const val PARCEL_ENVIRONMENT_BLOCK = (0xFFFF001D).toInt()    // Wire: FF FF 00 1D = -65507
    
    /** SetEnvironment - Set environment */
    const val SET_ENVIRONMENT = (0xFFFF001E).toInt()             // Wire: FF FF 00 1E = -65506

    // --- Notecard/Script Edit Messages ---
    /** UpdateTaskInventoryNotecardItem - Update notecard */
    const val UPDATE_TASK_INVENTORY_NOTECARD_ITEM = (0xFFFF0136).toInt() // Wire: FF FF 01 36 = -65226
    
    /** UpdateNotecardAgentInventory - Update agent notecard */
    const val UPDATE_NOTECARD_AGENT_INVENTORY = (0xFFFF0137).toInt() // Wire: FF FF 01 37 = -65225
    
    /** UpdateGestureAgentInventory - Update gesture */
    const val UPDATE_GESTURE_AGENT_INVENTORY = (0xFFFF0138).toInt() // Wire: FF FF 01 38 = -65224
    
    /** UpdateScriptAgent - Update script agent */
    const val UPDATE_SCRIPT_AGENT = (0xFFFF0139).toInt()         // Wire: FF FF 01 39 = -65223
    
    /** UpdateScriptTask - Update script task */
    const val UPDATE_SCRIPT_TASK = (0xFFFF013A).toInt()          // Wire: FF FF 01 3A = -65222
    
    /** ScriptSensorRemove - Remove sensor */
    const val SCRIPT_SENSOR_REMOVE = (0xFFFF013B).toInt()        // Wire: FF FF 01 3B = -65221

    // --- Autopilot Messages ---
    /** Autopilot - Start autopilot */
    const val AUTOPILOT = (0xFFFF0070).toInt()                   // Wire: FF FF 00 70 = -65424
    
    /** AutopilotCancel - Cancel autopilot */
    const val AUTOPILOT_CANCEL = (0xFFFF0071).toInt()            // Wire: FF FF 00 71 = -65423

    // --- Terrain Messages ---
    /** TerrainHeightData - Terrain height */
    const val TERRAIN_HEIGHT_DATA = (0xFFFF01A3).toInt()         // Wire: FF FF 01 A3 = -65117

    // --- God Mode Messages ---
        
        
        
    /** GodDeleteSim - God delete sim */
    const val GOD_DELETE_SIM = (0xFFFF006F).toInt()              // Wire: FF FF 00 6F = -65425
    
        
        
    /** SimOwnerRequest - Sim owner request */
    const val SIM_OWNER_REQUEST = (0xFFFF00A6).toInt()           // Wire: FF FF 00 A6 = -65370
    
    /** SimOwnerResponse - Sim owner response */
    const val SIM_OWNER_RESPONSE = (0xFFFF00A7).toInt()          // Wire: FF FF 00 A7 = -65369

    // --- Estate Manager Messages ---
        
    /** EstateChangeInfo - Estate change info */
    const val ESTATE_CHANGE_INFO = (0xFFFF00CD).toInt()          // Wire: FF FF 00 CD = -65331
    
    /** EstateExperienceReply - Estate experience reply */
    const val ESTATE_EXPERIENCE_REPLY = (0xFFFF00CE).toInt()     // Wire: FF FF 00 CE = -65330

    // --- Land Bank Messages ---
    /** LandBuy - Buy land */
    const val LAND_BUY = (0xFFFF00E6).toInt()                    // Wire: FF FF 00 E6 = -65306
    
    /** LandBuyPass - Buy land pass */
    const val LAND_BUY_PASS = (0xFFFF00E7).toInt()               // Wire: FF FF 00 E7 = -65305

    // --- Asset/Transfer Messages Extended ---
    /** AssetInfoRequest - Asset info request */
    const val ASSET_INFO_REQUEST = (0xFFFF019F).toInt()          // Wire: FF FF 01 9F = -65121
    
    /** AssetInfoResponse - Asset info response */
    const val ASSET_INFO_RESPONSE = (0xFFFF01A0).toInt()         // Wire: FF FF 01 A0 = -65120
    
    /** MapLayerRequest - Map layer request */
    const val MAP_LAYER_REQUEST_MSG = (0xFFFF01A4).toInt()       // Wire: FF FF 01 A4 = -65116
    
    /** MapLayerReply - Map layer reply */
    const val MAP_LAYER_REPLY_MSG = (0xFFFF01A5).toInt()         // Wire: FF FF 01 A5 = -65115

    // --- Agent Data Messages ---
        
    /** AgentDataUpdate - Agent data update */
    const val AGENT_DATA_UPDATE_MSG = (0xFFFF0182).toInt()       // Wire: FF FF 01 82 = -65150

    // --- Pick/Classified Messages Extended ---
        
    /** PickUpdateInfo - Update pick info */
    const val PICK_UPDATE_INFO = (0xFFFF00B4).toInt()            // Wire: FF FF 00 B4 = -65356
    
        
    
    // --- Interest List Messages ---
    /** InterestListRequest - Interest list request */
    const val INTEREST_LIST_REQUEST = (0xFFFF00AA).toInt()       // Wire: FF FF 00 AA = -65366
    
    /** InterestListReply - Interest list reply */
    const val INTEREST_LIST_REPLY = (0xFFFF00AB).toInt()         // Wire: FF FF 00 AB = -65365

    // --- Object Export Messages ---
    /** ExportDynaFile - Export dynamic file */
    const val EXPORT_DYNA_FILE = (0xFFFF0196).toInt()            // Wire: FF FF 01 96 = -65130
    
    /** ExportDynaFileRequest - Export request */
    const val EXPORT_DYNA_FILE_REQUEST = (0xFFFF0197).toInt()    // Wire: FF FF 01 97 = -65129

    // --- Upload Messages ---
    /** UploadBakedTexture - Upload baked texture */
    const val UPLOAD_BAKED_TEXTURE = (0xFFFF00AC).toInt()        // Wire: FF FF 00 AC = -65364
    
    /** UploadBakedTextureResult - Upload result */
    const val UPLOAD_BAKED_TEXTURE_RESULT = (0xFFFF00AD).toInt() // Wire: FF FF 00 AD = -65363

    // --- Object Permission Messages ---
    /** ObjectPermissionsRequest - Request permissions */
    const val OBJECT_PERMISSIONS_REQUEST = (0xFFFF008B).toInt()  // Wire: FF FF 00 8B = -65397
    
    /** ObjectPermissionsReply - Permissions reply */
    const val OBJECT_PERMISSIONS_REPLY = (0xFFFF008C).toInt()    // Wire: FF FF 00 8C = -65396

    // --- Agent Camera Messages ---
    /** AgentCameraConstraint - Camera constraint */
    const val AGENT_CAMERA_CONSTRAINT = (0xFFFF008D).toInt()     // Wire: FF FF 00 8D = -65395
    
    /** CameraConstraintMsg - Camera constraint message */
    const val CAMERA_CONSTRAINT_MSG = (0xFFFF008E).toInt()       // Wire: FF FF 00 8E = -65394

    // --- Voice Messages ---
    /** ProvisionVoiceAccountRequest - Voice account request */
    const val PROVISION_VOICE_ACCOUNT_REQUEST = (0xFFFF01B2).toInt() // Wire: FF FF 01 B2 = -65102
    
    /** ProvisionVoiceAccountReply - Voice account reply */
    const val PROVISION_VOICE_ACCOUNT_REPLY = (0xFFFF01B3).toInt() // Wire: FF FF 01 B3 = -65101
    
    /** ParcelVoiceInfoRequest - Parcel voice request */
    const val PARCEL_VOICE_INFO_REQUEST = (0xFFFF01B4).toInt()   // Wire: FF FF 01 B4 = -65100
    
    /** ParcelVoiceInfoReply - Parcel voice reply */
    const val PARCEL_VOICE_INFO_REPLY = (0xFFFF01B5).toInt()     // Wire: FF FF 01 B5 = -65099

    // --- Experience Messages ---
    /** ExperienceInfoRequest - Experience info request */
    const val EXPERIENCE_INFO_REQUEST = (0xFFFF01B6).toInt()     // Wire: FF FF 01 B6 = -65098
    
    /** ExperienceInfoReply - Experience info reply */
    const val EXPERIENCE_INFO_REPLY = (0xFFFF01B7).toInt()       // Wire: FF FF 01 B7 = -65097
    
    /** ExperiencePermissionRequest - Experience permission */
    const val EXPERIENCE_PERMISSION_REQUEST = (0xFFFF01B8).toInt() // Wire: FF FF 01 B8 = -65096
    
    /** ExperiencePermissionReply - Experience permission reply */
    const val EXPERIENCE_PERMISSION_REPLY = (0xFFFF01B9).toInt() // Wire: FF FF 01 B9 = -65095

    // --- Region Object Messages ---
    /** RegionObjectUpdate - Region object update */
    const val REGION_OBJECT_UPDATE = (0xFFFF008F).toInt()        // Wire: FF FF 00 8F = -65393
    
    /** RegionObjectComplete - Region object complete */
    const val REGION_OBJECT_COMPLETE = (0xFFFF0090).toInt()      // Wire: FF FF 00 90 = -65392

    // --- Pathfinding Messages ---
    /** NavMeshStatusRequest - NavMesh status request */
    const val NAV_MESH_STATUS_REQUEST = (0xFFFF01BA).toInt()     // Wire: FF FF 01 BA = -65094
    
    /** NavMeshStatusReply - NavMesh status reply */
    const val NAV_MESH_STATUS_REPLY = (0xFFFF01BB).toInt()       // Wire: FF FF 01 BB = -65093
    
    /** CharacterPropertiesRequest - Character properties */
    const val CHARACTER_PROPERTIES_REQUEST = (0xFFFF01BC).toInt() // Wire: FF FF 01 BC = -65092
    
    /** CharacterPropertiesReply - Character properties reply */
    const val CHARACTER_PROPERTIES_REPLY = (0xFFFF01BD).toInt()  // Wire: FF FF 01 BD = -65091

    // --- AO (Animation Override) Messages ---
    /** AgentAnimationOverride - Animation override */
    const val AGENT_ANIMATION_OVERRIDE = (0xFFFF0076).toInt()    // Wire: FF FF 00 76 = -65418
    
    /** ClearAnimationOverride - Clear override */
    const val CLEAR_ANIMATION_OVERRIDE = (0xFFFF0077).toInt()    // Wire: FF FF 00 77 = -65417

    // =====================================
    // Phase 6: Final Remaining Handlers
    // =====================================

    // --- Chat Extended ---
    /** ChatEvent - Chat event */
    const val CHAT_EVENT = (0xFFFF00A4).toInt()                  // Wire: FF FF 00 A4 = -65372
    
    /** ChatPass - Chat pass */
    const val CHAT_PASS = (0xFFFF00A5).toInt()                   // Wire: FF FF 00 A5 = -65371
    
    // --- Circuit ---
    /** CircuitReady - Circuit ready notification */
    const val CIRCUIT_READY = (0xFFFF0005).toInt()               // Wire: FF FF 00 05 = -65531
    
    // --- Data Home Location ---
    /** DataHomeLocationRequest - Request home location */
    const val DATA_HOME_LOCATION_REQUEST = (0xFFFF01A3).toInt()  // Wire: FF FF 01 A3 = -65117
    
    // --- Global Options ---
    /** GlobalOptionsChange - Global options change */
    const val GLOBAL_OPTIONS_CHANGE = (0xFFFF01A4).toInt()       // Wire: FF FF 01 A4 = -65116
    
    // --- UUID Request Messages ---
    /** UUIDNameRequest - Request name for UUID */
    const val UUID_NAME_REQUEST = (0xFFFF00ED).toInt()           // Wire: FF FF 00 ED = -65299
    
    /** UUIDGroupNameRequest - Request group name for UUID */
    const val UUID_GROUP_NAME_REQUEST = (0xFFFF00EF).toInt()     // Wire: FF FF 00 EF = -65297
}

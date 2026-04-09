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
 * These values match the SL protocol's internal representation for compatibility.
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
    const val AGENT_THROTTLE = (0xFFFF0051).toInt()            // Wire: FF FF 00 51 (Lumiya-verified: put(0), put(81))
    
    /** ChatFromSimulator - Chat message from simulator */
    const val CHAT_FROM_SIMULATOR = (0xFFFF008B).toInt()       // Wire: FF FF 00 8B (Lumiya-verified: put(0), put(-117))
    
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
    const val AGENT_ANIMATION = (0xFFFF021F).toInt()  // Wire: FF FF 02 1F = -64993 (Linkpoint-specific, unique reserved ID)
    
    /** AgentSetAppearance - Update avatar appearance */
    const val AGENT_SET_APPEARANCE = (0xFFFF0054).toInt()  // Wire: FF FF 00 54 = -65452 (Lumiya: AgentSetAppearance)
    
    /** AgentIsNowWearing - Notify what agent is wearing */
    const val AGENT_IS_NOW_WEARING = (0xFFFF017F).toInt()      // Wire: FF FF 01 7F (Lumiya-verified: put(1), put(127))
    
    /** AgentRequestSit - Request to sit on object (HIGH frequency) */
    const val AGENT_REQUEST_SIT = 6                            // Wire: 06 (Lumiya-verified: put(6), high frequency)
    
    /** AgentSit - Confirm sitting */
    const val AGENT_SIT = 7  // Wire: 0x07 (high frequency) (Lumiya: AgentSit)
    
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
    const val OBJECT_SELECT = (0xFFFF006E).toInt()  // Wire: FF FF 00 6E = -65426 (Lumiya: ObjectSelect)
    
    /** MultipleObjectUpdate - Update multiple objects */
    const val MULTIPLE_OBJECT_UPDATE = 65282  // Wire: FF 02 = 2 | 65280 (medium frequency) (Lumiya: MultipleObjectUpdate)
    
    /** RezObject - Rez object from inventory */
    const val REZ_OBJECT = (0xFFFF0125).toInt()  // Wire: FF FF 01 25 = -65243 (Lumiya: RezObject)
    
    /** DeRezObject - Take/delete object */
    const val DEREZ_OBJECT = (0xFFFF0123).toInt()  // Wire: FF FF 01 23 = -65245 (Lumiya naming mismatch fix)
    
    /** ObjectDelete - Delete objects */
    const val OBJECT_DELETE = (0xFFFF0059).toInt()  // Wire: FF FF 00 59 = -65447 (Lumiya: ObjectDelete)
    
    /** ObjectLink - Link objects */
    const val OBJECT_LINK = (0xFFFF0073).toInt()  // Wire: FF FF 00 73 = -65421 (Lumiya: ObjectLink)
    
    /** ObjectDelink - Unlink objects */
    const val OBJECT_DELINK = (0xFFFF0074).toInt()  // Wire: FF FF 00 74 = -65420 (Lumiya: ObjectDelink)
    
    /** ObjectName - Set object name */
    const val OBJECT_NAME = (0xFFFF006B).toInt()  // Wire: FF FF 00 6B = -65429 (Lumiya: ObjectName)
    
    /** ObjectDescription - Set object description */
    const val OBJECT_DESCRIPTION = (0xFFFF006C).toInt()  // Wire: FF FF 00 6C = -65428 (Lumiya: ObjectDescription)
    
    /** ObjectGrab - Grab/touch object */
    const val OBJECT_GRAB = (0xFFFF0075).toInt()  // Wire: FF FF 00 75 = -65419 (Lumiya: ObjectGrab)
    
    /** ObjectDeGrab - Release object */
    const val OBJECT_DEGRAB = (0xFFFF0077).toInt()  // Wire: FF FF 00 77 = -65417 (Lumiya: ObjectDeGrab)
    
    /** RequestMultipleObjects - Request full object data for cached objects */
    const val REQUEST_MULTIPLE_OBJECTS = 65283  // Wire: FF 03 = 3 | 65280 (medium frequency) (Lumiya: RequestMultipleObjects)
    
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
    const val MOVE_INVENTORY_ITEM = (0xFFFF010C).toInt()  // Wire: FF FF 01 0C = -65268 (Lumiya: MoveInventoryItem)

    // =====================================
    // Teleport Messages
    // =====================================
    
    /** TeleportLandmarkRequest - Teleport to landmark */
    const val TELEPORT_LANDMARK_REQUEST = (0xFFFF0041).toInt()  // Wire: FF FF 00 41 = -65471 (Lumiya: TeleportLandmarkRequest)
    
    /** TeleportHomeRequest - Teleport home */
    const val TELEPORT_HOME_REQUEST = (0xFFFF023A).toInt()  // Wire: FF FF 02 3A = -64966 (Linkpoint-specific, unique reserved ID)
    
    /** TeleportLocationRequest - Teleport to location */
    const val TELEPORT_LOCATION_REQUEST = (0xFFFF003F).toInt()  // Wire: FF FF 00 3F = -65473 (Lumiya: TeleportLocationRequest)
    
    /** TeleportLureRequest - Accept teleport lure */
    const val TELEPORT_LURE_REQUEST = (0xFFFF0047).toInt()  // Wire: FF FF 00 47 = -65465 (Lumiya: TeleportLureRequest)
    
    /** StartLure - Send teleport offer */
    const val START_LURE = (0xFFFF0046).toInt()  // Wire: FF FF 00 46 = -65466 (Lumiya: StartLure)
    
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
    const val ACTIVATE_GROUP = (0xFFFF0170).toInt()  // Wire: FF FF 01 70 = -65168 (Lumiya: ActivateGroup)
    
    /** LeaveGroupRequest - Leave a group */
    const val LEAVE_GROUP_REQUEST = (0xFFFF015B).toInt()  // Wire: FF FF 01 5B = -65189 (Lumiya: LeaveGroupRequest)
    
    /** GroupProfileRequest - Request group profile */
    const val GROUP_PROFILE_REQUEST = (0xFFFF015F).toInt()  // Wire: FF FF 01 5F = -65185 (Lumiya: GroupProfileRequest)

    // =====================================
    // Friends Messages
    // =====================================
    
    /** TerminateFriendship - Remove friend */
    const val TERMINATE_FRIENDSHIP = (0xFFFF012C).toInt()  // Wire: FF FF 01 2C = -65236 (Lumiya: TerminateFriendship)
    
    /** GrantUserRights - Grant/revoke friend permissions */
    const val GRANT_USER_RIGHTS = (0xFFFF0140).toInt()  // Wire: FF FF 01 40 = -65216 (Lumiya: GrantUserRights)
    
    /** FindAgent - Find agent location */
    const val FIND_AGENT = (0xFFFF0100).toInt()  // Wire: FF FF 01 00 = -65280 (Lumiya: FindAgent)

    // =====================================
    // Parcel Messages
    // =====================================
    
    /** ParcelBuy - Purchase parcel */
    const val PARCEL_BUY = (0xFFFF00D5).toInt()  // Wire: FF FF 00 D5 = -65323 (Lumiya: ParcelBuy)
    
    /** ParcelDeedToGroup - Deed parcel to group */
    const val PARCEL_DEED_TO_GROUP = (0xFFFF00CF).toInt()  // Wire: FF FF 00 CF = -65329 (Lumiya: ParcelDeedToGroup)
    
    /** ParcelRelease - Abandon parcel */
    const val PARCEL_RELEASE = (0xFFFF00D4).toInt()  // Wire: FF FF 00 D4 = -65324 (Lumiya: ParcelRelease)
    
    /** ParcelPropertiesUpdate - Update parcel properties */
    const val PARCEL_PROPERTIES_UPDATE = (0xFFFF00C6).toInt()  // Wire: FF FF 00 C6 = -65338 (Lumiya: ParcelPropertiesUpdate)
    
    /** ParcelReturnObjects - Return objects on parcel */
    const val PARCEL_RETURN_OBJECTS = (0xFFFF00C7).toInt()  // Wire: FF FF 00 C7 = -65337 (Lumiya: ParcelReturnObjects)
    
    /** ParcelAccessListUpdate - Update parcel access list */
    const val PARCEL_ACCESS_LIST_UPDATE = (0xFFFF00D9).toInt()  // Wire: FF FF 00 D9 = -65319 (Lumiya: ParcelAccessListUpdate)

    // =====================================
    // Estate Messages
    // =====================================
    
    /** EstateOwnerMessage - Estate management commands */
    const val ESTATE_OWNER_MESSAGE = (0xFFFF0104).toInt()  // Wire: FF FF 01 04 = -65276 (Lumiya: EstateOwnerMessage)
    
    /** FreezeUser - Freeze/unfreeze user */
    const val FREEZE_USER = (0xFFFF00A8).toInt()  // Wire: FF FF 00 A8 = -65368 (Lumiya: FreezeUser)

    // =====================================
    // Gesture Messages
    // =====================================

    /** ActivateGestures - Activate a gesture */
    const val ACTIVATE_GESTURES = (0xFFFF013C).toInt()  // Wire: FF FF 01 3C = -65220 (Lumiya: ActivateGestures)

    /** DeactivateGestures - Deactivate a gesture */
    const val DEACTIVATE_GESTURES = (0xFFFF013D).toInt()       // Wire: FF FF 01 3D (Lumiya-verified: put(1), put(61))

    // =====================================
    // Inventory Messages
    // =====================================

    /** CopyInventoryItem - Copy item to new folder */
    const val COPY_INVENTORY_ITEM = (0xFFFF010D).toInt()  // Wire: FF FF 01 0D = -65267 (Lumiya: CopyInventoryItem)

    /** UpdateInventoryItem - Update item properties */
    const val UPDATE_INVENTORY_ITEM = (0xFFFF010A).toInt()     // Wire: FF FF 01 0A

    /** CreateInventoryFolder - Create a new inventory folder */
    const val CREATE_INVENTORY_FOLDER = (0xFFFF0111).toInt()   // Wire: FF FF 01 11

    /** RezSingleAttachmentFromInv - Wear object from inventory */
    const val REZ_SINGLE_ATTACHMENT_FROM_INV = (0xFFFF018B).toInt()  // Wire: FF FF 01 8B = -65141 (Lumiya: RezSingleAttachmentFromInv)

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
    const val SCRIPT_QUESTION = (0xFFFF00BC).toInt()  // Wire: FF FF 00 BC = -65348 (Lumiya: ScriptQuestion)
    
    /** LoadURL - Open URL request from script */
    const val LOAD_URL = (0xFFFF00C2).toInt()  // Wire: FF FF 00 C2 = -65342 (Lumiya naming mismatch fix)
    
    // --- Economy Messages ---
    /** MoneyBalanceReply - L$ balance update */
    const val MONEY_BALANCE_REPLY = (0xFFFF013A).toInt()  // Wire: FF FF 01 3A = -65222 (Lumiya: MoneyBalanceReply)
    
    /** MoneyBalanceRequest - Request L$ balance */
    const val MONEY_BALANCE_REQUEST = (0xFFFF0139).toInt()  // Wire: FF FF 01 39 = -65223 (Lumiya: MoneyBalanceRequest)
    
    /** EconomyData - Region economy info */
    const val ECONOMY_DATA = (0xFFFF0019).toInt()               // Wire: FF FF 00 19 = -65511
    
    // --- Inventory Messages ---
    /** InventoryDescendents - Folder contents */
    const val INVENTORY_DESCENDENTS = (0xFFFF0116).toInt()  // Wire: FF FF 01 16 = -65258 (Lumiya: InventoryDescendents)
    
    /** FetchInventoryReply - Item details */
    const val FETCH_INVENTORY_REPLY = (0xFFFF0118).toInt()  // Wire: FF FF 01 18 = -65256 (Lumiya: FetchInventoryReply)
    
    /** BulkUpdateInventory - Batch inventory update */
    const val BULK_UPDATE_INVENTORY = (0xFFFF0119).toInt()      // Wire: FF FF 01 19 (Lumiya-verified: put(1), put(25))
    
    /** UpdateCreateInventoryItem - Item created notification */
    const val UPDATE_CREATE_INVENTORY_ITEM = (0xFFFF010B).toInt() // Wire: FF FF 01 0B (correct per Lumiya)
    
    /** RemoveInventoryItem - Item removed */
    const val REMOVE_INVENTORY_ITEM = (0xFFFF010E).toInt()      // Wire: FF FF 01 0E = -65266
    
    /** RemoveInventoryFolder - Folder removed */
    const val REMOVE_INVENTORY_FOLDER = (0xFFFF0114).toInt()  // Wire: FF FF 01 14 = -65260 (Lumiya: RemoveInventoryFolder)
    
    // --- Avatar/Appearance Messages ---
    /** AvatarAppearance - Full avatar appearance */
    const val AVATAR_APPEARANCE = (0xFFFF009E).toInt()          // Wire: FF FF 00 9E = -65378
    
    /** AgentWearablesUpdate - Outfit update */
    const val AGENT_WEARABLES_UPDATE = (0xFFFF017E).toInt()     // Wire: FF FF 01 7E (Lumiya-verified: put(1), put(126))
    
    /** AgentCachedTexture - Baked texture cache request */
    const val AGENT_CACHED_TEXTURE = (0xFFFF0180).toInt()       // Wire: FF FF 01 80 (Lumiya-verified: put(1), put(-128))
    
    /** AgentCachedTextureResponse - Baked texture cache response */
    const val AGENT_CACHED_TEXTURE_RESPONSE = (0xFFFF0181).toInt() // Wire: FF FF 01 81 (Lumiya-verified: put(1), put(-127))
    
    /** AvatarPropertiesReply - Avatar profile data */
    const val AVATAR_PROPERTIES_REPLY = (0xFFFF00AB).toInt()  // Wire: FF FF 00 AB = -65365 (Lumiya: AvatarPropertiesReply)
    
    /** AvatarPropertiesRequest - Request avatar profile */
    const val AVATAR_PROPERTIES_REQUEST = (0xFFFF00A9).toInt()  // Wire: FF FF 00 A9 = -65367 (Lumiya: AvatarPropertiesRequest)
    
    /** AvatarInterestsReply - Avatar interests/picks */
    const val AVATAR_INTERESTS_REPLY = (0xFFFF00AC).toInt()  // Wire: FF FF 00 AC = -65364 (Lumiya: AvatarInterestsReply)

    /** AvatarInterestsRequest - Request avatar interests */
    const val AVATAR_INTERESTS_REQUEST = (0xFFFF00AE).toInt()  // Legacy manager compatibility ID

    /** AvatarNotesRequest - Request avatar notes */
    const val AVATAR_NOTES_REQUEST = (0xFFFF00B2).toInt()      // Legacy manager compatibility ID

    /** AvatarPicksRequest - Request avatar picks */
    const val AVATAR_PICKS_REQUEST = (0xFFFF00B4).toInt()      // Legacy manager compatibility ID

    /** AvatarClassifiedsRequest - Request avatar classifieds */
    const val AVATAR_CLASSIFIEDS_REQUEST = (0xFFFF00B5).toInt() // Legacy manager compatibility ID
    
    /** AvatarGroupsReply - Avatar group memberships */
    const val AVATAR_GROUPS_REPLY = (0xFFFF00AD).toInt()  // Wire: FF FF 00 AD = -65363 (Lumiya: AvatarGroupsReply)
    
    // --- Group Messages ---
    /** GroupProfileReply - Group profile data */
    const val GROUP_PROFILE_REPLY = (0xFFFF0160).toInt()        // Wire: FF FF 01 60 = -65184
    
    /** GroupMembersReply - Group member list */
    const val GROUP_MEMBERS_REPLY = (0xFFFF016F).toInt()  // Wire: FF FF 01 6F = -65169 (Lumiya: GroupMembersReply)
    
    /** GroupRoleDataReply - Group roles */
    const val GROUP_ROLE_DATA_REPLY = (0xFFFF0174).toInt()  // Wire: FF FF 01 74 = -65164 (Lumiya: GroupRoleDataReply)
    
    /** GroupTitlesReply - Group titles */
    const val GROUP_TITLES_REPLY = (0xFFFF0178).toInt()  // Wire: FF FF 01 78 = -65160 (Lumiya: GroupTitlesReply)
    
    /** GroupNoticeAdd - New group notice */
    const val GROUP_NOTICE_ADD = (0xFFFF003D).toInt()  // Wire: FF FF 00 3D = -65475 (Lumiya: GroupNoticeAdd)
    
    /** AgentGroupDataUpdate - Agent's group list */
    const val AGENT_GROUP_DATA_UPDATE = (0xFFFF0185).toInt()  // Wire: FF FF 01 85 = -65147 (Lumiya: AgentGroupDataUpdate)
    
    // --- Friends Messages ---
    /** AcceptFriendship - Accept friend request */
    const val ACCEPT_FRIENDSHIP = (0xFFFF0129).toInt()  // Wire: FF FF 01 29 = -65239 (Lumiya: AcceptFriendship)
    
    /** DeclineFriendship - Decline friend request */
    const val DECLINE_FRIENDSHIP = (0xFFFF012A).toInt()  // Wire: FF FF 01 2A = -65238 (Lumiya: DeclineFriendship)
    
    /** FormFriendship - Friend request sent */
    const val FORM_FRIENDSHIP = (0xFFFF012B).toInt()  // Wire: FF FF 01 2B = -65237 (Lumiya: FormFriendship)
    
    // --- Map Messages ---
    /** MapBlockReply - Map tile data */
    const val MAP_BLOCK_REPLY = (0xFFFF0199).toInt()  // Wire: FF FF 01 99 = -65127 (Lumiya: MapBlockReply)
    
    /** MapItemReply - Map markers/items */
    const val MAP_ITEM_REPLY = (0xFFFF019B).toInt()  // Wire: FF FF 01 9B = -65125 (Lumiya: MapItemReply)
    
    /** MapLayerReply - Map layer data */
    const val MAP_LAYER_REPLY = (0xFFFF0196).toInt()  // Wire: FF FF 01 96 = -65130 (Lumiya: MapLayerReply)
    
    // --- Search Messages ---
    /** DirPlacesReply - Places search results */
    const val DIR_PLACES_REPLY = (0xFFFF0023).toInt()  // Wire: FF FF 00 23 = -65501 (Lumiya: DirPlacesReply)
    
    /** DirPeopleReply - People search results */
    const val DIR_PEOPLE_REPLY = (0xFFFF0024).toInt()  // Wire: FF FF 00 24 = -65500 (Lumiya: DirPeopleReply)
    
    /** DirGroupsReply - Groups search results */
    const val DIR_GROUPS_REPLY = (0xFFFF0026).toInt()  // Wire: FF FF 00 26 = -65498 (Lumiya: DirGroupsReply)
    
    /** DirEventsReply - Events search results */
    const val DIR_EVENTS_REPLY = (0xFFFF0025).toInt()  // Wire: FF FF 00 25 = -65499 (Lumiya: DirEventsReply)
    
    /** DirLandReply - Land search results */
    const val DIR_LAND_REPLY = (0xFFFF0032).toInt()  // Wire: FF FF 00 32 = -65486 (Lumiya: DirLandReply)
    
    /** DirClassifiedReply - Classified search results */
    const val DIR_CLASSIFIED_REPLY = (0xFFFF0029).toInt()       // Wire: FF FF 00 29 = -65495
    
    // --- Region/Estate Messages ---
    /** RegionInfo - Region settings */
    const val REGION_INFO = (0xFFFF008E).toInt()                // Wire: FF FF 00 8E (Lumiya-verified: put(0), put(-114))
    
    /** SimStats - Simulator statistics */
    const val SIM_STATS = (0xFFFF008C).toInt()                  // Wire: FF FF 00 8C (Lumiya-verified: put(0), put(-116))
    
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
    const val IMAGE_NOT_IN_DATABASE = (0xFFFF0056).toInt()      // Wire: FF FF 00 56 (Lumiya-verified: put(0), put(86))
    
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
    const val AGENT_PAUSE = (0xFFFF004E).toInt()                // Wire: FF FF 00 4E (Lumiya-verified: put(0), put(78))
    
    /** AgentResume - Resume agent updates */
    const val AGENT_RESUME = (0xFFFF004F).toInt()               // Wire: FF FF 00 4F (Lumiya-verified: follows AgentPause at 0x4E)
    
    /** AgentFOV - Agent field of view */
    const val AGENT_FOV = (0xFFFF0052).toInt()  // Wire: FF FF 00 52 = -65454 (Lumiya naming mismatch fix)
    
    /** AgentHeightWidth - Agent viewport size */
    const val AGENT_HEIGHT_WIDTH = (0xFFFF0053).toInt()  // Wire: FF FF 00 53 = -65453 (Lumiya: AgentHeightWidth)
    
    /** AgentQuitCopy - Agent quit notification */
    const val AGENT_QUIT_COPY = (0xFFFF0055).toInt()  // Wire: FF FF 00 55 = -65451 (Lumiya: AgentQuitCopy)
    
    /** AgentDropGroup - Agent dropped from group */
    const val AGENT_DROP_GROUP = (0xFFFF0186).toInt()  // Wire: FF FF 01 86 = -65146 (Lumiya: AgentDropGroup)
    
    /** AgentWearablesRequest - Request wearables */
    const val AGENT_WEARABLES_REQUEST = (0xFFFF017D).toInt()  // Wire: FF FF 01 7D = -65155 (Lumiya: AgentWearablesRequest)
    
    /** AgentDataUpdateRequest - Request agent data */
    const val AGENT_DATA_UPDATE_REQUEST = (0xFFFF0182).toInt()  // Wire: FF FF 01 82 = -65150 (Lumiya: AgentDataUpdateRequest)

    // --- Avatar Messages ---
    /** AvatarTextureUpdate - Avatar texture update */
    const val AVATAR_TEXTURE_UPDATE = (0xFFFF0004).toInt()      // Wire: FF FF 00 04 = -65532
    
    /** AvatarPickerReply - Avatar picker results */
    const val AVATAR_PICKER_REPLY = (0xFFFF001C).toInt()        // Wire: FF FF 00 1C = -65508
    
    /** AvatarPickerRequest - Request avatar picker */
    const val AVATAR_PICKER_REQUEST = (0xFFFF001A).toInt()  // Wire: FF FF 00 1A = -65510 (Lumiya: AvatarPickerRequest)
    
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
    const val GROUP_ROLE_MEMBERS_REPLY = (0xFFFF0176).toInt()  // Wire: FF FF 01 76 = -65162 (Lumiya: GroupRoleMembersReply)
    
    /** GroupNoticesListReply - Group notices list */
    const val GROUP_NOTICES_LIST_REPLY = (0xFFFF003B).toInt()  // Wire: FF FF 00 3B = -65477 (Lumiya: GroupNoticesListReply)
    
    /** GroupNoticeRequest - Request group notice */
    const val GROUP_NOTICE_REQUEST = (0xFFFF003C).toInt()  // Wire: FF FF 00 3C = -65476 (Lumiya: GroupNoticeRequest)
    
    /** CreateGroupReply - Create group result */
    const val CREATE_GROUP_REPLY = (0xFFFF0154).toInt()  // Wire: FF FF 01 54 = -65196 (Lumiya: CreateGroupReply)
    
    /** JoinGroupReply - Join group result */
    const val JOIN_GROUP_REPLY = (0xFFFF0158).toInt()  // Wire: FF FF 01 58 = -65192 (Lumiya: JoinGroupReply)
    
    /** LeaveGroupReply - Leave group result */
    const val LEAVE_GROUP_REPLY = (0xFFFF015C).toInt()  // Wire: FF FF 01 5C = -65188 (Lumiya: LeaveGroupReply)
    
    /** EjectGroupMemberReply - Eject member result */
    const val EJECT_GROUP_MEMBER_REPLY = (0xFFFF015A).toInt()  // Wire: FF FF 01 5A = -65190 (Lumiya: EjectGroupMemberReply)
    
    /** InviteGroupResponse - Group invite response */
    const val INVITE_GROUP_RESPONSE = (0xFFFF015E).toInt()  // Wire: FF FF 01 5E = -65186 (Lumiya: InviteGroupResponse)
    
    /** GroupAccountSummaryReply - Group account summary */
    const val GROUP_ACCOUNT_SUMMARY_REPLY = (0xFFFF0162).toInt()  // Wire: FF FF 01 62 = -65182 (Lumiya: GroupAccountSummaryReply)
    
    /** GroupAccountDetailsReply - Group account details */
    const val GROUP_ACCOUNT_DETAILS_REPLY = (0xFFFF0164).toInt()  // Wire: FF FF 01 64 = -65180 (Lumiya: GroupAccountDetailsReply)
    
    /** GroupAccountTransactionsReply - Group transactions */
    const val GROUP_ACCOUNT_TRANSACTIONS_REPLY = (0xFFFF0166).toInt()  // Wire: FF FF 01 66 = -65178 (Lumiya: GroupAccountTransactionsReply)
    
    /** GroupActiveProposalItemReply - Active proposal */
    const val GROUP_ACTIVE_PROPOSAL_ITEM_REPLY = (0xFFFF0168).toInt()  // Wire: FF FF 01 68 = -65176 (Lumiya: GroupActiveProposalItemReply)
    
    /** GroupVoteHistoryItemReply - Vote history */
    const val GROUP_VOTE_HISTORY_ITEM_REPLY = (0xFFFF016A).toInt()  // Wire: FF FF 01 6A = -65174 (Lumiya: GroupVoteHistoryItemReply)

    // --- Calling Card Messages ---
    /** OfferCallingCard - Offer calling card */
    const val OFFER_CALLING_CARD = (0xFFFF012D).toInt()  // Wire: FF FF 01 2D = -65235 (Lumiya: OfferCallingCard)
    
    /** AcceptCallingCard - Accept calling card */
    const val ACCEPT_CALLING_CARD = (0xFFFF012E).toInt()  // Wire: FF FF 01 2E = -65234 (Lumiya: AcceptCallingCard)
    
    /** DeclineCallingCard - Decline calling card */
    const val DECLINE_CALLING_CARD = (0xFFFF012F).toInt()  // Wire: FF FF 01 2F = -65233 (Lumiya: DeclineCallingCard)

    // --- Inventory Messages (Extended) ---
    /** FetchInventoryDescendents - Fetch folder contents */
    const val FETCH_INVENTORY_DESCENDENTS = (0xFFFF0115).toInt()  // Wire: FF FF 01 15 = -65259 (Lumiya: FetchInventoryDescendents)
    
    /** FetchInventory - Fetch item details */
    const val FETCH_INVENTORY = (0xFFFF0117).toInt()  // Wire: FF FF 01 17 = -65257 (Lumiya: FetchInventory)
    
    /** PurgeInventoryDescendents - Purge folder */
    const val PURGE_INVENTORY_DESCENDENTS = (0xFFFF011D).toInt() // Wire: FF FF 01 1D (Lumiya-verified: put(1), put(29))
    
    /** RemoveInventoryObjects - Remove multiple items */
    const val REMOVE_INVENTORY_OBJECTS = (0xFFFF011C).toInt()   // Wire: FF FF 01 1C (Lumiya-verified: put(1), put(28))
    
    /** InventoryAssetResponse - Asset fetch response */
    const val INVENTORY_ASSET_RESPONSE = (0xFFFF011B).toInt()  // Wire: FF FF 01 1B = -65253 (Lumiya: InventoryAssetResponse)
    
    /** UpdateInventoryFolder - Update folder */
    const val UPDATE_INVENTORY_FOLDER = (0xFFFF0112).toInt()  // Wire: FF FF 01 12 = -65262 (Lumiya: UpdateInventoryFolder)
    
    /** MoveInventoryFolder - Move folder */
    const val MOVE_INVENTORY_FOLDER = (0xFFFF0113).toInt()  // Wire: FF FF 01 13 = -65261 (Lumiya: MoveInventoryFolder)
    
    /** CopyInventoryFromNotecard - Copy from notecard */
    const val COPY_INVENTORY_FROM_NOTECARD = (0xFFFF0109).toInt() // Wire: FF FF 01 09 = -65271
    
    /** CreateInventoryItem - Create item */
    const val CREATE_INVENTORY_ITEM = (0xFFFF0131).toInt()  // Wire: FF FF 01 31 = -65231 (Lumiya: CreateInventoryItem)
    
    /** SaveAssetIntoInventory - Save asset to inventory */
    const val SAVE_ASSET_INTO_INVENTORY = (0xFFFF0110).toInt()  // Wire: FF FF 01 10 (Lumiya-verified: put(1), put(16))

    // --- Task/Object Inventory Messages ---
    /** RequestTaskInventory - Request object contents */
    const val REQUEST_TASK_INVENTORY = (0xFFFF0121).toInt()  // Wire: FF FF 01 21 = -65247 (Lumiya: RequestTaskInventory)
    
    /** ReplyTaskInventory - Object contents reply */
    const val REPLY_TASK_INVENTORY = (0xFFFF0122).toInt()       // Wire: FF FF 01 22 (Lumiya-verified: put(1), put(34))
    
    /** UpdateTaskInventory - Update object contents */
    const val UPDATE_TASK_INVENTORY = (0xFFFF011E).toInt()      // Wire: FF FF 01 1E (Lumiya-verified: put(1), put(30))
    
    /** RemoveTaskInventory - Remove from object */
    const val REMOVE_TASK_INVENTORY = (0xFFFF011F).toInt()      // Wire: FF FF 01 1F (Lumiya-verified: put(1), put(31))
    
    /** MoveTaskInventory - Move in object */
    const val MOVE_TASK_INVENTORY = (0xFFFF0120).toInt()        // Wire: FF FF 01 20 (Lumiya-verified: put(1), put(32))

    // --- Object Messages (Extended) ---
    /** ObjectDuplicate - Duplicate object */
    const val OBJECT_DUPLICATE = (0xFFFF005A).toInt()  // Wire: FF FF 00 5A = -65446 (Lumiya: ObjectDuplicate)
    
    /** ObjectDuplicateOnRay - Duplicate on ray */
    const val OBJECT_DUPLICATE_ON_RAY = (0xFFFF005B).toInt()  // Wire: FF FF 00 5B = -65445 (Lumiya: ObjectDuplicateOnRay)
    
    /** ObjectScale - Scale object */
    const val OBJECT_SCALE = (0xFFFF005C).toInt()  // Wire: FF FF 00 5C = -65444 (Lumiya: ObjectScale)
    
    /** ObjectRotation - Rotate object */
    const val OBJECT_ROTATION = (0xFFFF005D).toInt()  // Wire: FF FF 00 5D = -65443 (Lumiya: ObjectRotation)
    
    /** ObjectPosition - Position object (low freq) */
    const val OBJECT_POSITION = 65284                           // Wire: FF 04 = 4 | 65280 (medium frequency)
    
    /** ObjectFlagUpdate - Update object flags */
    const val OBJECT_FLAG_UPDATE = (0xFFFF005E).toInt()  // Wire: FF FF 00 5E = -65442 (Lumiya: ObjectFlagUpdate)
    
    /** ObjectClickAction - Set click action */
    const val OBJECT_CLICK_ACTION = (0xFFFF005F).toInt()  // Wire: FF FF 00 5F = -65441 (Lumiya: ObjectClickAction)
    
    /** ObjectImage - Set object texture */
    const val OBJECT_IMAGE = (0xFFFF0060).toInt()  // Wire: FF FF 00 60 = -65440 (Lumiya: ObjectImage)
    
    /** ObjectMaterial - Set object material */
    const val OBJECT_MATERIAL = (0xFFFF0061).toInt()  // Wire: FF FF 00 61 = -65439 (Lumiya: ObjectMaterial)
    
    /** ObjectShape - Set object shape */
    const val OBJECT_SHAPE = (0xFFFF0062).toInt()  // Wire: FF FF 00 62 = -65438 (Lumiya: ObjectShape)
    
    /** ObjectExtraParams - Set extra params */
    const val OBJECT_EXTRA_PARAMS = (0xFFFF0063).toInt()  // Wire: FF FF 00 63 = -65437 (Lumiya: ObjectExtraParams)
    
    /** ObjectOwner - Set object owner */
    const val OBJECT_OWNER = (0xFFFF0064).toInt()  // Wire: FF FF 00 64 = -65436 (Lumiya: ObjectOwner)
    
    /** ObjectGroup - Set object group */
    const val OBJECT_GROUP = (0xFFFF0065).toInt()  // Wire: FF FF 00 65 = -65435 (Lumiya: ObjectGroup)
    
    /** ObjectBuy - Buy object */
    const val OBJECT_BUY = (0xFFFF0066).toInt()  // Wire: FF FF 00 66 = -65434 (Lumiya: ObjectBuy)
    
    /** ObjectPermissions - Set permissions */
    const val OBJECT_PERMISSIONS = (0xFFFF0069).toInt()         // Wire: FF FF 00 69 (Lumiya-verified: put(0), put(105))
    
    /** ObjectSaleInfo - Set sale info */
    const val OBJECT_SALE_INFO = (0xFFFF006A).toInt()           // Wire: FF FF 00 6A (Lumiya-verified: put(0), put(106))
    
    /** ObjectCategory - Set category */
    const val OBJECT_CATEGORY = (0xFFFF006D).toInt()            // Wire: FF FF 00 6D (Lumiya-verified: put(0), put(109))
    
    /** ObjectDeselect - Deselect object */
    const val OBJECT_DESELECT = (0xFFFF006F).toInt()  // Wire: FF FF 00 6F = -65425 (Lumiya: ObjectDeselect)
    
    /** ObjectAttach - Attach object */
    const val OBJECT_ATTACH = (0xFFFF0070).toInt()  // Wire: FF FF 00 70 = -65424 (Lumiya: ObjectAttach)
    
    /** ObjectDetach - Detach object */
    const val OBJECT_DETACH = (0xFFFF0071).toInt()  // Wire: FF FF 00 71 = -65423 (Lumiya: ObjectDetach)
    
    /** ObjectDrop - Drop object */
    const val OBJECT_DROP = (0xFFFF0072).toInt()  // Wire: FF FF 00 72 = -65422 (Lumiya: ObjectDrop)
    
    /** ObjectSpinStart - Start spinning */
    const val OBJECT_SPIN_START = (0xFFFF0078).toInt()          // Wire: FF FF 00 78 (Lumiya-verified: put(0), put(120))
    
    /** ObjectSpinUpdate - Update spin */
    const val OBJECT_SPIN_UPDATE = (0xFFFF0079).toInt()         // Wire: FF FF 00 79 (Lumiya-verified: put(0), put(121))
    
    /** ObjectSpinStop - Stop spinning */
    const val OBJECT_SPIN_STOP = (0xFFFF007A).toInt()           // Wire: FF FF 00 7A (Lumiya-verified: put(0), put(122))
    
    /** ObjectGrabUpdate - Update grab */
    const val OBJECT_GRAB_UPDATE = (0xFFFF0076).toInt()         // Wire: FF FF 00 76 (Lumiya-verified: put(0), put(118))

    // --- Land/Terrain Messages ---
    /** ModifyLand - Modify terrain */
    const val MODIFY_LAND = (0xFFFF007C).toInt()  // Wire: FF FF 00 7C = -65412 (Lumiya: ModifyLand)
    
    /** UndoLand - Undo terrain change */
    const val UNDO_LAND = (0xFFFF004D).toInt()  // Wire: FF FF 00 4D = -65459 (Lumiya: UndoLand)

    // --- Parcel Messages (Extended) ---
    /** ParcelPropertiesRequest - Request parcel props */
    const val PARCEL_PROPERTIES_REQUEST = 65291                 // Wire: FF 0B = 11 | 65280 (medium frequency)
    
    /** ParcelPropertiesRequestByID - Request by ID */
    const val PARCEL_PROPERTIES_REQUEST_BY_ID = (0xFFFF00C5).toInt()  // Wire: FF FF 00 C5 = -65339 (Lumiya naming mismatch fix)
    
    /** ParcelDisableObjects - Disable objects */
    const val PARCEL_DISABLE_OBJECTS = (0xFFFF00C9).toInt()  // Wire: FF FF 00 C9 = -65335 (Lumiya: ParcelDisableObjects)
    
    /** ParcelSelectObjects - Select parcel objects */
    const val PARCEL_SELECT_OBJECTS = (0xFFFF00CA).toInt()  // Wire: FF FF 00 CA = -65334 (Lumiya: ParcelSelectObjects)
    
    /** ParcelMediaCommandMessage - Media command */
    const val PARCEL_MEDIA_COMMAND_MESSAGE = (0xFFFF01A3).toInt()  // Wire: FF FF 01 A3 = -65117 (Lumiya: ParcelMediaCommandMessage)
    
    /** ParcelMediaUpdate - Media update */
    const val PARCEL_MEDIA_UPDATE = (0xFFFF01A4).toInt()  // Wire: FF FF 01 A4 = -65116 (Lumiya: ParcelMediaUpdate)
    
    /** ParcelObjectOwnersReply - Object owners list */
    const val PARCEL_OBJECT_OWNERS_REPLY = (0xFFFF0039).toInt() // Wire: FF FF 00 39 = -65479
    
    /** ForceObjectSelect - Force select object */
    const val FORCE_OBJECT_SELECT = (0xFFFF00CD).toInt()  // Wire: FF FF 00 CD = -65331 (Lumiya: ForceObjectSelect)

    // --- Money/Economy Messages ---
    /** MoneyTransferRequest - Transfer L$ */
    const val MONEY_TRANSFER_REQUEST = (0xFFFF0137).toInt()  // Wire: FF FF 01 37 = -65225 (Lumiya: MoneyTransferRequest)
    
    /** RoutedMoneyBalanceReply - Routed balance */
    const val ROUTED_MONEY_BALANCE_REPLY = (0xFFFF013B).toInt()  // Wire: FF FF 01 3B = -65221 (Lumiya: RoutedMoneyBalanceReply)
    
    /** PayPriceReply - Pay price info */
    const val PAY_PRICE_REPLY = (0xFFFF00A2).toInt()            // Wire: FF FF 00 A2 = -65374
    
    /** RequestPayPrice - Request pay info */
    const val REQUEST_PAY_PRICE = (0xFFFF00A1).toInt()          // Wire: FF FF 00 A1 = -65375
    
    /** BuyObjectInventory - Buy object contents */
    const val BUY_OBJECT_INVENTORY = (0xFFFF0067).toInt()       // Wire: FF FF 00 67 (Lumiya-verified: put(0), put(103))

    // --- Script Messages (Extended) ---
    /** ScriptRunningReply - Script status reply */
    const val SCRIPT_RUNNING_REPLY = (0xFFFF00F4).toInt()  // Wire: FF FF 00 F4 = -65292 (Lumiya: ScriptRunningReply)
    
    /** GetScriptRunning - Get script status */
    const val GET_SCRIPT_RUNNING = (0xFFFF00F3).toInt()  // Wire: FF FF 00 F3 = -65293 (Lumiya: GetScriptRunning)
    
    /** SetScriptRunning - Set script status */
    const val SET_SCRIPT_RUNNING = (0xFFFF00F5).toInt()  // Wire: FF FF 00 F5 = -65291 (Lumiya: SetScriptRunning)
    
    /** ScriptReset - Reset script */
    const val SCRIPT_RESET = (0xFFFF00F6).toInt()  // Wire: FF FF 00 F6 = -65290 (Lumiya: ScriptReset)
    
    /** ScriptSensorReply - Sensor results */
    const val SCRIPT_SENSOR_REPLY = (0xFFFF00F8).toInt()  // Wire: FF FF 00 F8 = -65288 (Lumiya: ScriptSensorReply)
    
    /** ScriptTeleportRequest - Script teleport */
    const val SCRIPT_TELEPORT_REQUEST = (0xFFFF00C3).toInt()  // Wire: FF FF 00 C3 = -65341 (Lumiya: ScriptTeleportRequest)
    
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
    const val ASSET_UPLOAD_REQUEST = (0xFFFF014D).toInt()  // Wire: FF FF 01 4D = -65203 (Lumiya: AssetUploadRequest)
    
    /** AssetUploadComplete - Upload complete */
    const val ASSET_UPLOAD_COMPLETE = (0xFFFF014E).toInt()  // Wire: FF FF 01 4E = -65202 (Lumiya: AssetUploadComplete)

    // --- Region/Sim Messages ---
    /** RequestRegionInfo - Request region info */
    const val REQUEST_REGION_INFO = (0xFFFF008D).toInt()  // Wire: FF FF 00 8D = -65395 (Lumiya: RequestRegionInfo)
    
    /** GodUpdateRegionInfo - God region update */
    const val GOD_UPDATE_REGION_INFO = (0xFFFF008F).toInt()  // Wire: FF FF 00 8F = -65393 (Lumiya: GodUpdateRegionInfo)
    
    /** SimulatorViewerTimeMessage - Sim time */
    const val SIMULATOR_VIEWER_TIME_MESSAGE = (0xFFFF0096).toInt()  // Wire: FF FF 00 96 = -65386 (Lumiya: SimulatorViewerTimeMessage)
    
    /** TeleportLocal - Local teleport */
    const val TELEPORT_LOCAL = (0xFFFF0040).toInt()  // Wire: FF FF 00 40 = -65472 (Lumiya: TeleportLocal)
    
    /** TeleportCancel - Cancel teleport */
    const val TELEPORT_CANCEL = (0xFFFF0048).toInt()            // Wire: FF FF 00 48 = -65464
    
    /** TeleportRequest - Teleport request */
    const val TELEPORT_REQUEST = (0xFFFF003E).toInt()  // Wire: FF FF 00 3E = -65474 (Lumiya: TeleportRequest)
    
    /** SimCrashed - Sim crash notification */
    const val SIM_CRASHED = (0xFFFF0148).toInt()  // Wire: FF FF 01 48 = -65208 (Lumiya: SimCrashed)

    // --- Map Messages (Extended) ---
    /** MapBlockRequest - Request map block */
    const val MAP_BLOCK_REQUEST = (0xFFFF0197).toInt()  // Wire: FF FF 01 97 = -65129 (Lumiya: MapBlockRequest)
    
    /** MapNameRequest - Request map by name */
    const val MAP_NAME_REQUEST = (0xFFFF0198).toInt()  // Wire: FF FF 01 98 = -65128 (Lumiya: MapNameRequest)
    
    /** MapLayerRequest - Request map layer */
    const val MAP_LAYER_REQUEST = (0xFFFF0195).toInt()  // Wire: FF FF 01 95 = -65131 (Lumiya: MapLayerRequest)
    
    /** MapItemRequest - Request map items */
    const val MAP_ITEM_REQUEST = (0xFFFF019A).toInt()  // Wire: FF FF 01 9A = -65126 (Lumiya: MapItemRequest)

    // --- Mute Messages ---
    /** MuteListRequest - Request mute list */
    const val MUTE_LIST_REQUEST = (0xFFFF0106).toInt()          // Wire: FF FF 01 06 = -65274
    
    /** UpdateMuteListEntry - Update mute */
    const val UPDATE_MUTE_LIST_ENTRY = (0xFFFF0107).toInt()     // Wire: FF FF 01 07 = -65273
    
    /** RemoveMuteListEntry - Remove mute */
    const val REMOVE_MUTE_LIST_ENTRY = (0xFFFF0108).toInt()     // Wire: FF FF 01 08 = -65272
    
    /** MuteListUpdate - Mute list updated */
    const val MUTE_LIST_UPDATE = (0xFFFF013E).toInt()  // Wire: FF FF 01 3E = -65218 (Lumiya: MuteListUpdate)
    
    /** UseCachedMuteList - Use cached list */
    const val USE_CACHED_MUTE_LIST = (0xFFFF013F).toInt()  // Wire: FF FF 01 3F = -65217 (Lumiya: UseCachedMuteList)

    // --- User Info Messages ---
    /** UserInfoRequest - Request user info */
    const val USER_INFO_REQUEST = (0xFFFF018F).toInt()  // Wire: FF FF 01 8F = -65137 (Lumiya: UserInfoRequest)
    
    /** UserInfoReply - User info reply */
    const val USER_INFO_REPLY = (0xFFFF0190).toInt()  // Wire: FF FF 01 90 = -65136 (Lumiya: UserInfoReply)
    
    /** UpdateUserInfo - Update user info */
    const val UPDATE_USER_INFO = (0xFFFF0191).toInt()  // Wire: FF FF 01 91 = -65135 (Lumiya: UpdateUserInfo)

    // --- Godlike Messages ---
    /** GodlikeMessage - Godlike command */
    const val GODLIKE_MESSAGE = (0xFFFF0103).toInt()            // Wire: FF FF 01 03 (Lumiya-verified: put(1), put(3))
    
    /** GrantGodlikePowers - Grant god powers */
    const val GRANT_GODLIKE_POWERS = (0xFFFF0102).toInt()       // Wire: FF FF 01 02 = -65278
    
    /** RequestGodlikePowers - Request powers */
    const val REQUEST_GODLIKE_POWERS = (0xFFFF0101).toInt()     // Wire: FF FF 01 01 = -65279
    
    /** GodKickUser - God kick user */
    const val GOD_KICK_USER = (0xFFFF00A5).toInt()  // Wire: FF FF 00 A5 = -65371 (Lumiya: GodKickUser)
    
    /** SystemKickUser - System kick */
    const val SYSTEM_KICK_USER = (0xFFFF00A6).toInt()           // Wire: FF FF 00 A6 (Lumiya-verified: put(0), put(-90))
    
    /** EjectUser - Eject user */
    const val EJECT_USER = (0xFFFF00A7).toInt()                 // Wire: FF FF 00 A7 (Lumiya-verified: put(0), put(-89))
    
    /** FreezeUser_God - God freeze user */
    const val FREEZE_USER_GOD = (0xFFFF0239).toInt()  // Wire: FF FF 02 39 = -64967 (Linkpoint-specific, unique reserved ID)
    
    /** KickUser - Kick user */
    const val KICK_USER = (0xFFFF00A3).toInt()  // Wire: FF FF 00 A3 = -65373 (Lumiya: KickUser)
    
    /** KickUserAck - Kick acknowledged */
    const val KICK_USER_ACK = (0xFFFF00A4).toInt()  // Wire: FF FF 00 A4 = -65372 (Lumiya: KickUserAck)

    // --- Generic/System Messages ---
    /** GenericMessage - Generic message */
    const val GENERIC_MESSAGE = (0xFFFF0105).toInt()            // Wire: FF FF 01 05 = -65275
    
    /** SystemMessage - System message */
    const val SYSTEM_MESSAGE = (0xFFFF0194).toInt()  // Wire: FF FF 01 94 = -65132 (Lumiya: SystemMessage)
    
    /** Error - Error message */
    const val ERROR_MESSAGE = (0xFFFF01A7).toInt()  // Wire: FF FF 01 A7 = -65113 (Lumiya: Error = -65113)
    
    /** FeatureDisabled - Feature disabled */
    const val FEATURE_DISABLED = (0xFFFF0013).toInt()           // Wire: FF FF 00 13 = -65517
    
    /** ViewerFrozenMessage - Viewer frozen */
    const val VIEWER_FROZEN_MESSAGE = (0xFFFF0089).toInt()      // Wire: FF FF 00 89 = -65399
    
    /** ViewerStats - Viewer statistics */
    const val VIEWER_STATS = (0xFFFF0083).toInt()  // Wire: FF FF 00 83 = -65405 (Lumiya: ViewerStats)

    // --- Attachment Messages ---
    /** RezMultipleAttachmentsFromInv - Rez multiple */
    const val REZ_MULTIPLE_ATTACHMENTS_FROM_INV = (0xFFFF018C).toInt()  // Wire: FF FF 01 8C = -65140 (Lumiya: RezMultipleAttachmentsFromInv)
    
    /** DetachAttachmentIntoInv - Detach to inventory */
    const val DETACH_ATTACHMENT_INTO_INV = (0xFFFF018D).toInt()  // Wire: FF FF 01 8D = -65139 (Lumiya: DetachAttachmentIntoInv)
    
    /** CreateNewOutfitAttachments - Create outfit */
    const val CREATE_NEW_OUTFIT_ATTACHMENTS = (0xFFFF018E).toInt()  // Wire: FF FF 01 8E = -65138 (Lumiya: CreateNewOutfitAttachments)
    
    /** UpdateAttachment - Update attachment */
    const val UPDATE_ATTACHMENT = (0xFFFF014B).toInt()  // Wire: FF FF 01 4B = -65205 (Lumiya: UpdateAttachment)
    
    /** RemoveAttachment - Remove attachment */
    const val REMOVE_ATTACHMENT = (0xFFFF014C).toInt()  // Wire: FF FF 01 4C = -65204 (Lumiya: RemoveAttachment)
    
    /** RebakeAvatarTextures - Rebake textures */
    const val REBAKE_AVATAR_TEXTURES = (0xFFFF0057).toInt()     // Wire: FF FF 00 57 (Lumiya-verified: put(0), put(87))

    // --- Rez/DeRez Messages ---
    /** RezObjectFromNotecard - Rez from notecard */
    const val REZ_OBJECT_FROM_NOTECARD = (0xFFFF0126).toInt()   // Wire: FF FF 01 26 = -65242
    
    /** RezRestoreToWorld - Restore to world */
    const val REZ_RESTORE_TO_WORLD = (0xFFFF01A9).toInt()  // Wire: FF FF 01 A9 = -65111 (Lumiya: RezRestoreToWorld)
    
    /** RezScript - Rez script */
    const val REZ_SCRIPT = (0xFFFF0130).toInt()  // Wire: FF FF 01 30 = -65232 (Lumiya: RezScript)
    
    /** DeRezAck - DeRez acknowledged */
    const val DEREZ_ACK = (0xFFFF0124).toInt()                  // Wire: FF FF 01 24 = -65244

    // --- Misc Messages ---
    /** Undo - Undo action */
    const val UNDO = (0xFFFF004B).toInt()  // Wire: FF FF 00 4B = -65461 (Lumiya: Undo)
    
    /** Redo - Redo action */
    const val REDO = (0xFFFF004C).toInt()  // Wire: FF FF 00 4C = -65460 (Lumiya: Redo)
    
    /** SetAlwaysRun - Set always run */
    const val SET_ALWAYS_RUN = (0xFFFF0058).toInt()             // Wire: FF FF 00 58 = -65448
    
    /** SetFollowCamProperties - Set follow cam */
    const val SET_FOLLOW_CAM_PROPERTIES = (0xFFFF009F).toInt()  // Wire: FF FF 00 9F = -65377
    
    /** ClearFollowCamProperties - Clear follow cam */
    const val CLEAR_FOLLOW_CAM_PROPERTIES = (0xFFFF00A0).toInt() // Wire: FF FF 00 A0 = -65376
    
    /** SetStartLocationRequest - Set start location */
    const val SET_START_LOCATION_REQUEST = (0xFFFF0144).toInt()  // Wire: FF FF 01 44 = -65212 (Lumiya: SetStartLocationRequest)
    
    /** InitiateDownload - Start download */
    const val INITIATE_DOWNLOAD = (0xFFFF0193).toInt()  // Wire: FF FF 01 93 = -65133 (Lumiya: InitiateDownload)
    
    /** DataHomeLocationReply - Home location reply */
    const val DATA_HOME_LOCATION_REPLY = (0xFFFF0044).toInt()  // Wire: FF FF 00 44 = -65468 (Lumiya: DataHomeLocationReply)

    // --- Places/Directory Messages ---
    /** PlacesReply - Places search reply */
    const val PLACES_REPLY = (0xFFFF001E).toInt()               // Wire: FF FF 00 1E = -65506
    
    /** DirPopularReply - Popular places reply */
    const val DIR_POPULAR_REPLY = (0xFFFF0035).toInt()  // Wire: FF FF 00 35 = -65483 (Lumiya: DirPopularReply)

    // =====================================
    // PHASE 4: 100 Additional Message Handlers
    // =====================================

    // --- Circuit/Connection Messages ---
    /** CloseCircuit - Close UDP circuit */
    const val CLOSE_CIRCUIT = -3  // Wire: FF FF FF FD = -3 (Lumiya: CloseCircuit)
    
    /** OpenCircuit - Open UDP circuit */
    const val OPEN_CIRCUIT = -4  // Wire: FF FF FF FC = -4 (Lumiya: OpenCircuit)
    
    /** AddCircuitCode - Add circuit code */
    const val ADD_CIRCUIT_CODE = (0xFFFF0002).toInt()  // Wire: FF FF 00 02 = -65534 (Lumiya: AddCircuitCode)
    
    /** CreateTrustedCircuit - Create trusted circuit */
    const val CREATE_TRUSTED_CIRCUIT = (0xFFFF0188).toInt()  // Wire: FF FF 01 88 = -65144 (Lumiya: CreateTrustedCircuit)
    
    /** DenyTrustedCircuit - Deny trusted circuit */
    const val DENY_TRUSTED_CIRCUIT = (0xFFFF0189).toInt()  // Wire: FF FF 01 89 = -65143 (Lumiya: DenyTrustedCircuit)
    
    /** RequestTrustedCircuit - Request trusted circuit */
    const val REQUEST_TRUSTED_CIRCUIT = (0xFFFF018A).toInt()  // Wire: FF FF 01 8A = -65142 (Lumiya: RequestTrustedCircuit)

    // --- Auction Messages ---
    /** CancelAuction - Cancel auction */
    const val CANCEL_AUCTION = (0xFFFF00E8).toInt()  // Wire: FF FF 00 E8 = -65304 (Lumiya: CancelAuction)
    
    /** CompleteAuction - Complete auction */
    const val COMPLETE_AUCTION = (0xFFFF00E7).toInt()  // Wire: FF FF 00 E7 = -65305 (Lumiya: CompleteAuction)
    
    /** ConfirmAuctionStart - Confirm auction start */
    const val CONFIRM_AUCTION_START = (0xFFFF00E6).toInt()  // Wire: FF FF 00 E6 = -65306 (Lumiya: ConfirmAuctionStart)
    
    /** StartAuction - Start auction */
    const val START_AUCTION = (0xFFFF00E5).toInt()  // Wire: FF FF 00 E5 = -65307 (Lumiya: StartAuction)
    
    /** ViewerStartAuction - Viewer start auction */
    const val VIEWER_START_AUCTION = (0xFFFF00E4).toInt()  // Wire: FF FF 00 E4 = -65308 (Lumiya: ViewerStartAuction)
    
    /** CheckParcelAuctions - Check parcel auctions */
    const val CHECK_PARCEL_AUCTIONS = (0xFFFF00E9).toInt()  // Wire: FF FF 00 E9 = -65303 (Lumiya: CheckParcelAuctions)
    
    /** CheckParcelSales - Check parcel sales */
    const val CHECK_PARCEL_SALES = (0xFFFF00E1).toInt()  // Wire: FF FF 00 E1 = -65311 (Lumiya: CheckParcelSales)
    
    /** ParcelAuctions - Parcel auctions */
    const val PARCEL_AUCTIONS = (0xFFFF00EA).toInt()  // Wire: FF FF 00 EA = -65302 (Lumiya: ParcelAuctions)
    
    /** ParcelSales - Parcel sales */
    const val PARCEL_SALES = (0xFFFF00E2).toInt()  // Wire: FF FF 00 E2 = -65310 (Lumiya: ParcelSales)

    // --- Parcel Extended Messages ---
    /** ParcelBuyPass - Buy parcel pass */
    const val PARCEL_BUY_PASS = (0xFFFF00CE).toInt()  // Wire: FF FF 00 CE = -65330 (Lumiya: ParcelBuyPass)
    
    /** ParcelClaim - Claim parcel */
    const val PARCEL_CLAIM = (0xFFFF00D1).toInt()  // Wire: FF FF 00 D1 = -65327 (Lumiya: ParcelClaim)
    
    /** ParcelDivide - Divide parcel */
    const val PARCEL_DIVIDE = (0xFFFF00D3).toInt()  // Wire: FF FF 00 D3 = -65325 (Lumiya: ParcelDivide)
    
    /** ParcelJoin - Join parcel */
    const val PARCEL_JOIN = (0xFFFF00D2).toInt()  // Wire: FF FF 00 D2 = -65326 (Lumiya: ParcelJoin)
    
    /** ParcelReclaim - Reclaim parcel */
    const val PARCEL_RECLAIM = (0xFFFF00D0).toInt()  // Wire: FF FF 00 D0 = -65328 (Lumiya: ParcelReclaim)
    
    /** ParcelRename - Rename parcel */
    const val PARCEL_RENAME = (0xFFFF0192).toInt()  // Wire: FF FF 01 92 = -65134 (Lumiya: ParcelRename)
    
    /** ParcelSetOtherCleanTime - Set clean time */
    const val PARCEL_SET_OTHER_CLEAN_TIME = (0xFFFF00C8).toInt()  // Wire: FF FF 00 C8 = -65336 (Lumiya: ParcelSetOtherCleanTime)
    
    /** ParcelGodForceOwner - God force owner */
    const val PARCEL_GOD_FORCE_OWNER = (0xFFFF00D6).toInt()  // Wire: FF FF 00 D6 = -65322 (Lumiya: ParcelGodForceOwner)
    
    /** ParcelGodMarkAsContent - God mark as content */
    const val PARCEL_GOD_MARK_AS_CONTENT = (0xFFFF00E3).toInt()  // Wire: FF FF 00 E3 = -65309 (Lumiya: ParcelGodMarkAsContent)
    
    /** MergeParcel - Merge parcel */
    const val MERGE_PARCEL = (0xFFFF00DF).toInt()               // Wire: FF FF 00 DF = -65313
    
    /** RemoveParcel - Remove parcel */
    const val REMOVE_PARCEL = (0xFFFF00DE).toInt()  // Wire: FF FF 00 DE = -65314 (Lumiya: RemoveParcel)

    // --- Land Statistics Messages ---
    /** LandStatRequest - Request land stats */
    const val LAND_STAT_REQUEST = (0xFFFF01A5).toInt()  // Wire: FF FF 01 A5 = -65115 (Lumiya: LandStatRequest)
    
    /** LandStatReply - Land stats reply */
    const val LAND_STAT_REPLY = (0xFFFF01A6).toInt()  // Wire: FF FF 01 A6 = -65114 (Lumiya: LandStatReply)

    // --- Simulator Messages ---
    /** SimulatorLoad - Simulator load info */
    const val SIMULATOR_LOAD = (0xFFFF000C).toInt()  // Wire: FF FF 00 0C = -65524 (Lumiya: SimulatorLoad)
    
    /** SimulatorReady - Simulator ready */
    const val SIMULATOR_READY = (0xFFFF0009).toInt()  // Wire: FF FF 00 09 = -65527 (Lumiya: SimulatorReady)
    
    /** SimulatorShutdownRequest - Request shutdown */
    const val SIMULATOR_SHUTDOWN_REQUEST = (0xFFFF000D).toInt()  // Wire: FF FF 00 0D = -65523 (Lumiya: SimulatorShutdownRequest)
    
    /** SimulatorMapUpdate - Sim map update */
    const val SIMULATOR_MAP_UPDATE = (0xFFFF0005).toInt()  // Wire: FF FF 00 05 = -65531 (Lumiya: SimulatorMapUpdate)
    
    /** SimulatorSetMap - Set sim map */
    const val SIMULATOR_SET_MAP = (0xFFFF0006).toInt()  // Wire: FF FF 00 06 = -65530 (Lumiya: SimulatorSetMap)
    
    /** SimulatorPresentAtLocation - Present at location */
    const val SIMULATOR_PRESENT_AT_LOCATION = (0xFFFF000B).toInt()  // Wire: FF FF 00 0B = -65525 (Lumiya: SimulatorPresentAtLocation)
    
    /** UpdateSimulator - Update simulator */
    const val UPDATE_SIMULATOR = (0xFFFF0011).toInt()  // Wire: FF FF 00 11 = -65519 (Lumiya: UpdateSimulator)
    
    /** SimWideDeletes - Sim-wide delete */
    const val SIM_WIDE_DELETES = (0xFFFF0081).toInt()  // Wire: FF FF 00 81 = -65407 (Lumiya: SimWideDeletes)

    // --- Child Agent Messages ---
    /** ChildAgentDying - Child agent dying */
    const val CHILD_AGENT_DYING = (0xFFFF00F0).toInt()  // Wire: FF FF 00 F0 = -65296 (Lumiya: ChildAgentDying)
    
    /** ChildAgentUnknown - Child agent unknown */
    const val CHILD_AGENT_UNKNOWN = (0xFFFF00F1).toInt()  // Wire: FF FF 00 F1 = -65295 (Lumiya: ChildAgentUnknown)
    
    /** KillChildAgents - Kill child agents */
    const val KILL_CHILD_AGENTS = (0xFFFF00F2).toInt()  // Wire: FF FF 00 F2 = -65294 (Lumiya: KillChildAgents)

    // --- Postcard/Email Messages ---
    /** SendPostcard - Send postcard */
    const val SEND_POSTCARD = (0xFFFF019C).toInt()  // Wire: FF FF 01 9C = -65124 (Lumiya: SendPostcard)
    
    /** EmailMessageRequest - Email request */
    const val EMAIL_MESSAGE_REQUEST = (0xFFFF014F).toInt()  // Wire: FF FF 01 4F = -65201 (Lumiya: EmailMessageRequest)
    
    /** EmailMessageReply - Email reply */
    const val EMAIL_MESSAGE_REPLY = (0xFFFF0150).toInt()  // Wire: FF FF 01 50 = -65200 (Lumiya: EmailMessageReply)

    // --- RPC Messages ---
    /** RpcChannelRequest - RPC channel request */
    const val RPC_CHANNEL_REQUEST = (0xFFFF019D).toInt()  // Wire: FF FF 01 9D = -65123 (Lumiya: RpcChannelRequest)
    
    /** RpcChannelReply - RPC channel reply */
    const val RPC_CHANNEL_REPLY = (0xFFFF019E).toInt()  // Wire: FF FF 01 9E = -65122 (Lumiya: RpcChannelReply)
    
    /** RpcScriptRequestInbound - RPC script request */
    const val RPC_SCRIPT_REQUEST_INBOUND = (0xFFFF019F).toInt()  // Wire: FF FF 01 9F = -65121 (Lumiya: RpcScriptRequestInbound)
    
    /** RpcScriptReplyInbound - RPC script reply */
    const val RPC_SCRIPT_REPLY_INBOUND = (0xFFFF01A1).toInt()  // Wire: FF FF 01 A1 = -65119 (Lumiya: RpcScriptReplyInbound)

    // --- Script Messages Extended ---
    /** ScriptDataRequest - Script data request */
    const val SCRIPT_DATA_REQUEST = (0xFFFF0151).toInt()  // Wire: FF FF 01 51 = -65199 (Lumiya: ScriptDataRequest)
    
    /** ScriptDataReply - Script data reply */
    const val SCRIPT_DATA_REPLY = (0xFFFF0152).toInt()  // Wire: FF FF 01 52 = -65198 (Lumiya: ScriptDataReply)
    
    /** ScriptMailRegistration - Script mail registration */
    const val SCRIPT_MAIL_REGISTRATION = (0xFFFF01A2).toInt()  // Wire: FF FF 01 A2 = -65118 (Lumiya: ScriptMailRegistration)
    
    /** ScriptSensorRequest - Sensor request */
    const val SCRIPT_SENSOR_REQUEST = (0xFFFF00F7).toInt()  // Wire: FF FF 00 F7 = -65289 (Lumiya: ScriptSensorRequest)
    
    /** ScriptAnswerYes - Script permission answer */
    const val SCRIPT_ANSWER_YES = (0xFFFF0084).toInt()  // Wire: FF FF 00 84 = -65404 (Lumiya: ScriptAnswerYes)
    
    /** InternalScriptMail - Internal script mail */
    const val INTERNAL_SCRIPT_MAIL = 65296                      // Wire: FF 10 = 16 | 65280 (medium frequency)

    // --- Tracking Messages ---
    /** TrackAgent - Track agent location */
    const val TRACK_AGENT = (0xFFFF0082).toInt()  // Wire: FF FF 00 82 = -65406 (Lumiya: TrackAgent)
    
    /** FindAgent - Find agent (extended) */
    const val FIND_AGENT_EXTENDED = (0xFFFF022C).toInt()  // Wire: FF FF 02 2C = -64980 (Linkpoint-specific, unique reserved ID)

    // --- Region Messages Extended ---
    /** RegionHandleRequest - Request region handle */
    const val REGION_HANDLE_REQUEST = (0xFFFF0135).toInt()  // Wire: FF FF 01 35 = -65227 (Lumiya: RegionHandleRequest)
    
    /** RegionIDAndHandleReply - Region ID reply */
    const val REGION_ID_AND_HANDLE_REPLY = (0xFFFF0136).toInt()  // Wire: FF FF 01 36 = -65226 (Lumiya naming mismatch fix)
    
    /** RegionPresenceRequestByHandle - Region presence */
    const val REGION_PRESENCE_REQUEST_BY_HANDLE = (0xFFFF000F).toInt()  // Wire: FF FF 00 0F = -65521 (Lumiya: RegionPresenceRequestByHandle)
    
    /** RegionPresenceRequestByRegionID - Region presence by ID */
    const val REGION_PRESENCE_REQUEST_BY_REGION_ID = (0xFFFF000E).toInt()  // Wire: FF FF 00 0E = -65522 (Lumiya naming mismatch fix)
    
    /** RegionPresenceResponse - Region presence response */
    const val REGION_PRESENCE_RESPONSE = (0xFFFF0010).toInt()  // Wire: FF FF 00 10 = -65520 (Lumiya: RegionPresenceResponse)
    
    /** NearestLandingRegionRequest - Nearest landing request */
    const val NEAREST_LANDING_REGION_REQUEST = (0xFFFF0090).toInt()  // Wire: FF FF 00 90 = -65392 (Lumiya: NearestLandingRegionRequest)
    
    /** NearestLandingRegionReply - Nearest landing reply */
    const val NEAREST_LANDING_REGION_REPLY = (0xFFFF0091).toInt()  // Wire: FF FF 00 91 = -65391 (Lumiya: NearestLandingRegionReply)
    
    /** NearestLandingRegionUpdated - Landing region updated */
    const val NEAREST_LANDING_REGION_UPDATED = (0xFFFF0092).toInt()  // Wire: FF FF 00 92 = -65390 (Lumiya: NearestLandingRegionUpdated)
    
    /** TelehubInfo - Telehub info */
    const val TELEHUB_INFO = (0xFFFF000A).toInt()  // Wire: FF FF 00 0A = -65526 (Lumiya: TelehubInfo)
    
    /** TeleportLandingStatusChanged - Landing status changed */
    const val TELEPORT_LANDING_STATUS_CHANGED = (0xFFFF0093).toInt()  // Wire: FF FF 00 93 = -65389 (Lumiya: TeleportLandingStatusChanged)

    // --- User Reports Messages ---
    /** UserReport - User report */
    const val USER_REPORT = (0xFFFF0085).toInt()  // Wire: FF FF 00 85 = -65403 (Lumiya: UserReport)
    
    /** UserReportInternal - Internal report */
    const val USER_REPORT_INTERNAL = (0xFFFF0015).toInt()  // Wire: FF FF 00 15 = -65515 (Lumiya: UserReportInternal)
    
    /** ReportAutosaveCrash - Autosave crash report */
    const val REPORT_AUTOSAVE_CRASH = (0xFFFF0080).toInt()  // Wire: FF FF 00 80 = -65408 (Lumiya: ReportAutosaveCrash)

    // --- Event Messages Extended ---
    /** EventLocationRequest - Event location request */
    const val EVENT_LOCATION_REQUEST = (0xFFFF0133).toInt()  // Wire: FF FF 01 33 = -65229 (Lumiya: EventLocationRequest)
    
    /** EventLocationReply - Event location reply */
    const val EVENT_LOCATION_REPLY = (0xFFFF0134).toInt()  // Wire: FF FF 01 34 = -65228 (Lumiya: EventLocationReply)
    
    /** EventGodDelete - God delete event */
    const val EVENT_GOD_DELETE = (0xFFFF00B7).toInt()  // Wire: FF FF 00 B7 = -65353 (Lumiya: EventGodDelete)
    
    /** CreateLandmarkForEvent - Create landmark for event */
    const val CREATE_LANDMARK_FOR_EVENT = (0xFFFF0132).toInt()  // Wire: FF FF 01 32 = -65230 (Lumiya: CreateLandmarkForEvent)

    // --- Pick Messages Extended ---
    /** PickGodDelete - God delete pick */
    const val PICK_GOD_DELETE = (0xFFFF0217).toInt()  // Wire: FF FF 02 17 = -65001 (Linkpoint-specific, unique reserved ID)

    // --- Classified Messages Extended ---
    /** ClassifiedGodDelete - God delete classified */
    const val CLASSIFIED_GOD_DELETE = (0xFFFF002F).toInt()      // Wire: FF FF 00 2F = -65489

    // --- Directory Query Messages ---
    /** DirFindQuery - Directory find query */
    const val DIR_FIND_QUERY = (0xFFFF001F).toInt()  // Wire: FF FF 00 1F = -65505 (Lumiya: DirFindQuery)
    
    /** DirPlacesQuery - Places query */
    const val DIR_PLACES_QUERY = (0xFFFF0021).toInt()  // Wire: FF FF 00 21 = -65503 (Lumiya: DirPlacesQuery)
    
    /** DirClassifiedQuery - Classified query */
    const val DIR_CLASSIFIED_QUERY = (0xFFFF0027).toInt()  // Wire: FF FF 00 27 = -65497 (Lumiya: DirClassifiedQuery)
    
    /** DirLandQuery - Land query */
    const val DIR_LAND_QUERY = (0xFFFF0030).toInt()  // Wire: FF FF 00 30 = -65488 (Lumiya: DirLandQuery)
    
    /** DirPopularQuery - Popular query */
    const val DIR_POPULAR_QUERY = (0xFFFF0033).toInt()  // Wire: FF FF 00 33 = -65485 (Lumiya: DirPopularQuery)
    
    /** PlacesQuery - Places query */
    const val PLACES_QUERY = (0xFFFF001D).toInt()  // Wire: FF FF 00 1D = -65507 (Lumiya: PlacesQuery)

    // --- Inventory Messages Extended ---
    /** LinkInventoryItem - Link inventory item */
    const val LINK_INVENTORY_ITEM = (0xFFFF01AA).toInt()  // Wire: FF FF 01 AA = -65110 (Lumiya: LinkInventoryItem)
    
    /** ChangeInventoryItemFlags - Change item flags */
    const val CHANGE_INVENTORY_ITEM_FLAGS = (0xFFFF010F).toInt()  // Wire: FF FF 01 0F = -65265 (Lumiya: ChangeInventoryItemFlags)
    
    /** RequestInventoryAsset - Request asset */
    const val REQUEST_INVENTORY_ASSET = (0xFFFF011A).toInt()  // Wire: FF FF 01 1A = -65254 (Lumiya: RequestInventoryAsset)
    
    /** TransferInventory - Transfer inventory */
    const val TRANSFER_INVENTORY = (0xFFFF0127).toInt()  // Wire: FF FF 01 27 = -65241 (Lumiya: TransferInventory)
    
    /** TransferInventoryAck - Transfer ACK */
    const val TRANSFER_INVENTORY_ACK = (0xFFFF0128).toInt()  // Wire: FF FF 01 28 = -65240 (Lumiya: TransferInventoryAck)
    
    /** RetrieveInstantMessages - Retrieve IMs */
    const val RETRIEVE_INSTANT_MESSAGES = (0xFFFF00FF).toInt()  // Wire: FF FF 00 FF = -65281 (Lumiya: RetrieveInstantMessages)

    // --- Group Request Messages ---
    /** CreateGroupRequest - Create group */
    const val CREATE_GROUP_REQUEST = (0xFFFF0153).toInt()  // Wire: FF FF 01 53 = -65197 (Lumiya: CreateGroupRequest)
    
    /** JoinGroupRequest - Join group */
    const val JOIN_GROUP_REQUEST = (0xFFFF0157).toInt()  // Wire: FF FF 01 57 = -65193 (Lumiya: JoinGroupRequest)
    
    /** EjectGroupMemberRequest - Eject member */
    const val EJECT_GROUP_MEMBER_REQUEST = (0xFFFF0159).toInt()  // Wire: FF FF 01 59 = -65191 (Lumiya: EjectGroupMemberRequest)
    
    /** InviteGroupRequest - Invite to group */
    const val INVITE_GROUP_REQUEST = (0xFFFF015D).toInt()  // Wire: FF FF 01 5D = -65187 (Lumiya: InviteGroupRequest)
    
    /** GroupTitlesRequest - Request titles */
    const val GROUP_TITLES_REQUEST = (0xFFFF0177).toInt()  // Wire: FF FF 01 77 = -65161 (Lumiya: GroupTitlesRequest)
    
    /** GroupMembersRequest - Request members */
    const val GROUP_MEMBERS_REQUEST = (0xFFFF016E).toInt()  // Wire: FF FF 01 6E = -65170 (Lumiya: GroupMembersRequest)
    
    /** GroupRoleMembersRequest - Request role members */
    const val GROUP_ROLE_MEMBERS_REQUEST = (0xFFFF0175).toInt()  // Wire: FF FF 01 75 = -65163 (Lumiya: GroupRoleMembersRequest)
    
    /** GroupRoleDataRequest - Request role data */
    const val GROUP_ROLE_DATA_REQUEST = (0xFFFF0173).toInt()  // Wire: FF FF 01 73 = -65165 (Lumiya: GroupRoleDataRequest)
    
    /** GroupNoticesListRequest - Request notices list */
    const val GROUP_NOTICES_LIST_REQUEST = (0xFFFF003A).toInt()  // Wire: FF FF 00 3A = -65478 (Lumiya: GroupNoticesListRequest)
    
    /** GroupActiveProposalsRequest - Active proposals */
    const val GROUP_ACTIVE_PROPOSALS_REQUEST = (0xFFFF0167).toInt()  // Wire: FF FF 01 67 = -65177 (Lumiya: GroupActiveProposalsRequest)
    
    /** GroupVoteHistoryRequest - Vote history */
    const val GROUP_VOTE_HISTORY_REQUEST = (0xFFFF0169).toInt()  // Wire: FF FF 01 69 = -65175 (Lumiya: GroupVoteHistoryRequest)
    
    /** GroupAccountSummaryRequest - Account summary */
    const val GROUP_ACCOUNT_SUMMARY_REQUEST = (0xFFFF0161).toInt()  // Wire: FF FF 01 61 = -65183 (Lumiya: GroupAccountSummaryRequest)
    
    /** GroupAccountDetailsRequest - Account details */
    const val GROUP_ACCOUNT_DETAILS_REQUEST = (0xFFFF0163).toInt()  // Wire: FF FF 01 63 = -65181 (Lumiya: GroupAccountDetailsRequest)
    
    /** GroupAccountTransactionsRequest - Transactions */
    const val GROUP_ACCOUNT_TRANSACTIONS_REQUEST = (0xFFFF0165).toInt()  // Wire: FF FF 01 65 = -65179 (Lumiya: GroupAccountTransactionsRequest)
    
    /** GroupDataUpdate - Group data update */
    const val GROUP_DATA_UPDATE = (0xFFFF0184).toInt()  // Wire: FF FF 01 84 = -65148 (Lumiya: GroupDataUpdate)
    
    /** GroupRoleUpdate - Role update */
    const val GROUP_ROLE_UPDATE = (0xFFFF017A).toInt()  // Wire: FF FF 01 7A = -65158 (Lumiya: GroupRoleUpdate)
    
    /** GroupRoleChanges - Role changes */
    const val GROUP_ROLE_CHANGES = (0xFFFF0156).toInt()         // Wire: FF FF 01 56 = -65194
    
    /** GroupTitleUpdate - Title update */
    const val GROUP_TITLE_UPDATE = (0xFFFF0179).toInt()  // Wire: FF FF 01 79 = -65159 (Lumiya: GroupTitleUpdate)
    
    /** UpdateGroupInfo - Update group info */
    const val UPDATE_GROUP_INFO = (0xFFFF0155).toInt()  // Wire: FF FF 01 55 = -65195 (Lumiya: UpdateGroupInfo)
    
    /** GroupProposalBallot - Proposal ballot */
    const val GROUP_PROPOSAL_BALLOT = (0xFFFF016C).toInt()  // Wire: FF FF 01 6C = -65172 (Lumiya: GroupProposalBallot)
    
    /** StartGroupProposal - Start proposal */
    const val START_GROUP_PROPOSAL = (0xFFFF016B).toInt()  // Wire: FF FF 01 6B = -65173 (Lumiya: StartGroupProposal)
    
    /** SetGroupAcceptNotices - Set accept notices */
    const val SET_GROUP_ACCEPT_NOTICES = (0xFFFF0172).toInt()  // Wire: FF FF 01 72 = -65166 (Lumiya: SetGroupAcceptNotices)
    
    /** SetGroupContribution - Set contribution */
    const val SET_GROUP_CONTRIBUTION = (0xFFFF0171).toInt()  // Wire: FF FF 01 71 = -65167 (Lumiya: SetGroupContribution)
    
    /** TallyVotes - Tally votes */
    const val TALLY_VOTES = (0xFFFF016D).toInt()  // Wire: FF FF 01 6D = -65171 (Lumiya: TallyVotes)

    // --- Live Help Messages ---
    /** LiveHelpGroupRequest - Live help request */
    const val LIVE_HELP_GROUP_REQUEST = (0xFFFF017B).toInt()  // Wire: FF FF 01 7B = -65157 (Lumiya: LiveHelpGroupRequest)
    
    /** LiveHelpGroupReply - Live help reply */
    const val LIVE_HELP_GROUP_REPLY = (0xFFFF017C).toInt()  // Wire: FF FF 01 7C = -65156 (Lumiya: LiveHelpGroupReply)

    // --- Avatar Messages Extended ---
    /** AvatarTextureUpdate - Texture update */
    const val AVATAR_TEXTURE_UPDATE_MSG = (0xFFFF0004).toInt()  // Wire: FF FF 00 04 = -65532
    
    /** AvatarPickerRequest - Picker request */
    const val AVATAR_PICKER_REQUEST_MSG = (0xFFFF001A).toInt()  // Wire: FF FF 00 1A = -65510 (Lumiya: AvatarPickerRequest)
    
    /** AvatarPropertiesRequest - Properties request */
    const val AVATAR_PROPERTIES_REQUEST_MSG = (0xFFFF00A9).toInt()  // Wire: FF FF 00 A9 = -65367 (Lumiya: AvatarPropertiesRequest)
    
    /** RebakeAvatarTextures - Rebake textures (alias) */
    const val REBAKE_AVATAR_TEXTURES_MSG = (0xFFFF0057).toInt() // Wire: FF FF 00 57 (Lumiya-verified, matches REBAKE_AVATAR_TEXTURES)

    // --- Velocity Interpolation Messages ---
    /** VelocityInterpolateOn - Enable interpolation */
    const val VELOCITY_INTERPOLATE_ON = (0xFFFF007D).toInt()  // Wire: FF FF 00 7D = -65411 (Lumiya: VelocityInterpolateOn)
    
    /** VelocityInterpolateOff - Disable interpolation */
    const val VELOCITY_INTERPOLATE_OFF = (0xFFFF007E).toInt()  // Wire: FF FF 00 7E = -65410 (Lumiya: VelocityInterpolateOff)

    // --- Object Messages Extended ---
    /** ObjectIncludeInSearch - Include in search */
    const val OBJECT_INCLUDE_IN_SEARCH = (0xFFFF01A8).toInt()  // Wire: FF FF 01 A8 = -65112 (Lumiya: ObjectIncludeInSearch)
    
    /** ObjectExportSelected - Export selected */
    const val OBJECT_EXPORT_SELECTED = (0xFFFF007B).toInt()  // Wire: FF FF 00 7B = -65413 (Lumiya: ObjectExportSelected)
    
    /** DerezContainer - Derez container */
    const val DEREZ_CONTAINER = (0xFFFF0068).toInt()  // Wire: FF FF 00 68 = -65432 (Lumiya: DerezContainer)
    
    /** BuyObjectInventory - Buy object inventory */
    const val BUY_OBJECT_INVENTORY_MSG = (0xFFFF0067).toInt()  // Wire: FF FF 00 67 = -65433 (Lumiya: BuyObjectInventory)

    // --- Test/Debug Messages ---
    /** TestMessage - Test message */
    const val TEST_MESSAGE = (0xFFFF0001).toInt()  // Wire: FF FF 00 01 = -65535 (Lumiya: TestMessage)
    
    /** NetTest - Network test */
    const val NET_TEST = (0xFFFF0146).toInt()  // Wire: FF FF 01 46 = -65210 (Lumiya: NetTest)
    
    /** StateSave - State save */
    const val STATE_SAVE = (0xFFFF007F).toInt()  // Wire: FF FF 00 7F = -65409 (Lumiya: StateSave)
    
    /** SubscribeLoad - Subscribe load */
    const val SUBSCRIBE_LOAD = (0xFFFF0007).toInt()  // Wire: FF FF 00 07 = -65529 (Lumiya: SubscribeLoad)
    
    /** UnsubscribeLoad - Unsubscribe load */
    const val UNSUBSCRIBE_LOAD = (0xFFFF0008).toInt()  // Wire: FF FF 00 08 = -65528 (Lumiya: UnsubscribeLoad)

    // --- Logging Messages ---
    /** LogTextMessage - Log text message */
    const val LOG_TEXT_MESSAGE = (0xFFFF0187).toInt()  // Wire: FF FF 01 87 = -65145 (Lumiya: LogTextMessage)
    
    /** LogDwellTime - Log dwell time */
    const val LOG_DWELL_TIME = (0xFFFF0012).toInt()  // Wire: FF FF 00 12 = -65518 (Lumiya: LogDwellTime)
    
    /** LogFailedMoneyTransaction - Log failed transaction */
    const val LOG_FAILED_MONEY_TRANSACTION = (0xFFFF0014).toInt()  // Wire: FF FF 00 14 = -65516 (Lumiya: LogFailedMoneyTransaction)
    
    /** LogParcelChanges - Log parcel changes */
    const val LOG_PARCEL_CHANGES = (0xFFFF00E0).toInt()  // Wire: FF FF 00 E0 = -65312 (Lumiya: LogParcelChanges)
    
    /** DataServerLogout - Data server logout */
    const val DATA_SERVER_LOGOUT = (0xFFFF00FB).toInt()  // Wire: FF FF 00 FB = -65285 (Lumiya: DataServerLogout)

    // --- Kick/Eject Messages ---
    /** KickUser - Kick user */
    const val KICK_USER_MSG = (0xFFFF00A3).toInt()  // Wire: FF FF 00 A3 = -65373 (Lumiya: KickUser)
    
    
    // --- Money Messages Extended ---
    /** MoneyBalanceRequest - Balance request */
    const val MONEY_BALANCE_REQUEST_MSG = (0xFFFF0139).toInt()  // Wire: FF FF 01 39 = -65223 (Lumiya: MoneyBalanceRequest)
    
    /** MoneyTransferBackend - Transfer backend */
    const val MONEY_TRANSFER_BACKEND = (0xFFFF0138).toInt()  // Wire: FF FF 01 38 = -65224 (Lumiya: MoneyTransferBackend)
    
    /** EconomyDataRequest - Economy request */
    const val ECONOMY_DATA_REQUEST = (0xFFFF0018).toInt()       // Wire: FF FF 00 18 = -65512
    
    /** RequestPayPrice - Request pay price */
    const val REQUEST_PAY_PRICE_MSG = (0xFFFF00A1).toInt()      // Wire: FF FF 00 A1 = -65375

    // --- Parcel Request Messages ---
    /** ParcelAccessListRequest - Access list request */
    const val PARCEL_ACCESS_LIST_REQUEST = (0xFFFF00D7).toInt()  // Wire: FF FF 00 D7 = -65321 (Lumiya: ParcelAccessListRequest)
    
    /** ParcelDwellRequest - Dwell request */
    const val PARCEL_DWELL_REQUEST = (0xFFFF00DA).toInt()  // Wire: FF FF 00 DA = -65318 (Lumiya: ParcelDwellRequest)
    
    /** ParcelInfoRequest - Info request */
    const val PARCEL_INFO_REQUEST = (0xFFFF0036).toInt()  // Wire: FF FF 00 36 = -65482 (Lumiya: ParcelInfoRequest)
    
    /** ParcelObjectOwnersRequest - Owners request */
    const val PARCEL_OBJECT_OWNERS_REQUEST = (0xFFFF0038).toInt()  // Wire: FF FF 00 38 = -65480 (Lumiya: ParcelObjectOwnersRequest)
    
    /** RequestParcelTransfer - Parcel transfer */
    const val REQUEST_PARCEL_TRANSFER = (0xFFFF00DC).toInt()  // Wire: FF FF 00 DC = -65316 (Lumiya: RequestParcelTransfer)
    
    /** UpdateParcel - Update parcel */
    const val UPDATE_PARCEL = (0xFFFF00DD).toInt()  // Wire: FF FF 00 DD = -65315 (Lumiya: UpdateParcel)

    // --- Estate Request Messages ---
    /** EstateCovenantRequest - Covenant request */
    const val ESTATE_COVENANT_REQUEST = (0xFFFF00CB).toInt()    // Wire: FF FF 00 CB = -65333

    // --- Name/Value Messages ---
    /** NameValuePair - Name value pair */
    const val NAME_VALUE_PAIR = (0xFFFF0149).toInt()  // Wire: FF FF 01 49 = -65207 (Lumiya: NameValuePair)
    
    /** RemoveNameValuePair - Remove name value */
    const val REMOVE_NAME_VALUE_PAIR = (0xFFFF014A).toInt()  // Wire: FF FF 01 4A = -65206 (Lumiya: RemoveNameValuePair)

    // --- CPU/System Messages ---
    /** SetCPURatio - Set CPU ratio */
    const val SET_CPU_RATIO = (0xFFFF0147).toInt()  // Wire: FF FF 01 47 = -65209 (Lumiya naming mismatch fix)
    
    /** SetSimPresenceInDatabase - Sim presence */
    const val SET_SIM_PRESENCE_IN_DATABASE = (0xFFFF0017).toInt()  // Wire: FF FF 00 17 = -65513 (Lumiya: SetSimPresenceInDatabase)
    
    /** SetSimStatusInDatabase - Sim status */
    const val SET_SIM_STATUS_IN_DATABASE = (0xFFFF0016).toInt()  // Wire: FF FF 00 16 = -65514 (Lumiya: SetSimStatusInDatabase)

    // =====================================
    // PHASE 5: 100 Additional Message Handlers
    // =====================================

    // --- Agent Movement Messages ---
        
        
        
        
        
    
    // --- Object Request Messages ---
        
        
    /** ObjectDeselect - Deselect object (alias for OBJECT_DESELECT) */
    const val OBJECT_DESELECT_MSG = (0xFFFF006F).toInt()  // Wire: FF FF 00 6F = -65425 (Lumiya: ObjectDeselect)

    /** ObjectGrab - Start grabbing object (alias for OBJECT_GRAB) */
    const val OBJECT_GRAB_MSG = (0xFFFF0075).toInt()             // Wire: FF FF 00 75 (Lumiya-verified, matches OBJECT_GRAB)

    /** ObjectGrabUpdate - Update grab position (alias for OBJECT_GRAB_UPDATE) */
    const val OBJECT_GRAB_UPDATE_MSG = (0xFFFF0076).toInt()      // Wire: FF FF 00 76 (Lumiya-verified, matches OBJECT_GRAB_UPDATE)

    /** ObjectDeGrab - Release grabbed object */
    const val OBJECT_DE_GRAB = (0xFFFF0077).toInt()              // Wire: FF FF 00 77 (Lumiya-verified, matches OBJECT_DEGRAB)

    /** ObjectSpinStart - Start spinning object (alias for OBJECT_SPIN_START) */
    const val OBJECT_SPIN_START_MSG = (0xFFFF0078).toInt()       // Wire: FF FF 00 78 (Lumiya-verified, matches OBJECT_SPIN_START)

    /** ObjectSpinUpdate - Update spin (alias for OBJECT_SPIN_UPDATE) */
    const val OBJECT_SPIN_UPDATE_MSG = (0xFFFF0079).toInt()      // Wire: FF FF 00 79 (Lumiya-verified, matches OBJECT_SPIN_UPDATE)

    /** ObjectSpinStop - Stop spinning object (alias for OBJECT_SPIN_STOP) */
    const val OBJECT_SPIN_STOP_MSG = (0xFFFF007A).toInt()        // Wire: FF FF 00 7A (Lumiya-verified, matches OBJECT_SPIN_STOP)
    
        
        
        
        
        
    
    // --- Object Update Messages Extended ---
        
        
    /** ObjectPosition - Update position */
    const val OBJECT_POSITION_MSG = 65284                        // Wire: FF 04 (medium frequency)

    // --- Disable Simulator ---
    
    // --- Sound Messages Extended ---
    /** SoundTrigger - High frequency sound trigger */
    const val SOUND_TRIGGER_HF = 29                              // Wire: 1D (high frequency)
    
    /** StopSound - Stop playing sound */
    const val STOP_SOUND = (0xFFFF0218).toInt()  // Wire: FF FF 02 18 = -65000 (Linkpoint-specific, unique reserved ID)
    
    /** SoundPreload - Preload sound */
    const val SOUND_PRELOAD = (0xFFFF0219).toInt()  // Wire: FF FF 02 19 = -64999 (Linkpoint-specific, unique reserved ID)
    
    /** SoundGainChange - Sound volume change */
    const val SOUND_GAIN_CHANGE = (0xFFFF021A).toInt()  // Wire: FF FF 02 1A = -64998 (Linkpoint-specific, unique reserved ID)

    // --- Animation Messages ---
        
    /** AgentRequestAnimation - Request animation */
    const val AGENT_REQUEST_ANIMATION = (0xFFFF0208).toInt()  // Wire: FF FF 02 08 = -65016 (Linkpoint-specific, unique reserved ID)
    
    /** AvatarAnimationDone - Animation completed */
    const val AVATAR_ANIMATION_DONE = (0xFFFF0209).toInt()  // Wire: FF FF 02 09 = -65015 (Linkpoint-specific, unique reserved ID)

    // --- Gesture Messages ---
        
        
    /** GestureRequest - Request gesture */
    const val GESTURE_REQUEST = (0xFFFF022D).toInt()  // Wire: FF FF 02 2D = -64979 (Linkpoint-specific, unique reserved ID)
    
    /** GestureResponse - Gesture response */
    const val GESTURE_RESPONSE = (0xFFFF022E).toInt()  // Wire: FF FF 02 2E = -64978 (Linkpoint-specific, unique reserved ID)

    // --- Appearance Messages Extended ---
        
    /** SetFollowCamPropertiesMsg - Follow cam properties */
    const val SET_FOLLOW_CAM_PROPERTIES_MSG = (0xFFFF009F).toInt() // Wire: FF FF 00 9F = -65377
    
    /** ClearFollowCamPropertiesMsg - Clear follow cam */
    const val CLEAR_FOLLOW_CAM_PROPERTIES_MSG = (0xFFFF00A0).toInt() // Wire: FF FF 00 A0 = -65376

    // --- Attachment Messages Extended ---
    /** ObjectAttachResponse - Attachment response */
    const val OBJECT_ATTACH_RESPONSE = (0xFFFF0220).toInt()  // Wire: FF FF 02 20 = -64992 (Linkpoint-specific, unique reserved ID)
    
    /** AttachmentIntoInventory - Attachment to inventory */
    const val ATTACHMENT_INTO_INVENTORY = (0xFFFF0221).toInt()  // Wire: FF FF 02 21 = -64991 (Linkpoint-specific, unique reserved ID)
    
    /** AttachFromInventory - Attach from inventory */
    const val ATTACH_FROM_INVENTORY = (0xFFFF0222).toInt()  // Wire: FF FF 02 22 = -64990 (Linkpoint-specific, unique reserved ID)

    // --- User Data Messages ---
    /** UserInfoReqMsg - User info request */
    const val USER_INFO_REQ_MSG = (0xFFFF01AF).toInt()           // Wire: FF FF 01 AF = -65105
    
    /** UserInfoReplyMsg - User info reply */
    const val USER_INFO_REPLY_MSG = (0xFFFF01B0).toInt()         // Wire: FF FF 01 B0 = -65104
    
    /** UpdateUserInfoMsg - Update user info */
    const val UPDATE_USER_INFO_MSG = (0xFFFF01B1).toInt()        // Wire: FF FF 01 B1 = -65103

    // --- Friendship Messages Extended ---
        
        
    /** TrackAgentSession - Track agent session */
    const val TRACK_AGENT_SESSION = (0xFFFF0229).toInt()  // Wire: FF FF 02 29 = -64983 (Linkpoint-specific, unique reserved ID)
    
    /** OfferFriendship - Offer friendship */
    const val OFFER_FRIENDSHIP = (0xFFFF022A).toInt()  // Wire: FF FF 02 2A = -64982 (Linkpoint-specific, unique reserved ID)
    
    /** FriendshipOffered - Friendship offered */
    const val FRIENDSHIP_OFFERED = (0xFFFF022B).toInt()  // Wire: FF FF 02 2B = -64981 (Linkpoint-specific, unique reserved ID)

    // --- Group Messages Extended ---
        
        
        
    /** GroupNoticeRequest - Group notice request */
    const val GROUP_NOTICE_REQUEST_MSG = (0xFFFF003C).toInt()  // Wire: FF FF 00 3C = -65476 (Lumiya: GroupNoticeRequest)
    
    /** GroupNoticesResponse - Group notices response */
    const val GROUP_NOTICES_RESPONSE = (0xFFFF0204).toInt()  // Wire: FF FF 02 04 = -65020 (Linkpoint-specific, unique reserved ID)

    // --- Script Control Messages ---
    /** ScriptControlChange - Control change */
    const val SCRIPT_CONTROL_CHANGE_MSG = (0xFFFF00BD).toInt()  // Wire: FF FF 00 BD = -65347 (Lumiya: ScriptControlChange)

    // --- Environment Messages ---
    /** SimulatorViewerTimeMessageMsg - Viewer time */
    const val SIMULATOR_VIEWER_TIME_MESSAGE_MSG = (0xFFFF0096).toInt()  // Wire: FF FF 00 96 = -65386 (Lumiya: SimulatorViewerTimeMessage)
    
    /** WindLightSettingsUpdate - Windlight settings */
    const val WINDLIGHT_SETTINGS_UPDATE = (0xFFFF0201).toInt()  // Wire: FF FF 02 01 = -65023 (Linkpoint-specific, unique reserved ID)
    
    /** ParcelEnvironmentBlock - Parcel environment */
    const val PARCEL_ENVIRONMENT_BLOCK = (0xFFFF0202).toInt()  // Wire: FF FF 02 02 = -65022 (Linkpoint-specific, unique reserved ID)
    
    /** SetEnvironment - Set environment */
    const val SET_ENVIRONMENT = (0xFFFF0203).toInt()  // Wire: FF FF 02 03 = -65021 (Linkpoint-specific, unique reserved ID)

    // --- Notecard/Script Edit Messages ---
    /** UpdateTaskInventoryNotecardItem - Update notecard */
    const val UPDATE_TASK_INVENTORY_NOTECARD_ITEM = (0xFFFF0223).toInt()  // Wire: FF FF 02 23 = -64989 (Linkpoint-specific, unique reserved ID)
    
    /** UpdateNotecardAgentInventory - Update agent notecard */
    const val UPDATE_NOTECARD_AGENT_INVENTORY = (0xFFFF0224).toInt()  // Wire: FF FF 02 24 = -64988 (Linkpoint-specific, unique reserved ID)
    
    /** UpdateGestureAgentInventory - Update gesture */
    const val UPDATE_GESTURE_AGENT_INVENTORY = (0xFFFF0225).toInt()  // Wire: FF FF 02 25 = -64987 (Linkpoint-specific, unique reserved ID)
    
    /** UpdateScriptAgent - Update script agent */
    const val UPDATE_SCRIPT_AGENT = (0xFFFF0226).toInt()  // Wire: FF FF 02 26 = -64986 (Linkpoint-specific, unique reserved ID)
    
    /** UpdateScriptTask - Update script task */
    const val UPDATE_SCRIPT_TASK = (0xFFFF0227).toInt()  // Wire: FF FF 02 27 = -64985 (Linkpoint-specific, unique reserved ID)
    
    /** ScriptSensorRemove - Remove sensor */
    const val SCRIPT_SENSOR_REMOVE = (0xFFFF0228).toInt()  // Wire: FF FF 02 28 = -64984 (Linkpoint-specific, unique reserved ID)

    // --- Autopilot Messages ---
    /** Autopilot - Start autopilot */
    const val AUTOPILOT = (0xFFFF0206).toInt()  // Wire: FF FF 02 06 = -65018 (Linkpoint-specific, unique reserved ID)
    
    /** AutopilotCancel - Cancel autopilot */
    const val AUTOPILOT_CANCEL = (0xFFFF0207).toInt()  // Wire: FF FF 02 07 = -65017 (Linkpoint-specific, unique reserved ID)

    // --- Terrain Messages ---
    /** TerrainHeightData - Terrain height */
    const val TERRAIN_HEIGHT_DATA = (0xFFFF0233).toInt()  // Wire: FF FF 02 33 = -64973 (Linkpoint-specific, unique reserved ID)

    // --- God Mode Messages ---
        
        
        
    /** GodDeleteSim - God delete sim */
    const val GOD_DELETE_SIM = (0xFFFF0205).toInt()  // Wire: FF FF 02 05 = -65019 (Linkpoint-specific, unique reserved ID)
    
        
        
    /** SimOwnerRequest - Sim owner request */
    const val SIM_OWNER_REQUEST = (0xFFFF0237).toInt()  // Wire: FF FF 02 37 = -64969 (Linkpoint-specific, unique reserved ID)
    
    /** SimOwnerResponse - Sim owner response */
    const val SIM_OWNER_RESPONSE = (0xFFFF0238).toInt()  // Wire: FF FF 02 38 = -64968 (Linkpoint-specific, unique reserved ID)

    // --- Estate Manager Messages ---
        
    /** EstateChangeInfo - Estate change info */
    const val ESTATE_CHANGE_INFO = (0xFFFF021B).toInt()  // Wire: FF FF 02 1B = -64997 (Linkpoint-specific, unique reserved ID)
    
    /** EstateExperienceReply - Estate experience reply */
    const val ESTATE_EXPERIENCE_REPLY = (0xFFFF021C).toInt()  // Wire: FF FF 02 1C = -64996 (Linkpoint-specific, unique reserved ID)

    // --- Land Bank Messages ---
    /** LandBuy - Buy land */
    const val LAND_BUY = (0xFFFF021D).toInt()  // Wire: FF FF 02 1D = -64995 (Linkpoint-specific, unique reserved ID)
    
    /** LandBuyPass - Buy land pass */
    const val LAND_BUY_PASS = (0xFFFF021E).toInt()  // Wire: FF FF 02 1E = -64994 (Linkpoint-specific, unique reserved ID)

    // --- Asset/Transfer Messages Extended ---
    /** AssetInfoRequest - Asset info request */
    const val ASSET_INFO_REQUEST = (0xFFFF0231).toInt()  // Wire: FF FF 02 31 = -64975 (Linkpoint-specific, unique reserved ID)
    
    /** AssetInfoResponse - Asset info response */
    const val ASSET_INFO_RESPONSE = (0xFFFF0232).toInt()  // Wire: FF FF 02 32 = -64974 (Linkpoint-specific, unique reserved ID)
    
    /** MapLayerRequest - Map layer request */
    const val MAP_LAYER_REQUEST_MSG = (0xFFFF0195).toInt()  // Wire: FF FF 01 95 = -65131 (Lumiya: MapLayerRequest)
    
    /** MapLayerReply - Map layer reply */
    const val MAP_LAYER_REPLY_MSG = (0xFFFF0196).toInt()  // Wire: FF FF 01 96 = -65130 (Lumiya: MapLayerReply)

    // --- Agent Data Messages ---
        
    /** AgentDataUpdate - Agent data update */
    const val AGENT_DATA_UPDATE_MSG = (0xFFFF0183).toInt()  // Wire: FF FF 01 83 = -65149 (Lumiya: AgentDataUpdate)

    // --- Pick/Classified Messages Extended ---
        
    /** PickUpdateInfo - Update pick info */
    const val PICK_UPDATE_INFO = (0xFFFF0216).toInt()  // Wire: FF FF 02 16 = -65002 (Linkpoint-specific, unique reserved ID)
    
        
    
    // --- Interest List Messages ---
    /** InterestListRequest - Interest list request */
    const val INTEREST_LIST_REQUEST = (0xFFFF0212).toInt()  // Wire: FF FF 02 12 = -65006 (Linkpoint-specific, unique reserved ID)
    
    /** InterestListReply - Interest list reply */
    const val INTEREST_LIST_REPLY = (0xFFFF0213).toInt()  // Wire: FF FF 02 13 = -65005 (Linkpoint-specific, unique reserved ID)

    // --- Object Export Messages ---
    /** ExportDynaFile - Export dynamic file */
    const val EXPORT_DYNA_FILE = (0xFFFF022F).toInt()  // Wire: FF FF 02 2F = -64977 (Linkpoint-specific, unique reserved ID)
    
    /** ExportDynaFileRequest - Export request */
    const val EXPORT_DYNA_FILE_REQUEST = (0xFFFF0230).toInt()  // Wire: FF FF 02 30 = -64976 (Linkpoint-specific, unique reserved ID)

    // --- Upload Messages ---
    /** UploadBakedTexture - Upload baked texture */
    const val UPLOAD_BAKED_TEXTURE = (0xFFFF0214).toInt()  // Wire: FF FF 02 14 = -65004 (Linkpoint-specific, unique reserved ID)
    
    /** UploadBakedTextureResult - Upload result */
    const val UPLOAD_BAKED_TEXTURE_RESULT = (0xFFFF0215).toInt()  // Wire: FF FF 02 15 = -65003 (Linkpoint-specific, unique reserved ID)

    // --- Object Permission Messages ---
    /** ObjectPermissionsRequest - Request permissions */
    const val OBJECT_PERMISSIONS_REQUEST = (0xFFFF020C).toInt()  // Wire: FF FF 02 0C = -65012 (Linkpoint-specific, unique reserved ID)
    
    /** ObjectPermissionsReply - Permissions reply */
    const val OBJECT_PERMISSIONS_REPLY = (0xFFFF020D).toInt()  // Wire: FF FF 02 0D = -65011 (Linkpoint-specific, unique reserved ID)

    // --- Agent Camera Messages ---
    /** AgentCameraConstraint - Camera constraint */
    const val AGENT_CAMERA_CONSTRAINT = (0xFFFF020E).toInt()  // Wire: FF FF 02 0E = -65010 (Linkpoint-specific, unique reserved ID)
    
    /** CameraConstraintMsg - Camera constraint message */
    const val CAMERA_CONSTRAINT_MSG = (0xFFFF020F).toInt()  // Wire: FF FF 02 0F = -65009 (Linkpoint-specific, unique reserved ID)

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
    const val REGION_OBJECT_UPDATE = (0xFFFF0210).toInt()  // Wire: FF FF 02 10 = -65008 (Linkpoint-specific, unique reserved ID)
    
    /** RegionObjectComplete - Region object complete */
    const val REGION_OBJECT_COMPLETE = (0xFFFF0211).toInt()  // Wire: FF FF 02 11 = -65007 (Linkpoint-specific, unique reserved ID)

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
    const val AGENT_ANIMATION_OVERRIDE = (0xFFFF020A).toInt()  // Wire: FF FF 02 0A = -65014 (Linkpoint-specific, unique reserved ID)
    
    /** ClearAnimationOverride - Clear override */
    const val CLEAR_ANIMATION_OVERRIDE = (0xFFFF020B).toInt()  // Wire: FF FF 02 0B = -65013 (Linkpoint-specific, unique reserved ID)

    // =====================================
    // Phase 6: Final Remaining Handlers
    // =====================================

    // --- Chat Extended ---
    /** ChatEvent - Chat event */
    const val CHAT_EVENT = (0xFFFF0236).toInt()  // Wire: FF FF 02 36 = -64970 (Linkpoint-specific, unique reserved ID)
    
    /** ChatPass - Chat pass */
    const val CHAT_PASS = (0xFFFF00EF).toInt()  // Wire: FF FF 00 EF = -65297 (Lumiya: ChatPass)
    
    // --- Circuit ---
    /** CircuitReady - Circuit ready notification */
    const val CIRCUIT_READY = (0xFFFF0200).toInt()  // Wire: FF FF 02 00 = -65024 (Linkpoint-specific, unique reserved ID)
    
    // --- Data Home Location ---
    /** DataHomeLocationRequest - Request home location */
    const val DATA_HOME_LOCATION_REQUEST = (0xFFFF0234).toInt()  // Wire: FF FF 02 34 = -64972 (Linkpoint-specific, unique reserved ID)
    
    // --- Global Options ---
    /** GlobalOptionsChange - Global options change */
    const val GLOBAL_OPTIONS_CHANGE = (0xFFFF0235).toInt()  // Wire: FF FF 02 35 = -64971 (Linkpoint-specific, unique reserved ID)
    
    // --- UUID Request Messages ---
    /** UUIDNameRequest - Request name for UUID */
    const val UUID_NAME_REQUEST = (0xFFFF00EB).toInt()  // Wire: FF FF 00 EB = -65301 (Lumiya naming mismatch fix)

    /** UUIDGroupNameRequest - Request group name for UUID */
    const val UUID_GROUP_NAME_REQUEST = (0xFFFF00ED).toInt()  // Wire: FF FF 00 ED = -65299 (Lumiya naming mismatch fix)

    // =====================================
    // MESSAGE NAME LOOKUP
    // =====================================

    /**
     * Get human-readable message name from message ID.
     * This is the canonical lookup function - use this instead of maintaining
     * duplicate mappings elsewhere.
     *
     * @param messageId The message ID (can be positive, negative, or medium frequency)
     * @return Human-readable message name, or "Unknown(0xXXXX)" if not found
     */
    fun getMessageName(messageId: Int): String {
        return messageNameMap[messageId] ?: formatUnknownMessageId(messageId)
    }

    /**
     * Format an unknown message ID for display.
     * Shows the ID in appropriate format based on frequency band.
     */
    private fun formatUnknownMessageId(messageId: Int): String {
        return when {
            // High frequency (1-254)
            messageId in 1..254 -> "Unknown(0x${messageId.toString(16).uppercase().padStart(2, '0')})"
            // Medium frequency (65280-65535, i.e., 0xFF00-0xFFFF)
            messageId in 65280..65535 -> {
                val lowByte = messageId and 0xFF
                "Unknown(0xFF${lowByte.toString(16).uppercase().padStart(2, '0')})"
            }
            // Low frequency (negative values, 0xFFFFxxxx)
            messageId < 0 -> {
                // Convert to unsigned representation for display
                val unsignedVal = messageId.toLong() and 0xFFFFFFFFL
                "Unknown(0x${unsignedVal.toString(16).uppercase().padStart(8, '0')})"
            }
            // PacketAck special case (-5)
            else -> "Unknown(0x${messageId.toString(16).uppercase()})"
        }
    }

    /**
     * Map of all known message IDs to their names.
     * Comprehensive lookup for protocol debugging and logging.
     */
    private val messageNameMap: Map<Int, String> by lazy {
        mapOf(
            // High Frequency Messages (1-254)
            START_PING_CHECK to "StartPingCheck",
            COMPLETE_PING_CHECK to "CompletePingCheck",
            NEIGHBOR_LIST to "NeighborList",
            AGENT_UPDATE to "AgentUpdate",
            AGENT_ANIMATION_HF to "AgentAnimation",
            REQUEST_IMAGE to "RequestImage",
            IMAGE_DATA to "ImageData",
            IMAGE_PACKET to "ImagePacket",
            LAYER_DATA to "LayerData",
            OBJECT_UPDATE to "ObjectUpdate",
            OBJECT_UPDATE_COMPRESSED to "ObjectUpdateCompressed",
            OBJECT_UPDATE_CACHED to "ObjectUpdateCached",
            IMPROVED_TERSE_OBJECT_UPDATE to "ImprovedTerseObjectUpdate",
            KILL_OBJECT to "KillObject",
            TRANSFER_PACKET to "TransferPacket",
            SEND_XFER_PACKET to "SendXferPacket",
            CONFIRM_XFER_PACKET to "ConfirmXferPacket",
            AVATAR_ANIMATION to "AvatarAnimation",
            AVATAR_SIT_RESPONSE to "AvatarSitResponse",
            CAMERA_CONSTRAINT to "CameraConstraint",
            PARCEL_PROPERTIES to "ParcelProperties",
            EDGE_DATA_PACKET to "EdgeDataPacket",
            CHILD_AGENT_UPDATE to "ChildAgentUpdate",
            CHILD_AGENT_ALIVE to "ChildAgentAlive",
            CHILD_AGENT_POSITION_UPDATE to "ChildAgentPositionUpdate",
            ATOMIC_PASS_OBJECT to "AtomicPassObject",
            SOUND_TRIGGER to "SoundTrigger",
            PACKET_ACK to "PacketAck",

            // Medium Frequency Messages (65280-65535)
            COARSE_LOCATION_UPDATE to "CoarseLocationUpdate",
            CROSSED_REGION to "CrossedRegion",
            CONFIRM_ENABLE_SIMULATOR to "ConfirmEnableSimulator",
            OBJECT_PROPERTIES to "ObjectProperties",
            OBJECT_PROPERTIES_FAMILY to "ObjectPropertiesFamily",
            REQUEST_OBJECT_PROPERTIES_FAMILY to "RequestObjectPropertiesFamily",
            OBJECT_ADD to "ObjectAdd",
            OBJECT_POSITION to "ObjectPosition",
            PARCEL_PROPERTIES_REQUEST to "ParcelPropertiesRequest",
            SIM_STATUS to "SimStatus",
            ATTACHED_SOUND to "AttachedSound",
            ATTACHED_SOUND_GAIN_CHANGE to "AttachedSoundGainChange",
            PRELOAD_SOUND to "PreloadSound",
            INTERNAL_SCRIPT_MAIL to "InternalScriptMail",
            VIEWER_EFFECT to "ViewerEffect",

            // Low Frequency Messages (negative values)
            USE_CIRCUIT_CODE to "UseCircuitCode",
            REGION_HANDSHAKE to "RegionHandshake",
            REGION_HANDSHAKE_REPLY to "RegionHandshakeReply",
            AGENT_THROTTLE to "AgentThrottle",
            CHAT_FROM_SIMULATOR to "ChatFromSimulator",
            COMPLETE_AGENT_MOVEMENT to "CompleteAgentMovement",
            AGENT_MOVEMENT_COMPLETE to "AgentMovementComplete",
            LOGOUT_REQUEST to "LogoutRequest",
            LOGOUT_REPLY to "LogoutReply",
            IMPROVED_INSTANT_MESSAGE to "ImprovedInstantMessage",
            CHAT_FROM_VIEWER to "ChatFromViewer",
            AGENT_ANIMATION to "AgentAnimation",
            AGENT_SET_APPEARANCE to "AgentSetAppearance",
            AGENT_IS_NOW_WEARING to "AgentIsNowWearing",
            AGENT_REQUEST_SIT to "AgentRequestSit",
            AGENT_SIT to "AgentSit",
            AGENT_DATA_UPDATE to "AgentDataUpdate",
            HEALTH_MESSAGE to "HealthMessage",
            ONLINE_NOTIFICATION to "OnlineNotification",
            OFFLINE_NOTIFICATION to "OfflineNotification",
            CHANGE_USER_RIGHTS to "ChangeUserRights",
            PARCEL_OVERLAY to "ParcelOverlay",
            OBJECT_SELECT to "ObjectSelect",
            MULTIPLE_OBJECT_UPDATE to "MultipleObjectUpdate",
            REZ_OBJECT to "RezObject",
            DEREZ_OBJECT to "DeRezObject",
            OBJECT_DELETE to "ObjectDelete",
            OBJECT_LINK to "ObjectLink",
            OBJECT_DELINK to "ObjectDelink",
            OBJECT_NAME to "ObjectName",
            OBJECT_DESCRIPTION to "ObjectDescription",
            OBJECT_GRAB to "ObjectGrab",
            OBJECT_DEGRAB to "ObjectDeGrab",
            REQUEST_MULTIPLE_OBJECTS to "RequestMultipleObjects",
            SCRIPT_CONTROL_CHANGE to "ScriptControlChange",
            MOVE_INVENTORY_ITEM to "MoveInventoryItem",
            TELEPORT_LANDMARK_REQUEST to "TeleportLandmarkRequest",
            TELEPORT_HOME_REQUEST to "TeleportHomeRequest",
            TELEPORT_LOCATION_REQUEST to "TeleportLocationRequest",
            TELEPORT_LURE_REQUEST to "TeleportLureRequest",
            START_LURE to "StartLure",
            TELEPORT_FINISH to "TeleportFinish",
            TELEPORT_FAILED to "TeleportFailed",
            TELEPORT_PROGRESS to "TeleportProgress",
            TELEPORT_START to "TeleportStart",
            TELEPORT_LOCAL to "TeleportLocal",
            TELEPORT_CANCEL to "TeleportCancel",
            TELEPORT_REQUEST to "TeleportRequest",
            ACTIVATE_GROUP to "ActivateGroup",
            LEAVE_GROUP_REQUEST to "LeaveGroupRequest",
            GROUP_PROFILE_REQUEST to "GroupProfileRequest",
            TERMINATE_FRIENDSHIP to "TerminateFriendship",
            GRANT_USER_RIGHTS to "GrantUserRights",
            FIND_AGENT to "FindAgent",
            PARCEL_BUY to "ParcelBuy",
            PARCEL_DEED_TO_GROUP to "ParcelDeedToGroup",
            PARCEL_RELEASE to "ParcelRelease",
            PARCEL_PROPERTIES_UPDATE to "ParcelPropertiesUpdate",
            PARCEL_RETURN_OBJECTS to "ParcelReturnObjects",
            PARCEL_ACCESS_LIST_UPDATE to "ParcelAccessListUpdate",
            ESTATE_OWNER_MESSAGE to "EstateOwnerMessage",
            FREEZE_USER to "FreezeUser",
            ACTIVATE_GESTURES to "ActivateGestures",
            DEACTIVATE_GESTURES to "DeactivateGestures",
            COPY_INVENTORY_ITEM to "CopyInventoryItem",
            UPDATE_INVENTORY_ITEM to "UpdateInventoryItem",
            CREATE_INVENTORY_FOLDER to "CreateInventoryFolder",
            REZ_SINGLE_ATTACHMENT_FROM_INV to "RezSingleAttachmentFromInv",
            ALERT_MESSAGE to "AlertMessage",
            AGENT_ALERT_MESSAGE to "AgentAlertMessage",
            ENABLE_SIMULATOR to "EnableSimulator",
            DISABLE_SIMULATOR to "DisableSimulator",
            SCRIPT_DIALOG to "ScriptDialog",
            SCRIPT_DIALOG_REPLY to "ScriptDialogReply",
            SCRIPT_QUESTION to "ScriptQuestion",
            LOAD_URL to "LoadURL",
            MONEY_BALANCE_REPLY to "MoneyBalanceReply",
            MONEY_BALANCE_REQUEST to "MoneyBalanceRequest",
            ECONOMY_DATA to "EconomyData",
            INVENTORY_DESCENDENTS to "InventoryDescendents",
            FETCH_INVENTORY_REPLY to "FetchInventoryReply",
            BULK_UPDATE_INVENTORY to "BulkUpdateInventory",
            UPDATE_CREATE_INVENTORY_ITEM to "UpdateCreateInventoryItem",
            REMOVE_INVENTORY_ITEM to "RemoveInventoryItem",
            REMOVE_INVENTORY_FOLDER to "RemoveInventoryFolder",
            AVATAR_APPEARANCE to "AvatarAppearance",
            AGENT_WEARABLES_UPDATE to "AgentWearablesUpdate",
            AGENT_CACHED_TEXTURE to "AgentCachedTexture",
            AGENT_CACHED_TEXTURE_RESPONSE to "AgentCachedTextureResponse",
            AVATAR_PROPERTIES_REPLY to "AvatarPropertiesReply",
            AVATAR_PROPERTIES_REQUEST to "AvatarPropertiesRequest",
            AVATAR_INTERESTS_REPLY to "AvatarInterestsReply",
            AVATAR_GROUPS_REPLY to "AvatarGroupsReply",
            GROUP_PROFILE_REPLY to "GroupProfileReply",
            GROUP_MEMBERS_REPLY to "GroupMembersReply",
            GROUP_ROLE_DATA_REPLY to "GroupRoleDataReply",
            GROUP_TITLES_REPLY to "GroupTitlesReply",
            GROUP_NOTICE_ADD to "GroupNoticeAdd",
            AGENT_GROUP_DATA_UPDATE to "AgentGroupDataUpdate",
            ACCEPT_FRIENDSHIP to "AcceptFriendship",
            DECLINE_FRIENDSHIP to "DeclineFriendship",
            FORM_FRIENDSHIP to "FormFriendship",
            MAP_BLOCK_REPLY to "MapBlockReply",
            MAP_ITEM_REPLY to "MapItemReply",
            MAP_LAYER_REPLY to "MapLayerReply",
            DIR_PLACES_REPLY to "DirPlacesReply",
            DIR_PEOPLE_REPLY to "DirPeopleReply",
            DIR_GROUPS_REPLY to "DirGroupsReply",
            DIR_EVENTS_REPLY to "DirEventsReply",
            DIR_LAND_REPLY to "DirLandReply",
            DIR_CLASSIFIED_REPLY to "DirClassifiedReply",
            REGION_INFO to "RegionInfo",
            SIM_STATS to "SimStats",
            ESTATE_COVENANT_REPLY to "EstateCovenantReply",
            PARCEL_INFO_REPLY to "ParcelInfoReply",
            PARCEL_ACCESS_LIST_REPLY to "ParcelAccessListReply",
            PARCEL_DWELL_REPLY to "ParcelDwellReply",
            TRANSFER_INFO to "TransferInfo",
            ABORT_XFER to "AbortXfer",
            MEAN_COLLISION_ALERT to "MeanCollisionAlert",
            UUID_NAME_REPLY to "UUIDNameReply",
            UUID_GROUP_NAME_REPLY to "UUIDGroupNameReply",
            CLOSE_CIRCUIT to "CloseCircuit",
            OPEN_CIRCUIT to "OpenCircuit",
            ADD_CIRCUIT_CODE to "AddCircuitCode",
            SIM_CRASHED to "SimCrashed"
        )
    }
}

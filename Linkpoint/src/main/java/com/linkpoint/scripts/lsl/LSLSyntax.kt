package com.linkpoint.scripts.lsl

import android.graphics.Color
import java.util.regex.Pattern

/**
 * LSL (Linden Scripting Language) Syntax Definition
 * 
 * Complete syntax highlighting rules for Second Life's scripting language.
 * Includes all functions, events, constants, and types from the official
 * LSL Portal wiki.
 * 
 * Based on Firestorm's LSL editor syntax highlighting.
 */
object LSLSyntax {
    
    // ==================== COLORS ====================
    
    object Colors {
        val KEYWORD = Color.parseColor("#CC7832")      // Orange for keywords
        val TYPE = Color.parseColor("#6897BB")          // Blue for types
        val FUNCTION = Color.parseColor("#FFC66D")      // Yellow for functions
        val EVENT = Color.parseColor("#B389CB")         // Purple for events
        val CONSTANT = Color.parseColor("#9876AA")      // Purple for constants
        val STRING = Color.parseColor("#6A8759")        // Green for strings
        val COMMENT = Color.parseColor("#808080")       // Gray for comments
        val NUMBER = Color.parseColor("#6897BB")        // Blue for numbers
        val OPERATOR = Color.parseColor("#A9B7C6")      // Light gray for operators
        val DEFAULT = Color.parseColor("#A9B7C6")       // Default text color
        val STATE = Color.parseColor("#FF6B68")         // Red for state names
        val DEPRECATED = Color.parseColor("#FF0000")    // Red for deprecated
    }
    
    // ==================== KEYWORDS ====================
    
    val KEYWORDS = setOf(
        // Control flow
        "if", "else", "for", "while", "do", "jump", "return", "state",
        // Declaration
        "default", "event",
        // Logical
        "TRUE", "FALSE"
    )
    
    // ==================== TYPES ====================
    
    val TYPES = setOf(
        "integer", "float", "string", "key", "vector", "rotation", "quaternion", "list"
    )
    
    // ==================== EVENTS ====================
    
    val EVENTS = setOf(
        // Touch events
        "touch_start", "touch", "touch_end",
        // State events
        "state_entry", "state_exit",
        // Sensor events
        "sensor", "no_sensor",
        // Timer events
        "timer",
        // Listen events
        "listen",
        // Money events
        "money",
        // Collision events
        "collision_start", "collision", "collision_end",
        // Land collision events
        "land_collision_start", "land_collision", "land_collision_end",
        // Object events
        "at_target", "not_at_target", "at_rot_target", "not_at_rot_target",
        // Control events
        "control", "run_time_permissions",
        // Attach events
        "attach",
        // Dataserver events
        "dataserver",
        // Link message events
        "link_message",
        // Moving events
        "moving_start", "moving_end",
        // Object rez events
        "object_rez",
        // On rez events
        "on_rez",
        // Changed events
        "changed",
        // Email events
        "email",
        // HTTP events
        "http_response", "http_request",
        // Remote data events
        "remote_data",
        // Experience events
        "experience_permissions", "experience_permissions_denied",
        // Transaction events
        "transaction_result",
        // Path update events
        "path_update"
    )
    
    // ==================== CONSTANTS ====================
    
    val CONSTANTS = setOf(
        // Boolean
        "TRUE", "FALSE",
        
        // Status constants
        "STATUS_PHYSICS", "STATUS_ROTATE_X", "STATUS_ROTATE_Y", "STATUS_ROTATE_Z",
        "STATUS_PHANTOM", "STATUS_SANDBOX", "STATUS_BLOCK_GRAB", "STATUS_DIE_AT_EDGE",
        "STATUS_RETURN_AT_EDGE", "STATUS_CAST_SHADOWS", "STATUS_BLOCK_GRAB_OBJECT",
        
        // Agent info
        "AGENT_FLYING", "AGENT_ATTACHMENTS", "AGENT_SCRIPTED", "AGENT_MOUSELOOK",
        "AGENT_SITTING", "AGENT_ON_OBJECT", "AGENT_AWAY", "AGENT_WALKING",
        "AGENT_IN_AIR", "AGENT_TYPING", "AGENT_CROUCHING", "AGENT_BUSY",
        "AGENT_ALWAYS_RUN", "AGENT_AUTOPILOT", "AGENT_LIST_PARCEL",
        "AGENT_LIST_PARCEL_OWNER", "AGENT_LIST_REGION",
        
        // Camera permissions
        "CAMERA_PITCH", "CAMERA_FOCUS_OFFSET", "CAMERA_FOCUS_OFFSET_X",
        "CAMERA_FOCUS_OFFSET_Y", "CAMERA_FOCUS_OFFSET_Z", "CAMERA_POSITION_LAG",
        "CAMERA_FOCUS_LAG", "CAMERA_DISTANCE", "CAMERA_BEHINDNESS_ANGLE",
        "CAMERA_BEHINDNESS_LAG", "CAMERA_POSITION_THRESHOLD", "CAMERA_FOCUS_THRESHOLD",
        "CAMERA_ACTIVE", "CAMERA_POSITION", "CAMERA_POSITION_LOCKED", "CAMERA_FOCUS",
        "CAMERA_FOCUS_LOCKED",
        
        // Animation constants
        "ANIM_ON", "ANIM_OFF",
        
        // Loop mode
        "LOOP",
        
        // Boolean string
        "EOF",
        
        // Changed constants
        "CHANGED_INVENTORY", "CHANGED_COLOR", "CHANGED_SHAPE", "CHANGED_SCALE",
        "CHANGED_TEXTURE", "CHANGED_LINK", "CHANGED_ALLOWED_DROP", "CHANGED_OWNER",
        "CHANGED_REGION", "CHANGED_TELEPORT", "CHANGED_REGION_START", "CHANGED_MEDIA",
        
        // Type constants
        "TYPE_INTEGER", "TYPE_FLOAT", "TYPE_STRING", "TYPE_KEY", "TYPE_VECTOR",
        "TYPE_ROTATION", "TYPE_INVALID",
        
        // Permission constants
        "PERMISSION_DEBIT", "PERMISSION_TAKE_CONTROLS", "PERMISSION_REMAP_CONTROLS",
        "PERMISSION_TRIGGER_ANIMATION", "PERMISSION_ATTACH", "PERMISSION_RELEASE_OWNERSHIP",
        "PERMISSION_CHANGE_LINKS", "PERMISSION_CHANGE_JOINTS", "PERMISSION_CHANGE_PERMISSIONS",
        "PERMISSION_TRACK_CAMERA", "PERMISSION_CONTROL_CAMERA", "PERMISSION_TELEPORT",
        "PERMISSION_EXPERIENCE", "PERMISSION_SILENT_ESTATE_MANAGEMENT",
        "PERMISSION_OVERRIDE_ANIMATIONS", "PERMISSION_RETURN_OBJECTS",
        
        // Attach constants
        "ATTACH_CHEST", "ATTACH_HEAD", "ATTACH_LSHOULDER", "ATTACH_RSHOULDER",
        "ATTACH_LHAND", "ATTACH_RHAND", "ATTACH_LFOOT", "ATTACH_RFOOT",
        "ATTACH_BACK", "ATTACH_PELVIS", "ATTACH_MOUTH", "ATTACH_CHIN",
        "ATTACH_LEAR", "ATTACH_REAR", "ATTACH_LEYE", "ATTACH_REYE",
        "ATTACH_NOSE", "ATTACH_RUARM", "ATTACH_RLARM", "ATTACH_LUARM",
        "ATTACH_LLARM", "ATTACH_RHIP", "ATTACH_RULEG", "ATTACH_RLLEG",
        "ATTACH_LHIP", "ATTACH_LULEG", "ATTACH_LLLEG", "ATTACH_BELLY",
        "ATTACH_RPEC", "ATTACH_LPEC", "ATTACH_LEFT_PEC", "ATTACH_RIGHT_PEC",
        "ATTACH_HUD_CENTER_2", "ATTACH_HUD_TOP_RIGHT", "ATTACH_HUD_TOP_CENTER",
        "ATTACH_HUD_TOP_LEFT", "ATTACH_HUD_CENTER_1", "ATTACH_HUD_BOTTOM_LEFT",
        "ATTACH_HUD_BOTTOM", "ATTACH_HUD_BOTTOM_RIGHT", "ATTACH_NECK",
        "ATTACH_AVATAR_CENTER", "ATTACH_LHAND_RING1", "ATTACH_RHAND_RING1",
        "ATTACH_TAIL_BASE", "ATTACH_TAIL_TIP", "ATTACH_LWING", "ATTACH_RWING",
        "ATTACH_FACE_JAW", "ATTACH_FACE_LEAR", "ATTACH_FACE_REAR",
        "ATTACH_FACE_LEYE", "ATTACH_FACE_REYE", "ATTACH_FACE_TONGUE",
        "ATTACH_GROIN", "ATTACH_HIND_LFOOT", "ATTACH_HIND_RFOOT",
        
        // Land constants
        "LAND_LEVEL", "LAND_RAISE", "LAND_LOWER", "LAND_SMOOTH", "LAND_NOISE", "LAND_REVERT",
        "LAND_SMALL_BRUSH", "LAND_MEDIUM_BRUSH", "LAND_LARGE_BRUSH",
        
        // Link constants
        "LINK_ROOT", "LINK_SET", "LINK_ALL_OTHERS", "LINK_ALL_CHILDREN", "LINK_THIS",
        
        // Inventory constants
        "INVENTORY_TEXTURE", "INVENTORY_SOUND", "INVENTORY_LANDMARK", "INVENTORY_CLOTHING",
        "INVENTORY_OBJECT", "INVENTORY_NOTECARD", "INVENTORY_SCRIPT", "INVENTORY_BODYPART",
        "INVENTORY_ANIMATION", "INVENTORY_GESTURE", "INVENTORY_ALL", "INVENTORY_NONE",
        "INVENTORY_SETTING", "INVENTORY_MATERIAL",
        
        // Pay constants
        "PAY_HIDE", "PAY_DEFAULT",
        
        // Remote data constants
        "REMOTE_DATA_CHANNEL", "REMOTE_DATA_REQUEST", "REMOTE_DATA_REPLY",
        
        // Control constants
        "CONTROL_FWD", "CONTROL_BACK", "CONTROL_LEFT", "CONTROL_RIGHT", "CONTROL_ROT_LEFT",
        "CONTROL_ROT_RIGHT", "CONTROL_UP", "CONTROL_DOWN", "CONTROL_LBUTTON", "CONTROL_ML_LBUTTON",
        
        // Prim type constants
        "PRIM_TYPE", "PRIM_MATERIAL", "PRIM_PHYSICS", "PRIM_TEMP_ON_REZ", "PRIM_PHANTOM",
        "PRIM_POSITION", "PRIM_SIZE", "PRIM_ROTATION", "PRIM_TYPE_BOX", "PRIM_TYPE_CYLINDER",
        "PRIM_TYPE_PRISM", "PRIM_TYPE_SPHERE", "PRIM_TYPE_TORUS", "PRIM_TYPE_TUBE",
        "PRIM_TYPE_RING", "PRIM_TYPE_SCULPT", "PRIM_HOLE_DEFAULT", "PRIM_HOLE_CIRCLE",
        "PRIM_HOLE_SQUARE", "PRIM_HOLE_TRIANGLE", "PRIM_MATERIAL_STONE", "PRIM_MATERIAL_METAL",
        "PRIM_MATERIAL_GLASS", "PRIM_MATERIAL_WOOD", "PRIM_MATERIAL_FLESH", "PRIM_MATERIAL_PLASTIC",
        "PRIM_MATERIAL_RUBBER", "PRIM_MATERIAL_LIGHT", "PRIM_SHINY_NONE", "PRIM_SHINY_LOW",
        "PRIM_SHINY_MEDIUM", "PRIM_SHINY_HIGH", "PRIM_BUMP_NONE", "PRIM_BUMP_BRIGHT",
        "PRIM_BUMP_DARK", "PRIM_BUMP_WOOD", "PRIM_BUMP_BARK", "PRIM_BUMP_BRICKS",
        "PRIM_BUMP_CHECKER", "PRIM_BUMP_CONCRETE", "PRIM_BUMP_TILE", "PRIM_BUMP_STONE",
        "PRIM_BUMP_DISKS", "PRIM_BUMP_GRAVEL", "PRIM_BUMP_BLOBS", "PRIM_BUMP_SIDING",
        "PRIM_BUMP_LARGETILE", "PRIM_BUMP_STUCCO", "PRIM_BUMP_SUCTION", "PRIM_BUMP_WEAVE",
        "PRIM_TEXGEN_DEFAULT", "PRIM_TEXGEN_PLANAR", "PRIM_SCULPT_TYPE_SPHERE",
        "PRIM_SCULPT_TYPE_TORUS", "PRIM_SCULPT_TYPE_PLANE", "PRIM_SCULPT_TYPE_CYLINDER",
        "PRIM_SCULPT_FLAG_INVERT", "PRIM_SCULPT_FLAG_MIRROR", "PRIM_PHYSICS_SHAPE_PRIM",
        "PRIM_PHYSICS_SHAPE_CONVEX", "PRIM_PHYSICS_SHAPE_NONE", "PRIM_PHYSICS_SHAPE_TYPE",
        "PRIM_GLOW", "PRIM_TEXT", "PRIM_NAME", "PRIM_DESC", "PRIM_FULLBRIGHT", "PRIM_FLEXIBLE",
        "PRIM_TEXGEN", "PRIM_POINT_LIGHT", "PRIM_CAST_SHADOWS", "PRIM_COLOR", "PRIM_BUMP_SHINY",
        "PRIM_TEXTURE", "PRIM_SLICE", "PRIM_LINK_TARGET", "PRIM_SPECULAR", "PRIM_NORMAL",
        "PRIM_ALPHA_MODE", "PRIM_ALPHA_MODE_NONE", "PRIM_ALPHA_MODE_BLEND", "PRIM_ALPHA_MODE_MASK",
        "PRIM_ALPHA_MODE_EMISSIVE", "PRIM_RENDER_MATERIAL", "PRIM_GLTF_BASE_COLOR",
        "PRIM_GLTF_NORMAL", "PRIM_GLTF_METALLIC_ROUGHNESS", "PRIM_GLTF_EMISSIVE",
        
        // Mask constants
        "MASK_BASE", "MASK_OWNER", "MASK_GROUP", "MASK_EVERYONE", "MASK_NEXT",
        "PERM_TRANSFER", "PERM_MODIFY", "PERM_COPY", "PERM_MOVE", "PERM_ALL",
        
        // Parcel constants
        "PARCEL_COUNT_TOTAL", "PARCEL_COUNT_OWNER", "PARCEL_COUNT_GROUP", "PARCEL_COUNT_OTHER",
        "PARCEL_COUNT_SELECTED", "PARCEL_COUNT_TEMP", "PARCEL_DETAILS_NAME", "PARCEL_DETAILS_DESC",
        "PARCEL_DETAILS_OWNER", "PARCEL_DETAILS_GROUP", "PARCEL_DETAILS_AREA", "PARCEL_DETAILS_ID",
        "PARCEL_DETAILS_SEE_AVATARS", "PARCEL_DETAILS_ANY_AVATAR_SOUNDS", "PARCEL_DETAILS_GROUP_SOUNDS",
        "PARCEL_FLAG_ALLOW_FLY", "PARCEL_FLAG_ALLOW_SCRIPTS", "PARCEL_FLAG_ALLOW_LANDMARK",
        "PARCEL_FLAG_ALLOW_TERRAFORM", "PARCEL_FLAG_ALLOW_DAMAGE", "PARCEL_FLAG_ALLOW_CREATE_OBJECTS",
        "PARCEL_FLAG_USE_ACCESS_GROUP", "PARCEL_FLAG_USE_ACCESS_LIST", "PARCEL_FLAG_USE_BAN_LIST",
        "PARCEL_FLAG_USE_LAND_PASS_LIST", "PARCEL_FLAG_LOCAL_SOUND_ONLY", "PARCEL_FLAG_RESTRICT_PUSHOBJECT",
        "PARCEL_FLAG_ALLOW_GROUP_SCRIPTS", "PARCEL_FLAG_ALLOW_CREATE_GROUP_OBJECTS",
        "PARCEL_FLAG_ALLOW_ALL_OBJECT_ENTRY", "PARCEL_FLAG_ALLOW_GROUP_OBJECT_ENTRY",
        
        // List stat constants
        "LIST_STAT_RANGE", "LIST_STAT_MIN", "LIST_STAT_MAX", "LIST_STAT_MEAN",
        "LIST_STAT_MEDIAN", "LIST_STAT_STD_DEV", "LIST_STAT_SUM", "LIST_STAT_SUM_SQUARES",
        "LIST_STAT_NUM_COUNT", "LIST_STAT_GEOMETRIC_MEAN",
        
        // Click action constants
        "CLICK_ACTION_NONE", "CLICK_ACTION_TOUCH", "CLICK_ACTION_SIT", "CLICK_ACTION_BUY",
        "CLICK_ACTION_PAY", "CLICK_ACTION_OPEN", "CLICK_ACTION_PLAY", "CLICK_ACTION_OPEN_MEDIA",
        "CLICK_ACTION_ZOOM", "CLICK_ACTION_DISABLED", "CLICK_ACTION_IGNORE",
        
        // Texture constants
        "TEXTURE_BLANK", "TEXTURE_DEFAULT", "TEXTURE_PLYWOOD", "TEXTURE_TRANSPARENT",
        "TEXTURE_MEDIA",
        
        // Touch constants
        "TOUCH_INVALID_FACE", "TOUCH_INVALID_VECTOR", "TOUCH_INVALID_TEXCOORD",
        
        // Profile constants
        "PROFILE_NONE", "PROFILE_SCRIPT_MEMORY",
        
        // Region constants
        "REGION_FLAG_ALLOW_DAMAGE", "REGION_FLAG_FIXED_SUN", "REGION_FLAG_BLOCK_TERRAFORM",
        "REGION_FLAG_SANDBOX", "REGION_FLAG_DISABLE_COLLISIONS", "REGION_FLAG_DISABLE_PHYSICS",
        "REGION_FLAG_BLOCK_FLY", "REGION_FLAG_ALLOW_DIRECT_TELEPORT", "REGION_FLAG_RESTRICT_PUSHOBJECT",
        
        // HTTP constants
        "HTTP_METHOD", "HTTP_MIMETYPE", "HTTP_BODY_MAXLENGTH", "HTTP_VERIFY_CERT",
        "HTTP_BODY_TRUNCATED", "HTTP_CUSTOM_HEADER", "HTTP_PRAGMA_NO_CACHE",
        "HTTP_VERBOSE_THROTTLE", "HTTP_USER_AGENT",
        
        // Content type constants
        "CONTENT_TYPE_TEXT", "CONTENT_TYPE_HTML", "CONTENT_TYPE_XML", "CONTENT_TYPE_XHTML",
        "CONTENT_TYPE_ATOM", "CONTENT_TYPE_JSON", "CONTENT_TYPE_LLSD", "CONTENT_TYPE_FORM",
        "CONTENT_TYPE_RSS",
        
        // Math constants
        "PI", "TWO_PI", "PI_BY_TWO", "DEG_TO_RAD", "RAD_TO_DEG", "SQRT2",
        
        // Null constants
        "NULL_KEY", "EOF",
        
        // Rotation constants
        "ZERO_ROTATION", "ZERO_VECTOR",
        
        // String constants
        "URL_REQUEST_GRANTED", "URL_REQUEST_DENIED",
        
        // JSON constants
        "JSON_INVALID", "JSON_OBJECT", "JSON_ARRAY", "JSON_NUMBER", "JSON_STRING",
        "JSON_NULL", "JSON_TRUE", "JSON_FALSE", "JSON_DELETE", "JSON_APPEND",
        
        // XP error constants
        "XP_ERROR_NONE", "XP_ERROR_THROTTLED", "XP_ERROR_EXPERIENCES_DISABLED",
        "XP_ERROR_INVALID_PARAMETERS", "XP_ERROR_NOT_PERMITTED", "XP_ERROR_NO_EXPERIENCE",
        "XP_ERROR_NOT_FOUND", "XP_ERROR_INVALID_EXPERIENCE", "XP_ERROR_EXPERIENCE_DISABLED",
        "XP_ERROR_EXPERIENCE_SUSPENDED", "XP_ERROR_UNKNOWN_ERROR", "XP_ERROR_QUOTA_EXCEEDED",
        "XP_ERROR_STORE_DISABLED", "XP_ERROR_STORAGE_EXCEPTION", "XP_ERROR_KEY_NOT_FOUND",
        "XP_ERROR_RETRY_UPDATE", "XP_ERROR_MATURITY_EXCEEDED",
        
        // Prim media constants
        "PRIM_MEDIA_ALT_IMAGE_ENABLE", "PRIM_MEDIA_CONTROLS", "PRIM_MEDIA_CURRENT_URL",
        "PRIM_MEDIA_HOME_URL", "PRIM_MEDIA_AUTO_LOOP", "PRIM_MEDIA_AUTO_PLAY",
        "PRIM_MEDIA_AUTO_SCALE", "PRIM_MEDIA_AUTO_ZOOM", "PRIM_MEDIA_FIRST_CLICK_INTERACT",
        "PRIM_MEDIA_WIDTH_PIXELS", "PRIM_MEDIA_HEIGHT_PIXELS", "PRIM_MEDIA_WHITELIST_ENABLE",
        "PRIM_MEDIA_WHITELIST", "PRIM_MEDIA_PERMS_INTERACT", "PRIM_MEDIA_PERMS_CONTROL",
        "PRIM_MEDIA_CONTROLS_STANDARD", "PRIM_MEDIA_CONTROLS_MINI"
    )
    
    // ==================== FUNCTIONS ====================
    
    val FUNCTIONS = setOf(
        // Math functions
        "llAbs", "llAcos", "llAsin", "llAtan2", "llCeil", "llCos", "llFabs", "llFloor",
        "llFrand", "llLog", "llLog10", "llModPow", "llPow", "llRound", "llSin", "llSqrt",
        "llTan", "llVecDist", "llVecMag", "llVecNorm",
        
        // String functions
        "llDeleteSubString", "llGetSubString", "llInsertString", "llStringLength",
        "llStringToBase64", "llBase64ToString", "llBase64ToInteger", "llIntegerToBase64",
        "llMD5String", "llSHA1String", "llSHA256String", "llToLower", "llToUpper",
        "llSubStringIndex", "llChar", "llOrd", "llHash", "llEscapeURL", "llUnescapeURL",
        
        // List functions
        "llCSV2List", "llDeleteSubList", "llDumpList2String", "llGetListEntryType",
        "llGetListLength", "llList2CSV", "llList2Float", "llList2Integer", "llList2Key",
        "llList2List", "llList2ListStrided", "llList2Rot", "llList2String", "llList2Vector",
        "llListFindList", "llListInsertList", "llListRandomize", "llListReplaceList",
        "llListSort", "llListStatistics", "llParseString2List", "llParseStringKeepNulls",
        
        // Vector/Rotation functions
        "llAngleBetween", "llAxes2Rot", "llAxisAngle2Rot", "llEuler2Rot", "llRot2Angle",
        "llRot2Axis", "llRot2Euler", "llRot2Fwd", "llRot2Left", "llRot2Up", "llRotBetween",
        
        // Agent functions
        "llGetAgentInfo", "llGetAgentLanguage", "llGetAgentList", "llGetAgentSize",
        "llGetAnimationList", "llGetDisplayName", "llGetUsername", "llKey2Name",
        "llRequestAgentData", "llRequestDisplayName", "llRequestUsername", "llSameGroup",
        
        // Object functions
        "llAttachToAvatar", "llAttachToAvatarTemp", "llDetachFromAvatar", "llDie",
        "llGetAttached", "llGetAttachedList", "llGetBoundingBox", "llGetCenterOfMass",
        "llGetCreator", "llGetGeometricCenter", "llGetKey", "llGetLinkKey",
        "llGetLinkNumber", "llGetLinkNumberOfSides", "llGetLinkPrimitiveParams",
        "llGetMass", "llGetMassMKS", "llGetNumberOfPrims", "llGetNumberOfSides",
        "llGetObjectDesc", "llGetObjectDetails", "llGetObjectMass", "llGetObjectName",
        "llGetObjectPermMask", "llGetObjectPrimCount", "llGetOwner", "llGetOwnerKey",
        "llGetPrimitiveParams", "llGetRootPosition", "llGetRootRotation",
        "llGetScale", "llGetStatus", "llPassCollisions", "llPassTouches",
        "llSetClickAction", "llSetLinkAlpha", "llSetLinkCamera", "llSetLinkColor",
        "llSetLinkMedia", "llSetLinkPrimitiveParams", "llSetLinkPrimitiveParamsFast",
        "llSetLinkTexture", "llSetLinkTextureAnim", "llSetObjectDesc", "llSetObjectName",
        "llSetPayPrice", "llSetPrimitiveParams", "llSetScale", "llSetStatus",
        
        // Movement functions
        "llApplyImpulse", "llApplyRotationalImpulse", "llGetAccel", "llGetForce",
        "llGetOmega", "llGetPos", "llGetLocalPos", "llGetRegionCorner", "llGetRot",
        "llGetLocalRot", "llGetTorque", "llGetVel", "llGroundContour", "llGroundNormal",
        "llGroundRepel", "llGroundSlope", "llMoveToTarget", "llPushObject",
        "llRotLookAt", "llRotTarget", "llRotTargetRemove", "llSetAngularVelocity",
        "llSetBuoyancy", "llSetForce", "llSetForceAndTorque", "llSetHoverHeight",
        "llSetPos", "llSetRegionPos", "llSetRot", "llSetTorque", "llSetVelocity",
        "llStopHover", "llStopLookAt", "llStopMoveToTarget", "llTarget", "llTargetRemove",
        
        // Communication functions
        "llDialog", "llInstantMessage", "llListen", "llListenControl", "llListenRemove",
        "llOwnerSay", "llRegionSay", "llRegionSayTo", "llSay", "llShout", "llWhisper",
        "llTextBox", "llGetNotecardLine", "llGetNumberOfNotecardLines",
        
        // Animation functions
        "llGetAnimation", "llGetAnimationOverride", "llResetAnimationOverride",
        "llSetAnimationOverride", "llStartAnimation", "llStopAnimation",
        
        // Sensor functions
        "llSensor", "llSensorRemove", "llSensorRepeat", "llDetectedGrab", "llDetectedGroup",
        "llDetectedKey", "llDetectedLinkNumber", "llDetectedName", "llDetectedOwner",
        "llDetectedPos", "llDetectedRot", "llDetectedTouchBinormal", "llDetectedTouchFace",
        "llDetectedTouchNormal", "llDetectedTouchPos", "llDetectedTouchST",
        "llDetectedTouchUV", "llDetectedType", "llDetectedVel",
        
        // Inventory functions
        "llAllowInventoryDrop", "llGetInventoryCreator", "llGetInventoryKey",
        "llGetInventoryName", "llGetInventoryNumber", "llGetInventoryPermMask",
        "llGetInventoryType", "llGiveInventory", "llGiveInventoryList",
        "llRemoveInventory", "llRezAtRoot", "llRezObject",
        
        // Sound functions
        "llAdjustSoundVolume", "llGetSoundLength", "llLoopSound", "llLoopSoundMaster",
        "llLoopSoundSlave", "llPlaySound", "llPlaySoundSlave", "llPreloadSound",
        "llSetSoundQueueing", "llSetSoundRadius", "llSound", "llSoundPreload",
        "llStopSound", "llTriggerSound", "llTriggerSoundLimited",
        
        // Texture functions
        "llGetAlpha", "llGetColor", "llGetTexture", "llGetTextureOffset", "llGetTextureRot",
        "llGetTextureScale", "llOffsetTexture", "llRotateTexture", "llScaleTexture",
        "llSetAlpha", "llSetColor", "llSetTexture", "llSetTextureAnim",
        
        // Particle functions
        "llLinkParticleSystem", "llParticleSystem",
        
        // Text functions
        "llSetText",
        
        // Light functions
        "llGetLightColor", "llGetLightDirection", "llGetLightRange", "llSetLightColor",
        "llSetLightDirection", "llSetLightFalloff", "llSetLightIntensity", "llSetLightRadius",
        
        // Timer functions
        "llGetTime", "llGetTimeOfDay", "llGetUnixTime", "llGetTimestamp", "llGetDate",
        "llGetGMTclock", "llResetTime", "llSetTimerEvent", "llSleep",
        
        // Permission functions
        "llGetPermissions", "llGetPermissionsKey", "llRequestPermissions",
        
        // Control functions
        "llReleaseControls", "llTakeControls",
        
        // Camera functions
        "llClearCameraParams", "llSetCameraAtOffset", "llSetCameraEyeOffset",
        "llSetCameraParams",
        
        // Parcel/Land functions
        "llAddToLandBanList", "llAddToLandPassList", "llEjectFromLand",
        "llGetLandOwnerAt", "llGetParcelDetails", "llGetParcelFlags", "llGetParcelMaxPrims",
        "llGetParcelMusicURL", "llGetParcelPrimCount", "llGetParcelPrimOwners",
        "llOverMyLand", "llRemoveFromLandBanList", "llRemoveFromLandPassList",
        "llReturnObjectsByID", "llReturnObjectsByOwner", "llSetParcelMusicURL",
        
        // Region functions
        "llCloud", "llEdgeOfWorld", "llGetEnv", "llGetRegionAgentCount", "llGetRegionFPS",
        "llGetRegionFlags", "llGetRegionName", "llGetRegionTimeDilation",
        "llGetSimStats", "llGetSimulatorHostname", "llGetSunDirection", "llGround",
        "llWater", "llWind",
        
        // HTTP functions
        "llGetFreeURLs", "llHTTPRequest", "llHTTPResponse", "llReleaseURL",
        "llRequestSecureURL", "llRequestURL", "llSetContentType",
        
        // Email functions
        "llEmail", "llGetNextEmail",
        
        // Experience functions
        "llAgentInExperience", "llCreateKeyValue", "llDataSizeKeyValue",
        "llDeleteKeyValue", "llGetExperienceDetails", "llGetExperienceErrorMessage",
        "llGetExperienceList", "llKeyCountKeyValue", "llKeysKeyValue",
        "llReadKeyValue", "llRequestExperiencePermissions", "llSitOnLink",
        "llUpdateKeyValue",
        
        // JSON functions
        "llJsonGetValue", "llJsonSetValue", "llJsonValueType", "llList2Json", "llJson2List",
        
        // Cast functions
        "llCastRay",
        
        // Keyframe motion
        "llGetPhysicsMaterial", "llSetKeyframedMotion", "llSetPhysicsMaterial",
        
        // Script functions
        "llGetFreeMemory", "llGetMemoryLimit", "llGetScriptName", "llGetScriptState",
        "llGetStartParameter", "llGetUsedMemory", "llMinEventDelay", "llResetOtherScript",
        "llResetScript", "llScriptDanger", "llScriptProfiler", "llSetMemoryLimit",
        "llSetScriptState",
        
        // Link message functions
        "llMessageLinked", "llLinkSitTarget", "llSetLinkMedia",
        
        // Sit functions
        "llAvatarOnLinkSitTarget", "llAvatarOnSitTarget", "llForceUnsit", "llSitTarget",
        "llUnSit",
        
        // Teleport functions
        "llMapDestination", "llTeleportAgent", "llTeleportAgentGlobalCoords",
        "llTeleportAgentHome",
        
        // Avatar functions
        "llGetAgentSize", "llGetEnergy", "llRequestAgentData",
        
        // Pathfinding functions
        "llCreateCharacter", "llDeleteCharacter", "llEvade", "llExecCharacterCmd",
        "llFleeFrom", "llGetClosestNavPoint", "llGetStaticPath", "llNavigateTo",
        "llPatrolPoints", "llPursue", "llUpdateCharacter", "llWanderWithin",
        
        // Collision functions
        "llCollisionFilter", "llCollisionSound", "llCollisionSprite",
        "llPassCollisions", "llVolumeDetect",
        
        // Link functions
        "llBreakAllLinks", "llBreakLink", "llCreateLink", "llGetLinkName",
        "llSetLinkPrimitiveParams", "llSetLinkPrimitiveParamsFast",
        
        // Misc functions
        "llAllowInventoryDrop", "llDumpList2String", "llGetEnergy", "llGetFreeMemory",
        "llGetMemoryLimit", "llGetObjectPrimCount", "llGetRegionAgentCount",
        "llGetSPMaxMemory", "llGetStaticPath", "llListenControl", "llLoadURL",
        "llManageEstateAccess", "llModifyLand", "llOverMyLand", "llRequestAgentData",
        "llRequestExperiencePermissions", "llRequestInventoryData", "llRequestSimulatorData",
        "llSetAnimationOverride", "llSetContentType", "llSetDamage",
        "llSetHoverHeight", "llSetLinkMedia", "llSetLinkTexture", "llSetLinkTextureAnim",
        "llSetMemoryLimit", "llSetRemoteScriptAccessPin", "llSetSitText",
        "llSetTouchText", "llTransferLindenDollars", "llTriggerSoundLimited"
    )
    
    // ==================== DEPRECATED FUNCTIONS ====================
    
    val DEPRECATED_FUNCTIONS = setOf(
        "llMakeExplosion", "llMakeFire", "llMakeFountain", "llMakeSmoke",
        "llSound", "llSoundPreload", "llRemoteLoadScript", "llRemoteLoadScriptPin",
        "llXorBase64StringsCorrect", "llSetInventoryPermMask", "llPointAt",
        "llStopPointAt", "llRefreshPrimURL", "llSetPrimURL", "llGetPrimURL"
    )
    
    // ==================== PATTERNS ====================
    
    val PATTERNS = object {
        val STRING = Pattern.compile("\"([^\"\\\\]|\\\\.)*\"")
        val SINGLE_LINE_COMMENT = Pattern.compile("//.*")
        val MULTI_LINE_COMMENT = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL)
        val NUMBER = Pattern.compile("\\b(0x[0-9A-Fa-f]+|\\d+\\.?\\d*([eE][+-]?\\d+)?|\\d*\\.\\d+([eE][+-]?\\d+)?)\\b")
        val IDENTIFIER = Pattern.compile("\\b[a-zA-Z_][a-zA-Z0-9_]*\\b")
        val OPERATOR = Pattern.compile("[+\\-*/%=<>!&|^~?:;,{}()\\[\\]]")
        val VECTOR_ROTATION = Pattern.compile("<[^>]+>")
    }
    
    /**
     * Check if a word is an LSL keyword.
     */
    fun isKeyword(word: String): Boolean = word in KEYWORDS
    
    /**
     * Check if a word is an LSL type.
     */
    fun isType(word: String): Boolean = word in TYPES
    
    /**
     * Check if a word is an LSL event.
     */
    fun isEvent(word: String): Boolean = word in EVENTS
    
    /**
     * Check if a word is an LSL constant.
     */
    fun isConstant(word: String): Boolean = word in CONSTANTS
    
    /**
     * Check if a word is an LSL function.
     */
    fun isFunction(word: String): Boolean = word in FUNCTIONS
    
    /**
     * Check if a function is deprecated.
     */
    fun isDeprecated(word: String): Boolean = word in DEPRECATED_FUNCTIONS
    
    /**
     * Get all LSL function names for autocomplete.
     */
    fun getAllFunctions(): List<String> = FUNCTIONS.sorted()
    
    /**
     * Get all LSL constants for autocomplete.
     */
    fun getAllConstants(): List<String> = CONSTANTS.sorted()
    
    /**
     * Get all LSL events for autocomplete.
     */
    fun getAllEvents(): List<String> = EVENTS.sorted()
}

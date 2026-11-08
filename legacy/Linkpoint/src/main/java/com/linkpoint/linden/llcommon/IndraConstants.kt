package com.linkpoint.linden.llcommon

object IndraConstants {

    const val APP_NAME = "Firestorm"

    const val REGION_WIDTH_METERS = 256f
    const val REGION_WIDTH_UNITS = 256
    const val REGION_WIDTH_U32 = 256
    const val REGION_HEIGHT_METERS = 4096f

    const val DEFAULT_AGENT_DEPTH = 0.45f
    const val DEFAULT_AGENT_WIDTH = 0.60f
    const val DEFAULT_AGENT_HEIGHT = 1.9f

    const val DEFAULT_WATER_HEIGHT = 20.0f

    const val CHAT_NORMAL_RADIUS = 20f
    val CHAT_CHANNEL_DEBUG = Int.MAX_VALUE

    const val KEY_COUNT = 256

    const val MASK_NONE: Int         = 0x0000
    const val MASK_CONTROL: Int      = 0x0001
    const val MASK_ALT: Int          = 0x0002
    const val MASK_SHIFT: Int        = 0x0004
    const val MASK_NORMALKEYS: Int   = 0x0007
    const val MASK_MAC_CONTROL: Int  = 0x0008
    const val MASK_MODIFIERS: Int    = MASK_CONTROL or MASK_ALT or MASK_SHIFT or MASK_MAC_CONTROL

    const val KEY_SPECIAL: Int      = 0x80
    const val KEY_RETURN: Int       = 0x81
    const val KEY_LEFT: Int         = 0x82
    const val KEY_RIGHT: Int        = 0x83
    const val KEY_UP: Int           = 0x84
    const val KEY_DOWN: Int         = 0x85
    const val KEY_ESCAPE: Int       = 0x86
    const val KEY_BACKSPACE: Int    = 0x87
    const val KEY_DELETE: Int       = 0x88
    const val KEY_SHIFT: Int        = 0x89
    const val KEY_CONTROL: Int      = 0x8A
    const val KEY_ALT: Int          = 0x8B
    const val KEY_HOME: Int         = 0x8C
    const val KEY_END: Int          = 0x8D
    const val KEY_PAGE_UP: Int      = 0x8E
    const val KEY_PAGE_DOWN: Int    = 0x8F
    const val KEY_HYPHEN: Int       = 0x90
    const val KEY_EQUALS: Int       = 0x91
    const val KEY_INSERT: Int       = 0x92
    const val KEY_CAPSLOCK: Int     = 0x93
    const val KEY_TAB: Int          = 0x94
    const val KEY_ADD: Int          = 0x95
    const val KEY_SUBTRACT: Int     = 0x96
    const val KEY_MULTIPLY: Int     = 0x97
    const val KEY_DIVIDE: Int       = 0x98
    const val KEY_CONTEXT_MENU: Int = 0x99
    const val KEY_F1: Int           = 0xA1
    const val KEY_F2: Int           = 0xA2
    const val KEY_F3: Int           = 0xA3
    const val KEY_F4: Int           = 0xA4
    const val KEY_F5: Int           = 0xA5
    const val KEY_F6: Int           = 0xA6
    const val KEY_F7: Int           = 0xA7
    const val KEY_F8: Int           = 0xA8
    const val KEY_F9: Int           = 0xA9
    const val KEY_F10: Int          = 0xAA
    const val KEY_F11: Int          = 0xAB
    const val KEY_F12: Int          = 0xAC

    const val KEY_PAD_UP: Int       = 0xC0
    const val KEY_PAD_DOWN: Int     = 0xC1
    const val KEY_PAD_LEFT: Int     = 0xC2
    const val KEY_PAD_RIGHT: Int    = 0xC3
    const val KEY_PAD_HOME: Int     = 0xC4
    const val KEY_PAD_END: Int      = 0xC5
    const val KEY_PAD_PGUP: Int     = 0xC6
    const val KEY_PAD_PGDN: Int     = 0xC7
    const val KEY_PAD_CENTER: Int   = 0xC8
    const val KEY_PAD_INS: Int      = 0xC9
    const val KEY_PAD_DEL: Int      = 0xCA
    const val KEY_PAD_RETURN: Int   = 0xCB
    const val KEY_PAD_ADD: Int      = 0xCC
    const val KEY_PAD_SUBTRACT: Int = 0xCD
    const val KEY_PAD_MULTIPLY: Int = 0xCE
    const val KEY_PAD_DIVIDE: Int   = 0xCF

    const val KEY_BUTTON0: Int  = 0xD0
    const val KEY_BUTTON1: Int  = 0xD1
    const val KEY_BUTTON2: Int  = 0xD2
    const val KEY_BUTTON3: Int  = 0xD3
    const val KEY_BUTTON4: Int  = 0xD4
    const val KEY_BUTTON5: Int  = 0xD5
    const val KEY_BUTTON6: Int  = 0xD6
    const val KEY_BUTTON7: Int  = 0xD7
    const val KEY_BUTTON8: Int  = 0xD8
    const val KEY_BUTTON9: Int  = 0xD9
    const val KEY_BUTTON10: Int = 0xDA
    const val KEY_BUTTON11: Int = 0xDB
    const val KEY_BUTTON12: Int = 0xDC
    const val KEY_BUTTON13: Int = 0xDD
    const val KEY_BUTTON14: Int = 0xDE
    const val KEY_BUTTON15: Int = 0xDF

    const val KEY_NONE: Int = 0xFF

    const val SIM_ACCESS_MIN: Int   = 0
    const val SIM_ACCESS_PG: Int    = 13
    const val SIM_ACCESS_MATURE: Int = 21
    const val SIM_ACCESS_ADULT: Int = 42
    const val SIM_ACCESS_DOWN: Int  = 254
    const val SIM_ACCESS_MAX: Int   = SIM_ACCESS_ADULT

    const val ATTACHMENT_ADD: Int = 0x80

    const val GOD_MAINTENANCE: Int      = 250
    const val GOD_FULL: Int             = 200
    const val GOD_LIAISON: Int          = 150
    const val GOD_CUSTOMER_SERVICE: Int = 100
    const val GOD_LIKE: Int             = 1
    const val GOD_NOT: Int              = 0

    const val CLICK_ACTION_NONE: Int         = 0
    const val CLICK_ACTION_TOUCH: Int        = 0
    const val CLICK_ACTION_SIT: Int          = 1
    const val CLICK_ACTION_BUY: Int          = 2
    const val CLICK_ACTION_PAY: Int          = 3
    const val CLICK_ACTION_OPEN: Int         = 4
    const val CLICK_ACTION_PLAY: Int         = 5
    const val CLICK_ACTION_OPEN_MEDIA: Int   = 6
    const val CLICK_ACTION_ZOOM: Int         = 7
    const val CLICK_ACTION_DISABLED: Int     = 8
    const val CLICK_ACTION_IGNORE: Int       = 9

    const val PARCEL_MEDIA_COMMAND_STOP: Int       = 0
    const val PARCEL_MEDIA_COMMAND_PAUSE: Int      = 1
    const val PARCEL_MEDIA_COMMAND_PLAY: Int       = 2
    const val PARCEL_MEDIA_COMMAND_LOOP: Int       = 3
    const val PARCEL_MEDIA_COMMAND_TEXTURE: Int    = 4
    const val PARCEL_MEDIA_COMMAND_URL: Int        = 5
    const val PARCEL_MEDIA_COMMAND_TIME: Int       = 6
    const val PARCEL_MEDIA_COMMAND_AGENT: Int      = 7
    const val PARCEL_MEDIA_COMMAND_UNLOAD: Int     = 8
    const val PARCEL_MEDIA_COMMAND_AUTO_ALIGN: Int = 9
    const val PARCEL_MEDIA_COMMAND_TYPE: Int       = 10
    const val PARCEL_MEDIA_COMMAND_SIZE: Int       = 11
    const val PARCEL_MEDIA_COMMAND_DESC: Int       = 12
    const val PARCEL_MEDIA_COMMAND_LOOP_SET: Int   = 13

    const val BEACON_SHOW_MAP: Int  = 0x0001
    const val BEACON_FOCUS_MAP: Int = 0x0002

    const val CONTROL_AT_POS_INDEX: Int          = 0
    const val CONTROL_AT_NEG_INDEX: Int          = 1
    const val CONTROL_LEFT_POS_INDEX: Int        = 2
    const val CONTROL_LEFT_NEG_INDEX: Int        = 3
    const val CONTROL_UP_POS_INDEX: Int          = 4
    const val CONTROL_UP_NEG_INDEX: Int          = 5
    const val CONTROL_PITCH_POS_INDEX: Int       = 6
    const val CONTROL_PITCH_NEG_INDEX: Int       = 7
    const val CONTROL_YAW_POS_INDEX: Int         = 8
    const val CONTROL_YAW_NEG_INDEX: Int         = 9
    const val CONTROL_FAST_AT_INDEX: Int         = 10
    const val CONTROL_FAST_LEFT_INDEX: Int       = 11
    const val CONTROL_FAST_UP_INDEX: Int         = 12
    const val CONTROL_FLY_INDEX: Int             = 13
    const val CONTROL_STOP_INDEX: Int            = 14
    const val CONTROL_FINISH_ANIM_INDEX: Int     = 15
    const val CONTROL_STAND_UP_INDEX: Int        = 16
    const val CONTROL_SIT_ON_GROUND_INDEX: Int   = 17
    const val CONTROL_MOUSELOOK_INDEX: Int       = 18
    const val CONTROL_NUDGE_AT_POS_INDEX: Int    = 19
    const val CONTROL_NUDGE_AT_NEG_INDEX: Int    = 20
    const val CONTROL_NUDGE_LEFT_POS_INDEX: Int  = 21
    const val CONTROL_NUDGE_LEFT_NEG_INDEX: Int  = 22
    const val CONTROL_NUDGE_UP_POS_INDEX: Int    = 23
    const val CONTROL_NUDGE_UP_NEG_INDEX: Int    = 24
    const val CONTROL_TURN_LEFT_INDEX: Int       = 25
    const val CONTROL_TURN_RIGHT_INDEX: Int      = 26
    const val CONTROL_AWAY_INDEX: Int            = 27
    const val CONTROL_LBUTTON_DOWN_INDEX: Int    = 28
    const val CONTROL_LBUTTON_UP_INDEX: Int      = 29
    const val CONTROL_ML_LBUTTON_DOWN_INDEX: Int = 30
    const val CONTROL_ML_LBUTTON_UP_INDEX: Int   = 31
    const val TOTAL_CONTROLS: Int                = 32

    val AGENT_CONTROL_AT_POS: Int          = 0x1 shl CONTROL_AT_POS_INDEX
    val AGENT_CONTROL_AT_NEG: Int          = 0x1 shl CONTROL_AT_NEG_INDEX
    val AGENT_CONTROL_LEFT_POS: Int        = 0x1 shl CONTROL_LEFT_POS_INDEX
    val AGENT_CONTROL_LEFT_NEG: Int        = 0x1 shl CONTROL_LEFT_NEG_INDEX
    val AGENT_CONTROL_UP_POS: Int          = 0x1 shl CONTROL_UP_POS_INDEX
    val AGENT_CONTROL_UP_NEG: Int          = 0x1 shl CONTROL_UP_NEG_INDEX
    val AGENT_CONTROL_PITCH_POS: Int       = 0x1 shl CONTROL_PITCH_POS_INDEX
    val AGENT_CONTROL_PITCH_NEG: Int       = 0x1 shl CONTROL_PITCH_NEG_INDEX
    val AGENT_CONTROL_YAW_POS: Int         = 0x1 shl CONTROL_YAW_POS_INDEX
    val AGENT_CONTROL_YAW_NEG: Int         = 0x1 shl CONTROL_YAW_NEG_INDEX
    val AGENT_CONTROL_FAST_AT: Int         = 0x1 shl CONTROL_FAST_AT_INDEX
    val AGENT_CONTROL_FAST_LEFT: Int       = 0x1 shl CONTROL_FAST_LEFT_INDEX
    val AGENT_CONTROL_FAST_UP: Int         = 0x1 shl CONTROL_FAST_UP_INDEX
    val AGENT_CONTROL_FLY: Int             = 0x1 shl CONTROL_FLY_INDEX
    val AGENT_CONTROL_STOP: Int            = 0x1 shl CONTROL_STOP_INDEX
    val AGENT_CONTROL_FINISH_ANIM: Int     = 0x1 shl CONTROL_FINISH_ANIM_INDEX
    val AGENT_CONTROL_STAND_UP: Int        = 0x1 shl CONTROL_STAND_UP_INDEX
    val AGENT_CONTROL_SIT_ON_GROUND: Int   = 0x1 shl CONTROL_SIT_ON_GROUND_INDEX
    val AGENT_CONTROL_MOUSELOOK: Int       = 0x1 shl CONTROL_MOUSELOOK_INDEX
    val AGENT_CONTROL_NUDGE_AT_POS: Int    = 0x1 shl CONTROL_NUDGE_AT_POS_INDEX
    val AGENT_CONTROL_NUDGE_AT_NEG: Int    = 0x1 shl CONTROL_NUDGE_AT_NEG_INDEX
    val AGENT_CONTROL_NUDGE_LEFT_POS: Int  = 0x1 shl CONTROL_NUDGE_LEFT_POS_INDEX
    val AGENT_CONTROL_NUDGE_LEFT_NEG: Int  = 0x1 shl CONTROL_NUDGE_LEFT_NEG_INDEX
    val AGENT_CONTROL_NUDGE_UP_POS: Int    = 0x1 shl CONTROL_NUDGE_UP_POS_INDEX
    val AGENT_CONTROL_NUDGE_UP_NEG: Int    = 0x1 shl CONTROL_NUDGE_UP_NEG_INDEX
    val AGENT_CONTROL_TURN_LEFT: Int       = 0x1 shl CONTROL_TURN_LEFT_INDEX
    val AGENT_CONTROL_TURN_RIGHT: Int      = 0x1 shl CONTROL_TURN_RIGHT_INDEX
    val AGENT_CONTROL_AWAY: Int            = 0x1 shl CONTROL_AWAY_INDEX
    val AGENT_CONTROL_LBUTTON_DOWN: Int    = 0x1 shl CONTROL_LBUTTON_DOWN_INDEX
    val AGENT_CONTROL_LBUTTON_UP: Int      = 0x1 shl CONTROL_LBUTTON_UP_INDEX
    val AGENT_CONTROL_ML_LBUTTON_DOWN: Int = 0x1 shl CONTROL_ML_LBUTTON_DOWN_INDEX
    val AGENT_CONTROL_ML_LBUTTON_UP: Long  = 0x1L shl CONTROL_ML_LBUTTON_UP_INDEX

    const val AGENT_ATTACH_OFFSET: Int = 4
    val AGENT_ATTACH_MASK: Int = 0xf shl AGENT_ATTACH_OFFSET

    val LL_UUID_ALL_AGENTS: LLUUID  = LLUUID.fromString("44e87126-e794-4ded-05b3-7c42da3d5cdb")!!
    val ALEXANDRIA_LINDEN_ID: LLUUID = LLUUID.fromString("ba2a564a-f0f1-4b82-9c61-b7520bfcd09f")!!
    val GOVERNOR_LINDEN_ID: LLUUID  = LLUUID.fromString("3d6181b0-6a4b-97ef-18d8-722652995cf1")!!
    val MAINTENANCE_GROUP_ID: LLUUID = LLUUID.fromString("dc7b21cd-3c89-fcaa-31c8-25f9ffd224cd")!!

    val IMG_SMOKE: LLUUID         = LLUUID.fromString("b4ba225c-373f-446d-9f7e-6cb7b5cf9b3d")!!
    val IMG_DEFAULT: LLUUID       = LLUUID.fromString("d2114404-dd59-4a4d-8e6c-49359e91bbf0")!!
    val IMG_SUN: LLUUID           = LLUUID.fromString("cce0f112-878f-4586-a2e2-a8f104bba271")!!
    val IMG_MOON: LLUUID          = LLUUID.fromString("d07f6eed-b96a-47cd-b51d-400ad4a1c428")!!
    val IMG_SHOT: LLUUID          = LLUUID.fromString("35f217a3-f618-49cf-bbca-c86d486551a9")!!
    val IMG_SPARK: LLUUID         = LLUUID.fromString("d2e75ac1-d0fb-4532-820e-a20034ac814d")!!
    val IMG_FIRE: LLUUID          = LLUUID.fromString("aca40aa8-44cf-44ca-a0fa-93e1a2986f82")!!
    val IMG_FACE_SELECT: LLUUID   = LLUUID.fromString("a85ac674-cb75-4af6-9499-df7c5aaf7a28")!!
    val IMG_DEFAULT_AVATAR: LLUUID = LLUUID.fromString("c228d1cf-4b5d-4ba8-84f4-899a0796aa97")!!
    val IMG_INVISIBLE: LLUUID     = LLUUID.fromString("3a367d1c-bef1-6d43-7595-e88c1e3aadb3")!!
    val IMG_WHITE: LLUUID         = LLUUID.fromString("5748decc-f629-461c-9a36-a35a221fe21f")!!

    val IMG_EXPLOSION: LLUUID   = LLUUID.fromString("68edcf47-ccd7-45b8-9f90-1649d7f12806")!!
    val IMG_EXPLOSION_2: LLUUID = LLUUID.fromString("21ce046c-83fe-430a-b629-c7660ac78d7c")!!
    val IMG_EXPLOSION_3: LLUUID = LLUUID.fromString("fedea30a-1be8-47a6-bc06-337a04a39c4b")!!
    val IMG_EXPLOSION_4: LLUUID = LLUUID.fromString("abf0d56b-82e5-47a2-a8ad-74741bb2c29e")!!
    val IMG_SMOKE_POOF: LLUUID  = LLUUID.fromString("1e63e323-5fe0-452e-92f8-b98bd0f764e3")!!

    val IMG_BIG_EXPLOSION_1: LLUUID = LLUUID.fromString("5e47a0dc-97bf-44e0-8b40-de06718cee9d")!!
    val IMG_BIG_EXPLOSION_2: LLUUID = LLUUID.fromString("9c8eca51-53d5-42a7-bb58-cef070395db8")!!

    val IMG_ALPHA_GRAD: LLUUID    = LLUUID.fromString("e97cf410-8e61-7005-ec06-629eba4cd1fb")!!
    val IMG_ALPHA_GRAD_2D: LLUUID = LLUUID.fromString("38b86f85-2575-52a9-a531-23108d8da837")!!
    val IMG_TRANSPARENT: LLUUID   = LLUUID.fromString("8dcd4a48-2d37-4909-9f78-f7a9eb4ef903")!!

    val TERRAIN_DIRT_DETAIL: LLUUID     = LLUUID.fromString("0bc58228-74a0-7e83-89bc-5c23464bcec5")!!
    val TERRAIN_GRASS_DETAIL: LLUUID    = LLUUID.fromString("63338ede-0037-c4fd-855b-015d77112fc8")!!
    val TERRAIN_MOUNTAIN_DETAIL: LLUUID = LLUUID.fromString("303cd381-8560-7579-23f1-f0a880799740")!!
    val TERRAIN_ROCK_DETAIL: LLUUID     = LLUUID.fromString("53a2f406-4895-1d13-d541-d2e3b86bc19c")!!

    val IMG_USE_BAKED_HEAD: LLUUID    = LLUUID.fromString("5a9f4a74-30f2-821c-b88d-70499d3e7183")!!
    val IMG_USE_BAKED_UPPER: LLUUID   = LLUUID.fromString("ae2de45c-d252-50b8-5c6e-19f39ce79317")!!
    val IMG_USE_BAKED_LOWER: LLUUID   = LLUUID.fromString("24daea5f-0539-cfcf-047f-fbc40b2786ba")!!
    val IMG_USE_BAKED_EYES: LLUUID    = LLUUID.fromString("52cc6bb6-2ee5-e632-d3ad-50197b1dcb8a")!!
    val IMG_USE_BAKED_SKIRT: LLUUID   = LLUUID.fromString("43529ce8-7faa-ad92-165a-bc4078371687")!!
    val IMG_USE_BAKED_HAIR: LLUUID    = LLUUID.fromString("09aac1fb-6bce-0bee-7d44-caac6dbb6c63")!!
    val IMG_USE_BAKED_LEFTARM: LLUUID = LLUUID.fromString("ff62763f-d60a-9855-890b-0c96f8f8cd98")!!
    val IMG_USE_BAKED_LEFTLEG: LLUUID = LLUUID.fromString("8e915e25-31d1-cc95-ae08-d58a47488251")!!
    val IMG_USE_BAKED_AUX1: LLUUID    = LLUUID.fromString("9742065b-19b5-297c-858a-29711d539043")!!
    val IMG_USE_BAKED_AUX2: LLUUID    = LLUUID.fromString("03642e83-2bd1-4eb9-34b4-4c47ed586d2d")!!
    val IMG_USE_BAKED_AUX3: LLUUID    = LLUUID.fromString("edd51b77-fc10-ce7a-4b3d-011dfc349e4f")!!

    val DEFAULT_WATER_NORMAL: LLUUID    = LLUUID.fromString("822ded49-9a6c-f61c-cb89-6df54f42cdf4")!!
    val DEFAULT_OBJECT_TEXTURE: LLUUID  = LLUUID.fromString("89556747-24cb-43ed-920b-47caed15465f")!!
    val DEFAULT_OBJECT_SPECULAR: LLUUID = LLUUID.fromString("87e0e8f7-8729-1ea8-cfc9-8915773009db")!!
    val DEFAULT_OBJECT_NORMAL: LLUUID   = LLUUID.fromString("85f28839-7a1c-b4e3-d71d-967792970a7b")!!
    val BLANK_OBJECT_NORMAL: LLUUID     = LLUUID.fromString("5b53359e-59dd-d8a2-04c3-9e65134da47a")!!
    val BLANK_MATERIAL_ASSET_ID: LLUUID = LLUUID.fromString("968cbad0-4dad-d64e-71b5-72bf13ad051a")!!
}

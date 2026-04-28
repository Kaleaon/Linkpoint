package com.linkpoint.linden.llinventory

enum class FolderType(val value: Int, val typeName: String) {
    TEXTURE(0, "texture"),
    SOUND(1, "sound"),
    CALLINGCARD(2, "callcard"),
    LANDMARK(3, "landmark"),
    CLOTHING(5, "clothing"),
    OBJECT(6, "object"),
    NOTECARD(7, "notecard"),
    ROOT_INVENTORY(8, "root_inv"),
    LSL_TEXT(10, "lsltext"),
    BODYPART(13, "bodypart"),
    TRASH(14, "trash"),
    SNAPSHOT_CATEGORY(15, "snapshot"),
    LOST_AND_FOUND(16, "lstndfnd"),
    ANIMATION(20, "animatn"),
    GESTURE(21, "gesture"),
    FAVORITE(23, "favorite"),
    ENSEMBLE_START(26, "ensemble_start"),
    ENSEMBLE_END(45, "ensemble_end"),
    CURRENT_OUTFIT(46, "current"),
    OUTFIT(47, "outfit"),
    MY_OUTFITS(48, "my_otfts"),
    MESH(49, "mesh"),
    INBOX(50, "inbox"),
    OUTBOX(51, "outbox"),
    BASIC_ROOT(52, "basic_rt"),
    MARKETPLACE_LISTINGS(53, "merchant"),
    MARKETPLACE_STOCK(54, "stock"),
    MARKETPLACE_VERSION(55, "version"),
    SETTINGS(56, "settings"),
    MATERIAL(57, "material"),
    FIRESTORM(58, "firestorm"),
    PHOENIX(59, "phoenix"),
    RLV(60, "rlv"),
    MY_SUITCASE(100, "my_suitcase"),
    NONE(-1, "none");

    companion object {
        private val byValue: Map<Int, FolderType> = entries.associateBy { it.value }
        private val byName: Map<String, FolderType> = entries.associateBy { it.typeName }

        fun fromValue(value: Int): FolderType = byValue[value] ?: NONE
        fun fromName(name: String): FolderType = byName[name] ?: NONE

        val isProtectedTypes: Set<FolderType> = setOf(
            ROOT_INVENTORY, BASIC_ROOT, TRASH, LOST_AND_FOUND,
            MY_OUTFITS, CURRENT_OUTFIT, FAVORITE, INBOX, OUTBOX,
            MARKETPLACE_LISTINGS, MY_SUITCASE
        )
        val isEnsembleRange: IntRange = ENSEMBLE_START.value..ENSEMBLE_END.value

        fun FolderType.isProtected(): Boolean = this in isProtectedTypes
        fun FolderType.isEnsemble(): Boolean = value in isEnsembleRange
        fun FolderType.isSingleton(): Boolean = isProtected()
        fun FolderType.isAutomatic(): Boolean = isProtected()
    }
}

enum class InventoryType(val value: Int, val typeName: String, val humanName: String) {
    TEXTURE(0, "texture", "Photo"),
    SOUND(1, "sound", "Sound"),
    CALLINGCARD(2, "callcard", "Calling Card"),
    LANDMARK(3, "landmark", "Landmark"),
    OBJECT(6, "object", "Object"),
    NOTECARD(7, "notecard", "Note Card"),
    CATEGORY(8, "category", "Folder"),
    ROOT_CATEGORY(9, "root", "Root"),
    LSL(10, "script", "Script"),
    SNAPSHOT(15, "snapshot", "Snapshot"),
    ATTACHMENT(17, "attach", "Attachment"),
    WEARABLE(18, "wearable", "Wearable"),
    ANIMATION(19, "animation", "Animation"),
    GESTURE(20, "gesture", "Gesture"),
    MESH(22, "mesh", "Mesh"),
    WIDGET(23, "widget", "Widget"),
    PERSON(24, "person", "Person"),
    SETTINGS(25, "settings", "Settings"),
    MATERIAL(26, "material", "Material"),
    GLTF(27, "gltf", "GLTF"),
    GLTF_BIN(28, "gltf_bin", "GLTF Binary"),
    UNKNOWN(255, "unknown", "Unknown"),
    NONE(-1, "none", "None");

    companion object {
        private val byValue: Map<Int, InventoryType> = entries.associateBy { it.value }
        private val byName: Map<String, InventoryType> = entries.associateBy { it.typeName }

        fun fromValue(value: Int): InventoryType = byValue[value] ?: UNKNOWN
        fun fromName(name: String): InventoryType = byName[name] ?: NONE

        val noRestrictPermissions: Set<InventoryType> = setOf(CALLINGCARD, LANDMARK)

        fun InventoryType.cannotRestrictPermissions(): Boolean = this in noRestrictPermissions
        fun InventoryType.showInWorldPermissions(): Boolean =
            this == OBJECT || this == ATTACHMENT
    }
}

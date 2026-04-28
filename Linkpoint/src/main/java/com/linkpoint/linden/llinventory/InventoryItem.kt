package com.linkpoint.linden.llinventory

import com.linkpoint.linden.llcommon.LLSD
import com.linkpoint.linden.llcommon.LLUUID

// Typealias for the asset type until the AssetType class is available from com.linkpoint.linden.llcommon
typealias AssetTypeId = Int

open class InventoryObject(
    var uuid: LLUUID = LLUUID.NULL,
    var parentId: LLUUID = LLUUID.NULL,
    var thumbnailId: LLUUID = LLUUID.NULL,
    var type: AssetTypeId = -1,
    var name: String = "",
    var creationDate: Long = 0L,
    var isFavorite: Boolean = false
) {
    open val linkedUUID: LLUUID get() = uuid

    fun correctName() {
        name = name.filterNot { it == '|' || it.code < 0x20 }.trim()
    }

    companion object {
        fun correctInventoryName(name: String): String =
            name.filterNot { it == '|' || it.code < 0x20 }.trim()
    }
}

class InventoryItem(
    uuid: LLUUID = LLUUID.NULL,
    parentId: LLUUID = LLUUID.NULL,
    thumbnailId: LLUUID = LLUUID.NULL,
    type: AssetTypeId = -1,
    name: String = "",
    creationDate: Long = 0L,
    isFavorite: Boolean = false,
    var permissions: Permissions = Permissions.DEFAULT,
    var assetId: LLUUID = LLUUID.NULL,
    var inventoryType: Int = InventoryType.NONE.value,
    var flags: UInt = 0u,
    var saleInfo: SaleInfo = SaleInfo.DEFAULT,
    var description: String = ""
) : InventoryObject(uuid, parentId, thumbnailId, type, name, creationDate, isFavorite) {

    override val linkedUUID: LLUUID
        get() = if (type == ASSET_TYPE_LINK) assetId else uuid

    val creatorId: LLUUID get() = permissions.creatorId

    fun accumulatePermissionSlamBits(oldItem: InventoryItem) {
        var slamFlags = flags
        if (permissions.ownerMask != oldItem.permissions.ownerMask ||
            permissions.groupMask != oldItem.permissions.groupMask ||
            permissions.everyoneMask != oldItem.permissions.everyoneMask ||
            permissions.nextOwnerMask != oldItem.permissions.nextOwnerMask) {
            slamFlags = slamFlags or InventoryDefines.II_FLAGS_OBJECT_SLAM_PERM
        }
        if (saleInfo != oldItem.saleInfo) {
            slamFlags = slamFlags or InventoryDefines.II_FLAGS_OBJECT_SLAM_SALE
        }
        flags = slamFlags
    }

    fun toSD(): LLSD = LLSD.map(
        "item_id"           to LLSD.uuid(uuid),
        "parent_id"         to LLSD.uuid(parentId),
        "thumbnail_id"      to LLSD.uuid(thumbnailId),
        "permissions"       to permissions.toSD(),
        "asset_id"          to LLSD.uuid(assetId),
        "type"              to LLSD.integer(type),
        "inv_type"          to LLSD.integer(inventoryType),
        "flags"             to LLSD.integer(flags.toInt()),
        "sale_info"         to saleInfo.toSD(),
        "name"              to LLSD.string(name),
        "desc"              to LLSD.string(description),
        "creation_date"     to LLSD.integer(creationDate.toInt())
    )

    companion object {
        private const val ASSET_TYPE_LINK = 24  // AT_LINK from llassettype

        fun fromSD(sd: LLSD, isNew: Boolean = true): InventoryItem {
            val perms = if (sd.has("permissions"))
                Permissions.fromSD(sd["permissions"])
            else
                Permissions.DEFAULT

            val item = InventoryItem(
                uuid          = sd["item_id"].asUUID(),
                parentId      = sd["parent_id"].asUUID(),
                thumbnailId   = if (sd.has("thumbnail_id")) sd["thumbnail_id"].asUUID() else LLUUID.NULL,
                type          = sd["type"].asInteger(),
                inventoryType = sd["inv_type"].asInteger(),
                name          = sd["name"].asString(),
                description   = sd["desc"].asString(),
                creationDate  = sd["creation_date"].asInteger().toLong(),
                flags         = sd["flags"].asInteger().toUInt(),
                permissions   = perms,
                saleInfo      = if (sd.has("sale_info")) SaleInfo.fromSD(sd["sale_info"]) else SaleInfo.DEFAULT
            )
            item.assetId = sd["asset_id"].asUUID()
            return item
        }
    }
}

class InventoryCategory(
    uuid: LLUUID = LLUUID.NULL,
    parentId: LLUUID = LLUUID.NULL,
    thumbnailId: LLUUID = LLUUID.NULL,
    type: AssetTypeId = -1,
    name: String = "",
    creationDate: Long = 0L,
    isFavorite: Boolean = false,
    var preferredType: Int = FolderType.NONE.value
) : InventoryObject(uuid, parentId, thumbnailId, type, name, creationDate, isFavorite) {

    fun toSD(): LLSD = LLSD.map(
        "folder_id"      to LLSD.uuid(uuid),
        "parent_id"      to LLSD.uuid(parentId),
        "thumbnail_id"   to LLSD.uuid(thumbnailId),
        "name"           to LLSD.string(name),
        "type"           to LLSD.integer(type),
        "preferred_type" to LLSD.integer(preferredType)
    )

    fun toAISCreateSD(): LLSD = LLSD.map(
        "name"           to LLSD.string(name),
        "type"           to LLSD.integer(type),
        "preferred_type" to LLSD.integer(preferredType)
    )

    companion object {
        fun fromSD(sd: LLSD): InventoryCategory = InventoryCategory(
            uuid          = sd["folder_id"].asUUID(),
            parentId      = sd["parent_id"].asUUID(),
            thumbnailId   = if (sd.has("thumbnail_id")) sd["thumbnail_id"].asUUID() else LLUUID.NULL,
            name          = sd["name"].asString(),
            type          = sd["type"].asInteger(),
            preferredType = sd["preferred_type"].asInteger()
        )
    }
}

class Notecard(val maxText: Int = MAX_SIZE) {
    companion object {
        const val MAX_SIZE: Int = 65536
    }

    val items: MutableList<InventoryItem> = mutableListOf()
    var text: String = ""
    var version: Int = 0
    var embeddedVersion: Int = 0
}

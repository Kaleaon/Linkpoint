package com.linkpoint.linden.llinventory

import com.linkpoint.linden.llcommon.LLSD
import com.linkpoint.linden.llcommon.LLUUID

class LLInventoryCategory(
    uuid: LLUUID = LLUUID.generate(),
    var parentId: LLUUID = LLUUID.NULL,
    var preferredType: FolderType = FolderType.NONE,
    name: String = ""
) {
    var uuid: LLUUID = uuid
    var name: String = name
    var version: Int = 0

    fun toLLSD(): LLSD = LLSD.ofMap(mapOf(
        "folder_id" to LLSD.of(uuid),
        "parent_id" to LLSD.of(parentId),
        "name"      to LLSD.of(name),
        "type"      to LLSD.of(preferredType.value),
        "version"   to LLSD.of(version)
    ))

    companion object {
        fun fromLLSD(sd: LLSD): LLInventoryCategory = LLInventoryCategory(
            uuid          = sd["folder_id"].asUUID(),
            parentId      = sd["parent_id"].asUUID(),
            name          = sd["name"].asString(),
            preferredType = FolderType.fromValue(sd["type"].asInt())
        ).also { it.version = sd["version"].asInt() }
    }
}

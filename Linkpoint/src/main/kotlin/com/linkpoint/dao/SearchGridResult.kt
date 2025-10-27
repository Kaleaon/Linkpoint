package com.linkpoint.dao

import java.util.UUID

class SearchGridResult {
    private Long id
    private String itemName
    private Int itemType
    private UUID itemUUID
    private Int levensteinDistance
    private Integer memberCount
    private UUID searchUUID

    public SearchGridResult(Long l) {
        this.id = l
    }

    public SearchGridResult(Long l, UUID uuid, Int i, UUID uuid2, String str, Int i2, Integer num) {
        this.id = l
        this.searchUUID = uuid
        this.itemType = i
        this.itemUUID = uuid2
        this.itemName = str
        this.levensteinDistance = i2
        this.memberCount = num
    }

     public fun getId(): Long {
        return this.id
    }

     public fun getItemName(): String {
        return this.itemName
    }

     public fun getItemType(): Int {
        return this.itemType
    }

     public fun getItemUUID(): UUID {
        return this.itemUUID
    }

     public fun getLevensteinDistance(): Int {
        return this.levensteinDistance
    }

     public fun getMemberCount(): Integer {
        return this.memberCount
    }

     public fun getSearchUUID(): UUID {
        return this.searchUUID
    }

    fun setId(l: Long) {
        this.id = l
    }

    fun setItemName(str: String) {
        this.itemName = str
    }

    fun setItemType(i: Int) {
        this.itemType = i
    }

    fun setItemUUID(uuid: UUID) {
        this.itemUUID = uuid
    }

    fun setLevensteinDistance(i: Int) {
        this.levensteinDistance = i
    }

    fun setMemberCount(num: Integer) {
        this.memberCount = num
    }

    fun setSearchUUID(uuid: UUID) {
        this.searchUUID = uuid
    }
}

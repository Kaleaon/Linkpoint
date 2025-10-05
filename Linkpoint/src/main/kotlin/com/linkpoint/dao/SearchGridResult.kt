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

    public Long getId() {
        return this.id
    }

    public String getItemName() {
        return this.itemName
    }

    public Int getItemType() {
        return this.itemType
    }

    public UUID getItemUUID() {
        return this.itemUUID
    }

    public Int getLevensteinDistance() {
        return this.levensteinDistance
    }

    public Integer getMemberCount() {
        return this.memberCount
    }

    public UUID getSearchUUID() {
        return this.searchUUID
    }

    fun setId(Long l) {
        this.id = l
    }

    fun setItemName(String str) {
        this.itemName = str
    }

    fun setItemType(Int i) {
        this.itemType = i
    }

    fun setItemUUID(UUID uuid) {
        this.itemUUID = uuid
    }

    fun setLevensteinDistance(Int i) {
        this.levensteinDistance = i
    }

    fun setMemberCount(Integer num) {
        this.memberCount = num
    }

    fun setSearchUUID(UUID uuid) {
        this.searchUUID = uuid
    }
}

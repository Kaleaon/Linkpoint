package com.lumiyaviewer.lumiya.dao;

import java.util.UUID;

public class SearchGridResult {
    private Long id;
    private String itemName;
    private int itemType;
    private UUID itemUUID;
    private int levensteinDistance;
    private Integer memberCount;
    private UUID searchUUID;

    public SearchGridResult(Long l) {
        this.id = l;
    }

    public SearchGridResult(Long l, UUID uuid, int i, UUID uuid2, String str, int i2, Integer num) {
        this.id = l;
        this.searchUUID = uuid;
        this.itemType = i;
        this.itemUUID = uuid2;
        this.itemName = str;
        this.levensteinDistance = i2;
        this.memberCount = num;
    }

    public Long getId() {
        return this.id;
    }

    public String getItemName() {
        return this.itemName;
    }

    public int getItemType() {
        return this.itemType;
    }

    public UUID getItemUUID() {
        return this.itemUUID;
    }

    public int getLevensteinDistance() {
        return this.levensteinDistance;
    }

    public Integer getMemberCount() {
        return this.memberCount;
    }

    public UUID getSearchUUID() {
        return this.searchUUID;
    }

    public void setId(Long l) {
        this.id = l;
    }

    public void setItemName(String str) {
        this.itemName = str;
    }

    public void setItemType(int i) {
        this.itemType = i;
    }

    public void setItemUUID(UUID uuid) {
        this.itemUUID = uuid;
    }

    public void setLevensteinDistance(int i) {
        this.levensteinDistance = i;
    }

    public void setMemberCount(Integer num) {
        this.memberCount = num;
    }

    public void setSearchUUID(UUID uuid) {
        this.searchUUID = uuid;
    }
}

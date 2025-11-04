// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.dao;

import java.util.UUID;

public class SearchGridResult
{
    private Long id;
    private String itemName;
    private int itemType;
    private UUID itemUUID;
    private int levensteinDistance;
    private Integer memberCount;
    private UUID searchUUID;
    
    public SearchGridResult() {
    }
    
    public SearchGridResult(final Long id) {
        this.id = id;
    }
    
    public SearchGridResult(final Long id, final UUID searchUUID, final int itemType, final UUID itemUUID, final String itemName, final int levensteinDistance, final Integer memberCount) {
        this.id = id;
        this.searchUUID = searchUUID;
        this.itemType = itemType;
        this.itemUUID = itemUUID;
        this.itemName = itemName;
        this.levensteinDistance = levensteinDistance;
        this.memberCount = memberCount;
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
    
    public void setId(final Long id) {
        this.id = id;
    }
    
    public void setItemName(final String itemName) {
        this.itemName = itemName;
    }
    
    public void setItemType(final int itemType) {
        this.itemType = itemType;
    }
    
    public void setItemUUID(final UUID itemUUID) {
        this.itemUUID = itemUUID;
    }
    
    public void setLevensteinDistance(final int levensteinDistance) {
        this.levensteinDistance = levensteinDistance;
    }
    
    public void setMemberCount(final Integer memberCount) {
        this.memberCount = memberCount;
    }
    
    public void setSearchUUID(final UUID searchUUID) {
        this.searchUUID = searchUUID;
    }
}

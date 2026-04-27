// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.dao;

import java.util.UUID;

public class Chatter
{
    private boolean active;
    private Long id;
    private Long lastMessageID;
    private UUID lastSessionID;
    private boolean muted;
    private int type;
    private int unreadCount;
    private UUID uuid;
    
    public Chatter() {
    }
    
    public Chatter(final Long id) {
        this.id = id;
    }
    
    public Chatter(final Long id, final int type, final UUID uuid, final boolean active, final boolean muted, final int unreadCount, final Long lastMessageID, final UUID lastSessionID) {
        this.id = id;
        this.type = type;
        this.uuid = uuid;
        this.active = active;
        this.muted = muted;
        this.unreadCount = unreadCount;
        this.lastMessageID = lastMessageID;
        this.lastSessionID = lastSessionID;
    }
    
    public boolean getActive() {
        return this.active;
    }
    
    public Long getId() {
        return this.id;
    }
    
    public Long getLastMessageID() {
        return this.lastMessageID;
    }
    
    public UUID getLastSessionID() {
        return this.lastSessionID;
    }
    
    public boolean getMuted() {
        return this.muted;
    }
    
    public int getType() {
        return this.type;
    }
    
    public int getUnreadCount() {
        return this.unreadCount;
    }
    
    public UUID getUuid() {
        return this.uuid;
    }
    
    public void setActive(final boolean active) {
        this.active = active;
    }
    
    public void setId(final Long id) {
        this.id = id;
    }
    
    public void setLastMessageID(final Long lastMessageID) {
        this.lastMessageID = lastMessageID;
    }
    
    public void setLastSessionID(final UUID lastSessionID) {
        this.lastSessionID = lastSessionID;
    }
    
    public void setMuted(final boolean muted) {
        this.muted = muted;
    }
    
    public void setType(final int type) {
        this.type = type;
    }
    
    public void setUnreadCount(final int unreadCount) {
        this.unreadCount = unreadCount;
    }
    
    public void setUuid(final UUID uuid) {
        this.uuid = uuid;
    }
}

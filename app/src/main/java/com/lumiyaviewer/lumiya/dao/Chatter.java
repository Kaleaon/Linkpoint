package com.lumiyaviewer.lumiya.dao;

import java.util.UUID;

public class Chatter {
    private boolean active;
    private Long id;
    private Long lastMessageID;
    private UUID lastSessionID;
    private boolean muted;
    private int type;
    private int unreadCount;
    private UUID uuid;

    public Chatter(Long l) {
        this.id = l;
    }

    public Chatter(Long l, int i, UUID uuid, boolean z, boolean z2, int i2, Long l2, UUID uuid2) {
        this.id = l;
        this.type = i;
        this.uuid = uuid;
        this.active = z;
        this.muted = z2;
        this.unreadCount = i2;
        this.lastMessageID = l2;
        this.lastSessionID = uuid2;
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

    public void setActive(boolean z) {
        this.active = z;
    }

    public void setId(Long l) {
        this.id = l;
    }

    public void setLastMessageID(Long l) {
        this.lastMessageID = l;
    }

    public void setLastSessionID(UUID uuid) {
        this.lastSessionID = uuid;
    }

    public void setMuted(boolean z) {
        this.muted = z;
    }

    public void setType(int i) {
        this.type = i;
    }

    public void setUnreadCount(int i) {
        this.unreadCount = i;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}

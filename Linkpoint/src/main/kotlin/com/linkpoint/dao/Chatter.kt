package com.linkpoint.dao

import java.util.UUID

class Chatter {
    private Boolean active
    private Long id
    private Long lastMessageID
    private UUID lastSessionID
    private Boolean muted
    private Int type
    private Int unreadCount
    private UUID uuid

    public Chatter(Long l) {
        this.id = l
    }

    public Chatter(Long l, Int i, UUID uuid, Boolean z, Boolean z2, Int i2, Long l2, UUID uuid2) {
        this.id = l
        this.type = i
        this.uuid = uuid
        this.active = z
        this.muted = z2
        this.unreadCount = i2
        this.lastMessageID = l2
        this.lastSessionID = uuid2
    }

    public Boolean getActive() {
        return this.active
    }

    public Long getId() {
        return this.id
    }

    public Long getLastMessageID() {
        return this.lastMessageID
    }

    public UUID getLastSessionID() {
        return this.lastSessionID
    }

    public Boolean getMuted() {
        return this.muted
    }

    public Int getType() {
        return this.type
    }

    public Int getUnreadCount() {
        return this.unreadCount
    }

    public UUID getUuid() {
        return this.uuid
    }

    public Unit setActive(Boolean z) {
        this.active = z
    }

    public Unit setId(Long l) {
        this.id = l
    }

    public Unit setLastMessageID(Long l) {
        this.lastMessageID = l
    }

    public Unit setLastSessionID(UUID uuid) {
        this.lastSessionID = uuid
    }

    public Unit setMuted(Boolean z) {
        this.muted = z
    }

    public Unit setType(Int i) {
        this.type = i
    }

    public Unit setUnreadCount(Int i) {
        this.unreadCount = i
    }

    public Unit setUuid(UUID uuid) {
        this.uuid = uuid
    }
}

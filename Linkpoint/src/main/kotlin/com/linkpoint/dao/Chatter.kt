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

     public fun getActive(): Boolean {
        return this.active
    }

     public fun getId(): Long {
        return this.id
    }

     public fun getLastMessageID(): Long {
        return this.lastMessageID
    }

     public fun getLastSessionID(): UUID {
        return this.lastSessionID
    }

     public fun getMuted(): Boolean {
        return this.muted
    }

     public fun getType(): Int {
        return this.type
    }

     public fun getUnreadCount(): Int {
        return this.unreadCount
    }

     public fun getUuid(): UUID {
        return this.uuid
    }

    fun setActive(z: Boolean) {
        this.active = z
    }

    fun setId(l: Long) {
        this.id = l
    }

    fun setLastMessageID(l: Long) {
        this.lastMessageID = l
    }

    fun setLastSessionID(uuid: UUID) {
        this.lastSessionID = uuid
    }

    fun setMuted(z: Boolean) {
        this.muted = z
    }

    fun setType(i: Int) {
        this.type = i
    }

    fun setUnreadCount(i: Int) {
        this.unreadCount = i
    }

    fun setUuid(uuid: UUID) {
        this.uuid = uuid
    }
}

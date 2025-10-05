package com.linkpoint.dao

import java.util.UUID

class Friend {
    const val GRANT_MAP_LOCATION: Int = 2
    const val GRANT_MODIFY_OBJECTS: Int = 4
    const val GRANT_ONLINE_STATUS: Int = 1
    private Boolean isOnline
    private Int rightsGiven
    private Int rightsHas
    private UUID uuid

    public Friend(UUID uuid) {
        this.uuid = uuid
    }

    public Friend(UUID uuid, Int i, Int i2, Boolean z) {
        this.uuid = uuid
        this.rightsGiven = i
        this.rightsHas = i2
        this.isOnline = z
    }

    public Boolean getIsOnline() {
        return this.isOnline
    }

    public Int getRightsGiven() {
        return this.rightsGiven
    }

    public Int getRightsHas() {
        return this.rightsHas
    }

    public UUID getUuid() {
        return this.uuid
    }

    public Unit setIsOnline(Boolean z) {
        this.isOnline = z
    }

    public Unit setRightsGiven(Int i) {
        this.rightsGiven = i
    }

    public Unit setRightsHas(Int i) {
        this.rightsHas = i
    }

    public Unit setUuid(UUID uuid) {
        this.uuid = uuid
    }
}

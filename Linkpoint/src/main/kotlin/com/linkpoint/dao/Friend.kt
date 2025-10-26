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

     public fun getIsOnline(): Boolean {
        return this.isOnline
    }

     public fun getRightsGiven(): Int {
        return this.rightsGiven
    }

     public fun getRightsHas(): Int {
        return this.rightsHas
    }

     public fun getUuid(): UUID {
        return this.uuid
    }

    fun setIsOnline(z: Boolean) {
        this.isOnline = z
    }

    fun setRightsGiven(i: Int) {
        this.rightsGiven = i
    }

    fun setRightsHas(i: Int) {
        this.rightsHas = i
    }

    fun setUuid(uuid: UUID) {
        this.uuid = uuid
    }
}

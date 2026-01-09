package com.linkpoint.dao

import java.util.UUID

data class Friend(
    var uuid: UUID,
    var rightsGiven: Int = 0,
    var rightsHas: Int = 0,
    var isOnline: Boolean = false,
) {
    companion object {
        const val GRANT_ONLINE_STATUS = 1
        const val GRANT_MAP_LOCATION = 2
        const val GRANT_MODIFY_OBJECTS = 4
    }
}

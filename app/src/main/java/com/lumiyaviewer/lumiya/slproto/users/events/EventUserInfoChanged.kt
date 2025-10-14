package com.lumiyaviewer.lumiya.slproto.users.events

import java.util.UUID

class EventUserInfoChanged(
    val agentUUID: UUID,
    val userUUID: UUID,
    val changedMask: Int,
) {
    fun isNameChanged(): Boolean {
        return (changedMask and CHANGED_NAME) != 0
    }

    fun isProfileChanged(): Boolean {
        return (changedMask and CHANGED_PROFILE) != 0
    }

    companion object {
        const val CHANGED_NAME = 2
        const val CHANGED_PROFILE = 4
    }
}
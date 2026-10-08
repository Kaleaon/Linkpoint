package com.linkpoint.groups.provider

import java.util.UUID

/**
 * Representation of a group member record returned by group service providers.
 */
data class GroupMemberRecord(
    val agentId: UUID,
    val title: String = "",
    val isOwner: Boolean = false,
    val isOnline: Boolean = false,
    val powers: Long = 0L,
    val contribution: Int = 0,
    val acceptNotices: Boolean = true,
    val listInProfile: Boolean = true
)

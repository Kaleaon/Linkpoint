package com.lumiyaviewer.lumiya.dao

import java.util.UUID

data class GroupMember(
    var groupID: UUID,
    var requestID: UUID,
    var userID: UUID,
    var contribution: Int,
    var onlineStatus: String,
    var agentPowers: Long,
    var title: String,
    var isOwner: Boolean
)
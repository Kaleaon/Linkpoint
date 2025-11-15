package com.lumiyaviewer.lumiya.dao

import java.util.UUID

data class GroupMemberList(
    var groupID: UUID,
    var requestID: UUID? = null,
)

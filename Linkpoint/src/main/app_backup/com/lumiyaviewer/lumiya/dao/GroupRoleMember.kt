package com.lumiyaviewer.lumiya.dao

import java.util.UUID

data class GroupRoleMember(
    var groupID: UUID,
    var requestID: UUID,
    var roleID: UUID,
    var userID: UUID,
)

package com.lumiyaviewer.lumiya.dao

import java.util.UUID

data class GroupRoleMemberList(
    var groupID: UUID,
    var roleID: UUID,
    var memberID: UUID
)

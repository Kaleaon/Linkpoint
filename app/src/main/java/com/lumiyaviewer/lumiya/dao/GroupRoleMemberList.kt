package com.lumiyaviewer.lumiya.dao

import java.util.UUID

data class GroupRoleMemberList(
    var groupID: UUID,
    var requestID: UUID? = null,
    var mustRevalidate: Boolean = false,
)

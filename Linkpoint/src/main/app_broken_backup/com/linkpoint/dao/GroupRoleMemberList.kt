package com.linkpoint.dao

import java.util.UUID

data class GroupRoleMemberList(
    var groupID: UUID,
    var requestID: UUID? = null,
    var mustRevalidate: Boolean = false,
)

package com.linkpoint.dao

import java.util.UUID

class GroupMemberList {
    var groupID: UUID
    var requestID: UUID?

    constructor(groupID: UUID) {
        this.groupID = groupID
        this.requestID = null
    }

    constructor(groupID: UUID, requestID: UUID) {
        this.groupID = groupID
        this.requestID = requestID
    }
}
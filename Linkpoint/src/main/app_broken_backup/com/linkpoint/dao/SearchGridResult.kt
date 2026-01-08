package com.linkpoint.dao

import java.util.UUID

data class SearchGridResult(
    var id: Long? = null,
    var searchUUID: UUID? = null,
    var itemType: Int = 0,
    var itemUUID: UUID? = null,
    var itemName: String? = null,
    var levensteinDistance: Int = 0,
    var memberCount: Int? = null,
)

package com.linkpoint.slproto.modules.search

import java.util.UUID

data class SearchGridQuery(
    val searchUUID: UUID,
    val searchText: String,
    val searchType: SearchType,
) {
    enum class SearchType {
        People,
        Groups,
        Places,
    }

    companion object {
        @JvmStatic
        fun create(uuid: UUID, str: String, searchType: SearchType): SearchGridQuery {
            return SearchGridQuery(uuid, str, searchType)
        }
    }
}
package com.linkpoint.slproto.modules.search

import java.util.UUID

abstract class SearchGridQuery {
    enum class SearchType {
        People,
        Groups,
        Places
    }

    abstract fun searchText(): String
    abstract fun searchType(): SearchType
    abstract fun searchUUID(): UUID

    companion object {
        @JvmStatic
        fun create(uuid: UUID, searchText: String, searchType: SearchType): SearchGridQuery {
            return AutoValue_SearchGridQuery(uuid, searchText, searchType)
        }
    }
}
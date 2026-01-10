package com.linkpoint.slproto.modules.search

import com.linkpoint.slproto.modules.search.SearchGridQuery
import java.util.UUID

class AutoValue_SearchGridQuery : SearchGridQuery {
    private String searchText
    private SearchGridQuery.SearchType searchType
    private UUID searchUUID

    AutoValue_SearchGridQuery(UUID uuid, String str, SearchGridQuery.SearchType searchType2) {
        if (uuid == null) {
            throw NullPointerException("Null searchUUID")
        }
        this.searchUUID = uuid
        if (str == null) {
            throw NullPointerException("Null searchText")
        }
        this.searchText = str
        if (searchType2 == null) {
            throw NullPointerException("Null searchType")
        }
        this.searchType = searchType2
    }

    fun equals(Any obj): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj is SearchGridQuery)) {
            return false
        }
        SearchGridQuery searchGridQuery = (SearchGridQuery) obj
        if (!this.searchUUID.equals(searchGridQuery.searchUUID()) || !this.searchText.equals(searchGridQuery.searchText())) {
            return false
        }
        return this.searchType.equals(searchGridQuery.searchType())
    }

    fun hashCode(): Int {
        return ((((this.searchUUID.hashCode() ^ 1000003) * 1000003) ^ this.searchText.hashCode()) * 1000003) ^ this.searchType.hashCode()
    }

    fun searchText(): String {
        return this.searchText
    }

    SearchGridQuery.SearchType searchType() {
        return this.searchType
    }

    fun searchUUID(): UUID {
        return this.searchUUID
    }

    fun toString(): String {
        return "SearchGridQuery{searchUUID=" + this.searchUUID + ", " + "searchText=" + this.searchText + ", " + "searchType=" + this.searchType + "}"
    }
}

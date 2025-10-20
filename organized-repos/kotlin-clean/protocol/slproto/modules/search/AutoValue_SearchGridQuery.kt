package com.linkpoint.slproto.modules.search

import com.linkpoint.slproto.modules.search.SearchGridQuery
import java.util.UUID

final class AutoValue_SearchGridQuery : SearchGridQuery() {
    private val String searchText
    private val SearchGridQuery.SearchType searchType
    private val UUID searchUUID

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

    public Boolean equals(Object obj) {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof SearchGridQuery)) {
            return false
        }
        SearchGridQuery searchGridQuery = (SearchGridQuery) obj
        if (!this.searchUUID.equals(searchGridQuery.searchUUID()) || !this.searchText.equals(searchGridQuery.searchText())) {
            return false
        }
        return this.searchType.equals(searchGridQuery.searchType())
    }

    public Int hashCode() {
        return ((((this.searchUUID.hashCode() ^ 1000003) * 1000003) ^ this.searchText.hashCode()) * 1000003) ^ this.searchType.hashCode()
    }

    public String searchText() {
        return this.searchText
    }

    public SearchGridQuery.SearchType searchType() {
        return this.searchType
    }

    public UUID searchUUID() {
        return this.searchUUID
    }

    public String toString() {
        return "SearchGridQuery{searchUUID=" + this.searchUUID + ", " + "searchText=" + this.searchText + ", " + "searchType=" + this.searchType + "}"
    }
}

package com.lumiyaviewer.lumiya.slproto.modules.search;

import com.lumiyaviewer.lumiya.slproto.modules.search.SearchGridQuery;
import java.util.UUID;

final class AutoValue_SearchGridQuery extends SearchGridQuery {
    private final String searchText;
    private final SearchGridQuery.SearchType searchType;
    private final UUID searchUUID;

    AutoValue_SearchGridQuery(UUID uuid, String str, SearchGridQuery.SearchType searchType2) {
        if (uuid == null) {
            throw new NullPointerException("Null searchUUID");
        }
        this.searchUUID = uuid;
        if (str == null) {
            throw new NullPointerException("Null searchText");
        }
        this.searchText = str;
        if (searchType2 == null) {
            throw new NullPointerException("Null searchType");
        }
        this.searchType = searchType2;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SearchGridQuery)) {
            return false;
        }
        SearchGridQuery searchGridQuery = (SearchGridQuery) obj;
        if (!this.searchUUID.equals(searchGridQuery.searchUUID()) || !this.searchText.equals(searchGridQuery.searchText())) {
            return false;
        }
        return this.searchType.equals(searchGridQuery.searchType());
    }

    public int hashCode() {
        return ((((this.searchUUID.hashCode() ^ 1000003) * 1000003) ^ this.searchText.hashCode()) * 1000003) ^ this.searchType.hashCode();
    }

    public String searchText() {
        return this.searchText;
    }

    public SearchGridQuery.SearchType searchType() {
        return this.searchType;
    }

    public UUID searchUUID() {
        return this.searchUUID;
    }

    public String toString() {
        return "SearchGridQuery{searchUUID=" + this.searchUUID + ", " + "searchText=" + this.searchText + ", " + "searchType=" + this.searchType + "}";
    }
}

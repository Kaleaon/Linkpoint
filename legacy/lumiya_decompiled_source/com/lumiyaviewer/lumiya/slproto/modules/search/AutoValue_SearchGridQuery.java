package com.lumiyaviewer.lumiya.slproto.modules.search;

import com.lumiyaviewer.lumiya.slproto.modules.search.SearchGridQuery;
import java.util.UUID;
/* loaded from: classes.dex */
final class AutoValue_SearchGridQuery extends SearchGridQuery {
    private final String searchText;
    private final SearchGridQuery.SearchType searchType;
    private final UUID searchUUID;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AutoValue_SearchGridQuery(UUID uuid, String str, SearchGridQuery.SearchType searchType) {
        if (uuid == null) {
            throw new NullPointerException("Null searchUUID");
        }
        this.searchUUID = uuid;
        if (str == null) {
            throw new NullPointerException("Null searchText");
        }
        this.searchText = str;
        if (searchType == null) {
            throw new NullPointerException("Null searchType");
        }
        this.searchType = searchType;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof SearchGridQuery) {
            SearchGridQuery searchGridQuery = (SearchGridQuery) obj;
            if (this.searchUUID.equals(searchGridQuery.searchUUID()) && this.searchText.equals(searchGridQuery.searchText())) {
                return this.searchType.equals(searchGridQuery.searchType());
            }
            return false;
        }
        return false;
    }

    public int hashCode() {
        return ((((this.searchUUID.hashCode() ^ 1000003) * 1000003) ^ this.searchText.hashCode()) * 1000003) ^ this.searchType.hashCode();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.modules.search.SearchGridQuery
    public String searchText() {
        return this.searchText;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.modules.search.SearchGridQuery
    public SearchGridQuery.SearchType searchType() {
        return this.searchType;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.modules.search.SearchGridQuery
    public UUID searchUUID() {
        return this.searchUUID;
    }

    public String toString() {
        return "SearchGridQuery{searchUUID=" + this.searchUUID + ", searchText=" + this.searchText + ", searchType=" + this.searchType + "}";
    }
}

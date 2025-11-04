// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.search;

import java.util.UUID;

final class AutoValue_SearchGridQuery extends SearchGridQuery
{
    private final String searchText;
    private final SearchType searchType;
    private final UUID searchUUID;
    
    AutoValue_SearchGridQuery(final UUID searchUUID, final String searchText, final SearchType searchType) {
        if (searchUUID == null) {
            throw new NullPointerException("Null searchUUID");
        }
        this.searchUUID = searchUUID;
        if (searchText == null) {
            throw new NullPointerException("Null searchText");
        }
        this.searchText = searchText;
        if (searchType == null) {
            throw new NullPointerException("Null searchType");
        }
        this.searchType = searchType;
    }
    
    @Override
    public boolean equals(final Object o) {
        final boolean b = false;
        if (o == this) {
            return true;
        }
        if (o instanceof SearchGridQuery) {
            final SearchGridQuery searchGridQuery = (SearchGridQuery)o;
            boolean equals = b;
            if (this.searchUUID.equals(searchGridQuery.searchUUID())) {
                equals = b;
                if (this.searchText.equals(searchGridQuery.searchText())) {
                    equals = this.searchType.equals(searchGridQuery.searchType());
                }
            }
            return equals;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return ((this.searchUUID.hashCode() ^ 0xF4243) * 1000003 ^ this.searchText.hashCode()) * 1000003 ^ this.searchType.hashCode();
    }
    
    @Override
    public String searchText() {
        return this.searchText;
    }
    
    @Override
    public SearchType searchType() {
        return this.searchType;
    }
    
    @Override
    public UUID searchUUID() {
        return this.searchUUID;
    }
    
    @Override
    public String toString() {
        return "SearchGridQuery{searchUUID=" + this.searchUUID + ", " + "searchText=" + this.searchText + ", " + "searchType=" + this.searchType + "}";
    }
}

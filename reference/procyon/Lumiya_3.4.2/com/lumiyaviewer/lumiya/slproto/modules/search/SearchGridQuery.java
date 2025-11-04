// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.search;

import javax.annotation.Nonnull;
import java.util.UUID;

public abstract class SearchGridQuery
{
    public static SearchGridQuery create(@Nonnull final UUID uuid, @Nonnull final String s, @Nonnull final SearchType searchType) {
        return new AutoValue_SearchGridQuery(uuid, s, searchType);
    }
    
    public abstract String searchText();
    
    public abstract SearchType searchType();
    
    public abstract UUID searchUUID();
    
    public enum SearchType
    {
        Groups("Groups", 1), 
        People("People", 0), 
        Places("Places", 2);
        
        private SearchType(final String name, final int ordinal) {
        }
    }
}

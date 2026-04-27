package com.lumiyaviewer.lumiya.slproto.modules.search;

import java.util.UUID;
import javax.annotation.Nonnull;

public abstract class SearchGridQuery {

    public enum SearchType {
        People,
        Groups,
        Places
    }

    public static SearchGridQuery create(@Nonnull UUID uuid, @Nonnull String str, @Nonnull SearchType searchType) {
        return new AutoValue_SearchGridQuery(uuid, str, searchType);
    }

    public abstract String searchText();

    public abstract SearchType searchType();

    public abstract UUID searchUUID();
}

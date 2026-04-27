package com.lumiyaviewer.lumiya.slproto.modules.search;

import java.util.UUID;
import javax.annotation.Nonnull;

/* loaded from: classes.dex */
public abstract class SearchGridQuery {

    /* loaded from: classes.dex */
    public enum SearchType {
        People,
        Groups,
        Places;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static SearchType[] valuesCustom() {
            return values();
        }
    }

    public static SearchGridQuery create(@Nonnull UUID uuid, @Nonnull String str, @Nonnull SearchType searchType) {
        return new AutoValue_SearchGridQuery(uuid, str, searchType);
    }

    public abstract String searchText();

    public abstract SearchType searchType();

    public abstract UUID searchUUID();
}

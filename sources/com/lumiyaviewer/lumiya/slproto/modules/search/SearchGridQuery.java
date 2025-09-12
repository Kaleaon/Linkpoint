// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.search;

import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.search:
//            AutoValue_SearchGridQuery

public abstract class SearchGridQuery
{
    public static final class SearchType extends Enum
    {

        private static final SearchType $VALUES[];
        public static final SearchType Groups;
        public static final SearchType People;
        public static final SearchType Places;

        public static SearchType valueOf(String s)
        {
            return (SearchType)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/modules/search/SearchGridQuery$SearchType, s);
        }

        public static SearchType[] values()
        {
            return $VALUES;
        }

        static 
        {
            People = new SearchType("People", 0);
            Groups = new SearchType("Groups", 1);
            Places = new SearchType("Places", 2);
            $VALUES = (new SearchType[] {
                People, Groups, Places
            });
        }

        private SearchType(String s, int i)
        {
            super(s, i);
        }
    }


    public SearchGridQuery()
    {
    }

    public static SearchGridQuery create(UUID uuid, String s, SearchType searchtype)
    {
        return new AutoValue_SearchGridQuery(uuid, s, searchtype);
    }

    public abstract String searchText();

    public abstract SearchType searchType();

    public abstract UUID searchUUID();
}

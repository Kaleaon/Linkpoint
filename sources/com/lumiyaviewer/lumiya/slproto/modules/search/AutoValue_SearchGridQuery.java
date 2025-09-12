// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.search;

import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.search:
//            SearchGridQuery

final class AutoValue_SearchGridQuery extends SearchGridQuery
{

    private final String searchText;
    private final SearchGridQuery.SearchType searchType;
    private final UUID searchUUID;

    AutoValue_SearchGridQuery(UUID uuid, String s, SearchGridQuery.SearchType searchtype)
    {
        if (uuid == null)
        {
            throw new NullPointerException("Null searchUUID");
        }
        searchUUID = uuid;
        if (s == null)
        {
            throw new NullPointerException("Null searchText");
        }
        searchText = s;
        if (searchtype == null)
        {
            throw new NullPointerException("Null searchType");
        } else
        {
            searchType = searchtype;
            return;
        }
    }

    public boolean equals(Object obj)
    {
        boolean flag1 = false;
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof SearchGridQuery)
        {
            obj = (SearchGridQuery)obj;
            boolean flag = flag1;
            if (searchUUID.equals(((SearchGridQuery) (obj)).searchUUID()))
            {
                flag = flag1;
                if (searchText.equals(((SearchGridQuery) (obj)).searchText()))
                {
                    flag = searchType.equals(((SearchGridQuery) (obj)).searchType());
                }
            }
            return flag;
        } else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return ((searchUUID.hashCode() ^ 0xf4243) * 0xf4243 ^ searchText.hashCode()) * 0xf4243 ^ searchType.hashCode();
    }

    public String searchText()
    {
        return searchText;
    }

    public SearchGridQuery.SearchType searchType()
    {
        return searchType;
    }

    public UUID searchUUID()
    {
        return searchUUID;
    }

    public String toString()
    {
        return (new StringBuilder()).append("SearchGridQuery{searchUUID=").append(searchUUID).append(", ").append("searchText=").append(searchText).append(", ").append("searchType=").append(searchType).append("}").toString();
    }
}

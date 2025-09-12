// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.orm;

import android.database.sqlite.SQLiteDatabase;

// Referenced classes of package com.lumiyaviewer.lumiya.orm:
//            DBHandleCache

private static class handleCount
{

    private final String fileName;
    private int handleCount;
    private final SQLiteDatabase sqliteDB;

    public final void acquireReference()
    {
        handleCount = handleCount + 1;
    }

    public final SQLiteDatabase getDB()
    {
        return sqliteDB;
    }

    public final String getFileName()
    {
        return fileName;
    }

    public final int releaseReference()
    {
        handleCount = handleCount - 1;
        return handleCount;
    }

    public _cls9(String s, SQLiteDatabase sqlitedatabase)
    {
        fileName = s;
        sqliteDB = sqlitedatabase;
        handleCount = 0;
    }
}

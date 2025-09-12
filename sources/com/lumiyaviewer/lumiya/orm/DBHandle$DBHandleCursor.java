// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.orm;

import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;

// Referenced classes of package com.lumiyaviewer.lumiya.orm:
//            DBHandle

private class this._cls0 extends SQLiteCursor
{

    final DBHandle this$0;

    public _cls9(SQLiteDatabase sqlitedatabase, SQLiteCursorDriver sqlitecursordriver, String s, SQLiteQuery sqlitequery)
    {
        this$0 = DBHandle.this;
        super(sqlitedatabase, sqlitecursordriver, s, sqlitequery);
    }
}

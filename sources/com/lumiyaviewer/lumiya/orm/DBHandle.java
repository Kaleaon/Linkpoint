// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.orm;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;

public final class DBHandle
    implements android.database.sqlite.SQLiteDatabase.CursorFactory
{
    private class DBHandleCursor extends SQLiteCursor
    {

        final DBHandle this$0;

        public DBHandleCursor(SQLiteDatabase sqlitedatabase, SQLiteCursorDriver sqlitecursordriver, String s, SQLiteQuery sqlitequery)
        {
            this$0 = DBHandle.this;
            super(sqlitedatabase, sqlitecursordriver, s, sqlitequery);
        }
    }


    private final SQLiteDatabase sqliteDB;

    public DBHandle(SQLiteDatabase sqlitedatabase)
    {
        sqliteDB = sqlitedatabase;
    }

    public final SQLiteDatabase getDB()
    {
        return sqliteDB;
    }

    public Cursor newCursor(SQLiteDatabase sqlitedatabase, SQLiteCursorDriver sqlitecursordriver, String s, SQLiteQuery sqlitequery)
    {
        return new DBHandleCursor(sqlitedatabase, sqlitecursordriver, s, sqlitequery);
    }
}

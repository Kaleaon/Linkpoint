// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.orm;

import android.database.sqlite.SQLiteCursor;
import android.database.Cursor;
import android.database.sqlite.SQLiteQuery;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase$CursorFactory;

public final class DBHandle implements SQLiteDatabase$CursorFactory
{
    private final SQLiteDatabase sqliteDB;
    
    public DBHandle(final SQLiteDatabase sqliteDB) {
        this.sqliteDB = sqliteDB;
    }
    
    public final SQLiteDatabase getDB() {
        return this.sqliteDB;
    }
    
    public Cursor newCursor(final SQLiteDatabase sqLiteDatabase, final SQLiteCursorDriver sqLiteCursorDriver, final String s, final SQLiteQuery sqLiteQuery) {
        return (Cursor)new DBHandleCursor(sqLiteDatabase, sqLiteCursorDriver, s, sqLiteQuery);
    }
    
    private class DBHandleCursor extends SQLiteCursor
    {
        public DBHandleCursor(final SQLiteDatabase sqLiteDatabase, final SQLiteCursorDriver sqLiteCursorDriver, final String s, final SQLiteQuery sqLiteQuery) {
            super(sqLiteDatabase, sqLiteCursorDriver, s, sqLiteQuery);
        }
    }
}

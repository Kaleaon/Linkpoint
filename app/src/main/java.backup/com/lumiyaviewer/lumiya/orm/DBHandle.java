package com.lumiyaviewer.lumiya.orm;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;

public final class DBHandle implements SQLiteDatabase.CursorFactory {
    private final SQLiteDatabase sqliteDB;

    private class DBHandleCursor extends SQLiteCursor {
        public DBHandleCursor(SQLiteDatabase sQLiteDatabase, SQLiteCursorDriver sQLiteCursorDriver, String str, SQLiteQuery sQLiteQuery) {
            super(sQLiteDatabase, sQLiteCursorDriver, str, sQLiteQuery);
        }
    }

    public DBHandle(SQLiteDatabase sQLiteDatabase) {
        this.sqliteDB = sQLiteDatabase;
    }

    public final SQLiteDatabase getDB() {
        return this.sqliteDB;
    }

    public Cursor newCursor(SQLiteDatabase sQLiteDatabase, SQLiteCursorDriver sQLiteCursorDriver, String str, SQLiteQuery sQLiteQuery) {
        return new DBHandleCursor(sQLiteDatabase, sQLiteCursorDriver, str, sQLiteQuery);
    }
}

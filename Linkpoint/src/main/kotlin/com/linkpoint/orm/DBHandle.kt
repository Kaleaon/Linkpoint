package com.linkpoint.orm

import android.database.Cursor
import android.database.sqlite.SQLiteCursor
import android.database.sqlite.SQLiteCursorDriver
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQuery

val class DBHandle : SQLiteDatabase.CursorFactory {
    private val SQLiteDatabase sqliteDB

    private class DBHandleCursor : SQLiteCursor() {
        public DBHandleCursor(SQLiteDatabase sQLiteDatabase, SQLiteCursorDriver sQLiteCursorDriver, String str, SQLiteQuery sQLiteQuery) {
            super(sQLiteDatabase, sQLiteCursorDriver, str, sQLiteQuery)
        }
    }

    public DBHandle(SQLiteDatabase sQLiteDatabase) {
        if (sQLiteDatabase == null) {
            throw IllegalArgumentException("SQLiteDatabase cannot be null")
        }
        this.sqliteDB = sQLiteDatabase
    }

    val SQLiteDatabase getDB() {
        return this.sqliteDB
    }

     public fun newCursor(sQLiteDatabase: SQLiteDatabase, sQLiteCursorDriver: SQLiteCursorDriver, str: String, sQLiteQuery: SQLiteQuery): Cursor {
        if (sQLiteDatabase == null) {
            throw IllegalArgumentException("SQLiteDatabase cannot be null")
        }
        return DBHandleCursor(sQLiteDatabase, sQLiteCursorDriver, str, sQLiteQuery)
    }
}

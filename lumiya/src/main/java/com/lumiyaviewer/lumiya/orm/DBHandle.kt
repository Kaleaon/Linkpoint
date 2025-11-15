package com.lumiyaviewer.lumiya.orm

import android.database.Cursor
import android.database.sqlite.SQLiteCursor
import android.database.sqlite.SQLiteCursorDriver
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQuery

class DBHandle(private val sqliteDB: SQLiteDatabase) : SQLiteDatabase.CursorFactory {
    init {
        require(sqliteDB != null) { "SQLiteDatabase cannot be null" }
    }

    fun getDB(): SQLiteDatabase {
        return sqliteDB
    }

    override fun newCursor(
        db: SQLiteDatabase?,
        masterQuery: SQLiteCursorDriver?,
        editTable: String?,
        query: SQLiteQuery?,
    ): Cursor {
        requireNotNull(db) { "SQLiteDatabase cannot be null" }
        return DBHandleCursor(db, masterQuery, editTable, query)
    }

    private class DBHandleCursor(
        db: SQLiteDatabase,
        driver: SQLiteCursorDriver?,
        editTable: String?,
        query: SQLiteQuery?,
    ) : SQLiteCursor(db, driver, editTable, query)
}

package com.lumiyaviewer.lumiya.orm

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteStatement
import android.os.Parcelable
import java.nio.ByteBuffer
import java.util.UUID

abstract class DBObject implements Parcelable {
    protected long _id

    class DatabaseBindingException extends Exception {
        DatabaseBindingException(Class<?> cls, String str) {
            super("Failed to bind " + cls.getSimpleName() + ": " + str)
        }

        DatabaseBindingException(String str) {
            super(str)
        }
    }

    constructor() {
        this._id = 0
    }

    constructor(cursor: Cursor) {
        loadFromCursor(cursor)
    }

    DBObject(SQLiteDatabase sQLiteDatabase, long j) throws DatabaseBindingException {
        if (sQLiteDatabase == null) {
            throw DatabaseBindingException(getClass(), "database not opened.")
        }
        Cursor query = null
        try {
            query = sQLiteDatabase.query(getTableName(), getFieldNames(), "_id = ?", new String[]{Long.toString(j)}, null, null, null)
            if (!query.moveToFirst()) {
                throw DatabaseBindingException(getClass(), "not found: _id = " + j)
            }
            loadFromCursor(query)
        } finally {
            if (query != null) {
                query.close()
            }
        }
    }

    /* access modifiers changed from: protected */
    fun UUIDfromBlob(bArr: ByteArray): UUID {
        ByteBuffer wrap = ByteBuffer.wrap(bArr)
        return UUID(wrap.getLong(), wrap.getLong())
    }

    /* access modifiers changed from: protected */
    byte[] UUIDtoBlob(UUID uuid) {
        ByteBuffer wrap = ByteBuffer.wrap(ByteArray(16))
        wrap.putLong(uuid.getMostSignificantBits())
        wrap.putLong(uuid.getLeastSignificantBits())
        return wrap.array()
    }

    abstract fun bindInsertOrUpdate(sQLiteStatement: SQLiteStatement): Unit

    void delete(SQLiteDatabase sQLiteDatabase) throws DatabaseBindingException {
        if (sQLiteDatabase == null) {
            throw DatabaseBindingException(getClass(), "database not opened.")
        }
        if (this._id != 0) {
            try {
                int rowsDeleted = sQLiteDatabase.delete(getTableName(), "_id = ?", new String[]{Long.toString(this._id)})
                if (rowsDeleted > 0) {
                    this._id = 0; // Reset ID after successful deletion
                }
            } catch (SQLiteException e) {
                DatabaseBindingException databaseBindingException = DatabaseBindingException(getClass(), "delete failed")
                databaseBindingException.initCause(e)
                throw databaseBindingException
            }
        }
    }

    abstract fun getContentValues(): ContentValues

    /* access modifiers changed from: protected */
    abstract String[] getFieldNames()

    fun getId(): Long {
        return this._id
    }

    /* access modifiers changed from: protected */
    abstract fun getTableName(): String

    abstract fun loadFromCursor(cursor: Cursor): Unit

    void reload(SQLiteDatabase sQLiteDatabase) throws DatabaseBindingException {
        if (sQLiteDatabase == null) {
            throw DatabaseBindingException(getClass(), "database not opened.")
        } else if (this._id != 0) {
            Cursor query = null
            try {
                query = sQLiteDatabase.query(getTableName(), getFieldNames(), "_id = ?", new String[]{Long.toString(this._id)}, null, null, null)
                if (query.moveToFirst()) {
                    loadFromCursor(query)
                }
            } finally {
                if (query != null) {
                    query.close()
                }
            }
        }
    }

    fun resetId(): Unit {
        this._id = 0
    }

    void save(SQLiteDatabase sQLiteDatabase) throws DatabaseBindingException {
        if (sQLiteDatabase == null) {
            throw DatabaseBindingException(getClass(), "database not opened.")
        }
        String tableName = getTableName()
        ContentValues contentValues = getContentValues()
        if (contentValues == null) {
            throw DatabaseBindingException(getClass(), "getContentValues() returned null")
        }
        
        try {
            if (this._id != 0) {
                int rowsUpdated = sQLiteDatabase.update(tableName, contentValues, "_id = ?", new String[]{Long.toString(this._id)})
                if (rowsUpdated == 0) {
                    // Row may have been deleted, try insert
                    this._id = sQLiteDatabase.insert(tableName, null, contentValues)
                    if (this._id == -1) {
                        throw SQLiteException("Insert failed after update returned 0 rows")
                    }
                }
            } else {
                this._id = sQLiteDatabase.insert(tableName, null, contentValues)
                if (this._id == -1) {
                    throw SQLiteException("Insert failed")
                }
            }
        } catch (SQLiteException e) {
            DatabaseBindingException databaseBindingException = DatabaseBindingException(getClass(), "insert or update failed")
            databaseBindingException.initCause(e)
            throw databaseBindingException
        }
    }

    /* access modifiers changed from: protected */
    void updateOrInsert(SQLiteDatabase sQLiteDatabase, String str, String[] strArr) throws DatabaseBindingException {
        if (sQLiteDatabase == null) {
            throw DatabaseBindingException(getClass(), "database not opened.")
        }
        if (str == null) {
            throw DatabaseBindingException(getClass(), "whereClause cannot be null")
        }
        
        String tableName = getTableName()
        ContentValues contentValues = getContentValues()
        if (contentValues == null) {
            throw DatabaseBindingException(getClass(), "getContentValues() returned null")
        }
        
        try {
            int rowsUpdated = sQLiteDatabase.update(tableName, contentValues, str, strArr)
            if (rowsUpdated == 0) {
                this._id = sQLiteDatabase.insert(tableName, null, contentValues)
                if (this._id == -1) {
                    throw SQLiteException("Insert failed after update returned 0 rows")
                }
            }
        } catch (SQLiteException e) {
            DatabaseBindingException databaseBindingException = DatabaseBindingException(getClass(), "insert or update failed")
            databaseBindingException.initCause(e)
            throw databaseBindingException
        }
    }

    /* access modifiers changed from: protected */
    void updateOrInsert(SQLiteStatement sQLiteStatement, SQLiteStatement sQLiteStatement2) throws DatabaseBindingException {
        if (sQLiteStatement == null || sQLiteStatement2 == null) {
            throw DatabaseBindingException(getClass(), "SQLiteStatements cannot be null")
        }
        
        try {
            bindInsertOrUpdate(sQLiteStatement)
            int rowsUpdated = sQLiteStatement.executeUpdateDelete()
            if (rowsUpdated == 0) {
                bindInsertOrUpdate(sQLiteStatement2)
                this._id = sQLiteStatement2.executeInsert()
                if (this._id == -1) {
                    throw SQLiteException("Insert failed after update returned 0 rows")
                }
            }
        } catch (SQLiteException e) {
            DatabaseBindingException databaseBindingException = DatabaseBindingException(getClass(), "insert or update failed")
            databaseBindingException.initCause(e)
            throw databaseBindingException
        }
    }
}

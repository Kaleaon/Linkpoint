package com.linkpoint.orm

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteStatement
import android.os.Parcelable
import java.nio.ByteBuffer
import java.util.UUID

abstract class DBObject : Parcelable {
    protected Long _id

    @JvmStatic
    class DatabaseBindingException : Exception() {
        public DatabaseBindingException(Class<?> cls, String str) {
            super("Failed to bind " + cls.getSimpleName() + ": " + str)
        }

        public DatabaseBindingException(String str) {
            super(str)
        }
    }

    public DBObject() {
        this._id = 0
    }

    public DBObject(Cursor cursor) {
        loadFromCursor(cursor)
    }

    public DBObject(SQLiteDatabase sQLiteDatabase, Long j) throws DatabaseBindingException {
        if (sQLiteDatabase == null) {
            throw DatabaseBindingException(getClass(), "database not opened.")
        }
        val query: Cursor = null
        try {
            query = sQLiteDatabase.query(getTableName(), getFieldNames(), "_id = ?", Array<String>{Long.toString(j)}, null, null, null)
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
    public fun UUIDfromBlob(bArr: ByteArray): UUID {
        val wrap: ByteBuffer = ByteBuffer.wrap(bArr)
        return UUID(wrap.getLong(), wrap.getLong())
    }

    /* access modifiers changed from: protected */
    public fun UUIDtoBlob(uuid: UUID): ByteArray {
        val wrap: ByteBuffer = ByteBuffer.wrap(Byte[16])
        wrap.putLong(uuid.getMostSignificantBits())
        wrap.putLong(uuid.getLeastSignificantBits())
        return wrap.array()
    }

    public abstract Unit bindInsertOrUpdate(SQLiteStatement sQLiteStatement)

    fun delete(sQLiteDatabase: SQLiteDatabase) throws DatabaseBindingException {
        if (sQLiteDatabase == null) {
            throw DatabaseBindingException(getClass(), "database not opened.")
        }
        if (this._id != 0) {
            try {
                val rowsDeleted: Int = sQLiteDatabase.delete(getTableName(), "_id = ?", Array<String>{Long.toString(this._id)})
                if (rowsDeleted > 0) {
                    this._id = 0; // Reset ID after successful deletion
                }
            } catch (SQLiteException e) {
                val databaseBindingException: DatabaseBindingException = DatabaseBindingException(getClass(), "delete failed")
                databaseBindingException.initCause(e)
                throw databaseBindingException
            }
        }
    }

    public abstract ContentValues getContentValues()

    /* access modifiers changed from: protected */
    public abstract Array<String> getFieldNames()

     public fun getId(): Long {
        return this._id
    }

    /* access modifiers changed from: protected */
    public abstract String getTableName()

    public abstract Unit loadFromCursor(Cursor cursor)

    fun reload(sQLiteDatabase: SQLiteDatabase) throws DatabaseBindingException {
        if (sQLiteDatabase == null) {
            throw DatabaseBindingException(getClass(), "database not opened.")
        } else if (this._id != 0) {
            val query: Cursor = null
            try {
                query = sQLiteDatabase.query(getTableName(), getFieldNames(), "_id = ?", Array<String>{Long.toString(this._id)}, null, null, null)
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

    fun resetId() {
        this._id = 0
    }

    fun save(sQLiteDatabase: SQLiteDatabase) throws DatabaseBindingException {
        if (sQLiteDatabase == null) {
            throw DatabaseBindingException(getClass(), "database not opened.")
        }
        val tableName: String = getTableName()
        val contentValues: ContentValues = getContentValues()
        if (contentValues == null) {
            throw DatabaseBindingException(getClass(), "getContentValues() returned null")
        }
        
        try {
            if (this._id != 0) {
                val rowsUpdated: Int = sQLiteDatabase.update(tableName, contentValues, "_id = ?", Array<String>{Long.toString(this._id)})
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
            val databaseBindingException: DatabaseBindingException = DatabaseBindingException(getClass(), "insert or update failed")
            databaseBindingException.initCause(e)
            throw databaseBindingException
        }
    }

    /* access modifiers changed from: protected */
    fun updateOrInsert(sQLiteDatabase: SQLiteDatabase, str: String, strArr: Array<String>) throws DatabaseBindingException {
        if (sQLiteDatabase == null) {
            throw DatabaseBindingException(getClass(), "database not opened.")
        }
        if (str == null) {
            throw DatabaseBindingException(getClass(), "whereClause cannot be null")
        }
        
        val tableName: String = getTableName()
        val contentValues: ContentValues = getContentValues()
        if (contentValues == null) {
            throw DatabaseBindingException(getClass(), "getContentValues() returned null")
        }
        
        try {
            val rowsUpdated: Int = sQLiteDatabase.update(tableName, contentValues, str, strArr)
            if (rowsUpdated == 0) {
                this._id = sQLiteDatabase.insert(tableName, null, contentValues)
                if (this._id == -1) {
                    throw SQLiteException("Insert failed after update returned 0 rows")
                }
            }
        } catch (SQLiteException e) {
            val databaseBindingException: DatabaseBindingException = DatabaseBindingException(getClass(), "insert or update failed")
            databaseBindingException.initCause(e)
            throw databaseBindingException
        }
    }

    /* access modifiers changed from: protected */
    fun updateOrInsert(sQLiteStatement: SQLiteStatement, sQLiteStatement2: SQLiteStatement) throws DatabaseBindingException {
        if (sQLiteStatement == null || sQLiteStatement2 == null) {
            throw DatabaseBindingException(getClass(), "SQLiteStatements cannot be null")
        }
        
        try {
            bindInsertOrUpdate(sQLiteStatement)
            val rowsUpdated: Int = sQLiteStatement.executeUpdateDelete()
            if (rowsUpdated == 0) {
                bindInsertOrUpdate(sQLiteStatement2)
                this._id = sQLiteStatement2.executeInsert()
                if (this._id == -1) {
                    throw SQLiteException("Insert failed after update returned 0 rows")
                }
            }
        } catch (SQLiteException e) {
            val databaseBindingException: DatabaseBindingException = DatabaseBindingException(getClass(), "insert or update failed")
            databaseBindingException.initCause(e)
            throw databaseBindingException
        }
    }
}

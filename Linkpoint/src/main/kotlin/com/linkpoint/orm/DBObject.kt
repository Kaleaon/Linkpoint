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
        Cursor query = null
        try {
            query = sQLiteDatabase.query(getTableName(), getFieldNames(), "_id = ?", String[]{Long.toString(j)}, null, null, null)
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
    public UUID UUIDfromBlob(Byte[] bArr) {
        ByteBuffer wrap = ByteBuffer.wrap(bArr)
        return UUID(wrap.getLong(), wrap.getLong())
    }

    /* access modifiers changed from: protected */
    public Byte[] UUIDtoBlob(UUID uuid) {
        ByteBuffer wrap = ByteBuffer.wrap(Byte[16])
        wrap.putLong(uuid.getMostSignificantBits())
        wrap.putLong(uuid.getLeastSignificantBits())
        return wrap.array()
    }

    public abstract Unit bindInsertOrUpdate(SQLiteStatement sQLiteStatement)

    public Unit delete(SQLiteDatabase sQLiteDatabase) throws DatabaseBindingException {
        if (sQLiteDatabase == null) {
            throw DatabaseBindingException(getClass(), "database not opened.")
        }
        if (this._id != 0) {
            try {
                Int rowsDeleted = sQLiteDatabase.delete(getTableName(), "_id = ?", String[]{Long.toString(this._id)})
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

    public abstract ContentValues getContentValues()

    /* access modifiers changed from: protected */
    public abstract String[] getFieldNames()

    public Long getId() {
        return this._id
    }

    /* access modifiers changed from: protected */
    public abstract String getTableName()

    public abstract Unit loadFromCursor(Cursor cursor)

    public Unit reload(SQLiteDatabase sQLiteDatabase) throws DatabaseBindingException {
        if (sQLiteDatabase == null) {
            throw DatabaseBindingException(getClass(), "database not opened.")
        } else if (this._id != 0) {
            Cursor query = null
            try {
                query = sQLiteDatabase.query(getTableName(), getFieldNames(), "_id = ?", String[]{Long.toString(this._id)}, null, null, null)
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

    public Unit resetId() {
        this._id = 0
    }

    public Unit save(SQLiteDatabase sQLiteDatabase) throws DatabaseBindingException {
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
                Int rowsUpdated = sQLiteDatabase.update(tableName, contentValues, "_id = ?", String[]{Long.toString(this._id)})
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
    public Unit updateOrInsert(SQLiteDatabase sQLiteDatabase, String str, String[] strArr) throws DatabaseBindingException {
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
            Int rowsUpdated = sQLiteDatabase.update(tableName, contentValues, str, strArr)
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
    public Unit updateOrInsert(SQLiteStatement sQLiteStatement, SQLiteStatement sQLiteStatement2) throws DatabaseBindingException {
        if (sQLiteStatement == null || sQLiteStatement2 == null) {
            throw DatabaseBindingException(getClass(), "SQLiteStatements cannot be null")
        }
        
        try {
            bindInsertOrUpdate(sQLiteStatement)
            Int rowsUpdated = sQLiteStatement.executeUpdateDelete()
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

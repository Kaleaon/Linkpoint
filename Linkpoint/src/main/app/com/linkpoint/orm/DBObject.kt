package com.linkpoint.orm

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteStatement
import android.os.Parcelable
import java.nio.ByteBuffer
import java.util.UUID

/**
 * Base class for database-backed objects with ORM functionality
 */
abstract class DBObject : Parcelable {
    
    protected var _id: Long = 0

    /**
     * Exception for database binding errors
     */
    class DatabaseBindingException : Exception {
        constructor(cls: Class<*>, message: String) : super(
            "Failed to bind ${cls.simpleName}: $message"
        )

        constructor(message: String) : super(message)
    }

    /**
     * Default constructor
     */
    constructor() {
        _id = 0
    }

    /**
     * Construct from cursor
     */
    constructor(cursor: Cursor) {
        loadFromCursor(cursor)
    }

    /**
     * Construct from database by ID
     */
    @Throws(DatabaseBindingException::class)
    constructor(database: SQLiteDatabase, id: Long) {
        if (!database.isOpen) {
            throw DatabaseBindingException(javaClass, "database not opened.")
        }
        
        var cursor: Cursor? = null
        try {
            cursor = database.query(
                getTableName(),
                getFieldNames(),
                "_id = ?",
                arrayOf(id.toString()),
                null,
                null,
                null
            )
            
            if (!cursor.moveToFirst()) {
                throw DatabaseBindingException(javaClass, "not found: _id = $id")
            }
            
            loadFromCursor(cursor)
        } finally {
            cursor?.close()
        }
    }

    /**
     * Convert UUID to blob for storage
     */
    protected fun UUIDtoBlob(uuid: UUID): ByteArray {
        val buffer = ByteBuffer.wrap(ByteArray(16))
        buffer.putLong(uuid.mostSignificantBits)
        buffer.putLong(uuid.leastSignificantBits)
        return buffer.array()
    }

    /**
     * Convert blob to UUID
     */
    protected fun UUIDfromBlob(blob: ByteArray): UUID {
        val buffer = ByteBuffer.wrap(blob)
        return UUID(buffer.long, buffer.long)
    }

    /**
     * Bind parameters for insert or update
     */
    abstract fun bindInsertOrUpdate(statement: SQLiteStatement)

    /**
     * Delete this object from database
     */
    @Throws(DatabaseBindingException::class)
    fun delete(database: SQLiteDatabase) {
        if (!database.isOpen) {
            throw DatabaseBindingException(javaClass, "database not opened.")
        }
        
        if (_id != 0L) {
            try {
                val rowsDeleted = database.delete(
                    getTableName(),
                    "_id = ?",
                    arrayOf(_id.toString())
                )
                if (rowsDeleted > 0) {
                    _id = 0
                }
            } catch (e: SQLiteException) {
                throw DatabaseBindingException(javaClass, "delete failed").apply {
                    initCause(e)
                }
            }
        }
    }

    /**
     * Get content values for this object
     */
    abstract fun getContentValues(): ContentValues

    /**
     * Get field names for queries
     */
    protected abstract fun getFieldNames(): Array<String>

    /**
     * Get object ID
     */
    fun getId(): Long = _id

    /**
     * Get table name
     */
    protected abstract fun getTableName(): String

    /**
     * Load data from cursor
     */
    abstract fun loadFromCursor(cursor: Cursor)

    /**
     * Reload object from database
     */
    @Throws(DatabaseBindingException::class)
    fun reload(database: SQLiteDatabase) {
        if (!database.isOpen) {
            throw DatabaseBindingException(javaClass, "database not opened.")
        }
        
        if (_id == 0L) return
        
        var cursor: Cursor? = null
        try {
            cursor = database.query(
                getTableName(),
                getFieldNames(),
                "_id = ?",
                arrayOf(_id.toString()),
                null,
                null,
                null
            )
            
            if (cursor.moveToFirst()) {
                loadFromCursor(cursor)
            }
        } finally {
            cursor?.close()
        }
    }

    /**
     * Reset object ID
     */
    fun resetId() {
        _id = 0
    }

    /**
     * Save object to database (insert or update)
     */
    @Throws(DatabaseBindingException::class)
    fun save(database: SQLiteDatabase) {
        if (!database.isOpen) {
            throw DatabaseBindingException(javaClass, "database not opened.")
        }
        
        val tableName = getTableName()
        val contentValues = getContentValues()
            ?: throw DatabaseBindingException(javaClass, "getContentValues() returned null")
        
        try {
            if (_id != 0L) {
                val rowsUpdated = database.update(
                    tableName,
                    contentValues,
                    "_id = ?",
                    arrayOf(_id.toString())
                )
                
                if (rowsUpdated == 0) {
                    // Row may have been deleted, try insert
                    _id = database.insert(tableName, null, contentValues)
                    if (_id == -1L) {
                        throw SQLiteException("Insert failed after update returned 0 rows")
                    }
                }
            } else {
                _id = database.insert(tableName, null, contentValues)
                if (_id == -1L) {
                    throw SQLiteException("Insert failed")
                }
            }
        } catch (e: SQLiteException) {
            throw DatabaseBindingException(javaClass, "insert or update failed").apply {
                initCause(e)
            }
        }
    }

    /**
     * Update or insert using where clause
     */
    @Throws(DatabaseBindingException::class)
    protected fun updateOrInsert(
        database: SQLiteDatabase,
        whereClause: String?,
        whereArgs: Array<String>?
    ) {
        if (!database.isOpen) {
            throw DatabaseBindingException(javaClass, "database not opened.")
        }
        
        requireNotNull(whereClause) {
            "whereClause cannot be null"
        }
        
        val tableName = getTableName()
        val contentValues = getContentValues()
            ?: throw DatabaseBindingException(javaClass, "getContentValues() returned null")
        
        try {
            val rowsUpdated = database.update(tableName, contentValues, whereClause, whereArgs)
            
            if (rowsUpdated == 0) {
                _id = database.insert(tableName, null, contentValues)
                if (_id == -1L) {
                    throw SQLiteException("Insert failed after update returned 0 rows")
                }
            }
        } catch (e: SQLiteException) {
            throw DatabaseBindingException(javaClass, "insert or update failed").apply {
                initCause(e)
            }
        }
    }

    /**
     * Update or insert using prepared statements
     */
    @Throws(DatabaseBindingException::class)
    protected fun updateOrInsert(
        updateStatement: SQLiteStatement,
        insertStatement: SQLiteStatement
    ) {
        try {
            bindInsertOrUpdate(updateStatement)
            val rowsUpdated = updateStatement.executeUpdateDelete()
            
            if (rowsUpdated == 0) {
                bindInsertOrUpdate(insertStatement)
                _id = insertStatement.executeInsert()
                if (_id == -1L) {
                    throw SQLiteException("Insert failed after update returned 0 rows")
                }
            }
        } catch (e: SQLiteException) {
            throw DatabaseBindingException(javaClass, "insert or update failed").apply {
                initCause(e)
            }
        }
    }
    
    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<InventoryQuery> {
            override fun createFromParcel(parcel: Parcel): InventoryQuery {
                val bundle = parcel.readBundle(javaClass.classLoader)!!
                return InventoryQuery.create(
                    UUIDPool.getUUID(bundle.getString("folderId")),
                    bundle.getString("containsString"),
                    bundle.getBoolean("includeFolders"),
                    bundle.getBoolean("includeItems"),
                    bundle.getBoolean("newestFirst"),
                    bundle.getInt("assetType", -1)
                )
            }

            override fun newArray(size: Int): Array<InventoryQuery?> {
                return arrayOfNulls(size)
            }
        }
    }
}

package com.lumiyaviewer.lumiya.slproto.inventory

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.orm.DBHandle
import com.lumiyaviewer.lumiya.orm.DBHandleCache
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.atomic.AtomicReference

class SLInventoryOpenHelper : DBHandleCache.DBOpenHelper {

    companion object {
        private const val DB_VERSION = 21
        private val instance = AtomicReference<SLInventoryOpenHelper>()

        fun getInstance(): SLInventoryOpenHelper {
            if (instance.get() == null) {
                instance.compareAndSet(null, SLInventoryOpenHelper())
            }
            return instance.get()!!
        }
    }

    private fun enableWriteAheadLogging(db: SQLiteDatabase?) {
        if (db == null) {
            Debug.Log("Cannot enable WAL on null database")
            return
        }

        try {
            val method = db.javaClass.getMethod("enableWriteAheadLogging")
            method.invoke(db)
            Debug.Log("Write-ahead logging enabled successfully.")
        } catch (e: NoSuchMethodException) {
            Debug.Log("Write-ahead logging not supported on this Android version.")
        } catch (e: Exception) {
            when (e) {
                is IllegalArgumentException,
                is IllegalAccessException -> Debug.Log("Write-ahead logging not accessible: ${e.message}")
                is InvocationTargetException -> Debug.Log("Failed to enable write-ahead logging: ${e.cause?.message ?: e.message}")
                else -> Debug.Log("Unexpected error enabling write-ahead logging: ${e.message}")
            }
        }
    }

    @Throws(SQLiteException::class)
    private fun initTables(db: SQLiteDatabase): Boolean {
        db.execSQL("CREATE TABLE IF NOT EXISTS DBVersion (Version INTEGER);")
        var query: Cursor? = null
        var isNewDb = false
        var needsUpgrade = false

        try {
            query = db.query("DBVersion", arrayOf("Version"), null, null, null, null, null)
            if (!query.moveToFirst()) {
                isNewDb = true
                needsUpgrade = true
            } else if (query.getInt(0) != DB_VERSION) {
                isNewDb = false
                needsUpgrade = true
            } else {
                isNewDb = false
                needsUpgrade = false
            }
        } finally {
            query?.close()
        }

        if (needsUpgrade) {
            Debug.Log("Database needs upgrade.")
            try {
                // Use transaction for atomic database operations
                db.beginTransaction()
                try {
                    // Assuming SLInventoryEntry has a static/companion method getCreateTableStatements
                    // If SLInventoryEntry is not available or doesn't have this, this will need adjustment.
                    // Based on context, it seems SLInventoryEntry is a key class.
                     val statements = SLInventoryEntry.getCreateTableStatements()
                     for (createStatement in statements) {
                        Debug.Log("Inventory init: $createStatement")
                        db.execSQL(createStatement)
                    }
                    
                    val contentValues = ContentValues()
                    contentValues.put("Version", DB_VERSION)
                    if (isNewDb) {
                        db.insert("DBVersion", null, contentValues)
                    } else {
                        db.update("DBVersion", contentValues, null, null)
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                Debug.Log("Upgraded database to version $DB_VERSION")
                return true
            } catch (e: Exception) {
                val sqliteException = SQLiteException("Database initialization failed: ${e.message}")
                sqliteException.initCause(e)
                throw sqliteException
            }
        } else {
            Debug.Log("Database does not need upgrade.")
            return false
        }
    }

    @Synchronized
    fun openDB(filename: String?): DBHandle? {
        if (filename.isNullOrEmpty()) {
            Debug.Log("Database filename cannot be null or empty")
            return null
        }

        return try {
            Debug.Log("Opening inventory DB '$filename'")
            DBHandleCache.getInstance().OpenDB(filename, this)
        } catch (e: SQLiteException) {
            Debug.Log("Failed to open inventory database: $filename - ${e.message}")
            null
        }
    }

    @Throws(SQLiteException::class)
    override fun openOrCreateDatabase(path: String): SQLiteDatabase {
        if (path.trim().isEmpty()) {
            throw SQLiteException("Database path cannot be null or empty")
        }

        // openOrCreateDatabase is a static method on SQLiteDatabase, or we can use the context based one.
        // Here it seems to imply the static one which takes a CursorFactory (null).
        var db = SQLiteDatabase.openOrCreateDatabase(path, null)
        
        Debug.Log("DB file '$path' opened")
        enableWriteAheadLogging(db)

        if (initTables(db)) {
            Debug.Log("Reopening DB file '$path' after initialization")
            db.close()

            db = SQLiteDatabase.openOrCreateDatabase(path, null)
            enableWriteAheadLogging(db)
        }

        return db
    }
}

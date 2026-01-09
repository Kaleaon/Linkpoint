package com.linkpoint.slproto.inventory

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import com.linkpoint.Debug
import com.linkpoint.orm.DBHandle
import com.linkpoint.orm.DBHandleCache
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class SLInventoryOpenHelper : DBHandleCache.DBOpenHelper {
    private val DB_VERSION: Int = 21

    private class InstanceHolder {
        /* access modifiers changed from: private */
        SLInventoryOpenHelper Instance = SLInventoryOpenHelper()

        private InstanceHolder() {
        }
    }

    private Unit enableWriteAheadLogging(SQLiteDatabase sQLiteDatabase) {
        if (sQLiteDatabase == null) {
            Debug.Printf("Cannot enable WAL on null database")
            return
        }
        
        try {
            Method method = sQLiteDatabase.getClass().getMethod("enableWriteAheadLogging")
            if (method != null) {
                method.invoke(sQLiteDatabase)
                Debug.Printf("Write-ahead logging enabled successfully.")
            }
        } catch (NoSuchMethodException e) {
            Debug.Printf("Write-ahead logging not supported on this Android version.")
        } catch (IllegalArgumentException | IllegalAccessException e) {
            Debug.Printf("Write-ahead logging not accessible: %s", e.getMessage())
        } catch (InvocationTargetException e) {
            Debug.Printf("Failed to enable write-ahead logging: %s", e.getCause() != null ? e.getCause().getMessage() : e.getMessage())
        } catch (Exception e) {
            Debug.Printf("Unexpected error enabling write-ahead logging: %s", e.getMessage())
        }
    }

    fun getInstance(): SLInventoryOpenHelper {
        return InstanceHolder.Instance
    }

    private Boolean initTables(SQLiteDatabase sQLiteDatabase) throws SQLiteException {
        sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS DBVersion (Version INTEGER);")
        Cursor query = null
        Boolean isNewDb = false
        Boolean needsUpgrade = false
        
        try {
            query = sQLiteDatabase.query("DBVersion", Array<String>{"Version"}, null, null, null, null, null)
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
            if (query != null) {
                query.close()
            }
        }
        
        if (needsUpgrade) {
            Debug.Printf("Database needs upgrade.", Object[0])
            try {
                // Use transaction for atomic database operations
                sQLiteDatabase.beginTransaction()
                try {
                    for (String createStatement : SLInventoryEntry.getCreateTableStatements()) {
                        Debug.Printf("Inventory init: %s", createStatement)
                        sQLiteDatabase.execSQL(createStatement)
                    }
                    ContentValues contentValues = ContentValues()
                    contentValues.put("Version", DB_VERSION)
                    if (isNewDb) {
                        sQLiteDatabase.insert("DBVersion", null, contentValues)
                    } else {
                        sQLiteDatabase.update("DBVersion", contentValues, null, null)
                    }
                    sQLiteDatabase.setTransactionSuccessful()
                } finally {
                    sQLiteDatabase.endTransaction()
                }
                Debug.Printf("Upgraded database to version %d", DB_VERSION)
                return true
            } catch (Exception e) {
                SQLiteException sQLiteException = SQLiteException("Database initialization failed: " + e.getMessage())
                sQLiteException.initCause(e)
                throw sQLiteException
            }
        } else {
            Debug.Printf("Database does not need upgrade.", Object[0])
            return false
        }
    }

    synchronized DBHandle openDB(String str) {
        if (str == null || str.trim().isEmpty()) {
            Debug.Warning("Database filename cannot be null or empty")
            return null
        }
        
        try {
            Debug.Printf("Opening inventory DB '%s'", str)
            return DBHandleCache.getInstance().OpenDB(str, this)
        } catch (SQLiteException e) {
            Debug.Warning("Failed to open inventory database: " + str, e)
            return null
        }
    }

    @Throws(SQLiteException::class)

    fun openOrCreateDatabase(String str): SQLiteDatabase {
        if (str == null || str.trim().isEmpty()) {
            throw SQLiteException("Database path cannot be null or empty")
        }
        
        SQLiteDatabase openOrCreateDatabase = SQLiteDatabase.openOrCreateDatabase(str, null)
        if (openOrCreateDatabase == null) {
            throw SQLiteException("Failed to open database: " + str)
        }
        
        Debug.Printf("DB file '%s' opened", str)
        enableWriteAheadLogging(openOrCreateDatabase)
        
        if (initTables(openOrCreateDatabase)) {
            Debug.Printf("Reopening DB file '%s' after initialization", str)
            openOrCreateDatabase.close()
            
            openOrCreateDatabase = SQLiteDatabase.openOrCreateDatabase(str, null)
            if (openOrCreateDatabase == null) {
                throw SQLiteException("Failed to reopen database after initialization: " + str)
            }
            enableWriteAheadLogging(openOrCreateDatabase)
        }
        
        return openOrCreateDatabase
    }
}

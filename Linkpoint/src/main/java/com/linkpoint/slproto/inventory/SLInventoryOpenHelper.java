package com.linkpoint.slproto.inventory;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import com.linkpoint.Debug;
import com.linkpoint.orm.DBHandle;
import com.linkpoint.orm.DBHandleCache;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SLInventoryOpenHelper implements DBHandleCache.DBOpenHelper {
    private static final int DB_VERSION = 21;

    private static class InstanceHolder {
        /* access modifiers changed from: private */
        public static final SLInventoryOpenHelper Instance = new SLInventoryOpenHelper();

        private InstanceHolder() {
        }
    }

    private void enableWriteAheadLogging(SQLiteDatabase sQLiteDatabase) {
        if (sQLiteDatabase == null) {
            Debug.Printf("Cannot enable WAL on null database");
            return;
        }
        
        try {
            Method method = sQLiteDatabase.getClass().getMethod("enableWriteAheadLogging");
            if (method != null) {
                method.invoke(sQLiteDatabase);
                Debug.Printf("Write-ahead logging enabled successfully.");
            }
        } catch (NoSuchMethodException e) {
            Debug.Printf("Write-ahead logging not supported on this Android version.");
        } catch (IllegalArgumentException | IllegalAccessException e) {
            Debug.Printf("Write-ahead logging not accessible: %s", e.getMessage());
        } catch (InvocationTargetException e) {
            Debug.Printf("Failed to enable write-ahead logging: %s", e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
        } catch (Exception e) {
            Debug.Printf("Unexpected error enabling write-ahead logging: %s", e.getMessage());
        }
    }

    public static SLInventoryOpenHelper getInstance() {
        return InstanceHolder.Instance;
    }

    private boolean initTables(SQLiteDatabase sQLiteDatabase) throws SQLiteException {
        sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS DBVersion (Version INTEGER);");
        Cursor query = null;
        boolean isNewDb = false;
        boolean needsUpgrade = false;
        
        try {
            query = sQLiteDatabase.query("DBVersion", new String[]{"Version"}, null, null, null, null, null);
            if (!query.moveToFirst()) {
                isNewDb = true;
                needsUpgrade = true;
            } else if (query.getInt(0) != DB_VERSION) {
                isNewDb = false;
                needsUpgrade = true;
            } else {
                isNewDb = false;
                needsUpgrade = false;
            }
        } finally {
            if (query != null) {
                query.close();
            }
        }
        
        if (needsUpgrade) {
            Debug.Printf("Database needs upgrade.", new Object[0]);
            try {
                // Use transaction for atomic database operations
                sQLiteDatabase.beginTransaction();
                try {
                    for (String createStatement : SLInventoryEntry.getCreateTableStatements()) {
                        Debug.Printf("Inventory init: %s", createStatement);
                        sQLiteDatabase.execSQL(createStatement);
                    }
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("Version", DB_VERSION);
                    if (isNewDb) {
                        sQLiteDatabase.insert("DBVersion", null, contentValues);
                    } else {
                        sQLiteDatabase.update("DBVersion", contentValues, null, null);
                    }
                    sQLiteDatabase.setTransactionSuccessful();
                } finally {
                    sQLiteDatabase.endTransaction();
                }
                Debug.Printf("Upgraded database to version %d", DB_VERSION);
                return true;
            } catch (Exception e) {
                SQLiteException sQLiteException = new SQLiteException("Database initialization failed: " + e.getMessage());
                sQLiteException.initCause(e);
                throw sQLiteException;
            }
        } else {
            Debug.Printf("Database does not need upgrade.", new Object[0]);
            return false;
        }
    }

    public synchronized DBHandle openDB(String str) {
        if (str == null || str.trim().isEmpty()) {
            Debug.Warning("Database filename cannot be null or empty");
            return null;
        }
        
        try {
            Debug.Printf("Opening inventory DB '%s'", str);
            return DBHandleCache.getInstance().OpenDB(str, this);
        } catch (SQLiteException e) {
            Debug.Warning("Failed to open inventory database: " + str, e);
            return null;
        }
    }

    public SQLiteDatabase openOrCreateDatabase(String str) throws SQLiteException {
        if (str == null || str.trim().isEmpty()) {
            throw new SQLiteException("Database path cannot be null or empty");
        }
        
        SQLiteDatabase openOrCreateDatabase = SQLiteDatabase.openOrCreateDatabase(str, null);
        if (openOrCreateDatabase == null) {
            throw new SQLiteException("Failed to open database: " + str);
        }
        
        Debug.Printf("DB file '%s' opened", str);
        enableWriteAheadLogging(openOrCreateDatabase);
        
        if (initTables(openOrCreateDatabase)) {
            Debug.Printf("Reopening DB file '%s' after initialization", str);
            openOrCreateDatabase.close();
            
            openOrCreateDatabase = SQLiteDatabase.openOrCreateDatabase(str, null);
            if (openOrCreateDatabase == null) {
                throw new SQLiteException("Failed to reopen database after initialization: " + str);
            }
            enableWriteAheadLogging(openOrCreateDatabase);
        }
        
        return openOrCreateDatabase;
    }
}

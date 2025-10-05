package com.linkpoint.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import com.linkpoint.Debug
import com.linkpoint.dao.ChatMessageDao.Properties
import com.linkpoint.dao.DaoMaster.DevOpenHelper

class DBOpenHelper : DevOpenHelper() {
    public DBOpenHelper(Context context, String str, CursorFactory cursorFactory) {
        super(context, str, cursorFactory)
    }

    private Boolean tryUpgradeTo71(SQLiteDatabase sQLiteDatabase, Int fromVersion) {
        if (fromVersion != 70) {
            return false
        }
        try {
            Debug.Printf("Upgrading to database version 71 from %d", Integer.valueOf(fromVersion))
            
            // Start transaction for atomic upgrade
            sQLiteDatabase.beginTransaction()
            try {
                sQLiteDatabase.execSQL("ALTER TABLE CHAT_MESSAGE ADD COLUMN " + Properties.SyncedToGoogleDrive.columnName + " INTEGER DEFAULT 0 NOT NULL;")
                sQLiteDatabase.execSQL("CREATE INDEX IDX_CHAT_MESSAGE__id_SYNCED_TO_GOOGLE_DRIVE ON CHAT_MESSAGE (_id,SYNCED_TO_GOOGLE_DRIVE);")
                sQLiteDatabase.setTransactionSuccessful()
            } finally {
                sQLiteDatabase.endTransaction()
            }
            
            Debug.Printf("Successfully upgraded database to version 71")
            return true
        } catch (Throwable e) {
            Debug.Warning("Database upgrade to version 71 failed", e)
            return false
        }
    }

    public Unit onDowngrade(SQLiteDatabase sQLiteDatabase, Int oldVersion, Int newVersion) {
        Debug.Printf("Database downgrade requested from %d to %d", oldVersion, newVersion)
        // Call onUpgrade which will recreate the database if needed
        super.onUpgrade(sQLiteDatabase, oldVersion, newVersion)
    }

    public Unit onUpgrade(SQLiteDatabase sQLiteDatabase, Int oldVersion, Int newVersion) {
        Debug.Printf("Database upgrade requested from %d to %d", oldVersion, newVersion)
        
        Boolean upgradeSuccessful = false
        if (newVersion == 71) {
            upgradeSuccessful = tryUpgradeTo71(sQLiteDatabase, oldVersion)
        }
        
        if (upgradeSuccessful) {
            Debug.Printf("Database upgrade completed successfully", Object[0])
        } else {
            Debug.Printf("Database upgrade failed or not supported, recreating database", Object[0])
            super.onUpgrade(sQLiteDatabase, oldVersion, newVersion)
        }
    }
}

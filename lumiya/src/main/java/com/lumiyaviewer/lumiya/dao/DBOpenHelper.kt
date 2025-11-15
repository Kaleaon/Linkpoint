package com.lumiyaviewer.lumiya.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.dao.ChatMessageDao.Properties
import com.lumiyaviewer.lumiya.dao.DaoMaster.DevOpenHelper

class DBOpenHelper : DevOpenHelper {
    constructor(context: Context, str: String, cursorFactory: CursorFactory) {
        super(context, str, cursorFactory)
    }

    private fun tryUpgradeTo71(sQLiteDatabase: SQLiteDatabase, fromVersion: Int): Boolean {
        if (fromVersion != 70) {
            return false
        }
        try {
            Debug.Printf("Upgrading to database version 71 from %d", Int.valueOf(fromVersion))
            
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

    fun onDowngrade(sQLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int): Unit {
        Debug.Printf("Database downgrade requested from %d to %d", oldVersion, newVersion)
        // Call onUpgrade which will recreate the database if needed
        super.onUpgrade(sQLiteDatabase, oldVersion, newVersion)
    }

    fun onUpgrade(sQLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int): Unit {
        Debug.Printf("Database upgrade requested from %d to %d", oldVersion, newVersion)
        
        Boolean upgradeSuccessful = false
        if (newVersion == 71) {
            upgradeSuccessful = tryUpgradeTo71(sQLiteDatabase, oldVersion)
        }
        
        if (upgradeSuccessful) {
            Debug.Printf("Database upgrade completed successfully", Any[0])
        } else {
            Debug.Printf("Database upgrade failed or not supported, recreating database", Any[0])
            super.onUpgrade(sQLiteDatabase, oldVersion, newVersion)
        }
    }
}

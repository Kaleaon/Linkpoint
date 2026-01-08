package com.lumiyaviewer.lumiya.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.dao.ChatMessageDao.Companion.Properties

class DBOpenHelper(
    context: Context,
    name: String?,
    cursorFactory: CursorFactory?
) : DaoMaster.DevOpenHelper(context, name, cursorFactory) {

    private fun tryUpgradeTo71(db: SQLiteDatabase, fromVersion: Int): Boolean {
        if (fromVersion != 70) {
            return false
        }
        try {
            Debug.Printf("Upgrading to database version 71 from $fromVersion")
            
            db.beginTransaction()
            try {
                db.execSQL("ALTER TABLE CHAT_MESSAGE ADD COLUMN " + Properties.SyncedToGoogleDrive.columnName + " INTEGER DEFAULT 0 NOT NULL;")
                db.execSQL("CREATE INDEX IDX_CHAT_MESSAGE__id_SYNCED_TO_GOOGLE_DRIVE ON CHAT_MESSAGE (_id,SYNCED_TO_GOOGLE_DRIVE);")
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
            
            Debug.Printf("Successfully upgraded database to version 71")
            return true
        } catch (e: Throwable) {
            Debug.Warning(e) // Assuming Debug.Warning takes Throwable
            return false
        }
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Debug.Printf("Database downgrade requested from $oldVersion to $newVersion")
        super.onUpgrade(db, oldVersion, newVersion)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Debug.Printf("Database upgrade requested from $oldVersion to $newVersion")
        
        var upgradeSuccessful = false
        if (newVersion == 71) {
            upgradeSuccessful = tryUpgradeTo71(db, oldVersion)
        }
        
        if (upgradeSuccessful) {
            Debug.Printf("Database upgrade completed successfully")
        } else {
            Debug.Printf("Database upgrade failed or not supported, recreating database")
            super.onUpgrade(db, oldVersion, newVersion)
        }
    }
}

package com.lumiyaviewer.lumiya.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.dao.ChatMessageDao.Properties;
import com.lumiyaviewer.lumiya.dao.DaoMaster.DevOpenHelper;

public class DBOpenHelper extends DevOpenHelper {
    public DBOpenHelper(Context context, String str, CursorFactory cursorFactory) {
        super(context, str, cursorFactory);
    }

    private boolean tryUpgradeTo71(SQLiteDatabase sQLiteDatabase, int fromVersion) {
        if (fromVersion != 70) {
            return false;
        }
        try {
            Debug.Printf("Upgrading to database version 71 from %d", Integer.valueOf(fromVersion));
            
            // Start transaction for atomic upgrade
            sQLiteDatabase.beginTransaction();
            try {
                sQLiteDatabase.execSQL("ALTER TABLE CHAT_MESSAGE ADD COLUMN " + Properties.SyncedToGoogleDrive.columnName + " INTEGER DEFAULT 0 NOT NULL;");
                sQLiteDatabase.execSQL("CREATE INDEX IDX_CHAT_MESSAGE__id_SYNCED_TO_GOOGLE_DRIVE ON CHAT_MESSAGE (_id,SYNCED_TO_GOOGLE_DRIVE);");
                sQLiteDatabase.setTransactionSuccessful();
            } finally {
                sQLiteDatabase.endTransaction();
            }
            
            Debug.Printf("Successfully upgraded database to version 71");
            return true;
        } catch (Throwable e) {
            Debug.Warning("Database upgrade to version 71 failed", e);
            return false;
        }
    }

    public void onDowngrade(SQLiteDatabase sQLiteDatabase, int oldVersion, int newVersion) {
        Debug.Printf("Database downgrade requested from %d to %d", oldVersion, newVersion);
        // Call onUpgrade which will recreate the database if needed
        super.onUpgrade(sQLiteDatabase, oldVersion, newVersion);
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int oldVersion, int newVersion) {
        Debug.Printf("Database upgrade requested from %d to %d", oldVersion, newVersion);
        
        boolean upgradeSuccessful = false;
        if (newVersion == 71) {
            upgradeSuccessful = tryUpgradeTo71(sQLiteDatabase, oldVersion);
        }
        
        if (upgradeSuccessful) {
            Debug.Printf("Database upgrade completed successfully", new Object[0]);
        } else {
            Debug.Printf("Database upgrade failed or not supported, recreating database", new Object[0]);
            super.onUpgrade(sQLiteDatabase, oldVersion, newVersion);
        }
    }
}

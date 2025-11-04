// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.dao;

import android.database.sqlite.SQLiteException;
import com.lumiyaviewer.lumiya.Debug;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase$CursorFactory;
import android.content.Context;

public class DBOpenHelper extends DevOpenHelper
{
    public DBOpenHelper(final Context context, final String s, final SQLiteDatabase$CursorFactory sqLiteDatabase$CursorFactory) {
        super(context, s, sqLiteDatabase$CursorFactory);
    }
    
    private boolean tryUpgradeTo71(final SQLiteDatabase sqLiteDatabase, final int i) {
        if (i == 70) {
            try {
                Debug.Printf("Upgrading to database version 71 from %d", i);
                sqLiteDatabase.execSQL("ALTER TABLE CHAT_MESSAGE ADD COLUMN " + ChatMessageDao.Properties.SyncedToGoogleDrive.columnName + " INTEGER DEFAULT 0 NOT NULL;");
                sqLiteDatabase.execSQL("CREATE INDEX IDX_CHAT_MESSAGE__id_SYNCED_TO_GOOGLE_DRIVE ON CHAT_MESSAGE (_id,SYNCED_TO_GOOGLE_DRIVE);");
                return true;
            }
            catch (final SQLiteException ex) {
                Debug.Warning((Throwable)ex);
                return false;
            }
        }
        return false;
    }
    
    public void onDowngrade(final SQLiteDatabase sqLiteDatabase, final int n, final int n2) {
        super.onUpgrade(sqLiteDatabase, n, n2);
    }
    
    @Override
    public void onUpgrade(final SQLiteDatabase sqLiteDatabase, final int n, final int n2) {
        if (n2 != 71 || !this.tryUpgradeTo71(sqLiteDatabase, n)) {
            Debug.Printf("Database upgrade failed, recreating.", new Object[0]);
            super.onUpgrade(sqLiteDatabase, n, n2);
        }
        else {
            Debug.Printf("Database upgrade success.", new Object[0]);
        }
    }
}

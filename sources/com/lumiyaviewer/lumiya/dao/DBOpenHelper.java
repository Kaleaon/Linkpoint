package com.lumiyaviewer.lumiya.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.dao.ChatMessageDao;
import com.lumiyaviewer.lumiya.dao.DaoMaster;

public class DBOpenHelper extends DaoMaster.DevOpenHelper {
    public DBOpenHelper(Context context, String str, SQLiteDatabase.CursorFactory cursorFactory) {
        super(context, str, cursorFactory);
    }

    private boolean tryUpgradeTo71(SQLiteDatabase sQLiteDatabase, int i) {
        if (i != 70) {
            return false;
        }
        try {
            Debug.Printf("Upgrading to database version 71 from %d", Integer.valueOf(i));
            sQLiteDatabase.execSQL("ALTER TABLE CHAT_MESSAGE ADD COLUMN " + ChatMessageDao.Properties.SyncedToGoogleDrive.columnName + " INTEGER DEFAULT 0 NOT NULL;");
            sQLiteDatabase.execSQL("CREATE INDEX IDX_CHAT_MESSAGE__id_SYNCED_TO_GOOGLE_DRIVE ON CHAT_MESSAGE (_id,SYNCED_TO_GOOGLE_DRIVE);");
            return true;
        } catch (SQLiteException e) {
            Debug.Warning(e);
            return false;
        }
    }

    public void onDowngrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        super.onUpgrade(sQLiteDatabase, i, i2);
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        if (!(i2 == 71 ? tryUpgradeTo71(sQLiteDatabase, i) : false)) {
            Debug.Printf("Database upgrade failed, recreating.", new Object[0]);
            super.onUpgrade(sQLiteDatabase, i, i2);
            return;
        }
        Debug.Printf("Database upgrade success.", new Object[0]);
    }
}

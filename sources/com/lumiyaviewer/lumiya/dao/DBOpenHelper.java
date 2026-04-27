// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import com.lumiyaviewer.lumiya.Debug;
import de.greenrobot.dao.Property;

public class DBOpenHelper extends DaoMaster.DevOpenHelper
{

    public DBOpenHelper(Context context, String s, android.database.sqlite.SQLiteDatabase.CursorFactory cursorfactory)
    {
        super(context, s, cursorfactory);
    }

    private boolean tryUpgradeTo71(SQLiteDatabase sqlitedatabase, int i)
    {
        if (i == 70)
        {
            try
            {
                Debug.Printf("Upgrading to database version 71 from %d", new Object[] {
                    Integer.valueOf(i)
                });
                sqlitedatabase.execSQL((new StringBuilder()).append("ALTER TABLE CHAT_MESSAGE ADD COLUMN ").append(ChatMessageDao.Properties.SyncedToGoogleDrive.columnName).append(" INTEGER DEFAULT 0 NOT NULL;").toString());
                sqlitedatabase.execSQL("CREATE INDEX IDX_CHAT_MESSAGE__id_SYNCED_TO_GOOGLE_DRIVE ON CHAT_MESSAGE (_id,SYNCED_TO_GOOGLE_DRIVE);");
            }
            // Misplaced declaration of an exception variable
            catch (SQLiteDatabase sqlitedatabase)
            {
                Debug.Warning(sqlitedatabase);
                return false;
            }
            return true;
        } else
        {
            return false;
        }
    }

    public void onDowngrade(SQLiteDatabase sqlitedatabase, int i, int j)
    {
        super.onUpgrade(sqlitedatabase, i, j);
    }

    public void onUpgrade(SQLiteDatabase sqlitedatabase, int i, int j)
    {
        boolean flag;
        if (j == 71)
        {
            flag = tryUpgradeTo71(sqlitedatabase, i);
        } else
        {
            flag = false;
        }
        if (!flag)
        {
            Debug.Printf("Database upgrade failed, recreating.", new Object[0]);
            super.onUpgrade(sqlitedatabase, i, j);
            return;
        } else
        {
            Debug.Printf("Database upgrade success.", new Object[0]);
            return;
        }
    }
}

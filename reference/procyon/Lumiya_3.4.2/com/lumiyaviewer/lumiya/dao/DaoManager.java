// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.dao;

import android.database.sqlite.SQLiteDatabase$CursorFactory;
import java.io.File;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.LumiyaApp;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map;

public class DaoManager
{
    private static final Object lock;
    private static final Map<UUID, DaoSession> userDaoSessions;
    
    static {
        lock = new Object();
        userDaoSessions = new HashMap<UUID, DaoSession>();
    }
    
    @Nullable
    public static DaoSession getUserDaoSession(@Nullable final UUID uuid) {
        if (uuid == null) {
            return null;
        }
        synchronized (DaoManager.lock) {
            DaoSession session;
            if ((session = DaoManager.userDaoSessions.get(uuid)) == null) {
                session = new DaoMaster(new DBOpenHelper(LumiyaApp.getContext(), new File(GlobalOptions.getInstance().getCacheDir("database"), "userdb-" + uuid.toString() + ".db").getAbsolutePath(), null).getWritableDatabase()).newSession();
                DaoManager.userDaoSessions.put(uuid, session);
            }
            return session;
        }
    }
}

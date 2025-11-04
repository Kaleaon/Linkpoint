package com.lumiyaviewer.lumiya.dao;

import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.LumiyaApp;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class DaoManager {
    private static final Object lock = new Object();
    private static final Map<UUID, DaoSession> userDaoSessions = new HashMap();

    @Nullable
    public static DaoSession getUserDaoSession(@Nullable UUID uuid) {
        DaoSession daoSession;
        if (uuid == null) {
            return null;
        }
        synchronized (lock) {
            daoSession = userDaoSessions.get(uuid);
            if (daoSession == null) {
                daoSession = new DaoMaster(new DBOpenHelper(LumiyaApp.getContext(), new File(GlobalOptions.getInstance().getCacheDir("database"), "userdb-" + uuid.toString() + ".db").getAbsolutePath(), null).getWritableDatabase()).newSession();
                userDaoSessions.put(uuid, daoSession);
            }
        }
        return daoSession;
    }
}

// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;

import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.LumiyaApp;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.dao:
//            DaoSession, DaoMaster, DBOpenHelper

public class DaoManager
{

    private static final Object lock = new Object();
    private static final Map userDaoSessions = new HashMap();

    public DaoManager()
    {
    }

    public static DaoSession getUserDaoSession(UUID uuid)
    {
        if (uuid == null)
        {
            return null;
        }
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        DaoSession daosession1 = (DaoSession)userDaoSessions.get(uuid);
        DaoSession daosession;
        daosession = daosession1;
        if (daosession1 != null)
        {
            break MISSING_BLOCK_LABEL_112;
        }
        daosession = (new DaoMaster((new DBOpenHelper(LumiyaApp.getContext(), (new File(GlobalOptions.getInstance().getCacheDir("database"), (new StringBuilder()).append("userdb-").append(uuid.toString()).append(".db").toString())).getAbsolutePath(), null)).getWritableDatabase())).newSession();
        userDaoSessions.put(uuid, daosession);
        obj;
        JVM INSTR monitorexit ;
        return daosession;
        uuid;
        throw uuid;
    }

}

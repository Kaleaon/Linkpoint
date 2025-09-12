// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto:
//            SLGridConnection

public class GridConnectionManager
{

    private static final Map connections = new WeakHashMap();
    private static final Object lock = new Object();

    public GridConnectionManager()
    {
    }

    public static SLGridConnection getConnection(UUID uuid)
    {
        if (uuid == null)
        {
            return null;
        }
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        uuid = (SLGridConnection)connections.get(uuid);
        obj;
        JVM INSTR monitorexit ;
        return uuid;
        uuid;
        throw uuid;
    }

    public static void removeConnection(UUID uuid, SLGridConnection slgridconnection)
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        if ((SLGridConnection)connections.get(uuid) == slgridconnection)
        {
            connections.remove(uuid);
        }
        obj;
        JVM INSTR monitorexit ;
        return;
        uuid;
        throw uuid;
    }

    public static void setConnection(UUID uuid, SLGridConnection slgridconnection)
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        connections.put(uuid, slgridconnection);
        obj;
        JVM INSTR monitorexit ;
        return;
        uuid;
        throw uuid;
    }

}

// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users:
//            ChatterNameRetriever, ChatterID

public class MultipleChatterNameRetriever
    implements ChatterNameRetriever.OnChatterNameUpdated
{
    public static interface OnChatterNameUpdated
    {

        public abstract void onChatterNameUpdated(MultipleChatterNameRetriever multiplechatternameretriever);
    }


    private final UUID agentUUID;
    private final Executor executor;
    private final WeakReference listener;
    private final Object lock = new Object();
    private final Map retrievers = new HashMap();

    public MultipleChatterNameRetriever(UUID uuid, OnChatterNameUpdated onchatternameupdated, Executor executor1)
    {
        agentUUID = uuid;
        listener = new WeakReference(onchatternameupdated);
        executor = executor1;
    }

    public String addChatter(UUID uuid)
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        ChatterNameRetriever chatternameretriever = (ChatterNameRetriever)retrievers.get(uuid);
        obj;
        JVM INSTR monitorexit ;
        if (chatternameretriever != null)
        {
            return chatternameretriever.getResolvedName();
        }
        break MISSING_BLOCK_LABEL_37;
        uuid;
        throw uuid;
        chatternameretriever = new ChatterNameRetriever(ChatterID.getUserChatterID(agentUUID, uuid), this, executor);
        obj = lock;
        obj;
        JVM INSTR monitorenter ;
        uuid = (ChatterNameRetriever)retrievers.put(uuid, chatternameretriever);
        obj;
        JVM INSTR monitorexit ;
        if (uuid != null)
        {
            uuid.dispose();
        }
        return chatternameretriever.getResolvedName();
        uuid;
        throw uuid;
    }

    public void clearChatters()
    {
        Object obj = null;
        Object obj2 = lock;
        obj2;
        JVM INSTR monitorenter ;
        Iterator iterator = retrievers.entrySet().iterator();
_L1:
        java.util.Map.Entry entry;
        if (!iterator.hasNext())
        {
            break MISSING_BLOCK_LABEL_95;
        }
        entry = (java.util.Map.Entry)iterator.next();
        Object obj1;
        obj1 = obj;
        if (obj != null)
        {
            break MISSING_BLOCK_LABEL_61;
        }
        obj1 = new HashSet();
        ((Set) (obj1)).add((ChatterNameRetriever)entry.getValue());
        iterator.remove();
        obj = obj1;
          goto _L1
        obj;
        throw obj;
        obj2;
        JVM INSTR monitorexit ;
        if (obj != null)
        {
            for (obj = ((Iterable) (obj)).iterator(); ((Iterator) (obj)).hasNext(); ((ChatterNameRetriever)((Iterator) (obj)).next()).dispose()) { }
        }
        return;
    }

    public void onChatterNameUpdated(ChatterNameRetriever chatternameretriever)
    {
        chatternameretriever = (OnChatterNameUpdated)listener.get();
        if (chatternameretriever != null)
        {
            chatternameretriever.onChatterNameUpdated(this);
        }
    }

    public void retainChatters(Set set)
    {
        HashSet hashset = null;
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        Iterator iterator = retrievers.entrySet().iterator();
_L1:
        java.util.Map.Entry entry;
        do
        {
            if (!iterator.hasNext())
            {
                break MISSING_BLOCK_LABEL_108;
            }
            entry = (java.util.Map.Entry)iterator.next();
        } while (set.contains(entry.getKey()));
        HashSet hashset1;
        hashset1 = hashset;
        if (hashset != null)
        {
            break MISSING_BLOCK_LABEL_79;
        }
        hashset1 = new HashSet();
        hashset1.add((ChatterNameRetriever)entry.getValue());
        iterator.remove();
        hashset = hashset1;
          goto _L1
        obj;
        JVM INSTR monitorexit ;
        if (hashset != null)
        {
            for (set = hashset.iterator(); set.hasNext(); ((ChatterNameRetriever)set.next()).dispose()) { }
        }
        break MISSING_BLOCK_LABEL_152;
        set;
        throw set;
    }
}

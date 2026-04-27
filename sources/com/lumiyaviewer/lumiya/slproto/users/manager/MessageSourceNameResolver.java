// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.dao.UserName;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.Subscription;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            UserManager

public class MessageSourceNameResolver
{
    private class NameRequestEntry
    {

        private final Set messageDatabaseIDs = new HashSet();
        private Subscription subscription;
        final MessageSourceNameResolver this$0;
        private final UUID userUUID;

        public void addMessageID(Long long1)
        {
            messageDatabaseIDs.add(long1);
        }

        public Set getMessageIDs()
        {
            return messageDatabaseIDs;
        }

        public void subscribe()
        {
            subscription = MessageSourceNameResolver._2D_get5(MessageSourceNameResolver.this).getUserNames().subscribe(userUUID, MessageSourceNameResolver._2D_get0(MessageSourceNameResolver.this), MessageSourceNameResolver._2D_get3(MessageSourceNameResolver.this));
        }

        public void unsubscribe()
        {
            subscription.unsubscribe();
            subscription = null;
        }

        public NameRequestEntry(UUID uuid, Long long1)
        {
            this$0 = MessageSourceNameResolver.this;
            super();
            userUUID = uuid;
            messageDatabaseIDs.add(long1);
        }
    }

    public static interface OnMessageSourcesResolvedListener
    {

        public abstract void onMessageSourcesResolved(Set set, UserName username);
    }


    private final Executor dbExecutor;
    private final OnMessageSourcesResolvedListener listener;
    private final Object lock = new Object();
    private final com.lumiyaviewer.lumiya.react.Subscription.OnData onUserName = new com.lumiyaviewer.lumiya.react.Subscription.OnData() {

        final MessageSourceNameResolver this$0;

        public void onData(UserName username)
        {
            Object obj1 = null;
            Object obj3 = MessageSourceNameResolver._2D_get2(MessageSourceNameResolver.this);
            obj3;
            JVM INSTR monitorenter ;
            NameRequestEntry namerequestentry = (NameRequestEntry)MessageSourceNameResolver._2D_get4(MessageSourceNameResolver.this).get(username.getUuid());
            if (namerequestentry == null) goto _L2; else goto _L1
_L1:
            Object obj = new HashSet(namerequestentry.getMessageIDs());
            if (!username.isComplete()) goto _L4; else goto _L3
_L3:
            MessageSourceNameResolver._2D_get4(MessageSourceNameResolver.this).remove(username.getUuid());
            obj1 = obj;
            obj = namerequestentry;
_L6:
            obj3;
            JVM INSTR monitorexit ;
            if (obj != null)
            {
                ((NameRequestEntry) (obj)).unsubscribe();
            }
            if (obj1 != null)
            {
                MessageSourceNameResolver._2D_get1(MessageSourceNameResolver.this).onMessageSourcesResolved(((Set) (obj1)), username);
            }
            return;
            username;
            throw username;
_L4:
            Object obj2 = null;
            obj1 = obj;
            obj = obj2;
            continue; /* Loop/switch isn't completed */
_L2:
            obj = null;
            if (true) goto _L6; else goto _L5
_L5:
        }

        public volatile void onData(Object obj)
        {
            onData((UserName)obj);
        }

            
            {
                this$0 = MessageSourceNameResolver.this;
                super();
            }
    };
    private final Map requestEntryMap = new ConcurrentHashMap();
    private final UserManager userManager;

    static Executor _2D_get0(MessageSourceNameResolver messagesourcenameresolver)
    {
        return messagesourcenameresolver.dbExecutor;
    }

    static OnMessageSourcesResolvedListener _2D_get1(MessageSourceNameResolver messagesourcenameresolver)
    {
        return messagesourcenameresolver.listener;
    }

    static Object _2D_get2(MessageSourceNameResolver messagesourcenameresolver)
    {
        return messagesourcenameresolver.lock;
    }

    static com.lumiyaviewer.lumiya.react.Subscription.OnData _2D_get3(MessageSourceNameResolver messagesourcenameresolver)
    {
        return messagesourcenameresolver.onUserName;
    }

    static Map _2D_get4(MessageSourceNameResolver messagesourcenameresolver)
    {
        return messagesourcenameresolver.requestEntryMap;
    }

    static UserManager _2D_get5(MessageSourceNameResolver messagesourcenameresolver)
    {
        return messagesourcenameresolver.userManager;
    }

    public MessageSourceNameResolver(UserManager usermanager, OnMessageSourcesResolvedListener onmessagesourcesresolvedlistener)
    {
        userManager = usermanager;
        listener = onmessagesourcesresolvedlistener;
        dbExecutor = usermanager.getDatabaseExecutor();
    }

    public void requestResolve(UUID uuid, Long long1)
    {
        boolean flag = false;
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        NameRequestEntry namerequestentry = (NameRequestEntry)requestEntryMap.get(uuid);
        if (namerequestentry != null) goto _L2; else goto _L1
_L1:
        long1 = new NameRequestEntry(uuid, long1);
        flag = true;
        requestEntryMap.put(uuid, long1);
        uuid = long1;
_L4:
        obj;
        JVM INSTR monitorexit ;
        if (flag)
        {
            uuid.subscribe();
        }
        return;
_L2:
        namerequestentry.addMessageID(long1);
        uuid = namerequestentry;
        if (true) goto _L4; else goto _L3
_L3:
        uuid;
        throw uuid;
    }
}

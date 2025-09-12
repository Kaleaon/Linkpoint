// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.Subscription;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            MessageSourceNameResolver, UserManager

private class messageDatabaseIDs
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

    public (UUID uuid, Long long1)
    {
        this$0 = MessageSourceNameResolver.this;
        super();
        userUUID = uuid;
        messageDatabaseIDs.add(long1);
    }
}

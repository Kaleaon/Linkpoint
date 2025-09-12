// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.react.RequestFinalProcessor;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import java.util.UUID;
import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            ChatterList, UserManager

class val.userManager extends RequestFinalProcessor
{

    final ChatterList this$0;
    final UserManager val$userManager;

    protected Boolean processRequest(UUID uuid)
        throws Throwable
    {
        SLAgentCircuit slagentcircuit = val$userManager.getActiveAgentCircuit();
        if (slagentcircuit != null)
        {
            return slagentcircuit.isUserTyping(uuid);
        } else
        {
            return Boolean.valueOf(false);
        }
    }

    protected volatile Object processRequest(Object obj)
        throws Throwable
    {
        return processRequest((UUID)obj);
    }

    A(Executor executor, UserManager usermanager)
    {
        this$0 = final_chatterlist;
        val$userManager = usermanager;
        super(RequestSource.this, executor);
    }
}

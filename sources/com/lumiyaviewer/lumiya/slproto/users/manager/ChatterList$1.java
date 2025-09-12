// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.react.RequestFinalProcessor;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.modules.SLMinimap;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import java.util.UUID;
import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            ChatterList, UserManager

class val.userManager extends RequestFinalProcessor
{

    final ChatterList this$0;
    final UserManager val$userManager;

    protected Float processRequest(UUID uuid)
        throws Throwable
    {
        Object obj = val$userManager.getActiveAgentCircuit();
        if (obj != null)
        {
            obj = ((SLAgentCircuit) (obj)).getModules();
            if (obj != null)
            {
                return ((SLModules) (obj)).minimap.getDistanceToUser(uuid);
            }
        }
        return null;
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

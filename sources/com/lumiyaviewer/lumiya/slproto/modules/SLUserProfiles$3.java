// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules;

import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules:
//            SLUserProfiles

class  extends SimpleRequestHandler
{

    final SLUserProfiles this$0;

    public volatile void onRequest(Object obj)
    {
        onRequest((UUID)obj);
    }

    public void onRequest(UUID uuid)
    {
        agentCircuit.SendGenericMessage("avatarnotesrequest", new String[] {
            uuid.toString()
        });
    }

    ()
    {
        this$0 = SLUserProfiles.this;
        super();
    }
}

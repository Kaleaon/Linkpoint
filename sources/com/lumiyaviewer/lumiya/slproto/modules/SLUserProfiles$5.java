// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules;

import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.users.manager.AvatarPickKey;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules:
//            SLUserProfiles

class  extends SimpleRequestHandler
{

    final SLUserProfiles this$0;

    public void onRequest(AvatarPickKey avatarpickkey)
    {
        agentCircuit.SendGenericMessage("pickinforequest", new String[] {
            avatarpickkey.avatarID.toString(), avatarpickkey.pickID.toString()
        });
    }

    public volatile void onRequest(Object obj)
    {
        onRequest((AvatarPickKey)obj);
    }

    ey()
    {
        this$0 = SLUserProfiles.this;
        super();
    }
}

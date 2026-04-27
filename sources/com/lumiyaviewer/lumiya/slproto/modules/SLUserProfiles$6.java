// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.users.SLMessageResponseCacher;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules:
//            SLUserProfiles

class istener extends com.lumiyaviewer.lumiya.slproto.tener.SLMessageBaseEventListener
{

    final SLUserProfiles this$0;

    public void onMessageAcknowledged(SLMessage slmessage)
    {
        super.onMessageAcknowledged(slmessage);
        if (SLUserProfiles._2D_get0(SLUserProfiles.this) != null)
        {
            SLUserProfiles._2D_get0(SLUserProfiles.this).getAvatarProperties().requestUpdate(SLUserProfiles._2D_get0(SLUserProfiles.this).getUserID());
        }
    }

    cher()
    {
        this$0 = SLUserProfiles.this;
        super();
    }
}

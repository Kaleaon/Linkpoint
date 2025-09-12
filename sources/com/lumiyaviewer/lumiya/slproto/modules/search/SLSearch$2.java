// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.search;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.slproto.SLCircuitInfo;
import com.lumiyaviewer.lumiya.slproto.messages.ParcelInfoRequest;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.search:
//            SLSearch

class this._cls0 extends SimpleRequestHandler
{

    final SLSearch this$0;

    public volatile void onRequest(Object obj)
    {
        onRequest((UUID)obj);
    }

    public void onRequest(UUID uuid)
    {
        Debug.Printf("ParcelInfo: Requesting for %s", new Object[] {
            uuid
        });
        ParcelInfoRequest parcelinforequest = new ParcelInfoRequest();
        parcelinforequest.AgentData_Field.AgentID = SLSearch._2D_get0(SLSearch.this).agentID;
        parcelinforequest.AgentData_Field.SessionID = SLSearch._2D_get0(SLSearch.this).sessionID;
        parcelinforequest.Data_Field.ParcelID = uuid;
        parcelinforequest.isReliable = true;
        SendMessage(parcelinforequest);
    }

    .Data()
    {
        this$0 = SLSearch.this;
        super();
    }
}

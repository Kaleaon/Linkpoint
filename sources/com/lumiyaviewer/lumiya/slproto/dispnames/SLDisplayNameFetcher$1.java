// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.dispnames;

import com.lumiyaviewer.lumiya.react.RequestQueue;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.slproto.messages.UUIDNameRequest;
import java.util.ArrayList;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.dispnames:
//            SLDisplayNameFetcher

class this._cls0 extends SimpleRequestHandler
{

    final SLDisplayNameFetcher this$0;

    public volatile void onRequest(Object obj)
    {
        onRequest((UUID)obj);
    }

    public void onRequest(UUID uuid)
    {
        UUIDNameRequest uuidnamerequest = new UUIDNameRequest();
        com.lumiyaviewer.lumiya.slproto.messages.eBlock eblock = new com.lumiyaviewer.lumiya.slproto.messages.eBlock();
        eblock.ID = uuid;
        uuidnamerequest.UUIDNameBlock_Fields.add(eblock);
        com.lumiyaviewer.lumiya.slproto.messages.eBlock eblock1;
        for (; uuidnamerequest.UUIDNameBlock_Fields.size() < 4 && SLDisplayNameFetcher._2D_get0(SLDisplayNameFetcher.this) != null && (UUID)SLDisplayNameFetcher._2D_get0(SLDisplayNameFetcher.this).getNextRequest() != null; uuidnamerequest.UUIDNameBlock_Fields.add(eblock1))
        {
            eblock1 = new com.lumiyaviewer.lumiya.slproto.messages.eBlock();
            eblock1.ID = uuid;
        }

        uuidnamerequest.isReliable = true;
        SendMessage(uuidnamerequest);
    }

    Block()
    {
        this$0 = SLDisplayNameFetcher.this;
        super();
    }
}

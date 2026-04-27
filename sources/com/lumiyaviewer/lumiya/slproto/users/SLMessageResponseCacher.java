// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.SLMessageFactory;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users:
//            ResponseCacher

public class SLMessageResponseCacher extends ResponseCacher
{

    public SLMessageResponseCacher(DaoSession daosession, Executor executor, String s)
    {
        super(daosession, executor, s);
    }

    public volatile Subscribable getPool()
    {
        return super.getPool();
    }

    public volatile RequestSource getRequestSource()
    {
        return super.getRequestSource();
    }

    protected SLMessage loadCached(byte abyte0[])
    {
        abyte0 = ByteBuffer.wrap(abyte0).order(ByteOrder.nativeOrder());
        int i = SLMessage.DecodeMessageIDGeneric(abyte0);
        SLMessage slmessage = SLMessageFactory.CreateByID(i);
        if (slmessage != null)
        {
            slmessage.UnpackPayload(abyte0);
            return slmessage;
        } else
        {
            Debug.Printf("Failed to create message for id 0x%x", new Object[] {
                Integer.valueOf(i)
            });
            return null;
        }
    }

    protected volatile Object loadCached(byte abyte0[])
    {
        return loadCached(abyte0);
    }

    public volatile void requestUpdate(Object obj)
    {
        super.requestUpdate(obj);
    }

    protected byte[] storeCached(SLMessage slmessage)
    {
        byte abyte0[] = new byte[slmessage.CalcPayloadSize()];
        slmessage.PackPayload(ByteBuffer.wrap(abyte0).order(ByteOrder.nativeOrder()));
        return abyte0;
    }

    protected volatile byte[] storeCached(Object obj)
    {
        return storeCached((SLMessage)obj);
    }
}

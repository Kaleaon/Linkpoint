// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users:
//            ResponseCacher

public class LLSDResponseCacher extends ResponseCacher
{

    public LLSDResponseCacher(DaoSession daosession, Executor executor, String s)
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

    protected LLSDNode loadCached(byte abyte0[])
    {
        try
        {
            abyte0 = LLSDNode.fromBinary(new DataInputStream(new ByteArrayInputStream(abyte0)));
        }
        // Misplaced declaration of an exception variable
        catch (byte abyte0[])
        {
            Debug.Warning(abyte0);
            return null;
        }
        return abyte0;
    }

    protected volatile Object loadCached(byte abyte0[])
    {
        return loadCached(abyte0);
    }

    public volatile void requestUpdate(Object obj)
    {
        super.requestUpdate(obj);
    }

    protected byte[] storeCached(LLSDNode llsdnode)
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        try
        {
            llsdnode.toBinary(dataoutputstream);
            dataoutputstream.flush();
        }
        // Misplaced declaration of an exception variable
        catch (LLSDNode llsdnode)
        {
            Debug.Warning(llsdnode);
            return null;
        }
        return bytearrayoutputstream.toByteArray();
    }

    protected volatile byte[] storeCached(Object obj)
    {
        return storeCached((LLSDNode)obj);
    }
}

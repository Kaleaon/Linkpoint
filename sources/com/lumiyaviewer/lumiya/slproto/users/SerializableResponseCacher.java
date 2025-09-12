// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users;

import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.react.Subscribable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users:
//            ResponseCacher

public class SerializableResponseCacher extends ResponseCacher
{

    public SerializableResponseCacher(DaoSession daosession, Executor executor, String s)
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

    protected Serializable loadCached(byte abyte0[])
    {
        try
        {
            abyte0 = (Serializable)(new ObjectInputStream(new ByteArrayInputStream(abyte0))).readObject();
        }
        // Misplaced declaration of an exception variable
        catch (byte abyte0[])
        {
            return null;
        }
        // Misplaced declaration of an exception variable
        catch (byte abyte0[])
        {
            return null;
        }
        // Misplaced declaration of an exception variable
        catch (byte abyte0[])
        {
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

    protected byte[] storeCached(Serializable serializable)
    {
        try
        {
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
            ObjectOutputStream objectoutputstream = new ObjectOutputStream(bytearrayoutputstream);
            objectoutputstream.writeObject(serializable);
            objectoutputstream.flush();
            serializable = bytearrayoutputstream.toByteArray();
        }
        // Misplaced declaration of an exception variable
        catch (Serializable serializable)
        {
            return null;
        }
        return serializable;
    }

    protected volatile byte[] storeCached(Object obj)
    {
        return storeCached((Serializable)obj);
    }
}

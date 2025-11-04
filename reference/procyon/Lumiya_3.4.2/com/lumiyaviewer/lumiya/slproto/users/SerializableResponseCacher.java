// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users;

import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.react.Subscribable;
import java.util.concurrent.Executor;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import java.io.Serializable;

public class SerializableResponseCacher<Key, MessageType extends Serializable> extends ResponseCacher<Key, MessageType>
{
    public SerializableResponseCacher(final DaoSession daoSession, final Executor executor, final String s) {
        super(daoSession, executor, s);
    }
    
    @Override
    protected MessageType loadCached(final byte[] buf) {
        try {
            return (MessageType)new ObjectInputStream(new ByteArrayInputStream(buf)).readObject();
        }
        catch (final ClassCastException ex) {
            return null;
        }
        catch (final ClassNotFoundException ex2) {
            return null;
        }
        catch (final IOException ex3) {
            return null;
        }
    }
    
    @Override
    protected byte[] storeCached(@Nonnull final MessageType obj) {
        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
            objectOutputStream.writeObject(obj);
            objectOutputStream.flush();
            return out.toByteArray();
        }
        catch (final IOException ex) {
            return null;
        }
    }
}

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
import javax.annotation.Nonnull;

public class SerializableResponseCacher<Key, MessageType extends Serializable> extends ResponseCacher<Key, MessageType> {
    public SerializableResponseCacher(DaoSession daoSession, Executor executor, String str) {
        super(daoSession, executor, str);
    }

    public /* bridge */ /* synthetic */ Subscribable getPool() {
        return super.getPool();
    }

    public /* bridge */ /* synthetic */ RequestSource getRequestSource() {
        return super.getRequestSource();
    }

    /* access modifiers changed from: protected */
    public MessageType loadCached(byte[] bArr) {
        try {
            return (Serializable) new ObjectInputStream(new ByteArrayInputStream(bArr)).readObject();
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e2) {
            return null;
        } catch (ClassCastException e3) {
            return null;
        }
    }

    public /* bridge */ /* synthetic */ void requestUpdate(Object obj) {
        super.requestUpdate(obj);
    }

    /* access modifiers changed from: protected */
    public byte[] storeCached(@Nonnull MessageType messagetype) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(messagetype);
            objectOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }
}

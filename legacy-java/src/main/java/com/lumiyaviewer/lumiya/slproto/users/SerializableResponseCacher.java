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

/* loaded from: classes.dex */
public class SerializableResponseCacher<Key, MessageType extends Serializable> extends ResponseCacher<Key, MessageType> {
    public SerializableResponseCacher(DaoSession daoSession, Executor executor, String str) {
        super(daoSession, executor, str);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.ResponseCacher
    public /* bridge */ /* synthetic */ Subscribable getPool() {
        return super.getPool();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.ResponseCacher
    public /* bridge */ /* synthetic */ RequestSource getRequestSource() {
        return super.getRequestSource();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lumiyaviewer.lumiya.slproto.users.ResponseCacher
    public MessageType loadCached(byte[] bArr) {
        try {
            return (MessageType) new ObjectInputStream(new ByteArrayInputStream(bArr)).readObject();
        } catch (IOException e) {
            return null;
        } catch (ClassCastException e2) {
            return null;
        } catch (ClassNotFoundException e3) {
            return null;
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.ResponseCacher, com.lumiyaviewer.lumiya.react.Refreshable
    public /* bridge */ /* synthetic */ void requestUpdate(Object obj) {
        super.requestUpdate(obj);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lumiyaviewer.lumiya.slproto.users.ResponseCacher
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

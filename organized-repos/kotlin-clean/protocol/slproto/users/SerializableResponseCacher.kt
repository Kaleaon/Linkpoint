package com.linkpoint.slproto.users

import com.linkpoint.dao.DaoSession
import com.linkpoint.react.RequestSource
import com.linkpoint.react.Subscribable
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.util.concurrent.Executor
import javax.annotation.Nonnull

class SerializableResponseCacher<Key, MessageType : Serializable>, ResponseCacher<Key, MessageType> {
    public SerializableResponseCacher(DaoSession daoSession, Executor executor, String str) {
        super(daoSession, executor, str)
    }

    public /* bridge */ /* synthetic */ Subscribable getPool() {
        return super.getPool()
    }

    public /* bridge */ /* synthetic */ RequestSource getRequestSource() {
        return super.getRequestSource()
    }

    /* access modifiers changed from: protected */
    public MessageType loadCached(ByteArray bArr) {
        try {
            return (Serializable) ObjectInputStream(ByteArrayInputStream(bArr)).readObject()
        } catch (IOException e) {
            return null
        } catch (ClassNotFoundException e2) {
            return null
        } catch (ClassCastException e3) {
            return null
        }
    }

    public /* bridge */ /* synthetic */ Unit requestUpdate(Object obj) {
        super.requestUpdate(obj)
    }

    /* access modifiers changed from: protected */
    public ByteArray storeCached(MessageType messagetype) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = ByteArrayOutputStream()
            ObjectOutputStream objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
            objectOutputStream.writeObject(messagetype)
            objectOutputStream.flush()
            return byteArrayOutputStream.toByteArray()
        } catch (IOException e) {
            return null
        }
    }
}

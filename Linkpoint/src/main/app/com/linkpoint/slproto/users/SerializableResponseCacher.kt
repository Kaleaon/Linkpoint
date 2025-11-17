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
import androidx.annotation.NonNull

class SerializableResponseCacher<Key, MessageType : Serializable> : ResponseCacher<Key, MessageType> {
    SerializableResponseCacher(DaoSession daoSession, Executor executor, String str) {
        super(daoSession, executor, str)
    }

    /* bridge */ /* synthetic */ Subscribable getPool() {
        return super.getPool()
    }

    /* bridge */ /* synthetic */ RequestSource getRequestSource() {
        return super.getRequestSource()
    }

    /* access modifiers changed from: protected */
    MessageType loadCached(Byte[] bArr) {
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

    /* bridge */ /* synthetic */ Unit requestUpdate(Any obj) {
        super.requestUpdate(obj)
    }

    /* access modifiers changed from: protected */
    Byte[] storeCached(@NonNull MessageType messagetype) {
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

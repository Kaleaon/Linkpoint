package com.lumiyaviewer.lumiya.slproto.users

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.dao.DaoSession
import com.lumiyaviewer.lumiya.react.RequestSource
import com.lumiyaviewer.lumiya.react.Subscribable
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.util.concurrent.Executor
import javax.annotation.Nonnull

class LLSDResponseCacher<Key> : ResponseCacher<Key, LLSDNode> {
    LLSDResponseCacher(DaoSession daoSession, Executor executor, String str) {
        super(daoSession, executor, str)
    }

    /* bridge */ /* synthetic */ Subscribable getPool() {
        return super.getPool()
    }

    /* bridge */ /* synthetic */ RequestSource getRequestSource() {
        return super.getRequestSource()
    }

    /* access modifiers changed from: protected */
    LLSDNode loadCached(Byte[] bArr) {
        try {
            return LLSDNode.fromBinary(DataInputStream(ByteArrayInputStream(bArr)))
        } catch (LLSDException e) {
            Debug.Warning(e)
            return null
        }
    }

    /* bridge */ /* synthetic */ Unit requestUpdate(Any obj) {
        super.requestUpdate(obj)
    }

    /* access modifiers changed from: protected */
    Byte[] storeCached(@Nonnull LLSDNode lLSDNode) {
        ByteArrayOutputStream byteArrayOutputStream = ByteArrayOutputStream()
        DataOutputStream dataOutputStream = DataOutputStream(byteArrayOutputStream)
        try {
            lLSDNode.toBinary(dataOutputStream)
            dataOutputStream.flush()
            return byteArrayOutputStream.toByteArray()
        } catch (IOException e) {
            Debug.Warning(e)
            return null
        }
    }
}

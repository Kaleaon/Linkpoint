package com.linkpoint.slproto.users

import com.linkpoint.Debug
import com.linkpoint.dao.DaoSession
import com.linkpoint.react.RequestSource
import com.linkpoint.react.Subscribable
import com.linkpoint.slproto.llsd.LLSDException
import com.linkpoint.slproto.llsd.LLSDNode
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.util.concurrent.Executor
import javax.annotation.Nonnull

class LLSDResponseCacher<Key> : ResponseCacher<Key, LLSDNode> {
    public LLSDResponseCacher(DaoSession daoSession, Executor executor, String str) {
        super(daoSession, executor, str)
    }

    public /* bridge */ /* synthetic */ Subscribable getPool() {
        return super.getPool()
    }

    public /* bridge */ /* synthetic */ RequestSource getRequestSource() {
        return super.getRequestSource()
    }

    /* access modifiers changed from: protected */
    public LLSDNode loadCached(ByteArray bArr) {
        try {
            return LLSDNode.fromBinary(DataInputStream(ByteArrayInputStream(bArr)))
        } catch (LLSDException e) {
            Debug.Warning(e)
            return null
        }
    }

    public /* bridge */ /* synthetic */ Unit requestUpdate(Object obj) {
        super.requestUpdate(obj)
    }

    /* access modifiers changed from: protected */
    public ByteArray storeCached(LLSDNode lLSDNode) {
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

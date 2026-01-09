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
import androidx.annotation.NonNull

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
    fun loadCached(ByteArray bArr): LLSDNode {
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
    fun storeCached(@NonNull LLSDNode lLSDNode): ByteArray {
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

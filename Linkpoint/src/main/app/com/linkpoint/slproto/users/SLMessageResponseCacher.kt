package com.linkpoint.slproto.users

import com.linkpoint.Debug
import com.linkpoint.dao.DaoSession
import com.linkpoint.react.RequestSource
import com.linkpoint.react.Subscribable
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.messages.SLMessageFactory
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.Executor
import androidx.annotation.NonNull

class SLMessageResponseCacher<Key, MessageType : SLMessage> : ResponseCacher<Key, MessageType> {
    SLMessageResponseCacher(DaoSession daoSession, Executor executor, String str) {
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
        ByteBuffer order = ByteBuffer.wrap(bArr).order(ByteOrder.nativeOrder())
        Int DecodeMessageIDGeneric = SLMessage.DecodeMessageIDGeneric(order)
        MessageType CreateByID = SLMessageFactory.CreateByID(DecodeMessageIDGeneric)
        if (CreateByID != null) {
            CreateByID.UnpackPayload(order)
            return CreateByID
        }
        Debug.Printf("Failed to create message for id 0x%x", Int.valueOf(DecodeMessageIDGeneric))
        return null
    }

    /* bridge */ /* synthetic */ Unit requestUpdate(Any obj) {
        super.requestUpdate(obj)
    }

    /* access modifiers changed from: protected */
    Byte[] storeCached(@NonNull MessageType messagetype) {
        Byte[] bArr = Byte[messagetype.CalcPayloadSize()]
        messagetype.PackPayload(ByteBuffer.wrap(bArr).order(ByteOrder.nativeOrder()))
        return bArr
    }
}

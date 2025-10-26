package com.lumiyaviewer.lumiya.slproto.users

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.dao.DaoSession
import com.lumiyaviewer.lumiya.react.RequestSource
import com.lumiyaviewer.lumiya.react.Subscribable
import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.messages.SLMessageFactory
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.Executor
import javax.annotation.Nonnull

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
    MessageType loadCached(ByteArray bArr) {
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
    ByteArray storeCached(@Nonnull MessageType messagetype) {
        ByteArray bArr = Byte[messagetype.CalcPayloadSize()]
        messagetype.PackPayload(ByteBuffer.wrap(bArr).order(ByteOrder.nativeOrder()))
        return bArr
    }
}

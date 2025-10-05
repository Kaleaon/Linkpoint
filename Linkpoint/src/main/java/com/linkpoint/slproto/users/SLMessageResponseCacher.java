package com.linkpoint.slproto.users;

import com.linkpoint.Debug;
import com.linkpoint.dao.DaoSession;
import com.linkpoint.react.RequestSource;
import com.linkpoint.react.Subscribable;
import com.linkpoint.slproto.SLMessage;
import com.linkpoint.slproto.messages.SLMessageFactory;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;

public class SLMessageResponseCacher<Key, MessageType extends SLMessage> extends ResponseCacher<Key, MessageType> {
    public SLMessageResponseCacher(DaoSession daoSession, Executor executor, String str) {
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
        ByteBuffer order = ByteBuffer.wrap(bArr).order(ByteOrder.nativeOrder());
        int DecodeMessageIDGeneric = SLMessage.DecodeMessageIDGeneric(order);
        MessageType CreateByID = SLMessageFactory.CreateByID(DecodeMessageIDGeneric);
        if (CreateByID != null) {
            CreateByID.UnpackPayload(order);
            return CreateByID;
        }
        Debug.Printf("Failed to create message for id 0x%x", Integer.valueOf(DecodeMessageIDGeneric));
        return null;
    }

    public /* bridge */ /* synthetic */ void requestUpdate(Object obj) {
        super.requestUpdate(obj);
    }

    /* access modifiers changed from: protected */
    public byte[] storeCached(@Nonnull MessageType messagetype) {
        byte[] bArr = new byte[messagetype.CalcPayloadSize()];
        messagetype.PackPayload(ByteBuffer.wrap(bArr).order(ByteOrder.nativeOrder()));
        return bArr;
    }
}

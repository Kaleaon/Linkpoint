package com.lumiyaviewer.lumiya.slproto.users;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.SLMessageFactory;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;

/* loaded from: classes.dex */
public class SLMessageResponseCacher<Key, MessageType extends SLMessage> extends ResponseCacher<Key, MessageType> {
    public SLMessageResponseCacher(DaoSession daoSession, Executor executor, String str) {
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
        ByteBuffer order = ByteBuffer.wrap(bArr).order(ByteOrder.nativeOrder());
        int DecodeMessageIDGeneric = SLMessage.DecodeMessageIDGeneric(order);
        MessageType messagetype = (MessageType) SLMessageFactory.CreateByID(DecodeMessageIDGeneric);
        if (messagetype != null) {
            messagetype.UnpackPayload(order);
            return messagetype;
        }
        Debug.Printf("Failed to create message for id 0x%x", Integer.valueOf(DecodeMessageIDGeneric));
        return null;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.ResponseCacher, com.lumiyaviewer.lumiya.react.Refreshable
    public /* bridge */ /* synthetic */ void requestUpdate(Object obj) {
        super.requestUpdate(obj);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lumiyaviewer.lumiya.slproto.users.ResponseCacher
    public byte[] storeCached(@Nonnull MessageType messagetype) {
        byte[] bArr = new byte[messagetype.CalcPayloadSize()];
        messagetype.PackPayload(ByteBuffer.wrap(bArr).order(ByteOrder.nativeOrder()));
        return bArr;
    }
}

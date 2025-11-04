package com.lumiyaviewer.lumiya.slproto.users;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;

/* loaded from: classes.dex */
public class LLSDResponseCacher<Key> extends ResponseCacher<Key, LLSDNode> {
    public LLSDResponseCacher(DaoSession daoSession, Executor executor, String str) {
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
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.lumiyaviewer.lumiya.slproto.users.ResponseCacher
    public LLSDNode loadCached(byte[] bArr) {
        try {
            return LLSDNode.fromBinary(new DataInputStream(new ByteArrayInputStream(bArr)));
        } catch (LLSDException e) {
            Debug.Warning(e);
            return null;
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.ResponseCacher, com.lumiyaviewer.lumiya.react.Refreshable
    public /* bridge */ /* synthetic */ void requestUpdate(Object obj) {
        super.requestUpdate(obj);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lumiyaviewer.lumiya.slproto.users.ResponseCacher
    public byte[] storeCached(@Nonnull LLSDNode lLSDNode) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            lLSDNode.toBinary(dataOutputStream);
            dataOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            Debug.Warning(e);
            return null;
        }
    }
}

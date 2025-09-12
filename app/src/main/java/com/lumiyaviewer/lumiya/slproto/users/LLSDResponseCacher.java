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

public class LLSDResponseCacher<Key> extends ResponseCacher<Key, LLSDNode> {
    public LLSDResponseCacher(DaoSession daoSession, Executor executor, String str) {
        super(daoSession, executor, str);
    }

    public /* bridge */ /* synthetic */ Subscribable getPool() {
        return super.getPool();
    }

    public /* bridge */ /* synthetic */ RequestSource getRequestSource() {
        return super.getRequestSource();
    }

    /* access modifiers changed from: protected */
    public LLSDNode loadCached(byte[] bArr) {
        try {
            return LLSDNode.fromBinary(new DataInputStream(new ByteArrayInputStream(bArr)));
        } catch (LLSDException e) {
            Debug.Warning(e);
            return null;
        }
    }

    public /* bridge */ /* synthetic */ void requestUpdate(Object obj) {
        super.requestUpdate(obj);
    }

    /* access modifiers changed from: protected */
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

// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users;

import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.Debug;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.react.Subscribable;
import java.util.concurrent.Executor;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;

public class LLSDResponseCacher<Key> extends ResponseCacher<Key, LLSDNode>
{
    public LLSDResponseCacher(final DaoSession daoSession, final Executor executor, final String s) {
        super(daoSession, executor, s);
    }
    
    @Override
    protected LLSDNode loadCached(final byte[] buf) {
        try {
            return LLSDNode.fromBinary(new DataInputStream(new ByteArrayInputStream(buf)));
        }
        catch (final LLSDException ex) {
            Debug.Warning(ex);
            return null;
        }
    }
    
    @Override
    protected byte[] storeCached(@Nonnull final LLSDNode llsdNode) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final DataOutputStream dataOutputStream = new DataOutputStream(out);
        try {
            llsdNode.toBinary(dataOutputStream);
            dataOutputStream.flush();
            return out.toByteArray();
        }
        catch (final IOException ex) {
            Debug.Warning(ex);
            return null;
        }
    }
}

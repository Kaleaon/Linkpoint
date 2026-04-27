// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users;

import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.messages.SLMessageFactory;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.react.Subscribable;
import java.util.concurrent.Executor;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class SLMessageResponseCacher<Key, MessageType extends SLMessage> extends ResponseCacher<Key, MessageType>
{
    public SLMessageResponseCacher(final DaoSession daoSession, final Executor executor, final String s) {
        super(daoSession, executor, s);
    }
    
    @Override
    protected MessageType loadCached(final byte[] array) {
        final ByteBuffer order = ByteBuffer.wrap(array).order(ByteOrder.nativeOrder());
        final int decodeMessageIDGeneric = SLMessage.DecodeMessageIDGeneric(order);
        final SLMessage createByID = SLMessageFactory.CreateByID(decodeMessageIDGeneric);
        if (createByID != null) {
            createByID.UnpackPayload(order);
            return (MessageType)createByID;
        }
        Debug.Printf("Failed to create message for id 0x%x", decodeMessageIDGeneric);
        return null;
    }
    
    @Override
    protected byte[] storeCached(@Nonnull final MessageType messageType) {
        final byte[] array = new byte[messageType.CalcPayloadSize()];
        messageType.PackPayload(ByteBuffer.wrap(array).order(ByteOrder.nativeOrder()));
        return array;
    }
}

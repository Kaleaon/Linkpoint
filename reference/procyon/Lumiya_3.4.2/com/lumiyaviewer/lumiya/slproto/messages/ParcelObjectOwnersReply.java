// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ParcelObjectOwnersReply extends SLMessage
{
    public ArrayList<Data> Data_Fields;
    
    public ParcelObjectOwnersReply() {
        this.Data_Fields = new ArrayList<Data>();
        this.zeroCoded = true;
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.Data_Fields.size() * 22 + 5;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleParcelObjectOwnersReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)57);
        byteBuffer.put((byte)this.Data_Fields.size());
        for (final Data data : this.Data_Fields) {
            this.packUUID(byteBuffer, data.OwnerID);
            this.packBoolean(byteBuffer, data.IsGroupOwned);
            this.packInt(byteBuffer, data.Count);
            this.packBoolean(byteBuffer, data.OnlineStatus);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final Data e = new Data();
            e.OwnerID = this.unpackUUID(byteBuffer);
            e.IsGroupOwned = this.unpackBoolean(byteBuffer);
            e.Count = this.unpackInt(byteBuffer);
            e.OnlineStatus = this.unpackBoolean(byteBuffer);
            this.Data_Fields.add(e);
        }
    }
    
    public static class Data
    {
        public int Count;
        public boolean IsGroupOwned;
        public boolean OnlineStatus;
        public UUID OwnerID;
    }
}

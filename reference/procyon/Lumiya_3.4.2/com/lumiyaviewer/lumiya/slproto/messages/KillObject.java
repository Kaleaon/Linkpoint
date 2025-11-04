// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class KillObject extends SLMessage
{
    public ArrayList<ObjectData> ObjectData_Fields;
    
    public KillObject() {
        this.ObjectData_Fields = new ArrayList<ObjectData>();
        this.zeroCoded = false;
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ObjectData_Fields.size() * 4 + 2;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleKillObject(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)16);
        byteBuffer.put((byte)this.ObjectData_Fields.size());
        final Iterator<Object> iterator = this.ObjectData_Fields.iterator();
        while (iterator.hasNext()) {
            this.packInt(byteBuffer, iterator.next().ID);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final ObjectData e = new ObjectData();
            e.ID = this.unpackInt(byteBuffer);
            this.ObjectData_Fields.add(e);
        }
    }
    
    public static class ObjectData
    {
        public int ID;
    }
}

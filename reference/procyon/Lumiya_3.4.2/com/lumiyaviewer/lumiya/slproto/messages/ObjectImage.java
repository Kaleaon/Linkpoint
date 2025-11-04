// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ObjectImage extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<ObjectData> ObjectData_Fields;
    
    public ObjectImage() {
        this.ObjectData_Fields = new ArrayList<ObjectData>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.ObjectData_Fields.iterator();
        int n = 37;
        while (iterator.hasNext()) {
            final ObjectData objectData = iterator.next();
            n += objectData.TextureEntry.length + (objectData.MediaURL.length + 5 + 2);
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleObjectImage(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)96);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        byteBuffer.put((byte)this.ObjectData_Fields.size());
        for (final ObjectData objectData : this.ObjectData_Fields) {
            this.packInt(byteBuffer, objectData.ObjectLocalID);
            this.packVariable(byteBuffer, objectData.MediaURL, 1);
            this.packVariable(byteBuffer, objectData.TextureEntry, 2);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final ObjectData e = new ObjectData();
            e.ObjectLocalID = this.unpackInt(byteBuffer);
            e.MediaURL = this.unpackVariable(byteBuffer, 1);
            e.TextureEntry = this.unpackVariable(byteBuffer, 2);
            this.ObjectData_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class ObjectData
    {
        public byte[] MediaURL;
        public int ObjectLocalID;
        public byte[] TextureEntry;
    }
}

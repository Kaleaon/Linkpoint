// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class RequestImage extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<RequestImageData> RequestImageData_Fields;
    
    public RequestImage() {
        this.RequestImageData_Fields = new ArrayList<RequestImageData>();
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.RequestImageData_Fields.size() * 26 + 34;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleRequestImage(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)8);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        byteBuffer.put((byte)this.RequestImageData_Fields.size());
        for (final RequestImageData requestImageData : this.RequestImageData_Fields) {
            this.packUUID(byteBuffer, requestImageData.Image);
            this.packByte(byteBuffer, (byte)requestImageData.DiscardLevel);
            this.packFloat(byteBuffer, requestImageData.DownloadPriority);
            this.packInt(byteBuffer, requestImageData.Packet);
            this.packByte(byteBuffer, (byte)requestImageData.Type);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final RequestImageData e = new RequestImageData();
            e.Image = this.unpackUUID(byteBuffer);
            e.DiscardLevel = this.unpackByte(byteBuffer);
            e.DownloadPriority = this.unpackFloat(byteBuffer);
            e.Packet = this.unpackInt(byteBuffer);
            e.Type = (this.unpackByte(byteBuffer) & 0xFF);
            this.RequestImageData_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class RequestImageData
    {
        public int DiscardLevel;
        public float DownloadPriority;
        public UUID Image;
        public int Packet;
        public int Type;
    }
}

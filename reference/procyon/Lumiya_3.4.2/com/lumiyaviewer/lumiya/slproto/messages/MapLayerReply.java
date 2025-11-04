// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class MapLayerReply extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<LayerData> LayerData_Fields;
    
    public MapLayerReply() {
        this.LayerData_Fields = new ArrayList<LayerData>();
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.LayerData_Fields.size() * 32 + 25;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleMapLayerReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-106));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packInt(byteBuffer, this.AgentData_Field.Flags);
        byteBuffer.put((byte)this.LayerData_Fields.size());
        for (final LayerData layerData : this.LayerData_Fields) {
            this.packInt(byteBuffer, layerData.Left);
            this.packInt(byteBuffer, layerData.Right);
            this.packInt(byteBuffer, layerData.Top);
            this.packInt(byteBuffer, layerData.Bottom);
            this.packUUID(byteBuffer, layerData.ImageID);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.Flags = this.unpackInt(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final LayerData e = new LayerData();
            e.Left = this.unpackInt(byteBuffer);
            e.Right = this.unpackInt(byteBuffer);
            e.Top = this.unpackInt(byteBuffer);
            e.Bottom = this.unpackInt(byteBuffer);
            e.ImageID = this.unpackUUID(byteBuffer);
            this.LayerData_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public int Flags;
    }
    
    public static class LayerData
    {
        public int Bottom;
        public UUID ImageID;
        public int Left;
        public int Right;
        public int Top;
    }
}

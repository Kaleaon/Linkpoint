// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ModifyLand extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<ModifyBlockExtended> ModifyBlockExtended_Fields;
    public ModifyBlock ModifyBlock_Field;
    public ArrayList<ParcelData> ParcelData_Fields;
    
    public ModifyLand() {
        this.ParcelData_Fields = new ArrayList<ParcelData>();
        this.ModifyBlockExtended_Fields = new ArrayList<ModifyBlockExtended>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.ModifyBlock_Field = new ModifyBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ParcelData_Fields.size() * 20 + 47 + 1 + this.ModifyBlockExtended_Fields.size() * 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleModifyLand(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)124);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packByte(byteBuffer, (byte)this.ModifyBlock_Field.Action);
        this.packByte(byteBuffer, (byte)this.ModifyBlock_Field.BrushSize);
        this.packFloat(byteBuffer, this.ModifyBlock_Field.Seconds);
        this.packFloat(byteBuffer, this.ModifyBlock_Field.Height);
        byteBuffer.put((byte)this.ParcelData_Fields.size());
        for (final ParcelData parcelData : this.ParcelData_Fields) {
            this.packInt(byteBuffer, parcelData.LocalID);
            this.packFloat(byteBuffer, parcelData.West);
            this.packFloat(byteBuffer, parcelData.South);
            this.packFloat(byteBuffer, parcelData.East);
            this.packFloat(byteBuffer, parcelData.North);
        }
        byteBuffer.put((byte)this.ModifyBlockExtended_Fields.size());
        final Iterator<Object> iterator2 = this.ModifyBlockExtended_Fields.iterator();
        while (iterator2.hasNext()) {
            this.packFloat(byteBuffer, iterator2.next().BrushSize);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final int n = 0;
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.ModifyBlock_Field.Action = (this.unpackByte(byteBuffer) & 0xFF);
        this.ModifyBlock_Field.BrushSize = (this.unpackByte(byteBuffer) & 0xFF);
        this.ModifyBlock_Field.Seconds = this.unpackFloat(byteBuffer);
        this.ModifyBlock_Field.Height = this.unpackFloat(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final ParcelData e = new ParcelData();
            e.LocalID = this.unpackInt(byteBuffer);
            e.West = this.unpackFloat(byteBuffer);
            e.South = this.unpackFloat(byteBuffer);
            e.East = this.unpackFloat(byteBuffer);
            e.North = this.unpackFloat(byteBuffer);
            this.ParcelData_Fields.add(e);
        }
        final byte value2 = byteBuffer.get();
        for (int j = n; j < (value2 & 0xFF); ++j) {
            final ModifyBlockExtended e2 = new ModifyBlockExtended();
            e2.BrushSize = this.unpackFloat(byteBuffer);
            this.ModifyBlockExtended_Fields.add(e2);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class ModifyBlock
    {
        public int Action;
        public int BrushSize;
        public float Height;
        public float Seconds;
    }
    
    public static class ModifyBlockExtended
    {
        public float BrushSize;
    }
    
    public static class ParcelData
    {
        public float East;
        public int LocalID;
        public float North;
        public float South;
        public float West;
    }
}

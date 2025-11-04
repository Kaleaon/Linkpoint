// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ScriptSensorRequest extends SLMessage
{
    public Requester Requester_Field;
    
    public ScriptSensorRequest() {
        this.zeroCoded = true;
        this.Requester_Field = new Requester();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.Requester_Field.SearchName.length + 73 + 4 + 4 + 4 + 8 + 1 + 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleScriptSensorRequest(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-9));
        this.packUUID(byteBuffer, this.Requester_Field.SourceID);
        this.packUUID(byteBuffer, this.Requester_Field.RequestID);
        this.packUUID(byteBuffer, this.Requester_Field.SearchID);
        this.packLLVector3(byteBuffer, this.Requester_Field.SearchPos);
        this.packLLQuaternion(byteBuffer, this.Requester_Field.SearchDir);
        this.packVariable(byteBuffer, this.Requester_Field.SearchName, 1);
        this.packInt(byteBuffer, this.Requester_Field.Type);
        this.packFloat(byteBuffer, this.Requester_Field.Range);
        this.packFloat(byteBuffer, this.Requester_Field.Arc);
        this.packLong(byteBuffer, this.Requester_Field.RegionHandle);
        this.packByte(byteBuffer, (byte)this.Requester_Field.SearchRegions);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.Requester_Field.SourceID = this.unpackUUID(byteBuffer);
        this.Requester_Field.RequestID = this.unpackUUID(byteBuffer);
        this.Requester_Field.SearchID = this.unpackUUID(byteBuffer);
        this.Requester_Field.SearchPos = this.unpackLLVector3(byteBuffer);
        this.Requester_Field.SearchDir = this.unpackLLQuaternion(byteBuffer);
        this.Requester_Field.SearchName = this.unpackVariable(byteBuffer, 1);
        this.Requester_Field.Type = this.unpackInt(byteBuffer);
        this.Requester_Field.Range = this.unpackFloat(byteBuffer);
        this.Requester_Field.Arc = this.unpackFloat(byteBuffer);
        this.Requester_Field.RegionHandle = this.unpackLong(byteBuffer);
        this.Requester_Field.SearchRegions = (this.unpackByte(byteBuffer) & 0xFF);
    }
    
    public static class Requester
    {
        public float Arc;
        public float Range;
        public long RegionHandle;
        public UUID RequestID;
        public LLQuaternion SearchDir;
        public UUID SearchID;
        public byte[] SearchName;
        public LLVector3 SearchPos;
        public int SearchRegions;
        public UUID SourceID;
        public int Type;
    }
}

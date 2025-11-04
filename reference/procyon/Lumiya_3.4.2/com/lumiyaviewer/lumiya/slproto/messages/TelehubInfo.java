// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class TelehubInfo extends SLMessage
{
    public ArrayList<SpawnPointBlock> SpawnPointBlock_Fields;
    public TelehubBlock TelehubBlock_Field;
    
    public TelehubInfo() {
        this.SpawnPointBlock_Fields = new ArrayList<SpawnPointBlock>();
        this.zeroCoded = false;
        this.TelehubBlock_Field = new TelehubBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.TelehubBlock_Field.ObjectName.length + 17 + 12 + 12 + 4 + 1 + this.SpawnPointBlock_Fields.size() * 12;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleTelehubInfo(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)10);
        this.packUUID(byteBuffer, this.TelehubBlock_Field.ObjectID);
        this.packVariable(byteBuffer, this.TelehubBlock_Field.ObjectName, 1);
        this.packLLVector3(byteBuffer, this.TelehubBlock_Field.TelehubPos);
        this.packLLQuaternion(byteBuffer, this.TelehubBlock_Field.TelehubRot);
        byteBuffer.put((byte)this.SpawnPointBlock_Fields.size());
        final Iterator<Object> iterator = this.SpawnPointBlock_Fields.iterator();
        while (iterator.hasNext()) {
            this.packLLVector3(byteBuffer, iterator.next().SpawnPointPos);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.TelehubBlock_Field.ObjectID = this.unpackUUID(byteBuffer);
        this.TelehubBlock_Field.ObjectName = this.unpackVariable(byteBuffer, 1);
        this.TelehubBlock_Field.TelehubPos = this.unpackLLVector3(byteBuffer);
        this.TelehubBlock_Field.TelehubRot = this.unpackLLQuaternion(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final SpawnPointBlock e = new SpawnPointBlock();
            e.SpawnPointPos = this.unpackLLVector3(byteBuffer);
            this.SpawnPointBlock_Fields.add(e);
        }
    }
    
    public static class SpawnPointBlock
    {
        public LLVector3 SpawnPointPos;
    }
    
    public static class TelehubBlock
    {
        public UUID ObjectID;
        public byte[] ObjectName;
        public LLVector3 TelehubPos;
        public LLQuaternion TelehubRot;
    }
}

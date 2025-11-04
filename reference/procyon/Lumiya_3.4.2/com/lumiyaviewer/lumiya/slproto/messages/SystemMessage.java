// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class SystemMessage extends SLMessage
{
    public MethodData MethodData_Field;
    public ArrayList<ParamList> ParamList_Fields;
    
    public SystemMessage() {
        this.ParamList_Fields = new ArrayList<ParamList>();
        this.zeroCoded = true;
        this.MethodData_Field = new MethodData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final int length = this.MethodData_Field.Method.length;
        final Iterator<Object> iterator = this.ParamList_Fields.iterator();
        int n = length + 1 + 16 + 32 + 4 + 1;
        while (iterator.hasNext()) {
            n += iterator.next().Parameter.length + 1;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleSystemMessage(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-108));
        this.packVariable(byteBuffer, this.MethodData_Field.Method, 1);
        this.packUUID(byteBuffer, this.MethodData_Field.Invoice);
        this.packFixed(byteBuffer, this.MethodData_Field.Digest, 32);
        byteBuffer.put((byte)this.ParamList_Fields.size());
        final Iterator<Object> iterator = this.ParamList_Fields.iterator();
        while (iterator.hasNext()) {
            this.packVariable(byteBuffer, iterator.next().Parameter, 1);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.MethodData_Field.Method = this.unpackVariable(byteBuffer, 1);
        this.MethodData_Field.Invoice = this.unpackUUID(byteBuffer);
        this.MethodData_Field.Digest = this.unpackFixed(byteBuffer, 32);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final ParamList e = new ParamList();
            e.Parameter = this.unpackVariable(byteBuffer, 1);
            this.ParamList_Fields.add(e);
        }
    }
    
    public static class MethodData
    {
        public byte[] Digest;
        public UUID Invoice;
        public byte[] Method;
    }
    
    public static class ParamList
    {
        public byte[] Parameter;
    }
}

// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class GodlikeMessage extends SLMessage
{
    public AgentData AgentData_Field;
    public MethodData MethodData_Field;
    public ArrayList<ParamList> ParamList_Fields;
    
    public GodlikeMessage() {
        this.ParamList_Fields = new ArrayList<ParamList>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.MethodData_Field = new MethodData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final int length = this.MethodData_Field.Method.length;
        final Iterator<Object> iterator = this.ParamList_Fields.iterator();
        int n = length + 1 + 16 + 52 + 1;
        while (iterator.hasNext()) {
            n += iterator.next().Parameter.length + 1;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleGodlikeMessage(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)3);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.AgentData_Field.TransactionID);
        this.packVariable(byteBuffer, this.MethodData_Field.Method, 1);
        this.packUUID(byteBuffer, this.MethodData_Field.Invoice);
        byteBuffer.put((byte)this.ParamList_Fields.size());
        final Iterator<Object> iterator = this.ParamList_Fields.iterator();
        while (iterator.hasNext()) {
            this.packVariable(byteBuffer, iterator.next().Parameter, 1);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.TransactionID = this.unpackUUID(byteBuffer);
        this.MethodData_Field.Method = this.unpackVariable(byteBuffer, 1);
        this.MethodData_Field.Invoice = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final ParamList e = new ParamList();
            e.Parameter = this.unpackVariable(byteBuffer, 1);
            this.ParamList_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
        public UUID TransactionID;
    }
    
    public static class MethodData
    {
        public UUID Invoice;
        public byte[] Method;
    }
    
    public static class ParamList
    {
        public byte[] Parameter;
    }
}

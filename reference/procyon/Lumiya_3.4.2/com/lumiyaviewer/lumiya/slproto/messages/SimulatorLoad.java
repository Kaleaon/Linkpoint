// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class SimulatorLoad extends SLMessage
{
    public ArrayList<AgentList> AgentList_Fields;
    public SimulatorLoadData SimulatorLoadData_Field;
    
    public SimulatorLoad() {
        this.AgentList_Fields = new ArrayList<AgentList>();
        this.zeroCoded = false;
        this.SimulatorLoadData_Field = new SimulatorLoadData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.AgentList_Fields.size() * 6 + 14;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleSimulatorLoad(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)12);
        this.packFloat(byteBuffer, this.SimulatorLoadData_Field.TimeDilation);
        this.packInt(byteBuffer, this.SimulatorLoadData_Field.AgentCount);
        this.packBoolean(byteBuffer, this.SimulatorLoadData_Field.CanAcceptAgents);
        byteBuffer.put((byte)this.AgentList_Fields.size());
        for (final AgentList list : this.AgentList_Fields) {
            this.packInt(byteBuffer, list.CircuitCode);
            this.packByte(byteBuffer, (byte)list.X);
            this.packByte(byteBuffer, (byte)list.Y);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.SimulatorLoadData_Field.TimeDilation = this.unpackFloat(byteBuffer);
        this.SimulatorLoadData_Field.AgentCount = this.unpackInt(byteBuffer);
        this.SimulatorLoadData_Field.CanAcceptAgents = this.unpackBoolean(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final AgentList e = new AgentList();
            e.CircuitCode = this.unpackInt(byteBuffer);
            e.X = (this.unpackByte(byteBuffer) & 0xFF);
            e.Y = (this.unpackByte(byteBuffer) & 0xFF);
            this.AgentList_Fields.add(e);
        }
    }
    
    public static class AgentList
    {
        public int CircuitCode;
        public int X;
        public int Y;
    }
    
    public static class SimulatorLoadData
    {
        public int AgentCount;
        public boolean CanAcceptAgents;
        public float TimeDilation;
    }
}

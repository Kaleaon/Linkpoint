package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class SimulatorLoad extends SLMessage {
    public ArrayList<AgentList> AgentList_Fields = new ArrayList<>();
    public SimulatorLoadData SimulatorLoadData_Field;

    /* loaded from: classes.dex */
    public static class AgentList {
        public int CircuitCode;
        public int X;
        public int Y;
    }

    /* loaded from: classes.dex */
    public static class SimulatorLoadData {
        public int AgentCount;
        public boolean CanAcceptAgents;
        public float TimeDilation;
    }

    public SimulatorLoad() {
        this.zeroCoded = false;
        this.SimulatorLoadData_Field = new SimulatorLoadData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return (this.AgentList_Fields.size() * 6) + 14;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSimulatorLoad(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put(Ascii.FF);
        packFloat(byteBuffer, this.SimulatorLoadData_Field.TimeDilation);
        packInt(byteBuffer, this.SimulatorLoadData_Field.AgentCount);
        packBoolean(byteBuffer, this.SimulatorLoadData_Field.CanAcceptAgents);
        byteBuffer.put((byte) this.AgentList_Fields.size());
        for (AgentList agentList : this.AgentList_Fields) {
            packInt(byteBuffer, agentList.CircuitCode);
            packByte(byteBuffer, (byte) agentList.X);
            packByte(byteBuffer, (byte) agentList.Y);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.SimulatorLoadData_Field.TimeDilation = unpackFloat(byteBuffer);
        this.SimulatorLoadData_Field.AgentCount = unpackInt(byteBuffer);
        this.SimulatorLoadData_Field.CanAcceptAgents = unpackBoolean(byteBuffer);
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            AgentList agentList = new AgentList();
            agentList.CircuitCode = unpackInt(byteBuffer);
            agentList.X = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            agentList.Y = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            this.AgentList_Fields.add(agentList);
        }
    }
}

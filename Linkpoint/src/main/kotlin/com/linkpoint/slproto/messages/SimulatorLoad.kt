package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList

class SimulatorLoad : SLMessage() {
    public ArrayList<AgentList> AgentList_Fields = ArrayList<>()
    public SimulatorLoadData SimulatorLoadData_Field

    @JvmStatic
    class AgentList {
        public Int CircuitCode
        public Int X
        public Int Y
    }

    @JvmStatic
    class SimulatorLoadData {
        public Int AgentCount
        public Boolean CanAcceptAgents
        public Float TimeDilation
    }

    public SimulatorLoad() {
        this.zeroCoded = false
        this.SimulatorLoadData_Field = SimulatorLoadData()
    }

    public Int CalcPayloadSize() {
        return (this.AgentList_Fields.size() * 6) + 14
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSimulatorLoad(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put(Ascii.FF)
        packFloat(byteBuffer, this.SimulatorLoadData_Field.TimeDilation)
        packInt(byteBuffer, this.SimulatorLoadData_Field.AgentCount)
        packBoolean(byteBuffer, this.SimulatorLoadData_Field.CanAcceptAgents)
        byteBuffer.put((Byte) this.AgentList_Fields.size())
        for (AgentList agentList : this.AgentList_Fields) {
            packInt(byteBuffer, agentList.CircuitCode)
            packByte(byteBuffer, (Byte) agentList.X)
            packByte(byteBuffer, (Byte) agentList.Y)
        }
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.SimulatorLoadData_Field.TimeDilation = unpackFloat(byteBuffer)
        this.SimulatorLoadData_Field.AgentCount = unpackInt(byteBuffer)
        this.SimulatorLoadData_Field.CanAcceptAgents = unpackBoolean(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            AgentList agentList = AgentList()
            agentList.CircuitCode = unpackInt(byteBuffer)
            agentList.X = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            agentList.Y = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            this.AgentList_Fields.add(agentList)
        }
    }
}

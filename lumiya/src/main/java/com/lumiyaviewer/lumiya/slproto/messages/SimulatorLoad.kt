package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList

class SimulatorLoad : SLMessage {
    ArrayList<AgentList> AgentList_Fields = ArrayList<>()
    SimulatorLoadData SimulatorLoadData_Field

    class AgentList {
        Int CircuitCode
        Int X
        Int Y
    }

    class SimulatorLoadData {
        Int AgentCount
        Boolean CanAcceptAgents
        Float TimeDilation
    }

    SimulatorLoad() {
        this.zeroCoded = false
        this.SimulatorLoadData_Field = SimulatorLoadData()
    }

    Int CalcPayloadSize() {
        return (this.AgentList_Fields.size() * 6) + 14
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSimulatorLoad(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
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

    Unit UnpackPayload(ByteBuffer byteBuffer) {
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

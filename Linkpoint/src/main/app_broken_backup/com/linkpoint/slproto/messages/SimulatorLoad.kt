package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
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

    fun CalcPayloadSize(): Int {
        return (this.AgentList_Fields.size() * 6) + 14
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleSimulatorLoad(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put(Ascii.FF)
        packFloat(byteBuffer, this.SimulatorLoadData_Field.TimeDilation)
        packInt(byteBuffer, this.SimulatorLoadData_Field.AgentCount)
        packBoolean(byteBuffer, this.SimulatorLoadData_Field.CanAcceptAgents)
        byteBuffer.put((this as Byte).AgentList_Fields.size())
        for (AgentList agentList : this.AgentList_Fields) {
            packInt(byteBuffer, agentList.CircuitCode)
            packByte(byteBuffer, (agentList as Byte).X)
            packByte(byteBuffer, (agentList as Byte).Y)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.SimulatorLoadData_Field.TimeDilation = unpackFloat(byteBuffer)
        this.SimulatorLoadData_Field.AgentCount = unpackInt(byteBuffer)
        this.SimulatorLoadData_Field.CanAcceptAgents = unpackBoolean(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            AgentList agentList = AgentList()
            agentList.CircuitCode = unpackInt(byteBuffer)
            agentList.X = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            agentList.Y = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            this.AgentList_Fields.add(agentList)
        }
    }
}

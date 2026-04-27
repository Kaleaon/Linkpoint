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

    public fun CalcPayloadSize(): Int {
        return (this.AgentList_Fields.size() * 6) + 14
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleSimulatorLoad(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
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

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.SimulatorLoadData_Field.TimeDilation = unpackFloat(byteBuffer)
        this.SimulatorLoadData_Field.AgentCount = unpackInt(byteBuffer)
        this.SimulatorLoadData_Field.CanAcceptAgents = unpackBoolean(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val agentList: AgentList = AgentList()
            agentList.CircuitCode = unpackInt(byteBuffer)
            agentList.X = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            agentList.Y = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            this.AgentList_Fields.add(agentList)
        }
    }
}

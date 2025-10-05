package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class SetSimStatusInDatabase : SLMessage() {
    public Data Data_Field = Data()

    @JvmStatic
    class Data {
        public Int AgentCount
        public Byte[] HostName
        public Int PID
        public UUID RegionID
        public Byte[] Status
        public Int TimeToLive
        public Int X
        public Int Y
    }

    public SetSimStatusInDatabase() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return this.Data_Field.HostName.length + 17 + 4 + 4 + 4 + 4 + 4 + 1 + this.Data_Field.Status.length + 4
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSetSimStatusInDatabase(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put(Ascii.SYN)
        packUUID(byteBuffer, this.Data_Field.RegionID)
        packVariable(byteBuffer, this.Data_Field.HostName, 1)
        packInt(byteBuffer, this.Data_Field.X)
        packInt(byteBuffer, this.Data_Field.Y)
        packInt(byteBuffer, this.Data_Field.PID)
        packInt(byteBuffer, this.Data_Field.AgentCount)
        packInt(byteBuffer, this.Data_Field.TimeToLive)
        packVariable(byteBuffer, this.Data_Field.Status, 1)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.Data_Field.RegionID = unpackUUID(byteBuffer)
        this.Data_Field.HostName = unpackVariable(byteBuffer, 1)
        this.Data_Field.X = unpackInt(byteBuffer)
        this.Data_Field.Y = unpackInt(byteBuffer)
        this.Data_Field.PID = unpackInt(byteBuffer)
        this.Data_Field.AgentCount = unpackInt(byteBuffer)
        this.Data_Field.TimeToLive = unpackInt(byteBuffer)
        this.Data_Field.Status = unpackVariable(byteBuffer, 1)
    }
}

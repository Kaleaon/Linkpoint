package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class SendXferPacket : SLMessage() {
    val XferID_Field = XferID()
    val DataPacket_Field = DataPacket()

    class XferID {
        var ID: Long = 0
        var Packet: Int = 0
    }

    class DataPacket {
        lateinit var Data: ByteArray
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = DataPacket_Field.Data.size + 2 + 13

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleSendXferPacket(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.put(Ascii.DC2)
        packLong(buffer, XferID_Field.ID)
        packInt(buffer, XferID_Field.Packet)
        packVariable(buffer, DataPacket_Field.Data, 2)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        XferID_Field.ID = unpackLong(buffer)
        XferID_Field.Packet = unpackInt(buffer)
        DataPacket_Field.Data = unpackVariable(buffer, 2)
    }
}
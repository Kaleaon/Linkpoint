package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class SendXferPacket : SLMessage {
    DataPacket DataPacket_Field = DataPacket()
    XferID XferID_Field = XferID()

    class DataPacket {
        ByteArray Data
    }

    class XferID {
        Long ID
        Int Packet
    }

    SendXferPacket() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.DataPacket_Field.Data.size + 2 + 13
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleSendXferPacket(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.put(Ascii.DC2)
        packLong(byteBuffer, this.XferID_Field.ID)
        packInt(byteBuffer, this.XferID_Field.Packet)
        packVariable(byteBuffer, this.DataPacket_Field.Data, 2)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.XferID_Field.ID = unpackLong(byteBuffer)
        this.XferID_Field.Packet = unpackInt(byteBuffer)
        this.DataPacket_Field.Data = unpackVariable(byteBuffer, 2)
    }
}

package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.base.Ascii
import com.lumiyaviewer.lumiya.slproto.SLMessage
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

    Int CalcPayloadSize() {
        return this.DataPacket_Field.Data.length + 2 + 13
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSendXferPacket(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(Ascii.DC2)
        packLong(byteBuffer, this.XferID_Field.ID)
        packInt(byteBuffer, this.XferID_Field.Packet)
        packVariable(byteBuffer, this.DataPacket_Field.Data, 2)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.XferID_Field.ID = unpackLong(byteBuffer)
        this.XferID_Field.Packet = unpackInt(byteBuffer)
        this.DataPacket_Field.Data = unpackVariable(byteBuffer, 2)
    }
}

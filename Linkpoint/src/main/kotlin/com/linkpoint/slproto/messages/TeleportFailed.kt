package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class TeleportFailed : SLMessage() {
    public ArrayList<AlertInfo> AlertInfo_Fields = ArrayList<>()
    public Info Info_Field

    @JvmStatic
    class AlertInfo {
        public Byte[] ExtraParams
        public Byte[] Message
    }

    @JvmStatic
    class Info {
        public UUID AgentID
        public Byte[] Reason
    }

    public TeleportFailed() {
        this.zeroCoded = false
        this.Info_Field = Info()
    }

    public Int CalcPayloadSize() {
        Int length = this.Info_Field.Reason.length + 17 + 4 + 1
        Iterator<T> it = this.AlertInfo_Fields.iterator()
        while (true) {
            Int i = length
            if (!it.hasNext()) {
                return i
            }
            AlertInfo alertInfo = (AlertInfo) it.next()
            length = alertInfo.ExtraParams.length + alertInfo.Message.length + 1 + 1 + i
        }
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTeleportFailed(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 74)
        packUUID(byteBuffer, this.Info_Field.AgentID)
        packVariable(byteBuffer, this.Info_Field.Reason, 1)
        byteBuffer.put((Byte) this.AlertInfo_Fields.size())
        for (AlertInfo alertInfo : this.AlertInfo_Fields) {
            packVariable(byteBuffer, alertInfo.Message, 1)
            packVariable(byteBuffer, alertInfo.ExtraParams, 1)
        }
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.Info_Field.AgentID = unpackUUID(byteBuffer)
        this.Info_Field.Reason = unpackVariable(byteBuffer, 1)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            AlertInfo alertInfo = AlertInfo()
            alertInfo.Message = unpackVariable(byteBuffer, 1)
            alertInfo.ExtraParams = unpackVariable(byteBuffer, 1)
            this.AlertInfo_Fields.add(alertInfo)
        }
    }
}

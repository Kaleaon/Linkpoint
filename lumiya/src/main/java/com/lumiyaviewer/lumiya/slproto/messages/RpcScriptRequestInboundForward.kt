package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.net.Inet4Address
import java.nio.ByteBuffer
import java.util.UUID

class RpcScriptRequestInboundForward : SLMessage {
    DataBlock DataBlock_Field = DataBlock()

    class DataBlock {
        UUID ChannelID
        Int IntValue
        UUID ItemID
        Inet4Address RPCServerIP
        Int RPCServerPort
        ByteArray StringValue
        UUID TaskID
    }

    RpcScriptRequestInboundForward() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return this.DataBlock_Field.StringValue.length + 60 + 4
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRpcScriptRequestInboundForward(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -96)
        packIPAddress(byteBuffer, this.DataBlock_Field.RPCServerIP)
        packShort(byteBuffer, (Short) this.DataBlock_Field.RPCServerPort)
        packUUID(byteBuffer, this.DataBlock_Field.TaskID)
        packUUID(byteBuffer, this.DataBlock_Field.ItemID)
        packUUID(byteBuffer, this.DataBlock_Field.ChannelID)
        packInt(byteBuffer, this.DataBlock_Field.IntValue)
        packVariable(byteBuffer, this.DataBlock_Field.StringValue, 2)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.DataBlock_Field.RPCServerIP = unpackIPAddress(byteBuffer)
        this.DataBlock_Field.RPCServerPort = unpackShort(byteBuffer) & 65535
        this.DataBlock_Field.TaskID = unpackUUID(byteBuffer)
        this.DataBlock_Field.ItemID = unpackUUID(byteBuffer)
        this.DataBlock_Field.ChannelID = unpackUUID(byteBuffer)
        this.DataBlock_Field.IntValue = unpackInt(byteBuffer)
        this.DataBlock_Field.StringValue = unpackVariable(byteBuffer, 2)
    }
}

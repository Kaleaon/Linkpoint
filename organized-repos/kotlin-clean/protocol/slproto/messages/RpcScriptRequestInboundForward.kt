package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.net.Inet4Address
import java.nio.ByteBuffer
import java.util.UUID

class RpcScriptRequestInboundForward : SLMessage() {
    public DataBlock DataBlock_Field = DataBlock()

    @JvmStatic
    class DataBlock {
        public UUID ChannelID
        public Int IntValue
        public UUID ItemID
        public Inet4Address RPCServerIP
        public Int RPCServerPort
        public ByteArray StringValue
        public UUID TaskID
    }

    public RpcScriptRequestInboundForward() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return this.DataBlock_Field.StringValue.length + 60 + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRpcScriptRequestInboundForward(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
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

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.DataBlock_Field.RPCServerIP = unpackIPAddress(byteBuffer)
        this.DataBlock_Field.RPCServerPort = unpackShort(byteBuffer) & 65535
        this.DataBlock_Field.TaskID = unpackUUID(byteBuffer)
        this.DataBlock_Field.ItemID = unpackUUID(byteBuffer)
        this.DataBlock_Field.ChannelID = unpackUUID(byteBuffer)
        this.DataBlock_Field.IntValue = unpackInt(byteBuffer)
        this.DataBlock_Field.StringValue = unpackVariable(byteBuffer, 2)
    }
}

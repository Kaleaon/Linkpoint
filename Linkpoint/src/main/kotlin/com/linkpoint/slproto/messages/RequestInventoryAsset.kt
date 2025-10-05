package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RequestInventoryAsset : SLMessage() {
    public QueryData QueryData_Field = QueryData()

    @JvmStatic
    class QueryData {
        public UUID AgentID
        public UUID ItemID
        public UUID OwnerID
        public UUID QueryID
    }

    public RequestInventoryAsset() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return 68
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRequestInventoryAsset(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put(Ascii.SUB)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        packUUID(byteBuffer, this.QueryData_Field.AgentID)
        packUUID(byteBuffer, this.QueryData_Field.OwnerID)
        packUUID(byteBuffer, this.QueryData_Field.ItemID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        this.QueryData_Field.AgentID = unpackUUID(byteBuffer)
        this.QueryData_Field.OwnerID = unpackUUID(byteBuffer)
        this.QueryData_Field.ItemID = unpackUUID(byteBuffer)
    }
}

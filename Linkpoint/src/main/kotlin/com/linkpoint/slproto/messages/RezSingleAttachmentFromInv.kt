package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RezSingleAttachmentFromInv : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public ObjectData ObjectData_Field = ObjectData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class ObjectData {
        public Int AttachmentPt
        public Byte[] Description
        public Int EveryoneMask
        public Int GroupMask
        public Int ItemFlags
        public UUID ItemID
        public Byte[] Name
        public Int NextOwnerMask
        public UUID OwnerID
    }

    public RezSingleAttachmentFromInv() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return this.ObjectData_Field.Name.length + 50 + 1 + this.ObjectData_Field.Description.length + 36
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRezSingleAttachmentFromInv(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -117)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.ObjectData_Field.ItemID)
        packUUID(byteBuffer, this.ObjectData_Field.OwnerID)
        packByte(byteBuffer, (Byte) this.ObjectData_Field.AttachmentPt)
        packInt(byteBuffer, this.ObjectData_Field.ItemFlags)
        packInt(byteBuffer, this.ObjectData_Field.GroupMask)
        packInt(byteBuffer, this.ObjectData_Field.EveryoneMask)
        packInt(byteBuffer, this.ObjectData_Field.NextOwnerMask)
        packVariable(byteBuffer, this.ObjectData_Field.Name, 1)
        packVariable(byteBuffer, this.ObjectData_Field.Description, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.ObjectData_Field.ItemID = unpackUUID(byteBuffer)
        this.ObjectData_Field.OwnerID = unpackUUID(byteBuffer)
        this.ObjectData_Field.AttachmentPt = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ObjectData_Field.ItemFlags = unpackInt(byteBuffer)
        this.ObjectData_Field.GroupMask = unpackInt(byteBuffer)
        this.ObjectData_Field.EveryoneMask = unpackInt(byteBuffer)
        this.ObjectData_Field.NextOwnerMask = unpackInt(byteBuffer)
        this.ObjectData_Field.Name = unpackVariable(byteBuffer, 1)
        this.ObjectData_Field.Description = unpackVariable(byteBuffer, 1)
    }
}
